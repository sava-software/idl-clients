package software.sava.idl.clients.kamino.scope.entries;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.core.encoding.ByteUtil;
import software.sava.idl.clients.kamino.KaminoAccounts;
import software.sava.idl.clients.kamino.lend.gen.types.ScopeConfiguration;
import software.sava.idl.clients.kamino.scope.gen.types.DatedPrice;
import software.sava.idl.clients.kamino.scope.gen.types.OracleMappings;
import software.sava.idl.clients.kamino.scope.gen.types.OraclePrices;
import software.sava.idl.clients.kamino.scope.gen.types.OracleType;
import software.sava.idl.clients.kamino.scope.gen.types.Price;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/// Price-chain resolution over a parsed entry set, plus the price scaling
/// helpers. A chain is a list of entry indices terminated by the first index at
/// or past the mappings capacity; resolving it wrong yields a wrong price
/// chain, not a failure.
final class ScopeEntriesRecordTests {

  private static final int CHAIN_END = OracleMappings.PRICE_INFO_ACCOUNTS_LEN; // 512

  private static PublicKey key(final int fill) {
    final byte[] publicKey = new byte[PublicKey.PUBLIC_KEY_LENGTH];
    Arrays.fill(publicKey, (byte) fill);
    return PublicKey.createPubKey(publicKey);
  }

  private static ScopeEntries entries() {
    final var scopeEntries = new ScopeEntry[CHAIN_END];
    for (int i = 0; i < 8; ++i) {
      scopeEntries[i] = FixedPrice.createEntry(i, 100L + i, 2);
    }
    return new ScopeEntriesRecord(key(1), 42L, scopeEntries);
  }

  @Test
  void chainsResolveAndTrimAtTheTerminator() {
    final var priceChains = entries().readPriceChains(
        key(9),
        new ScopeConfiguration(key(2), new int[]{3, 1, CHAIN_END, CHAIN_END}, new int[]{5, CHAIN_END, CHAIN_END, CHAIN_END}));

    assertNotNull(priceChains);
    final var priceChain = priceChains.priceChain();
    assertEquals(2, priceChain.length, "trimmed at the first terminator");
    // resolved in chain order, not index order
    assertEquals(3, ((FixedPrice) priceChain[0]).index());
    assertEquals(1, ((FixedPrice) priceChain[1]).index());

    final var twapChain = priceChains.twapChain();
    assertEquals(1, twapChain.length);
    assertEquals(5, ((FixedPrice) twapChain[0]).index());
  }

  @Test
  void fullChainIsNotTrimmed() {
    final var priceChains = entries().readPriceChains(
        key(9),
        new ScopeConfiguration(key(2), new int[]{0, 1, 2, 3}, new int[]{CHAIN_END, CHAIN_END, CHAIN_END, CHAIN_END}));

    assertEquals(4, priceChains.priceChain().length);
    assertEquals(0, priceChains.twapChain().length);
  }

  /// A chain index inside the capacity must resolve to a parsed entry; a hole
  /// is an inconsistency between the mappings and the configuration.
  @Test
  void chainPointingAtAMissingEntryThrows() {
    assertThrows(IllegalStateException.class, () -> entries().readPriceChains(
        key(9),
        new ScopeConfiguration(key(2), new int[]{100, CHAIN_END, CHAIN_END, CHAIN_END}, new int[]{CHAIN_END, CHAIN_END, CHAIN_END, CHAIN_END})));
  }

  /// The none-mint sentinels mean "no scope configuration" — null, not an
  /// empty chain.
  @Test
  void noneMintsResolveToNull() {
    final var config = new ScopeConfiguration(key(2), new int[]{0, CHAIN_END, CHAIN_END, CHAIN_END}, new int[]{CHAIN_END, CHAIN_END, CHAIN_END, CHAIN_END});
    assertNull(entries().readPriceChains(PublicKey.NONE, config));
    assertNull(entries().readPriceChains(KaminoAccounts.NULL_KEY, config));
    // a real mint resolves
    assertNotNull(entries().readPriceChains(key(9), config));
  }

  @Test
  void oracleEntriesFiltersByTypeAndAccount() {
    final var oracle = key(5);
    final var scopeEntries = new ScopeEntry[]{
        new PythPull(0, oracle, java.util.Set.of(), null, java.util.OptionalInt.empty()),
        new PythPull(1, key(6), java.util.Set.of(), null, java.util.OptionalInt.empty()),
        new PythPullEMA(2, oracle, java.util.Set.of()),
        // not an OracleEntry at all: must be filtered, not crash the stream
        new Unused(3),
        new PythPull(4, oracle, java.util.Set.of(), null, java.util.OptionalInt.empty())
    };
    final var record = new ScopeEntriesRecord(key(1), 42L, scopeEntries);

    final var matches = record.oracleEntries(oracle, OracleType.PythPull);
    assertEquals(
        List.of(scopeEntries[0], scopeEntries[4]),
        matches,
        "same oracle AND same type only");
    assertEquals(List.of(scopeEntries[2]), record.oracleEntries(oracle, OracleType.PythPullEMA));
    assertEquals(List.of(), record.oracleEntries(key(7), OracleType.PythPull));

    assertEquals(5, record.numEntries());
    assertSame(scopeEntries[3], record.scopeEntry(3));
    assertEquals(key(1), record.pubKey());
    assertEquals(42L, record.slot());
  }

  // ---------------------------------------------------------------------------
  // Price scaling
  // ---------------------------------------------------------------------------

  @Test
  void scaleScopePriceFromDatedPrice() {
    final var scaled = ScopeReader.scaleScopePrice(
        new DatedPrice(new Price(1234500L, 4L), 0L, 0L, new byte[DatedPrice.GENERIC_DATA_LEN]));
    assertEquals(0, new BigDecimal("123.45").compareTo(scaled));

    // the value is a u64: the sign bit is magnitude, not a negative price
    final var unsigned = ScopeReader.scaleScopePrice(
        new DatedPrice(new Price(-1L, 0L), 0L, 0L, new byte[DatedPrice.GENERIC_DATA_LEN]));
    assertEquals(new BigDecimal("18446744073709551615"), unsigned);
    assertTrue(unsigned.signum() > 0);
  }

  @Test
  void scaleScopePriceFromRawAccountData() {
    // an OraclePrices account with two dated prices; read the second by index
    final byte[] data = new byte[OraclePrices.PRICES_OFFSET + (DatedPrice.BYTES * 2)];
    final int secondOffset = OraclePrices.PRICES_OFFSET + DatedPrice.BYTES;
    ByteUtil.putInt64LE(data, secondOffset, 987_650L); // value
    ByteUtil.putInt64LE(data, secondOffset + Long.BYTES, 4L); // exp

    assertEquals(0, new BigDecimal("98.765").compareTo(ScopeReader.scaleScopePrice(data, 1)));

    // a zero value short-circuits to ZERO without reading the exponent
    assertSame(BigDecimal.ZERO, ScopeReader.scaleScopePrice(data, 0));

    // u64 magnitude on the raw path too
    ByteUtil.putInt64LE(data, OraclePrices.PRICES_OFFSET, -1L);
    ByteUtil.putInt64LE(data, OraclePrices.PRICES_OFFSET + Long.BYTES, 0L);
    assertEquals(new BigDecimal("18446744073709551615"), ScopeReader.scaleScopePrice(data, 0));
  }

  /// The `Reserve` overload reads exactly two paths out of a fetched reserve —
  /// the liquidity mint and the token info's scope configuration — and must
  /// agree with the explicit two-arg call on both, including the null-mint
  /// sentinel short-circuit.
  @Test
  void reserveOverloadReadsTheMintAndScopeConfiguration() {
    final var mint = key(9);
    final var scopeConfig = new ScopeConfiguration(
        key(2), new int[]{3, 1, CHAIN_END, CHAIN_END}, new int[]{5, CHAIN_END, CHAIN_END, CHAIN_END});
    final var entries = entries();

    final var fromReserve = entries.readPriceChains(reserve(mint, scopeConfig));
    final var explicit = entries.readPriceChains(mint, scopeConfig);
    assertNotNull(fromReserve);
    assertArrayEquals(explicit.priceChain(), fromReserve.priceChain());
    assertArrayEquals(explicit.twapChain(), fromReserve.twapChain());

    // the null-mint sentinels short-circuit through the reserve path too
    assertNull(entries.readPriceChains(reserve(PublicKey.NONE, scopeConfig)));
    assertNull(entries.readPriceChains(reserve(KaminoAccounts.NULL_KEY, scopeConfig)));
  }

  /// Only the two paths the overload reads are populated; a generated record
  /// accepts null/zero for everything else.
  private static software.sava.idl.clients.kamino.lend.gen.types.Reserve reserve(
      final PublicKey mint,
      final ScopeConfiguration scopeConfiguration) {
    final var liquidity = new software.sava.idl.clients.kamino.lend.gen.types.ReserveLiquidity(
        mint, null, null, 0, null, null, 0, 0, 0, 0, null, null, null, null, null, null, 0, null, null);
    final var tokenInfo = new software.sava.idl.clients.kamino.lend.gen.types.TokenInfo(
        null, null, 0, 0, 0, scopeConfiguration, null, null, 0, null, null);
    final var config = new software.sava.idl.clients.kamino.lend.gen.types.ReserveConfig(
        0, 0, 0, 0, 0, 0, 0, null, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, null, null, 0, 0, 0,
        tokenInfo, null, null, null, 0, 0, 0, 0, 0, null, 0, 0, 0, 0, 0);
    return new software.sava.idl.clients.kamino.lend.gen.types.Reserve(
        null, null, 0, null, null, null, null, liquidity, null, null, null, config, null, 0, null, null, null);
  }
}

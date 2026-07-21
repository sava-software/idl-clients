package software.sava.idl.clients.kamino.scope.entries;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.core.encoding.ByteUtil;
import software.sava.idl.clients.kamino.scope.gen.types.OracleMappings;
import software.sava.idl.clients.kamino.scope.gen.types.OracleType;
import software.sava.idl.clients.kamino.scope.gen.types.TwapEnabledBitmask;

import java.math.BigDecimal;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

/// Hostile Scope account bytes must parse in bounded time, not just without crashing.
/// Both shapes here were fuzzScopeReader findings (the minimized inputs live in the
/// seed corpus at src/test/resources/fuzz/scopeReader); the synthetic mappings below
/// pin each mechanism independently of the corpus bytes. The unmutated code parses
/// each in well under a second — the generous preemptive timeouts are not a
/// thin-margin harness, they separate microseconds from minutes.
final class ScopeReaderHostileInputTests {

  private static final int SLOTS = OracleMappings.PRICE_INFO_ACCOUNTS_LEN;
  private static final int NO_REF = 0xFFFF;

  private static OracleMappings mappings(final byte[] priceTypes, final byte[][] generic) {
    final var priceInfoAccounts = new PublicKey[SLOTS];
    final var tolerance = new int[SLOTS];
    final var refPrice = new int[SLOTS];
    final var twapBitmasks = new TwapEnabledBitmask[SLOTS];
    for (int i = 0; i < SLOTS; ++i) {
      priceInfoAccounts[i] = PublicKey.NONE;
      tolerance[i] = NO_REF;
      refPrice[i] = NO_REF; // out of range -> resolves to no ref price entry
      twapBitmasks[i] = new TwapEnabledBitmask(0);
    }
    return new OracleMappings(PublicKey.NONE, null, priceInfoAccounts, priceTypes, tolerance, twapBitmasks, refPrice, generic);
  }

  /// A FixedPrice entry with a huge negative exponent used to take ~50s (and can
  /// exhaust memory): movePointLeft normalizes the negative resulting scale through
  /// setScale(0), materializing value*10^|exp| as a ~billion-digit BigInteger.
  /// scaleByPowerOfTen keeps the scale symbolic; the parse must stay instant.
  @Test
  void hostileFixedPriceExponentParsesInBoundedTime() {
    final var priceTypes = new byte[SLOTS];
    final var generic = new byte[SLOTS][20];
    priceTypes[0] = (byte) OracleType.FixedPrice.ordinal();
    ByteUtil.putInt64LE(generic[0], 0, 5L);              // Price.value
    ByteUtil.putInt64LE(generic[0], 8, -2_000_000_000L); // Price.exp
    for (int i = 1; i < SLOTS; ++i) {
      priceTypes[i] = (byte) OracleType.Unused.ordinal();
    }

    final var entries = assertTimeoutPreemptively(
        Duration.ofSeconds(10),
        () -> ScopeReader.parseEntries(-1L, mappings(priceTypes, generic)));

    assertEquals(SLOTS, entries.numEntries());
    final var fixedPrice = assertInstanceOf(FixedPrice.class, entries.scopeEntry(0));
    // 5 * 10^-(-2e9): the scale stays symbolic instead of expanding to 2e9 digits
    assertEquals(new BigDecimal("5E+2000000000"), fixedPrice.decimal());
  }

  /// A chain of CappedFloored entries whose source, cap, and floor all reference the
  /// next slot fans out 3 ways per level: without memoizing computed entries the walk
  /// is 3^511, with it each slot is computed once. Guards the entries[] write-through
  /// in ScopeReaderRecord.entry.
  @Test
  void forwardReferenceFanOutParsesInBoundedTime() {
    final var priceTypes = new byte[SLOTS];
    final var generic = new byte[SLOTS][20];
    for (int i = 0; i < SLOTS - 1; ++i) {
      priceTypes[i] = (byte) OracleType.CappedFloored.ordinal();
      final int next = i + 1;
      ByteUtil.putInt16LE(generic[i], 0, next);  // sourceEntry
      generic[i][2] = 1;                          // capEntry present
      ByteUtil.putInt16LE(generic[i], 3, next);
      generic[i][5] = 1;                          // floorEntry present
      ByteUtil.putInt16LE(generic[i], 6, next);
    }
    priceTypes[SLOTS - 1] = (byte) OracleType.Unused.ordinal();

    final var entries = assertTimeoutPreemptively(
        Duration.ofSeconds(10),
        () -> ScopeReader.parseEntries(-1L, mappings(priceTypes, generic)));

    assertEquals(SLOTS, entries.numEntries());
    final var head = assertInstanceOf(CappedFloored.class, entries.scopeEntry(0));
    // all three branches resolve to the same memoized instance of slot 1
    final var next = entries.scopeEntry(1);
    assertEquals(next, head.sourceEntry());
    assertEquals(next, head.capEntry());
    assertEquals(next, head.flooredEntry());
  }
}

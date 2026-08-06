package software.sava.idl.clients.kamino.scope.entries;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.kamino.KaminoAccounts;
import software.sava.idl.clients.kamino.lend.gen.types.Reserve;
import software.sava.idl.clients.kamino.lend.gen.types.ScopeConfiguration;
import software.sava.idl.clients.kamino.scope.gen.types.OracleMappings;
import software.sava.idl.clients.kamino.scope.gen.types.OracleType;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.OptionalInt;

record ScopeEntriesRecord(PublicKey pubKey,
                          PublicKey oraclePrices,
                          long slot,
                          ScopeEntry[] scopeEntries,
                          byte[] priceTypes,
                          ScopeEntry[] referencePrices,
                          int[] refPriceIndices,
                          int[] toleranceOrTwapSource) implements ScopeEntries {

  /// `u16::MAX` in the tolerance field means none is configured.
  private static final int NO_REF_PRICE_TOLERANCE = 0xFFFF;
  /// `MAX_REF_RATIO_TOLERANCE_BPS`: what the program uses when a slot has a reference
  /// price but no explicit tolerance. The refresh handlers enforce the bound whenever
  /// the reference-price *index* is set, and read the tolerance as
  /// `unwrap_or(MAX_REF_RATIO_TOLERANCE_BPS)` — so "unset" is a default, never "no bound".
  private static final int DEFAULT_REF_PRICE_TOLERANCE_BPS = 500;

  @Override
  public PriceChains readPriceChains(final Reserve reserve) {
    final var mintKey = reserve.liquidity().mintPubkey();
    final var scopeConfiguration = reserve.config().tokenInfo().scopeConfiguration();
    return readPriceChains(mintKey, scopeConfiguration);
  }

  @Override
  public PriceChains readPriceChains(final PublicKey mintKey, final ScopeConfiguration scopeConfiguration) {
    if (KaminoAccounts.isNullKey(mintKey)) {
      return null;
    }
    // `ScopeConfiguration::is_enabled` is exactly this test, and the program consults
    // the scope price path only when it passes — a disabled reserve is priced from
    // elsewhere entirely. Its price chain is allowed to keep whatever stale indices it
    // had (`is_valid` accepts any chain while disabled), so resolving them would invent
    // a price the program never reads.
    final var priceFeed = scopeConfiguration.priceFeed();
    if (KaminoAccounts.isNullKey(priceFeed)) {
      return null;
    }
    // A price chain is a list of slot indices, and the same index means a different
    // token in a different feed — so chains resolved against the wrong feed's entries
    // are silently wrong prices, not an error. The program will only accept the prices
    // account this field names, so when the parse knows which feed it came from a
    // mismatch is a caller mistake worth refusing.
    if (!KaminoAccounts.isNullKey(oraclePrices) && !priceFeed.equals(oraclePrices)) {
      throw new IllegalArgumentException("Price feed " + priceFeed
          + " is not the feed these entries came from (" + oraclePrices
          + "); its price chain indexes a different mapping.");
    }
    final var priceChain = parseChain(scopeConfiguration.priceChain(), scopeEntries);
    final var twapChain = parseChain(scopeConfiguration.twapChain(), scopeEntries);
    return new PriceChainsRecord(priceChain, twapChain);
  }

  @Override
  public boolean frozen(final int index) {
    return (priceTypes[index] & ScopeReaderRecord.FROZEN_FLAG) != 0;
  }

  @Override
  public ScopeEntry referencePrice(final int index) {
    return referencePrices[index];
  }

  /// The bound the program will actually enforce, which is why this is empty only when
  /// the slot has no reference price at all.
  ///
  /// The refresh handlers gate the divergence check on the reference-price *index*
  /// being set, then read the tolerance as `unwrap_or(MAX_REF_RATIO_TOLERANCE_BPS)`.
  /// So a configured reference price always carries a bound, and an unset tolerance
  /// field means 500 bps rather than "unbounded".
  ///
  /// Two cases make the field itself unreadable while the bound still applies, and
  /// both fall back to that default exactly as the program does — its accessor returns
  /// `None` for them and the caller unwraps it. For a TWAP type the field is a source
  /// slot index (`get_twap_source_or_ref_price_tolerance_bps` branches on `is_twap`
  /// first), and for an oracle type this IDL does not know `is_twap` itself fails, so
  /// the tolerance branch is never reached.
  @Override
  public OptionalInt referenceToleranceBps(final int index) {
    if (refPriceIndices[index] >= scopeEntries.length) {
      return OptionalInt.empty();
    } else if (scopeEntries[index].oracleType() == null || scopeEntries[index] instanceof ScopeTwap) {
      return OptionalInt.of(DEFAULT_REF_PRICE_TOLERANCE_BPS);
    }
    final int toleranceBps = toleranceOrTwapSource[index];
    return OptionalInt.of(toleranceBps == NO_REF_PRICE_TOLERANCE
        ? DEFAULT_REF_PRICE_TOLERANCE_BPS
        : toleranceBps);
  }

  @Override
  public ScopeEntry scopeEntry(final int index) {
    return scopeEntries[index];
  }

  @Override
  public int numEntries() {
    return scopeEntries.length;
  }

  @Override
  public List<ScopeEntry> oracleEntries(final PublicKey oracle, final OracleType oracleType) {
    return Arrays.stream(scopeEntries).filter(entry -> {
      if (entry instanceof OracleEntry oracleEntry) {
        return oracleEntry.oracleType() == oracleType && oracleEntry.oracle().equals(oracle);
      }
      return false;
    }).toList();
  }

  private ScopeEntry[] parseChain(final int[] priceChain, final ScopeEntry[] scopeEntries) {
    final var entries = new ScopeEntry[priceChain.length];
    int j = 0;
    for (int i = 0, entryIndex; i < priceChain.length; ++i) {
      entryIndex = priceChain[i];
      if (entryIndex >= OracleMappings.PRICE_INFO_ACCOUNTS_LEN) {
        break;
      }
      final var entry = scopeEntries[entryIndex];
      if (entry == null) {
        throw new IllegalStateException("Entry not found at index: " + entryIndex);
      }
      entries[j++] = entry;
    }
    if (j < entries.length) {
      final var trimmed = new ScopeEntry[j];
      System.arraycopy(entries, 0, trimmed, 0, j);
      return trimmed;
    } else {
      return entries;
    }
  }

  /// The account, the slot it was read at, the feed it was bound to, and the parsed
  /// entries are the identity; the remaining components are per-slot views of the same
  /// bytes and cannot vary independently of `scopeEntries`. Written out by name rather
  /// than with a record pattern, so adding a component is a compile error here only
  /// when it genuinely belongs to the identity.
  @Override
  public boolean equals(final Object o) {
    return o instanceof ScopeEntriesRecord other
        && slot == other.slot
        && Objects.equals(pubKey, other.pubKey)
        && Objects.equals(oraclePrices, other.oraclePrices)
        && Arrays.equals(scopeEntries, other.scopeEntries);
  }

  @Override
  public int hashCode() {
    // pubKey is null when the mappings were read from raw bytes rather than an
    // AccountInfo, which is how the fuzz harness parses them
    int result = Objects.hashCode(pubKey);
    result = 31 * result + Objects.hashCode(oraclePrices);
    result = 31 * result + Long.hashCode(slot);
    result = 31 * result + Arrays.hashCode(scopeEntries);
    return result;
  }

  @Override
  public String toString() {
    return "ScopeEntriesRecord{" +
        "pubKey=" + pubKey +
        ", oraclePrices=" + oraclePrices +
        ", slot=" + slot +
        ", scopeEntries=" + Arrays.toString(scopeEntries) +
        '}';
  }
}

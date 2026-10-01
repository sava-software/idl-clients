package software.sava.idl.clients.kamino.scope.entries;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.kamino.scope.gen.types.Condition;
import software.sava.idl.clients.kamino.scope.gen.types.EmaType;
import software.sava.idl.clients.kamino.scope.gen.types.OracleType;

import java.util.Arrays;
import java.util.OptionalInt;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/// The entry types carrying arrays override record equality by hand — each
/// override must compare **every** component, and against array *content*, not
/// identity. So does CappedFloored, whose three inputs are the fan-out the reader
/// shares, and every composite hashes an input by its slot and type alone. The
/// pattern throughout: an equal twin built from distinct array instances, then
/// one variant per component; a dropped comparison passes the twin but misses
/// its variant. A list also gets a variant changing only its first entry, which
/// its hash must still feel however many entries follow.
final class ScopeEntryEqualityTests {

  private static PublicKey key(final int fill) {
    final byte[] publicKey = new byte[PublicKey.PUBLIC_KEY_LENGTH];
    Arrays.fill(publicKey, (byte) fill);
    return PublicKey.createPubKey(publicKey);
  }

  private static ScopeEntry[] sources() {
    return new ScopeEntry[]{new Unused(7), FixedPrice.createEntry(8, 100L, 2)};
  }

  /// [#sources] with its first entry moved to slot 10. The two lists hash `31 * (31 + h(first)) +
  /// h(second)` apart by `31 * (h(Unused(10)) - h(Unused(7))) = 31 * 31 * 3`, the types' hashes
  /// cancelling, so the hashes differ whatever the enum constants' identity hashes are.
  private static ScopeEntry[] firstSourceMoved() {
    return new ScopeEntry[]{new Unused(10), FixedPrice.createEntry(8, 100L, 2)};
  }

  private static void assertBothDiffer(final Object base, final Object variant, final String component) {
    assertNotEquals(base, variant, component);
    assertNotEquals(variant, base, component + " (reversed)");
    assertNotEquals(base.hashCode(), variant.hashCode(), component + " hashCode");
  }

  @Test
  void mostRecentOfEntry() {
    final var refPrice = new Unused(9);
    final var base = new MostRecentOfEntry(1, sources(), 250, 3_600L, refPrice, OptionalInt.of(50));
    final var twin = new MostRecentOfEntry(1, sources(), 250, 3_600L, new Unused(9), OptionalInt.of(50));

    assertEquals(base, base);
    assertEquals(base, twin);
    assertEquals(twin, base);
    assertEquals(base.hashCode(), twin.hashCode());
    assertNotEquals(base, null);
    assertNotEquals(base, new Unused(1));

    assertBothDiffer(base, new MostRecentOfEntry(2, sources(), 250, 3_600L, refPrice, OptionalInt.of(50)), "index");
    assertBothDiffer(base, new MostRecentOfEntry(1, new ScopeEntry[]{new Unused(7)}, 250, 3_600L, refPrice, OptionalInt.of(50)), "sources");
    assertBothDiffer(base, new MostRecentOfEntry(1, firstSourceMoved(), 250, 3_600L, refPrice, OptionalInt.of(50)), "sources[0]");
    assertBothDiffer(base, new MostRecentOfEntry(1, sources(), 300, 3_600L, refPrice, OptionalInt.of(50)), "maxDivergenceBps");
    assertBothDiffer(base, new MostRecentOfEntry(1, sources(), 250, 60L, refPrice, OptionalInt.of(50)), "sourcesMaxAgeS");
    assertBothDiffer(base, new MostRecentOfEntry(1, sources(), 250, 3_600L, new Unused(10), OptionalInt.of(50)), "refPrice");
    assertBothDiffer(base, new MostRecentOfEntry(1, sources(), 250, 3_600L, refPrice, OptionalInt.empty()), "refPriceToleranceBps");

    // a null ref price on both sides still compares
    assertEquals(
        new MostRecentOfEntry(1, sources(), 250, 3_600L, null, OptionalInt.empty()),
        new MostRecentOfEntry(1, sources(), 250, 3_600L, null, OptionalInt.empty()));
  }

  @Test
  void cappedMostRecentOf() {
    final var cap = FixedPrice.createEntry(9, 5L, 0);
    final var base = new CappedMostRecentOf(1, sources(), 100, 60L, cap);
    final var twin = new CappedMostRecentOf(1, sources(), 100, 60L, FixedPrice.createEntry(9, 5L, 0));

    assertEquals(base, twin);
    assertEquals(twin, base);
    assertEquals(base.hashCode(), twin.hashCode());
    assertNotEquals(base, null);
    assertNotEquals(base, new Unused(1));

    assertBothDiffer(base, new CappedMostRecentOf(2, sources(), 100, 60L, cap), "index");
    assertBothDiffer(base, new CappedMostRecentOf(1, new ScopeEntry[]{new Unused(7)}, 100, 60L, cap), "sources");
    assertBothDiffer(base, new CappedMostRecentOf(1, firstSourceMoved(), 100, 60L, cap), "sources[0]");
    assertBothDiffer(base, new CappedMostRecentOf(1, sources(), 200, 60L, cap), "maxDivergenceBps");
    assertBothDiffer(base, new CappedMostRecentOf(1, sources(), 100, 61L, cap), "sourcesMaxAgeS");
    assertBothDiffer(base, new CappedMostRecentOf(1, sources(), 100, 60L, new Unused(11)), "capEntry");
  }

  @Test
  void conditional() {
    final var base = new Conditional(1, Condition.Gt, 50, sources());
    final var twin = new Conditional(1, Condition.Gt, 50, sources());

    assertEquals(base, twin);
    assertEquals(twin, base);
    assertEquals(base.hashCode(), twin.hashCode());
    assertNotEquals(base, null);
    assertNotEquals(base, new Unused(1));

    assertBothDiffer(base, new Conditional(2, Condition.Gt, 50, sources()), "index");
    assertBothDiffer(base, new Conditional(1, Condition.Lt, 50, sources()), "condition");
    assertBothDiffer(base, new Conditional(1, Condition.Gt, 51, sources()), "toleranceBps");
    assertBothDiffer(base, new Conditional(1, Condition.Gt, 50, new ScopeEntry[]{new Unused(7)}), "sources");
    assertBothDiffer(base, new Conditional(1, Condition.Gt, 50, firstSourceMoved()), "sources[0]");

    // a null condition is comparable and hashable
    final var nullCondition = new Conditional(1, null, 50, sources());
    assertEquals(nullCondition, new Conditional(1, null, 50, sources()));
    assertNotEquals(base, nullCondition);
    assertDoesNotThrow(nullCondition::hashCode);
  }

  @Test
  void cappedFloored() {
    final var base = new CappedFloored(1, new Unused(7), new Unused(8), new Unused(9), 7_200L);
    final var twin = new CappedFloored(1, new Unused(7), new Unused(8), new Unused(9), 7_200L);

    assertEquals(base, base);
    assertEquals(base, twin);
    assertEquals(twin, base);
    assertEquals(base.hashCode(), twin.hashCode());
    assertNotEquals(base, null);
    assertNotEquals(base, new Unused(1));

    assertBothDiffer(base, new CappedFloored(2, new Unused(7), new Unused(8), new Unused(9), 7_200L), "index");
    assertBothDiffer(base, new CappedFloored(1, new Unused(10), new Unused(8), new Unused(9), 7_200L), "sourceEntry");
    assertBothDiffer(base, new CappedFloored(1, new Unused(7), new Unused(10), new Unused(9), 7_200L), "capEntry");
    assertBothDiffer(base, new CappedFloored(1, new Unused(7), new Unused(8), new Unused(10), 7_200L), "flooredEntry");
    assertBothDiffer(base, new CappedFloored(1, new Unused(7), new Unused(8), new Unused(9), 60L), "sourcesMaxAgeS");

    // an absent bound compares and hashes, on both sides or one
    final var noFloor = new CappedFloored(1, new Unused(7), new Unused(8), null, 7_200L);
    assertEquals(noFloor, new CappedFloored(1, new Unused(7), new Unused(8), null, 7_200L));
    assertEquals(noFloor.hashCode(), new CappedFloored(1, new Unused(7), new Unused(8), null, 7_200L).hashCode());
    assertBothDiffer(base, noFloor, "flooredEntry absent");
  }

  @Test
  void multiplicationChain() {
    final var base = new MultiplicationChain(1, sources(), 120L);
    final var twin = new MultiplicationChain(1, sources(), 120L);

    assertEquals(base, base);
    assertEquals(base, twin);
    assertEquals(twin, base);
    assertEquals(base.hashCode(), twin.hashCode());
    assertNotEquals(base, null);
    assertNotEquals(base, new Unused(1));

    assertBothDiffer(base, new MultiplicationChain(2, sources(), 120L), "index");
    assertBothDiffer(base, new MultiplicationChain(1, new ScopeEntry[]{new Unused(7)}, 120L), "sourceEntries");
    assertBothDiffer(base, new MultiplicationChain(1, firstSourceMoved(), 120L), "sourceEntries[0]");
    assertBothDiffer(base, new MultiplicationChain(1, sources(), 60L), "sourcesMaxAgeS");

    // a missing source list compares and hashes like the record default did: equal to another
    // missing one, unequal to any list
    assertEquals(new MultiplicationChain(1, null, 120L), new MultiplicationChain(1, null, 120L));
    assertEquals(new MultiplicationChain(1, null, 120L).hashCode(), new MultiplicationChain(1, null, 120L).hashCode());
    assertBothDiffer(base, new MultiplicationChain(1, null, 120L), "sourceEntries absent");
  }

  /// Slot 0 is a slot like any other, and every hand-written hash starts its mix from the slot:
  /// each composite and NotYetSupported hashes there, equal twins alike, where a mix that divided
  /// by the slot would throw.
  @Test
  void anEntryAtSlotZeroHashes() {
    assertEquals(
        new MostRecentOfEntry(0, sources(), 250, 3_600L, new Unused(9), OptionalInt.of(50)).hashCode(),
        new MostRecentOfEntry(0, sources(), 250, 3_600L, new Unused(9), OptionalInt.of(50)).hashCode());
    assertEquals(
        new CappedMostRecentOf(0, sources(), 100, 60L, new Unused(9)).hashCode(),
        new CappedMostRecentOf(0, sources(), 100, 60L, new Unused(9)).hashCode());
    assertEquals(
        new Conditional(0, Condition.Gt, 50, sources()).hashCode(),
        new Conditional(0, Condition.Gt, 50, sources()).hashCode());
    assertEquals(
        new CappedFloored(0, new Unused(7), new Unused(8), new Unused(9), 7_200L).hashCode(),
        new CappedFloored(0, new Unused(7), new Unused(8), new Unused(9), 7_200L).hashCode());
    assertEquals(
        new MultiplicationChain(0, sources(), 120L).hashCode(),
        new MultiplicationChain(0, sources(), 120L).hashCode());
    assertEquals(
        new NotYetSupported(0, key(3), OracleType.PythPull, Set.of(EmaType.Ema1h), new Unused(9), OptionalInt.of(50), new byte[]{1, 2, 3}).hashCode(),
        new NotYetSupported(0, key(3), OracleType.PythPull, Set.of(EmaType.Ema1h), new Unused(9), OptionalInt.of(50), new byte[]{1, 2, 3}).hashCode());
  }

  @Test
  void notYetSupported() {
    final var account = key(3);
    final var refPrice = new Unused(9);
    final byte[] generic = {1, 2, 3};
    final var base = new NotYetSupported(1, account, OracleType.PythPull, Set.of(EmaType.Ema1h), refPrice, OptionalInt.of(50), generic);
    final var twin = new NotYetSupported(1, key(3), OracleType.PythPull, Set.of(EmaType.Ema1h), new Unused(9), OptionalInt.of(50), new byte[]{1, 2, 3});

    assertEquals(base, twin);
    assertEquals(twin, base);
    assertEquals(base.hashCode(), twin.hashCode());
    assertNotEquals(base, null);
    assertNotEquals(base, new Unused(1));

    assertBothDiffer(base, new NotYetSupported(2, account, OracleType.PythPull, Set.of(EmaType.Ema1h), refPrice, OptionalInt.of(50), generic), "index");
    assertBothDiffer(base, new NotYetSupported(1, key(4), OracleType.PythPull, Set.of(EmaType.Ema1h), refPrice, OptionalInt.of(50), generic), "priceAccount");
    assertBothDiffer(base, new NotYetSupported(1, account, OracleType.PythPullEMA, Set.of(EmaType.Ema1h), refPrice, OptionalInt.of(50), generic), "oracleType");
    assertBothDiffer(base, new NotYetSupported(1, account, OracleType.PythPull, Set.of(EmaType.Ema8h), refPrice, OptionalInt.of(50), generic), "emaTypes");
    assertBothDiffer(base, new NotYetSupported(1, account, OracleType.PythPull, Set.of(EmaType.Ema1h), new Unused(10), OptionalInt.of(50), generic), "refPrice");
    assertBothDiffer(base, new NotYetSupported(1, account, OracleType.PythPull, Set.of(EmaType.Ema1h), refPrice, OptionalInt.of(51), generic), "refPriceToleranceBps");
    assertBothDiffer(base, new NotYetSupported(1, account, OracleType.PythPull, Set.of(EmaType.Ema1h), refPrice, OptionalInt.of(50), new byte[]{9}), "generic");

    // the null oracle type produced for beyond-enum ordinals is comparable
    final var nullType = new NotYetSupported(1, account, null, Set.of(), null, OptionalInt.empty(), generic);
    assertEquals(nullType, new NotYetSupported(1, account, null, Set.of(), null, OptionalInt.empty(), new byte[]{1, 2, 3}));
    assertNotEquals(base, nullType);
    assertDoesNotThrow(nullType::hashCode);
  }

  @Test
  void scopeEntriesRecord() {
    final var entries = new ScopeEntry[]{new Unused(0), FixedPrice.createEntry(1, 100L, 2)};
    final var base = entriesRecord(key(1), 42L, entries);
    final var twin = entriesRecord(key(1), 42L, new ScopeEntry[]{new Unused(0), FixedPrice.createEntry(1, 100L, 2)});

    assertEquals(base, twin);
    assertEquals(twin, base);
    assertEquals(base.hashCode(), twin.hashCode());
    assertNotEquals(base, null);
    assertNotEquals(base, new Unused(1));

    assertBothDiffer(base, entriesRecord(key(2), 42L, entries), "pubKey");
    assertBothDiffer(base, entriesRecord(key(1), 43L, entries), "slot");
    assertBothDiffer(base, entriesRecord(key(1), 42L, new ScopeEntry[]{new Unused(0)}), "scopeEntries");

    // The bound feed is part of identity: two records over the same bytes behave
    // differently if one knows its feed — it refuses a reserve the other resolves.
    final var bound = entriesRecord(key(1), key(0x50), 42L, entries);
    assertBothDiffer(base, bound, "oraclePrices bound vs unbound");
    assertBothDiffer(bound, entriesRecord(key(1), key(0x51), 42L, entries), "oraclePrices");
    assertEquals(bound, entriesRecord(key(1), key(0x50), 42L, entries));

    // pubKey is null when the mappings came from raw bytes rather than an AccountInfo
    final var anonymous = entriesRecord(null, 42L, entries);
    assertEquals(anonymous, entriesRecord(null, 42L, entries));
    assertEquals(anonymous.hashCode(), entriesRecord(null, 42L, entries).hashCode());
    assertBothDiffer(base, anonymous, "null pubKey");
  }

  /// The hand-written toStrings render arrays by content — the default record
  /// toString would print identity hashes.
  @Test
  void toStringsRenderArrayContent() {
    final var mostRecent = new MostRecentOfEntry(1, sources(), 250, 3_600L, null, OptionalInt.empty());
    assertTrue(mostRecent.toString().contains("maxDivergenceBps=250"), mostRecent.toString());
    final var capped = new CappedMostRecentOf(1, sources(), 100, 60L, null);
    assertTrue(capped.toString().contains("maxDivergenceBps=100"), capped.toString());
    final var conditional = new Conditional(1, Condition.Gt, 50, sources());
    assertTrue(conditional.toString().contains("Gt"), conditional.toString());
    final var chain = new MultiplicationChain(1, sources(), 120L);
    assertTrue(chain.toString().contains("sourceEntries=[Unused"), chain.toString());
    final var entriesRecord = entriesRecord(key(1), 42L, sources());
    assertTrue(entriesRecord.toString().contains("slot=42"), entriesRecord.toString());
    final var chains = new PriceChainsRecord(sources(), new ScopeEntry[0]);
    assertTrue(chains.toString().contains("priceChain"), chains.toString());
  }

  /// Every entry type's `oracleType()` is load-bearing — `oracleEntries`
  /// filtering and refresh-account selection dispatch on it.
  @Test
  void everyEntryTypeReportsItsOracleType() {
    final var k = key(1);
    final var ema = Set.<EmaType>of();
    final var none = OptionalInt.empty();
    assertEquals(OracleType.Unused, new Unused(0).oracleType());
    assertEquals(OracleType.SplBalance, new SplBalance(0, k).oracleType());
    assertEquals(OracleType.SplStake, new SplStake(0, k).oracleType());
    assertEquals(OracleType.StakedSolBalance, new StakedSolBalance(0, k).oracleType());
    assertEquals(OracleType.TotalMintSupply, new TotalMintSupply(0, k, ema).oracleType());
    assertEquals(OracleType.Token2022Multiplier, new Token2022Multiplier(0, k, ema, 0).oracleType());
    assertEquals(OracleType.KlendCTokenExchangeRate, new KlendCTokenExchangeRate(0, k, ema).oracleType());
    assertEquals(OracleType.Canary, new Canary(0, k, ema).oracleType());
    assertEquals(OracleType.ExponentTranching, new ExponentTranching(0, k, null, ema).oracleType());
    assertEquals(OracleType.PythLazer, new PythLazer(0, k, 1, 2, 3L, 4L, ema, null, none).oracleType());
    assertEquals(OracleType.PythLazerEMA, new PythLazerEMA(0, null, ema).oracleType());
    assertEquals(OracleType.MultiplicationChain, new MultiplicationChain(0, new ScopeEntry[0], 0L).oracleType());
    assertEquals(OracleType.DiscountToMaturity, new DiscountToMaturity(0, 1, 2L).oracleType());
    assertEquals(OracleType.Conditional, new Conditional(0, Condition.Gt, 0, new ScopeEntry[0]).oracleType());
    assertEquals(OracleType.Chainlink, new Chainlink(0, k, 1L, ema, null, none).oracleType());
    assertEquals(OracleType.ChainlinkRWA, new ChainlinkRWA(0, k, null, ema).oracleType());
    assertEquals(OracleType.ChainlinkX, new ChainlinkX(0, k, null, ema, 0).oracleType());
    assertEquals(OracleType.CappedFloored, new CappedFloored(0, null, null, null, 0L).oracleType());
  }

  @Test
  void priceChainsRecord() {
    final var price = sources();
    final var twap = new ScopeEntry[]{new Unused(3)};
    final var base = new PriceChainsRecord(price, twap);
    final var twin = new PriceChainsRecord(sources(), new ScopeEntry[]{new Unused(3)});

    assertEquals(base, twin);
    assertEquals(twin, base);
    assertEquals(base.hashCode(), twin.hashCode());
    assertNotEquals(base, null);
    assertNotEquals(base, new Unused(1));

    assertBothDiffer(base, new PriceChainsRecord(new ScopeEntry[]{new Unused(7)}, twap), "priceChain");
    assertBothDiffer(base, new PriceChainsRecord(price, new ScopeEntry[]{new Unused(4)}), "twapChain");
    // the two chains are compared to their own counterparts, not to each other
    assertNotEquals(base, new PriceChainsRecord(twap, price));
  }

  /// The per-slot mapping views are not part of record identity and are not exercised
  /// here, so they are built empty: no frozen flags, no reference prices, and a
  /// reference-price index out of range on every slot, which is how a mapping with
  /// none configured reads.
  private static ScopeEntriesRecord entriesRecord(final PublicKey pubKey,
                                                  final long slot,
                                                  final ScopeEntry[] entries) {
    return entriesRecord(pubKey, null, slot, entries);
  }

  private static ScopeEntriesRecord entriesRecord(final PublicKey pubKey,
                                                  final PublicKey oraclePrices,
                                                  final long slot,
                                                  final ScopeEntry[] entries) {
    final int n = entries.length;
    final int[] none = new int[n];
    java.util.Arrays.fill(none, 0xFFFF);
    return new ScopeEntriesRecord(pubKey, oraclePrices, slot, entries, new byte[n], new ScopeEntry[n], none, none);
  }

}

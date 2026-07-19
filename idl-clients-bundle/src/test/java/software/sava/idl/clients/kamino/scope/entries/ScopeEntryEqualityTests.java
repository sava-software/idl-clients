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
/// identity. The pattern throughout: an equal twin built from distinct array
/// instances, then one variant per component; a dropped comparison passes the
/// twin but misses its variant.
final class ScopeEntryEqualityTests {

  private static PublicKey key(final int fill) {
    final byte[] publicKey = new byte[PublicKey.PUBLIC_KEY_LENGTH];
    Arrays.fill(publicKey, (byte) fill);
    return PublicKey.createPubKey(publicKey);
  }

  private static ScopeEntry[] sources() {
    return new ScopeEntry[]{new Unused(7), FixedPrice.createEntry(8, 100L, 2)};
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

    // a null condition is comparable and hashable
    final var nullCondition = new Conditional(1, null, 50, sources());
    assertEquals(nullCondition, new Conditional(1, null, 50, sources()));
    assertNotEquals(base, nullCondition);
    assertDoesNotThrow(nullCondition::hashCode);
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
    final var base = new ScopeEntriesRecord(key(1), 42L, entries);
    final var twin = new ScopeEntriesRecord(key(1), 42L, new ScopeEntry[]{new Unused(0), FixedPrice.createEntry(1, 100L, 2)});

    assertEquals(base, twin);
    assertEquals(twin, base);
    assertEquals(base.hashCode(), twin.hashCode());
    assertNotEquals(base, null);
    assertNotEquals(base, new Unused(1));

    assertBothDiffer(base, new ScopeEntriesRecord(key(2), 42L, entries), "pubKey");
    assertBothDiffer(base, new ScopeEntriesRecord(key(1), 43L, entries), "slot");
    assertBothDiffer(base, new ScopeEntriesRecord(key(1), 42L, new ScopeEntry[]{new Unused(0)}), "scopeEntries");
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
    final var entriesRecord = new ScopeEntriesRecord(key(1), 42L, sources());
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
    assertEquals(OracleType.PythLazer, new PythLazer(0, k, 1, 2, 3L, ema, null, none).oracleType());
    assertEquals(OracleType.PythLazerEMA, new PythLazerEMA(0, null, ema).oracleType());
    assertEquals(OracleType.MultiplicationChain, new MultiplicationChain(0, new ScopeEntry[0], 0L).oracleType());
    assertEquals(OracleType.DiscountToMaturity, new DiscountToMaturity(0, 1, 2L).oracleType());
    assertEquals(OracleType.Conditional, new Conditional(0, Condition.Gt, 0, new ScopeEntry[0]).oracleType());
    assertEquals(OracleType.Chainlink, new Chainlink(0, k, 1L, ema, null, none).oracleType());
    assertEquals(OracleType.ChainlinkRWA, new ChainlinkRWA(0, k, null, ema).oracleType());
    assertEquals(OracleType.ChainlinkX, new ChainlinkX(0, k, null, ema).oracleType());
    assertEquals(OracleType.CappedFloored, new CappedFloored(0, null, null, null).oracleType());
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
}

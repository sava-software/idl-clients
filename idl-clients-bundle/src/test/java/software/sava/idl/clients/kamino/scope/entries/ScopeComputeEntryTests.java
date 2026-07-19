package software.sava.idl.clients.kamino.scope.entries;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.kamino.scope.gen.types.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.OptionalInt;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static software.sava.idl.clients.kamino.scope.gen.types.OracleMappings.PRICE_INFO_ACCOUNTS_LEN;

/// Covers the `computeEntry` dispatch for the entry types the committed corpus
/// dumps do not contain. Each slot of a synthetic mappings account is
/// configured with an oracle type and its `generic` payload, and the parsed
/// entry is checked field-for-field — in this reader a wrong branch yields a
/// plausible wrong price configuration, not a failure.
final class ScopeComputeEntryTests {

  private static final int SLOTS = PRICE_INFO_ACCOUNTS_LEN;
  /// An index no slot can have: entry() resolves it to null / "absent".
  private static final int NONE = 0xFFFF;

  private static PublicKey key(final int fill) {
    final byte[] publicKey = new byte[PublicKey.PUBLIC_KEY_LENGTH];
    Arrays.fill(publicKey, (byte) fill);
    return PublicKey.createPubKey(publicKey);
  }

  /// A mutable synthetic OracleMappings: every slot defaults to `Unused` with
  /// no ref price, no tolerance, and no TWAP bitmask.
  private static final class Mappings {

    final PublicKey[] priceInfoAccounts = new PublicKey[SLOTS];
    final byte[] priceTypes = new byte[SLOTS];
    final int[] tolerance = new int[SLOTS];
    final TwapEnabledBitmask[] twapBitmasks = new TwapEnabledBitmask[SLOTS];
    final int[] refPrice = new int[SLOTS];
    final byte[][] generic = new byte[SLOTS][20];

    Mappings() {
      final byte unused = (byte) OracleType.Unused.ordinal();
      for (int i = 0; i < SLOTS; ++i) {
        priceInfoAccounts[i] = PublicKey.NONE;
        priceTypes[i] = unused;
        tolerance[i] = NONE;
        twapBitmasks[i] = new TwapEnabledBitmask(0);
        refPrice[i] = NONE;
      }
    }

    Mappings slot(final int i, final OracleType type) {
      priceTypes[i] = (byte) type.ordinal();
      priceInfoAccounts[i] = key(i + 1);
      return this;
    }

    Mappings generic(final int i, final software.sava.idl.clients.core.gen.SerDe data) {
      data.write(generic[i], 0);
      return this;
    }

    ScopeEntries parse() {
      return ScopeReader.parseEntries(42L, new OracleMappings(
          key(0x77), null, priceInfoAccounts, priceTypes, tolerance, twapBitmasks, refPrice, generic
      ));
    }
  }

  // ---------------------------------------------------------------------------
  // Fixed price
  // ---------------------------------------------------------------------------

  @Test
  void fixedPriceScalesByExponent() {
    final var entries = new Mappings()
        .slot(0, OracleType.FixedPrice)
        .generic(0, new Price(1234500L, 4L))
        .parse();

    final var fixed = assertInstanceOf(FixedPrice.class, entries.scopeEntry(0));
    assertEquals(0, fixed.index());
    assertEquals(1234500L, fixed.value());
    assertEquals(4, fixed.exp());
    // scaled, and trailing zeros stripped: 1234500 * 10^-4 = 123.45
    assertEquals(new BigDecimal("123.45"), fixed.decimal());
    assertEquals(OracleType.FixedPrice, fixed.oracleType());
  }

  /// The price value is a u64: a negative long is the unsigned upper half, not
  /// a negative price.
  @Test
  void fixedPriceValueIsUnsigned() {
    final var fixed = FixedPrice.createEntry(3, -1L, 0);
    assertEquals(new BigDecimal("18446744073709551615"), fixed.decimal());
    assertTrue(fixed.decimal().signum() > 0, "u64 must not surface as negative");

    // exponent still applies on the unsigned path
    final var scaled = FixedPrice.createEntry(3, -1L, 20);
    assertEquals(new BigDecimal("0.18446744073709551615"), scaled.decimal());
  }

  @Test
  void fixedPriceRejectsEmaTypes() {
    final var mappings = new Mappings()
        .slot(0, OracleType.FixedPrice)
        .generic(0, new Price(1L, 0L));
    mappings.twapBitmasks[0] = new TwapEnabledBitmask(1); // Ema1h enabled
    assertThrows(IllegalStateException.class, mappings::parse);
  }

  // ---------------------------------------------------------------------------
  // Composite entries: sources resolved through entry indices
  // ---------------------------------------------------------------------------

  @Test
  void mostRecentOfResolvesItsSources() {
    final var entries = new Mappings()
        .slot(1, OracleType.PythPull)
        .slot(2, OracleType.PythPull)
        .slot(0, OracleType.MostRecentOf)
        .generic(0, new MostRecentOfData(new int[]{1, 2, NONE, NONE}, 250, 3_600L))
        .parse();

    final var mostRecent = assertInstanceOf(MostRecentOfEntry.class, entries.scopeEntry(0));
    assertEquals(250, mostRecent.maxDivergenceBps());
    assertEquals(3_600L, mostRecent.sourcesMaxAgeS());
    // the source list stops at the first absent index — two live sources, trimmed
    assertEquals(2, mostRecent.sources().length);
    assertEquals(entries.scopeEntry(1), mostRecent.sources()[0]);
    assertEquals(entries.scopeEntry(2), mostRecent.sources()[1]);
    assertNull(mostRecent.refPrice());
    assertTrue(mostRecent.refPriceToleranceBps().isEmpty());
    assertEquals(OracleType.MostRecentOf, mostRecent.oracleType());
  }

  @Test
  void cappedMostRecentOfResolvesSourcesAndCap() {
    final var entries = new Mappings()
        .slot(1, OracleType.PythPull)
        .slot(2, OracleType.FixedPrice)
        .generic(2, new Price(5L, 0L))
        .slot(0, OracleType.CappedMostRecentOf)
        .generic(0, new CappedMostRecentOfData(new int[]{1, NONE, NONE, NONE}, 100, 60L, 2))
        .parse();

    final var capped = assertInstanceOf(CappedMostRecentOf.class, entries.scopeEntry(0));
    assertEquals(100, capped.maxDivergenceBps());
    assertEquals(60L, capped.sourcesMaxAgeS());
    assertEquals(1, capped.sources().length);
    assertEquals(entries.scopeEntry(1), capped.sources()[0]);
    assertEquals(entries.scopeEntry(2), capped.capEntry());
    assertEquals(OracleType.CappedMostRecentOf, capped.oracleType());
  }

  @Test
  void multiplicationChainResolvesItsSources() {
    final var entries = new Mappings()
        .slot(1, OracleType.PythPull)
        .slot(2, OracleType.PythPull)
        .slot(3, OracleType.PythPull)
        .slot(0, OracleType.MultiplicationChain)
        .generic(0, new MultiplicationChainData(new int[]{1, 2, 3, NONE, NONE, NONE}, 120L))
        .parse();

    final var chain = assertInstanceOf(MultiplicationChain.class, entries.scopeEntry(0));
    assertEquals(120L, chain.sourcesMaxAgeS());
    assertEquals(3, chain.sourceEntries().length);
    assertEquals(entries.scopeEntry(3), chain.sourceEntries()[2]);
  }

  @Test
  void cappedFlooredResolvesAllThreeReferences() {
    final var entries = new Mappings()
        .slot(1, OracleType.PythPull)
        .slot(2, OracleType.FixedPrice)
        .generic(2, new Price(9L, 0L))
        .slot(3, OracleType.FixedPrice)
        .generic(3, new Price(1L, 0L))
        .slot(0, OracleType.CappedFloored)
        .generic(0, new CappedFlooredData(1, OptionalInt.of(2), OptionalInt.of(3)))
        .parse();

    final var cappedFloored = assertInstanceOf(CappedFloored.class, entries.scopeEntry(0));
    assertEquals(entries.scopeEntry(1), cappedFloored.sourceEntry());
    assertEquals(entries.scopeEntry(2), cappedFloored.capEntry());
    assertEquals(entries.scopeEntry(3), cappedFloored.flooredEntry());

    // absent cap/floor stay null rather than resolving slot 0
    final var bare = new Mappings()
        .slot(1, OracleType.PythPull)
        .slot(0, OracleType.CappedFloored)
        .generic(0, new CappedFlooredData(1, OptionalInt.empty(), OptionalInt.empty()))
        .parse();
    final var noBounds = assertInstanceOf(CappedFloored.class, bare.scopeEntry(0));
    assertEquals(bare.scopeEntry(1), noBounds.sourceEntry());
    assertNull(noBounds.capEntry());
    assertNull(noBounds.flooredEntry());
  }

  // ---------------------------------------------------------------------------
  // Conditional: the condition selects how many sources are read
  // ---------------------------------------------------------------------------

  @Test
  void conditionalSourceCountDependsOnTheCondition() {
    // NonZero reads one source
    final var nonZero = new Mappings()
        .slot(1, OracleType.PythPull)
        .slot(2, OracleType.PythPull)
        .slot(3, OracleType.PythPull)
        .slot(0, OracleType.Conditional)
        .generic(0, new ConditionalData(Condition.NonZero.ordinal(), 50, new int[]{1, 2, 3}))
        .parse();
    final var oneSource = assertInstanceOf(Conditional.class, nonZero.scopeEntry(0));
    assertEquals(Condition.NonZero, oneSource.condition());
    assertEquals(50, oneSource.toleranceBps());
    assertEquals(1, oneSource.sources().length, "NonZero uses a single source");

    // WithinRangeAbs reads three
    final var within = new Mappings()
        .slot(1, OracleType.PythPull)
        .slot(2, OracleType.PythPull)
        .slot(3, OracleType.PythPull)
        .slot(0, OracleType.Conditional)
        .generic(0, new ConditionalData(Condition.WithinRangeAbs.ordinal(), 50, new int[]{1, 2, 3}))
        .parse();
    assertEquals(3, assertInstanceOf(Conditional.class, within.scopeEntry(0)).sources().length);

    final var outside = new Mappings()
        .slot(1, OracleType.PythPull)
        .slot(2, OracleType.PythPull)
        .slot(3, OracleType.PythPull)
        .slot(0, OracleType.Conditional)
        .generic(0, new ConditionalData(Condition.OutsideRangeAbs.ordinal(), 50, new int[]{1, 2, 3}))
        .parse();
    assertEquals(3, assertInstanceOf(Conditional.class, outside.scopeEntry(0)).sources().length);

    // every comparison condition reads two
    final var gt = new Mappings()
        .slot(1, OracleType.PythPull)
        .slot(2, OracleType.PythPull)
        .slot(3, OracleType.PythPull)
        .slot(0, OracleType.Conditional)
        .generic(0, new ConditionalData(Condition.Gt.ordinal(), 50, new int[]{1, 2, 3}))
        .parse();
    assertEquals(2, assertInstanceOf(Conditional.class, gt.scopeEntry(0)).sources().length);
  }

  @Test
  void conditionalRejectsARefPrice() {
    final var mappings = new Mappings()
        .slot(1, OracleType.PythPull)
        .slot(0, OracleType.Conditional)
        .generic(0, new ConditionalData(Condition.NonZero.ordinal(), 0, new int[]{1, NONE, NONE}));
    mappings.refPrice[0] = 1;
    assertThrows(IllegalStateException.class, mappings::parse);
  }

  // ---------------------------------------------------------------------------
  // TWAPs: the tolerance field doubles as the source index
  // ---------------------------------------------------------------------------

  @Test
  void scopeTwapResolvesItsSourceFromTheToleranceField() {
    for (final var twapType : new OracleType[]{
        OracleType.ScopeTwap1h, OracleType.ScopeTwap8h, OracleType.ScopeTwap24h, OracleType.ScopeTwap7d
    }) {
      final var mappings = new Mappings()
          .slot(1, OracleType.PythPull)
          .slot(0, twapType);
      mappings.tolerance[0] = 1; // for TWAP types this field is the source entry index
      final var entries = mappings.parse();

      final var twap = assertInstanceOf(ScopeTwap.class, entries.scopeEntry(0), twapType.name());
      assertEquals(twapType, twap.oracleType());
      assertEquals(entries.scopeEntry(1), twap.sourceEntry(), twapType.name());
    }
  }

  // ---------------------------------------------------------------------------
  // Ref prices, tolerances, and EMA bitmasks
  // ---------------------------------------------------------------------------

  @Test
  void refPriceAndToleranceAreResolvedTogether() {
    final var mappings = new Mappings()
        .slot(1, OracleType.PythPull)
        .slot(0, OracleType.PythPull);
    mappings.refPrice[0] = 1;
    mappings.tolerance[0] = 500;
    final var entries = mappings.parse();

    final var pyth = assertInstanceOf(PythPull.class, entries.scopeEntry(0));
    assertEquals(entries.scopeEntry(1), pyth.refPrice());
    assertEquals(OptionalInt.of(500), pyth.refPriceToleranceBps());

    // u16::MAX means "no tolerance configured", even with a ref price present
    final var noTolerance = new Mappings()
        .slot(1, OracleType.PythPull)
        .slot(0, OracleType.PythPull);
    noTolerance.refPrice[0] = 1;
    noTolerance.tolerance[0] = NONE;
    final var atMax = assertInstanceOf(PythPull.class, noTolerance.parse().scopeEntry(0));
    assertNotNull(atMax.refPrice());
    assertTrue(atMax.refPriceToleranceBps().isEmpty());

    // and without a ref price there is no tolerance either, whatever the field holds
    final var noRef = new Mappings().slot(0, OracleType.PythPull);
    noRef.tolerance[0] = 500;
    final var without = assertInstanceOf(PythPull.class, noRef.parse().scopeEntry(0));
    assertNull(without.refPrice());
    assertTrue(without.refPriceToleranceBps().isEmpty());
  }

  @Test
  void emaBitmaskDecodesEachBitToItsType() {
    final var mappings = new Mappings().slot(0, OracleType.PythPull);
    mappings.twapBitmasks[0] = new TwapEnabledBitmask(0b0101); // Ema1h | Ema24h
    final var pyth = assertInstanceOf(PythPull.class, mappings.parse().scopeEntry(0));
    assertEquals(Set.of(EmaType.Ema1h, EmaType.Ema24h), pyth.emaTypes());

    final var all = new Mappings().slot(0, OracleType.PythPull);
    all.twapBitmasks[0] = new TwapEnabledBitmask(0b1111);
    assertEquals(
        Set.of(EmaType.Ema1h, EmaType.Ema8h, EmaType.Ema24h, EmaType.Ema7d),
        assertInstanceOf(PythPull.class, all.parse().scopeEntry(0)).emaTypes());

    final var none = new Mappings().slot(0, OracleType.PythPull);
    assertEquals(Set.of(), assertInstanceOf(PythPull.class, none.parse().scopeEntry(0)).emaTypes());
  }

  // ---------------------------------------------------------------------------
  // Type dispatch edges
  // ---------------------------------------------------------------------------

  /// Bit 7 of the price type is a frozen flag; the oracle type lives in bits 0-6.
  @Test
  void frozenFlagBitIsMaskedOffTheOracleType() {
    final var mappings = new Mappings().slot(0, OracleType.PythPull);
    mappings.priceTypes[0] = (byte) (OracleType.PythPull.ordinal() | 0x80);
    assertInstanceOf(PythPull.class, mappings.parse().scopeEntry(0));
  }

  /// An ordinal beyond the generated enum degrades to NotYetSupported with a
  /// null type instead of failing the whole mappings parse.
  @Test
  void ordinalBeyondTheEnumDegradesToNotYetSupported() {
    final var mappings = new Mappings().slot(0, OracleType.PythPull);
    mappings.priceTypes[0] = (byte) 0x7F; // 127, far past the enum
    final var notYet = assertInstanceOf(NotYetSupported.class, mappings.parse().scopeEntry(0));
    assertNull(notYet.oracleType());
    assertEquals(0, notYet.index());
  }

  @Test
  void deprecatedTypesParseAsDeprecated() {
    final var entries = new Mappings().slot(0, OracleType.DeprecatedPlaceholder1).parse();
    final var deprecated = assertInstanceOf(Deprecated.class, entries.scopeEntry(0));
    assertEquals(OracleType.DeprecatedPlaceholder1, deprecated.oracleType());
  }

  @Test
  void pythLazerCarriesItsFeedConfiguration() {
    final var entries = new Mappings()
        .slot(0, OracleType.PythLazer)
        .generic(0, new PythLazerData(7, 8, 1_000L, false, 0L))
        .parse();

    final var lazer = assertInstanceOf(PythLazer.class, entries.scopeEntry(0));
    assertEquals(7, lazer.feedId());
    assertEquals(8, lazer.exponent());
    assertEquals(1_000L, lazer.confidenceFactor());
  }

  @Test
  void pythLazerEmaReferencesItsSourceEntry() {
    final var entries = new Mappings()
        .slot(1, OracleType.PythLazer)
        .generic(1, new PythLazerData(7, 8, 1_000L, false, 0L))
        .slot(0, OracleType.PythLazerEMA)
        .generic(0, new PythLazerEmaRefData(1))
        .parse();

    final var ema = assertInstanceOf(PythLazerEMA.class, entries.scopeEntry(0));
    assertEquals(entries.scopeEntry(1), ema.sourceEntry());
  }

  @Test
  void chainlinkVariantsReadTheirConfig() {
    final var entries = new Mappings()
        .slot(0, OracleType.Chainlink)
        .generic(0, new V3(77L))
        .slot(1, OracleType.ChainlinkRWA)
        .generic(1, new V8V10(MarketStatusBehavior.Open))
        .slot(2, OracleType.ChainlinkX)
        .generic(2, new V8V10(MarketStatusBehavior.OpenAndPrePost))
        .parse();

    final var chainlink = assertInstanceOf(Chainlink.class, entries.scopeEntry(0));
    assertEquals(77L, chainlink.confidenceFactor());
    assertEquals(MarketStatusBehavior.Open, assertInstanceOf(ChainlinkRWA.class, entries.scopeEntry(1)).marketStatusBehavior());
    assertEquals(MarketStatusBehavior.OpenAndPrePost, assertInstanceOf(ChainlinkX.class, entries.scopeEntry(2)).marketStatusBehavior());
  }

  @Test
  void discountToMaturityReadsItsSchedule() {
    final var entries = new Mappings()
        .slot(0, OracleType.DiscountToMaturity)
        .generic(0, new DiscountToMaturityData(250, 1_800_000_000L))
        .parse();

    final var dtm = assertInstanceOf(DiscountToMaturity.class, entries.scopeEntry(0));
    assertEquals(250, dtm.discountPerYearBps());
    assertEquals(1_800_000_000L, dtm.maturityTimestamp());
  }

  @Test
  void balanceTypesRejectARefPrice() {
    for (final var type : new OracleType[]{
        OracleType.SplBalance, OracleType.StakedSolBalance, OracleType.TotalMintSupply
    }) {
      final var mappings = new Mappings()
          .slot(1, OracleType.PythPull)
          .slot(0, type);
      mappings.refPrice[0] = 1;
      assertThrows(IllegalStateException.class, mappings::parse, type.name());
    }
    // SplStake deliberately tolerates one
    final var splStake = new Mappings()
        .slot(1, OracleType.PythPull)
        .slot(0, OracleType.SplStake);
    splStake.refPrice[0] = 1;
    assertInstanceOf(SplStake.class, splStake.parse().scopeEntry(0));
  }

  @Test
  void simpleOracleTypesCarryTheirAccount() {
    final var entries = new Mappings()
        .slot(0, OracleType.SplBalance)
        .slot(1, OracleType.StakedSolBalance)
        .slot(2, OracleType.SplStake)
        .slot(3, OracleType.TotalMintSupply)
        .slot(4, OracleType.Unused)
        .parse();

    assertEquals(key(1), assertInstanceOf(SplBalance.class, entries.scopeEntry(0)).priceAccount());
    assertEquals(key(2), assertInstanceOf(StakedSolBalance.class, entries.scopeEntry(1)).priceAccount());
    assertEquals(key(3), assertInstanceOf(SplStake.class, entries.scopeEntry(2)).priceAccount());
    assertEquals(key(4), assertInstanceOf(TotalMintSupply.class, entries.scopeEntry(3)).oracle());
    assertInstanceOf(Unused.class, entries.scopeEntry(4));
  }

  /// Every oracle type that forbids EMA configuration must reject a set TWAP
  /// bitmask — each call site is an independent guard, so each type is driven
  /// through it.
  @Test
  void emaTypesRejectedByEveryGuardedType() {
    for (final var type : new OracleType[]{
        OracleType.FixedPrice, OracleType.CappedFloored, OracleType.CappedMostRecentOf,
        OracleType.Conditional, OracleType.DiscountToMaturity, OracleType.MostRecentOf,
        OracleType.MultiplicationChain, OracleType.ScopeTwap1h, OracleType.ScopeTwap8h,
        OracleType.ScopeTwap24h, OracleType.ScopeTwap7d, OracleType.SplBalance,
        OracleType.SplStake, OracleType.StakedSolBalance, OracleType.Unused,
        OracleType.DeprecatedPlaceholder1
    }) {
      final var mappings = new Mappings().slot(0, type);
      mappings.twapBitmasks[0] = new TwapEnabledBitmask(1); // Ema1h enabled
      assertThrows(IllegalStateException.class, mappings::parse, type.name());
    }
  }

  /// A composite referencing an *earlier* slot resolves to the already-parsed
  /// instance — the memoized entry, not a value-equal recomputation.
  @Test
  void backwardReferencesResolveToTheMemoizedInstance() {
    final var entries = new Mappings()
        .slot(1, OracleType.PythPull)
        .slot(5, OracleType.MostRecentOf)
        .generic(5, new MostRecentOfData(new int[]{1, NONE, NONE, NONE}, 0, 0L))
        .parse();

    final var mostRecent = assertInstanceOf(MostRecentOfEntry.class, entries.scopeEntry(5));
    assertEquals(1, mostRecent.sources().length);
    assertSame(entries.scopeEntry(1), mostRecent.sources()[0], "slot 1 was parsed before slot 5");
  }

  /// Slot 0 is a valid entry index — an off-by-one in the lower bounds check
  /// would silently drop references to it.
  @Test
  void slotZeroIsReferenceable() {
    final var entries = new Mappings()
        .slot(0, OracleType.PythPull)
        .slot(5, OracleType.MostRecentOf)
        .generic(5, new MostRecentOfData(new int[]{0, NONE, NONE, NONE}, 0, 0L))
        .parse();

    final var mostRecent = assertInstanceOf(MostRecentOfEntry.class, entries.scopeEntry(5));
    assertEquals(1, mostRecent.sources().length, "slot 0 must resolve");
    assertSame(entries.scopeEntry(0), mostRecent.sources()[0]);
  }

  /// The AccountInfo entry point wires the context slot and account key
  /// through to the parsed entries.
  @Test
  void parseEntriesFromAccountInfo() {
    final var mappings = new Mappings().slot(0, OracleType.PythPull);
    final byte[] data = new byte[software.sava.idl.clients.kamino.scope.gen.types.OracleMappings.BYTES];
    new software.sava.idl.clients.kamino.scope.gen.types.OracleMappings(
        key(0x77),
        software.sava.idl.clients.kamino.scope.gen.types.OracleMappings.DISCRIMINATOR,
        mappings.priceInfoAccounts, mappings.priceTypes, mappings.tolerance,
        mappings.twapBitmasks, mappings.refPrice, mappings.generic
    ).write(data, 0);

    final var accountInfo = new software.sava.rpc.json.http.response.AccountInfo<>(
        key(0x77),
        new software.sava.rpc.json.http.response.Context(99L, null),
        false, 0L, PublicKey.NONE, java.math.BigInteger.ZERO, 0, data);

    final var entries = ScopeReader.parseEntries(accountInfo);
    assertEquals(99L, ((ScopeEntriesRecord) entries).slot());
    assertEquals(key(0x77), ((ScopeEntriesRecord) entries).pubKey());
    assertInstanceOf(PythPull.class, entries.scopeEntry(0));
  }

  /// A named enum value with no dedicated branch degrades to NotYetSupported
  /// carrying that type — only Deprecated* names map to Deprecated.
  @Test
  void namedButUnhandledTypeDegradesToNotYetSupported() {
    final var entries = new Mappings().slot(0, OracleType.KTokenToTokenA).parse();
    final var notYet = assertInstanceOf(NotYetSupported.class, entries.scopeEntry(0));
    assertEquals(OracleType.KTokenToTokenA, notYet.oracleType(), "the type is preserved, not nulled");
  }

  /// Sweep of the single-account oracle types: each must report its own type
  /// and carry its price account, and twapEnabled() must follow the bitmask.
  @Test
  void everyOracleEntryTypeReportsItsType() {
    final var types = new OracleType[]{
        OracleType.KToken, OracleType.MsolStake, OracleType.JupiterLpFetch,
        OracleType.OrcaWhirlpoolAtoB, OracleType.OrcaWhirlpoolBtoA,
        OracleType.RaydiumAmmV3AtoB, OracleType.RaydiumAmmV3BtoA,
        OracleType.MeteoraDlmmAtoB, OracleType.MeteoraDlmmBtoA,
        OracleType.JitoRestaking, OracleType.FlashtradeLp, OracleType.AdrenaLp,
        OracleType.ChainlinkExchangeRate, OracleType.ChainlinkNAV,
        OracleType.RedStone, OracleType.Securitize, OracleType.SwitchboardOnDemand,
        OracleType.PythPullEMA
    };
    final var mappings = new Mappings();
    for (int i = 0; i < types.length; ++i) {
      mappings.slot(i, types[i]);
    }
    // one slot with EMA enabled, to split twapEnabled()
    mappings.twapBitmasks[0] = new TwapEnabledBitmask(1);
    final var entries = mappings.parse();

    for (int i = 0; i < types.length; ++i) {
      final var entry = entries.scopeEntry(i);
      assertEquals(types[i], entry.oracleType(), types[i].name());
      final var oracleEntry = assertInstanceOf(OracleEntry.class, entry, types[i].name());
      assertEquals(key(i + 1), oracleEntry.oracle(), types[i].name());
      assertEquals(i == 0, oracleEntry.twapEnabled(), types[i].name());
    }
  }

  /// A composite's resolved sources agree with the top-level slots, and the
  /// parse populates all 512 slots.
  @Test
  void entriesAreMemoizedAndComplete() {
    final var entries = new Mappings()
        .slot(1, OracleType.PythPull)
        .slot(0, OracleType.MostRecentOf)
        .generic(0, new MostRecentOfData(new int[]{1, NONE, NONE, NONE}, 0, 0L))
        .slot(2, OracleType.ScopeTwap1h)
        .parse();
    // slot 2's twap also points at slot 1 via tolerance default? no — tolerance
    // default is NONE, so its source is absent
    assertEquals(SLOTS, entries.numEntries());
    final var mostRecent = assertInstanceOf(MostRecentOfEntry.class, entries.scopeEntry(0));
    assertEquals(entries.scopeEntry(1), mostRecent.sources()[0]);
  }
}

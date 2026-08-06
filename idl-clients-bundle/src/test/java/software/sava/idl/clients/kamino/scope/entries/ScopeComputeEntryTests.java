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

    Mappings bitmask(final int i, final int bitmask) {
      twapBitmasks[i] = new TwapEnabledBitmask(bitmask);
      return this;
    }

    Mappings refPrice(final int i, final int sourceSlot) {
      refPrice[i] = sourceSlot;
      return this;
    }

    /// The ref-price tolerance in bps — except on the TWAP types, where the same
    /// field is the source slot index.
    Mappings tolerance(final int i, final int toleranceOrSource) {
      tolerance[i] = toleranceOrSource;
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

  /// A TWAP bitmask is settable on any slot and does not disturb the decode of the
  /// type's own config — the price still reads exactly as it does without one.
  @Test
  void fixedPriceDecodesTheSameWithATwapBitmaskSet() {
    final var mappings = new Mappings()
        .slot(0, OracleType.FixedPrice)
        .generic(0, new Price(1L, 0L))
        .bitmask(0, 1); // Ema1h enabled
    final var fixed = assertInstanceOf(FixedPrice.class, mappings.parse().scopeEntry(0));
    assertEquals(BigDecimal.ONE, fixed.decimal());
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

  /// `MappingRefPrice` sets a ref price on any slot without consulting the oracle
  /// type, so a Conditional can carry one. `Conditional` has no field for it, but the
  /// sources it does model must still resolve — the ref price is dropped, not the entry.
  @Test
  void conditionalResolvesItsSourcesWithARefPriceSet() {
    final var mappings = new Mappings()
        .slot(1, OracleType.PythPull)
        .slot(0, OracleType.Conditional)
        .generic(0, new ConditionalData(Condition.NonZero.ordinal(), 0, new int[]{1, NONE, NONE}))
        .refPrice(0, 1);
    final var conditional = assertInstanceOf(Conditional.class, mappings.parse().scopeEntry(0));
    assertEquals(Condition.NonZero, conditional.condition());
    assertEquals(1, conditional.sources().length);
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
        .generic(0, new PythLazerData(7, 8, 1_000L, false, 0L, 2_000L))
        .parse();

    final var lazer = assertInstanceOf(PythLazer.class, entries.scopeEntry(0));
    assertEquals(7, lazer.feedId());
    assertEquals(8, lazer.exponent());
    assertEquals(1_000L, lazer.bidAskSpreadFactor());
    assertEquals(2_000L, lazer.priceConfidenceFactor());
  }

  @Test
  void pythLazerEmaReferencesItsSourceEntry() {
    final var entries = new Mappings()
        .slot(1, OracleType.PythLazer)
        .generic(1, new PythLazerData(7, 8, 1_000L, false, 0L, 2_000L))
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

  /// The balance types carry no ref-price field, and the program sets ref prices
  /// without consulting the oracle type — so all four decode identically whether or
  /// not one is configured. `SplStake` used to be the lone tolerated exception here;
  /// nothing distinguished it, and now nothing does.
  @Test
  void balanceTypesDecodeWithOrWithoutARefPrice() {
    for (final var type : new OracleType[]{
        OracleType.SplBalance, OracleType.StakedSolBalance,
        OracleType.TotalMintSupply, OracleType.SplStake
    }) {
      final var withRef = new Mappings()
          .slot(1, OracleType.PythPull)
          .slot(0, type)
          .refPrice(0, 1)
          .parse();
      final var without = new Mappings()
          .slot(1, OracleType.PythPull)
          .slot(0, type)
          .parse();
      // these four hold no arrays, so record equality is value equality here
      assertEquals(without.scopeEntry(0), withRef.scopeEntry(0), type.name());
      assertEquals(without.scopeEntry(0).getClass(), withRef.scopeEntry(0).getClass(), type.name());
    }
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

  /// No oracle type forbids a TWAP bitmask. The program's writer validates the
  /// bitmask's range and nothing else, and `refresh_prices` gates the EMA update on
  /// `is_twap_enabled(token)` without consulting the type — so every one of these
  /// slots is a mapping an admin can actually write, and each must decode to its own
  /// entry rather than failing the parse of all 512.
  ///
  /// This list is every type whose entry record has no EMA field, which is where a
  /// type-keyed guard would previously have fired.
  @Test
  void everyOracleTypeToleratesATwapBitmask() {
    for (final var type : new OracleType[]{
        OracleType.FixedPrice, OracleType.CappedFloored, OracleType.CappedMostRecentOf,
        OracleType.Conditional, OracleType.DiscountToMaturity, OracleType.MostRecentOf,
        OracleType.MultiplicationChain, OracleType.ScopeTwap1h, OracleType.ScopeTwap8h,
        OracleType.ScopeTwap24h, OracleType.ScopeTwap7d, OracleType.SplBalance,
        OracleType.SplStake, OracleType.StakedSolBalance, OracleType.Unused,
        OracleType.DeprecatedPlaceholder1
    }) {
      final var entries = new Mappings().slot(0, type).bitmask(0, 1).parse(); // Ema1h
      assertEquals(SLOTS, entries.numEntries(), type.name());
      final var entry = entries.scopeEntry(0);
      assertNotNull(entry, type.name());
      // and it decodes to the same shape an unset bitmask produces. Compared by class
      // and index rather than by equals: the composite entries hold their sources in
      // an array, so record equality on those is identity, not value.
      final var without = new Mappings().slot(0, type).parse().scopeEntry(0);
      assertEquals(without.getClass(), entry.getClass(), type.name());
      assertEquals(without.index(), entry.index(), type.name());
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

  /// Both configuration-bound entry points record the feed's prices account, which is
  /// what lets the entries recognise a reserve that belongs to a different feed. The
  /// overloads without one parse identically but leave it unbound.
  @Test
  void parseEntriesBindsTheFeedFromTheConfiguration() {
    final var mappings = new Mappings().slot(0, OracleType.PythPull);
    final var oracleMappings = new OracleMappings(
        key(0x77), OracleMappings.DISCRIMINATOR,
        mappings.priceInfoAccounts, mappings.priceTypes, mappings.tolerance,
        mappings.twapBitmasks, mappings.refPrice, mappings.generic);
    final var prices = key(0x50);
    final var configuration = new Configuration(
        key(0x41), null, key(0x42),
        key(0x77),  // oracleMappings
        prices,     // oraclePrices
        key(0x43), key(0x44), key(0x45), key(0x46), key(0x47),
        new long[Configuration.PADDING_LEN]);

    final var fromMappings = ScopeReader.parseEntries(7L, oracleMappings, configuration);
    assertEquals(prices, fromMappings.oraclePrices());
    assertEquals(7L, fromMappings.slot());
    assertInstanceOf(PythPull.class, fromMappings.scopeEntry(0));

    final byte[] data = new byte[OracleMappings.BYTES];
    oracleMappings.write(data, 0);
    final var accountInfo = new software.sava.rpc.json.http.response.AccountInfo<>(
        key(0x77),
        new software.sava.rpc.json.http.response.Context(99L, null),
        false, 0L, PublicKey.NONE, java.math.BigInteger.ZERO, 0, data);

    final var fromAccount = ScopeReader.parseEntries(accountInfo, configuration);
    assertEquals(prices, fromAccount.oraclePrices());
    assertEquals(99L, fromAccount.slot());
    assertInstanceOf(PythPull.class, fromAccount.scopeEntry(0));

    // unbound without a configuration
    assertNull(ScopeReader.parseEntries(7L, oracleMappings).oraclePrices());
    assertNull(ScopeReader.parseEntries(accountInfo).oraclePrices());
  }

  /// The two accounts are paired in one on-chain Configuration, so taking the prices
  /// key from a configuration that describes a different mappings account would attach
  /// a confident but wrong feed identity — every reserve of that other feed would then
  /// pass the identity check and be resolved against these indices, which is the exact
  /// silent-wrong-price the binding exists to prevent.
  @Test
  void parseEntriesRejectsAConfigurationForADifferentMappingsAccount() {
    final var mappings = new Mappings().slot(0, OracleType.PythPull);
    final var oracleMappings = new OracleMappings(
        key(0x77), OracleMappings.DISCRIMINATOR,
        mappings.priceInfoAccounts, mappings.priceTypes, mappings.tolerance,
        mappings.twapBitmasks, mappings.refPrice, mappings.generic);

    final var otherFeed = new Configuration(
        key(0x41), null, key(0x42),
        key(0x78),  // a different oracleMappings account
        key(0x50), key(0x43), key(0x44), key(0x45), key(0x46), key(0x47),
        new long[Configuration.PADDING_LEN]);
    final var ex = assertThrows(IllegalArgumentException.class,
        () -> ScopeReader.parseEntries(7L, oracleMappings, otherFeed));
    assertTrue(ex.getMessage().contains(key(0x78).toBase58()), ex.getMessage());

    // the matching configuration binds
    final var ownFeed = new Configuration(
        key(0x41), null, key(0x42),
        key(0x77), key(0x50), key(0x43), key(0x44), key(0x45), key(0x46), key(0x47),
        new long[Configuration.PADDING_LEN]);
    assertEquals(key(0x50), ScopeReader.parseEntries(7L, oracleMappings, ownFeed).oraclePrices());

    // mappings read from raw bytes carry no address, so there is nothing to check
    // against — that is how the fuzz harness parses them
    final byte[] data = new byte[OracleMappings.BYTES];
    oracleMappings.write(data, 0);
    final var anonymous = OracleMappings.read(data, 0);
    assertNull(anonymous._address());
    assertEquals(key(0x50), ScopeReader.parseEntries(7L, anonymous, otherFeed).oraclePrices());
  }

  /// `MAX_ENTRIES_U16` is the exclusive bound: an index equal to the slot count means
  /// no reference price, so the field beside it is not a tolerance either. The parser
  /// reads that field for every slot, so an off-by-one here reports a stale number as
  /// a live divergence bound.
  @Test
  void aReferencePriceIndexAtTheSlotCountIsAbsent() {
    final var entries = new Mappings()
        .slot(0, OracleType.PythPull)
        .refPrice(0, SLOTS)   // == PRICE_INFO_ACCOUNTS_LEN: one past the last slot
        .tolerance(0, 300)
        .parse();

    assertNull(entries.referencePrice(0));
    assertEquals(OptionalInt.empty(), entries.referenceToleranceBps(0));

    // one below the bound is a real slot, and then the tolerance counts
    final var inRange = new Mappings()
        .slot(0, OracleType.PythPull)
        .slot(SLOTS - 1, OracleType.PythPull)
        .refPrice(0, SLOTS - 1)
        .tolerance(0, 300)
        .parse();
    assertSame(inRange.scopeEntry(SLOTS - 1), inRange.referencePrice(0));
    assertEquals(OptionalInt.of(300), inRange.referenceToleranceBps(0));
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

  /// The parse publishes exactly one entry per slot: a composite resolved before
  /// the top-level walk reaches its source holds the very entry that walk then
  /// publishes, not a value-equal copy of it. Identity is the whole assertion —
  /// every entry is a record, so re-resolving a slot yields something `equals` to
  /// what is already published and distinguishable from it only by `==`, and a
  /// parse that hands two different objects out for one slot has published a graph
  /// that disagrees with itself.
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
    assertSame(entries.scopeEntry(1), mostRecent.sources()[0],
        "slot 1 was resolved while slot 0 was parsed, and slot 1's own turn must publish that entry");
  }

  /// A TWAP bitmask is legal on any slot. The program's only writer of the field,
  /// `MappingTwapEnabledBitmask`, validates the bitmask alone — `bitmask < 1 <<
  /// EmaType::COUNT` — and never the oracle type, and `refresh_prices` gates the EMA
  /// update on `is_twap_enabled(token)` alone, so a composite really does accumulate
  /// EMAs. The parse must therefore not treat a bitmask on a composite as impossible.
  ///
  /// The blast radius is what makes this matter: these entries are produced by one
  /// walk over all 512 slots, so a throw on one slot discards the whole account.
  @Test
  void aTwapBitmaskOnAnyOracleTypeStillParses() {
    final var entries = new Mappings()
        .slot(3, OracleType.PythPull)
        .slot(0, OracleType.MostRecentOf)
        .generic(0, new MostRecentOfData(new int[]{3, NONE, NONE, NONE}, 0, 0L))
        .slot(1, OracleType.CappedFloored)
        .generic(1, new CappedFlooredData(3, OptionalInt.empty(), OptionalInt.empty()))
        .slot(2, OracleType.FixedPrice)
        .generic(2, new Price(5L, 0L))
        .bitmask(0, 0b0001)
        .bitmask(1, 0b0011)
        .bitmask(2, 0b1111)
        .parse();

    assertEquals(SLOTS, entries.numEntries());
    assertInstanceOf(MostRecentOfEntry.class, entries.scopeEntry(0));
    assertInstanceOf(CappedFloored.class, entries.scopeEntry(1));
    assertInstanceOf(FixedPrice.class, entries.scopeEntry(2));
    // and the rest of the account survives
    assertInstanceOf(PythPull.class, entries.scopeEntry(3));
    assertInstanceOf(Unused.class, entries.scopeEntry(SLOTS - 1));
  }

  /// A reference price and its tolerance are per-slot mapping configuration, settable
  /// on any type, but only the four `ReferencesEntry` types have a field for them. The
  /// mapping-level view carries them for every slot, which is the only way to see the
  /// divergence bound the program enforces on the other ~30 types.
  @Test
  void everySlotExposesItsReferencePriceAndTolerance() {
    final var entries = new Mappings()
        .slot(4, OracleType.PythPull)
        .slot(0, OracleType.PythPull)      // a ReferencesEntry: models it on the entry too
        .slot(1, OracleType.TotalMintSupply) // no field of its own
        .slot(2, OracleType.SplBalance)      // likewise
        .refPrice(0, 4).tolerance(0, 150)
        .refPrice(1, 4).tolerance(1, 250)
        .refPrice(2, 4)                      // ref price, tolerance field left unset
        .parse();

    final var source = entries.scopeEntry(4);
    for (int slot : new int[]{0, 1, 2}) {
      assertSame(source, entries.referencePrice(slot), "slot " + slot);
    }
    assertEquals(OptionalInt.of(150), entries.referenceToleranceBps(0));
    assertEquals(OptionalInt.of(250), entries.referenceToleranceBps(1));
    // unset is the program's MAX_REF_RATIO_TOLERANCE_BPS default, not "no bound":
    // the refresh gates the divergence check on the index and unwrap_or's the field
    assertEquals(OptionalInt.of(500), entries.referenceToleranceBps(2));

    // the entry that models it agrees with the mapping-level view, for the acyclic
    // reference graphs that are the only ones the entry-level field can resolve
    final var pythPull = assertInstanceOf(PythPull.class, entries.scopeEntry(0));
    assertSame(entries.referencePrice(0), pythPull.refPrice());
    assertEquals(entries.referenceToleranceBps(0), pythPull.refPriceToleranceBps());

    // a slot with none configured reports none — the only case with no bound at all
    assertNull(entries.referencePrice(4));
    assertEquals(OptionalInt.empty(), entries.referenceToleranceBps(4));
  }

  /// An oracle type this IDL does not know breaks the program's own accessor before it
  /// reaches the tolerance branch (`is_twap` fails first), so the caller unwraps the
  /// default. Reading the field anyway would be a guess about a slot whose type has
  /// already told us the deployed program is ahead of us.
  @Test
  void anUnknownOracleTypeFallsBackToTheDefaultBound() {
    final var mappings = new Mappings()
        .slot(4, OracleType.PythPull)
        .refPrice(0, 4)
        .tolerance(0, 7);
    mappings.priceTypes[0] = (byte) OracleType.values().length; // one past the enum
    final var entries = mappings.parse();

    final var notYet = assertInstanceOf(NotYetSupported.class, entries.scopeEntry(0));
    assertNull(notYet.oracleType(), "an unknown ordinal has no type to report");
    assertSame(entries.scopeEntry(4), entries.referencePrice(0));
    assertEquals(OptionalInt.of(500), entries.referenceToleranceBps(0),
        "7 is not a tolerance here — the program never gets far enough to read it");
  }

  /// A reference-price cycle is the one place the entry-level field and the
  /// mapping-level view disagree, and the mapping-level one is the faithful answer:
  /// `get_ref_price` has no cycle guard, so the program reads the index whatever it
  /// points at. The entry-level field is resolved mid-walk, where the reader's cycle
  /// guard has to break the recursion, and only the second slot of the pair loses it.
  @Test
  void aCyclicReferencePriceIsResolvedAtTheMappingLevel() {
    final var entries = new Mappings()
        .slot(0, OracleType.PythPull)
        .refPrice(0, 0)      // its own reference price
        .tolerance(0, 300)
        .parse();

    assertSame(entries.scopeEntry(0), entries.referencePrice(0));
    assertEquals(OptionalInt.of(300), entries.referenceToleranceBps(0));

    // the entry-level field cannot see itself while it is being built
    final var pythPull = assertInstanceOf(PythPull.class, entries.scopeEntry(0));
    assertNull(pythPull.refPrice(), "documented limitation: prefer the mapping-level view");
  }

  /// For the TWAP types the same field is the source slot index, never a tolerance —
  /// `get_twap_source_or_ref_price_tolerance_bps` branches on `is_twap` first. Reading
  /// it as bps would report a slot number as a divergence bound.
  @Test
  void twapTypesReportNoReferenceToleranceBecauseTheFieldIsASourceIndex() {
    for (final var twapType : new OracleType[]{
        OracleType.ScopeTwap1h, OracleType.ScopeTwap8h,
        OracleType.ScopeTwap24h, OracleType.ScopeTwap7d
    }) {
      final var entries = new Mappings()
          .slot(1, OracleType.PythPull)
          .slot(0, twapType)
          .tolerance(0, 1) // for these types: the source slot, not 1 bps
          .refPrice(0, 1)
          .parse();
      final var twap = assertInstanceOf(ScopeTwap.class, entries.scopeEntry(0));
      assertSame(entries.scopeEntry(1), twap.sourceEntry(), twapType.name());
      // the field is unreadable as a tolerance here, but the reference price is still
      // set, so the program falls back to its default bound rather than skipping it
      assertSame(entries.scopeEntry(1), entries.referencePrice(0), twapType.name());
      assertEquals(OptionalInt.of(500), entries.referenceToleranceBps(0), twapType.name());
    }
  }

  /// Bit 7 of a price type is the program's frozen flag: the entry decodes identically,
  /// so nothing in it says the price is pinned and every refresh handler skips it.
  @Test
  void frozenSlotsAreVisibleAndDecodeLikeLiveOnes() {
    final var mappings = new Mappings()
        .slot(0, OracleType.PythPull)
        .slot(1, OracleType.PythPull);
    mappings.priceTypes[1] |= (byte) 0x80;
    final var entries = mappings.parse();

    assertFalse(entries.frozen(0));
    assertTrue(entries.frozen(1));
    assertEquals(OracleType.PythPull, ((OracleEntry) entries.scopeEntry(1)).oracleType(),
        "a frozen slot keeps its type");
  }

  /// Same shape for a reference price. `MappingRefPrice` sets the index and tolerance
  /// with no oracle-type gate, so a slot the model has no ref-price field for can
  /// still carry one on-chain. Decoding must drop the field, not the account.
  @Test
  void aReferencePriceOnAnyOracleTypeStillParses() {
    final var entries = new Mappings()
        .slot(5, OracleType.PythPull)
        .slot(0, OracleType.Conditional)
        .generic(0, new ConditionalData(Condition.NonZero.ordinal(), 0, new int[]{5, NONE, NONE}))
        .slot(1, OracleType.SplBalance)
        .slot(2, OracleType.TotalMintSupply)
        .refPrice(0, 5)
        .refPrice(1, 5)
        .refPrice(2, 5)
        .parse();

    assertEquals(SLOTS, entries.numEntries());
    assertInstanceOf(Conditional.class, entries.scopeEntry(0));
    assertInstanceOf(SplBalance.class, entries.scopeEntry(1));
    assertInstanceOf(TotalMintSupply.class, entries.scopeEntry(2));
    assertInstanceOf(PythPull.class, entries.scopeEntry(5));
  }
}

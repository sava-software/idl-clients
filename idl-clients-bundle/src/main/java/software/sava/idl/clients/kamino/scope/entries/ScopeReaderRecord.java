package software.sava.idl.clients.kamino.scope.entries;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.kamino.scope.gen.types.*;

import java.util.EnumSet;
import java.util.OptionalInt;
import java.util.Set;

record ScopeReaderRecord(ScopeEntry[] entries,
                         PublicKey[] priceInfoAccounts,
                         byte[] priceTypes,
                         int[] twapSourceOrRefPriceToleranceBps,
                         TwapEnabledBitmask[] twapEnabledBitmasks,
                         int[] refPrice,
                         byte[][] generic,
                         OracleType[] oracleTypes,
                         boolean[] visiting) implements ScopeReader {

  /// Bit 7 of `OracleMappings.price_types[i]` is the program's frozen flag; the oracle
  /// type is bits 0-6. Package-private on purpose — [ScopeReader#oracleType] is the
  /// only supported way to apply it, so a caller cannot mask by hand and drift.
  static final int ORACLE_TYPE_MASK = 0x7F;
  /// Bit 7 of `OracleMappings.price_types[i]`: the program's `FROZEN_FLAG`.
  static final int FROZEN_FLAG = 0x80;
  private static final int NO_REF_PRICE_TOLERANCE = 0xFFFF;

  ScopeEntries readEntries(final PublicKey pubKey, final PublicKey oraclePrices, final long slot) {
    for (int i = 0; i < priceInfoAccounts.length; ++i) {
      entries[i] = entry(i);
    }
    // Resolved after the walk, from the memoized entries: a reference price is
    // configurable on every slot, but only the ReferencesEntry types have a field
    // for it, so this is where the rest of them keep theirs.
    final var referencePrices = new ScopeEntry[priceInfoAccounts.length];
    for (int i = 0; i < referencePrices.length; ++i) {
      referencePrices[i] = entry(this.refPrice[i]);
    }
    // Cloned: these three come straight from the caller's OracleMappings, and the
    // parsed entries are a snapshot of one slot. `entries` and `referencePrices` are
    // allocated here so they need no copy.
    return new ScopeEntriesRecord(
        pubKey,
        oraclePrices,
        slot,
        entries,
        priceTypes.clone(),
        referencePrices,
        refPrice.clone(),
        twapSourceOrRefPriceToleranceBps.clone()
    );
  }

  private ScopeEntry entry(final OptionalInt i) {
    return i.isPresent() ? entry(i.getAsInt()) : null;
  }

  /// How a composite's refresh reads its source list, and so how the list is resolved. The
  /// program's sentinel is any index at or past the slot count. A slot the walk cut to break a
  /// cycle is left out of a list whose sources play no positional role, the rest kept in their
  /// order (a MostRecentOf tie goes to the first), since the program still reads every source
  /// after it.
  private enum SourceList {
    /// MostRecentOf and CappedMostRecentOf skip an index past the slot count and read on
    /// (`most_recent_of.rs`). Since Scope 0.35.0 `validate_source_entries` keeps the valid
    /// indices contiguous from the start, but an entry written before it was validated on its
    /// first source only, and the mapping is validated only where it is written, so a gap
    /// between two sources can still be live.
    SKIP_GAPS,
    /// MultiplicationChain stops at the first index past the slot count
    /// (`multiplication_chain.rs`).
    END_AT_SENTINEL,
    /// A Conditional's operands are positional: one past the slot count fails the refresh
    /// (`get_source_price` in `conditional.rs`), and a cut one ends the list rather than moving
    /// a later operand into its place.
    POSITIONAL
  }

  /// Resolves a composite's source list as `rule` says its program reads it.
  private ScopeEntry[] parseEntries(final int[] entryIndices, final SourceList rule) {
    final var entries = new ScopeEntry[entryIndices.length];
    int j = 0;
    for (final int index : entryIndices) {
      if (index >= priceInfoAccounts.length) {
        if (rule == SourceList.SKIP_GAPS) {
          continue;
        }
        break;
      }
      final var entry = entry(index);
      if (entry != null) {
        entries[j++] = entry;
      } else if (rule == SourceList.POSITIONAL) {
        break;
      }
    }
    if (j < entries.length) {
      final var trimmed = new ScopeEntry[j];
      System.arraycopy(entries, 0, trimmed, 0, j);
      return trimmed;
    } else {
      return entries;
    }
  }

  private static Set<EmaType> emaTypes(final int bitmask) {
    if (bitmask != 0) {
      final var types = EnumSet.noneOf(EmaType.class);
      for (final var type : EmaType.values()) {
        if ((bitmask & (1 << type.ordinal())) != 0) {
          types.add(type);
        }
      }
      return types;
    } else {
      return Set.of();
    }
  }

  /// The reference price a record of slot `i` stores, with its tolerance.
  private record Band(ScopeEntry refPrice, OptionalInt toleranceBps) {
  }

  /// Asked only by the types whose records store a band. Resolving one walks into the
  /// referenced slot while slot `i` is still being built, so a type that would discard the
  /// answer must not ask: a cycle closing through its band would be broken on some other
  /// edge, such as a source of the composite the band names. Every slot's band, stored or
  /// not, is resolved again after the walk by [ScopeEntries#referencePrice(int)].
  private Band band(final int i) {
    final var refPrice = entry(this.refPrice[i]);
    return new Band(refPrice, refPriceToleranceBps(i, refPrice));
  }

  /// For non-TWAP types, `twap_source_or_ref_price_tolerance_bps` holds the ref price
  /// tolerance in bps, where `u16::MAX` means no tolerance is configured.
  private OptionalInt refPriceToleranceBps(final int i, final ScopeEntry refPrice) {
    if (refPrice == null) {
      return OptionalInt.empty();
    }
    final int toleranceBps = twapSourceOrRefPriceToleranceBps[i];
    return toleranceBps == NO_REF_PRICE_TOLERANCE ? OptionalInt.empty() : OptionalInt.of(toleranceBps);
  }

  private ScopeEntry entry(final int i) {
    if (i < 0 || i >= priceInfoAccounts.length) {
      return null;
    }
    final var entry = entries[i];
    if (entry != null) {
      return entry;
    }
    if (visiting[i]) {
      // A reference cycle can exist on-chain and the refresh never notices one, because
      // it does not recurse: a reference price and a composite's sources are read as
      // the *last stored* price of that slot out of the OraclePrices account, so
      // `ref_price[i] == j, ref_price[j] == i` is simply each slot bounding its refresh
      // against the other's previous price. Since Scope 0.42.0 the mapping update
      // refuses a *direct* self-reference — a ref price (RefPriceSelfReference), a
      // TWAP source (TwapSourceSelfReference), a composite source, cap or floor
      // (OracleConfigInvalidSourceIndices, which Conditional already raised) and a
      // PythLazerEMA source (CompositeOracleInvalidSourceIndex) — but it validates only
      // the entry being written, so a mutual cycle is still configurable and a
      // self-reference written earlier still stands. Building a graph of entries does
      // recurse, so the cycle has to be broken here; the back-reference reads as
      // absent rather than overflowing the stack. ScopeEntries.referencePrice(int)
      // resolves a reference price after the walk and is the complete view for that
      // field; a composite's back-reference has no such second pass, so its source list
      // omits the cut slot (see parseEntries). Only the types whose records store a band
      // resolve one during the walk, so a band closes a cycle through a composite's
      // sources only when the slot carrying it is one of those types.
      return null;
    }
    visiting[i] = true;
    try {
      // cache here, not only from readEntries' top-level loop: fan-out types
      // (CappedFloored, MostRecentOf, Conditional, ...) share forward references,
      // and recomputing them per visit is exponential in a crafted mapping —
      // observed as ~50s parses of a single hostile 29KB account
      final var computed = computeEntry(i);
      entries[i] = computed;
      return computed;
    } finally {
      visiting[i] = false;
    }
  }

  /// Not every entry type has a field for every value the mapping can carry: a TWAP
  /// bitmask and a reference price are settable on any slot, and the program checks
  /// neither the bitmask nor the reference index against the oracle type.
  /// `MappingTwapEnabledBitmask` validates only that the bitmask is in range,
  /// `refresh_prices` gates the EMA update on the bitmask alone, and `MappingRefPrice`
  /// sets the index on any slot and refuses only a tolerance on a TWAP slot, whose
  /// field holds the TWAP source instead — so a composite really does accumulate
  /// EMAs, and a type modelled here without a ref-price field really can have one
  /// configured.
  ///
  /// Where this model has nowhere to put such a value it is dropped, never rejected.
  /// One walk builds all 512 entries, so refusing a slot the program permits would
  /// discard the whole account to describe one field it could not represent.
  private ScopeEntry computeEntry(final int i) {
    final var priceAccount = priceInfoAccounts[i];
    final var emaTypes = emaTypes(this.twapEnabledBitmasks[i].bitmask());
    final var oracleType = ScopeReader.oracleType(oracleTypes, priceTypes[i]);
    if (oracleType == null) {
      // the on-chain program has deployed an oracle type newer than the generated
      // OracleType enum; degrade until the IDL is re-synced instead of failing the
      // whole mappings parse
      final var band = band(i);
      return new NotYetSupported(i, priceAccount, null, emaTypes, band.refPrice(), band.toleranceBps(), generic[i]);
    }
    return switch (oracleType) {
      case AdrenaLp -> new AdrenaLp(i, priceAccount, emaTypes);
      case Canary -> new Canary(i, priceAccount, emaTypes);
      case CappedFloored -> {
        final var cappedFlooredData = CappedFlooredData.read(generic[i], 0);
        final var sourceEntry = entry(cappedFlooredData.sourceEntry());
        final var capEntry = entry(cappedFlooredData.capEntry());
        final var floorEntry = entry(cappedFlooredData.floorEntry());
        yield new CappedFloored(i, sourceEntry, capEntry, floorEntry, cappedFlooredData.sourcesMaxAgeS());
      }
      case CappedMostRecentOf -> {
        final var cappedMostRecentOf = CappedMostRecentOfData.read(generic[i], 0);
        final var sources = parseEntries(cappedMostRecentOf.sourceEntries(), SourceList.SKIP_GAPS);
        final var capEntry = entry(cappedMostRecentOf.capEntry());
        yield new CappedMostRecentOf(i, sources, cappedMostRecentOf.maxDivergenceBps(), cappedMostRecentOf.sourcesMaxAgeS(), capEntry);
      }
      case ChainlinkRWA -> {
        final var cfg = V8.read(generic[i], 0);
        yield new ChainlinkRWA(i, priceAccount, cfg.marketStatusBehavior(), emaTypes);
      }
      case ChainlinkX -> {
        final var cfg = ChainlinkXMappingData.read(generic[i], 0);
        yield new ChainlinkX(i, priceAccount, cfg.marketStatusBehavior(), emaTypes, cfg.dailyAutoApprovalBps());
      }
      case Chainlink -> {
        final var cfg = V3.read(generic[i], 0);
        final var band = band(i);
        yield new Chainlink(i, priceAccount, cfg.confidenceFactor(), emaTypes, band.refPrice(), band.toleranceBps());
      }
      case ChainlinkExchangeRate -> new ChainlinkExchangeRate(i, priceAccount, emaTypes);
      case Conditional -> {
        final var data = ConditionalData.read(generic[i], 0);
        final var condition = software.sava.idl.clients.kamino.scope.gen.types.Condition.values()[data.condition()];
        final int numSources = switch (condition) {
          case NonZero -> 1;
          case WithinRangeAbs, OutsideRangeAbs -> 3;
          default -> 2;
        };
        final var sourceIndices = data.sources();
        final var slice = new int[numSources];
        System.arraycopy(sourceIndices, 0, slice, 0, numSources);
        final var sources = parseEntries(slice, SourceList.POSITIONAL);
        yield new Conditional(i, condition, data.toleranceBps(), sources);
      }
      case ChainlinkNAV -> new ChainlinkNAV(i, priceAccount, emaTypes);
      case DiscountToMaturity -> {
        final var dtm = DiscountToMaturityData.read(generic[i], 0);
        yield new DiscountToMaturity(i, dtm.discountPerYearBps(), dtm.maturityTimestamp());
      }
      case ExponentTranching -> {
        final var data = ExponentTranchingData.read(generic[i], 0);
        yield new ExponentTranching(i, priceAccount, data.trancheSide(), emaTypes);
      }
      case FixedPrice -> {
        final var price = Price.read(generic[i], 0);
        yield software.sava.idl.clients.kamino.scope.entries.FixedPrice.createEntry(i, price.value(), Math.toIntExact(price.exp()));
      }
      case FlashtradeLp -> new FlashtradeLp(i, priceAccount, emaTypes);
      case JitoRestaking -> new JitoRestaking(i, priceAccount, emaTypes);
      case JupiterLpFetch -> new JupiterLpFetch(i, priceAccount, emaTypes);
      case KToken -> new KToken(i, priceAccount, emaTypes);
      case KlendCTokenExchangeRate -> new KlendCTokenExchangeRate(i, priceAccount, emaTypes);
      case MeteoraDlmmAtoB -> new MeteoraDlmmAtoB(i, priceAccount, emaTypes);
      case MeteoraDlmmBtoA -> new MeteoraDlmmBtoA(i, priceAccount, emaTypes);
      case MostRecentOf -> {
        final var mostRecentOf = MostRecentOfData.read(generic[i], 0);
        final var band = band(i);
        final var sources = parseEntries(mostRecentOf.sourceEntries(), SourceList.SKIP_GAPS);
        yield new MostRecentOfEntry(i, sources, mostRecentOf.maxDivergenceBps(), mostRecentOf.sourcesMaxAgeS(), band.refPrice(), band.toleranceBps());
      }
      case MsolStake -> new MsolStake(i, priceAccount, emaTypes);
      case MultiplicationChain -> {
        final var data = MultiplicationChainData.read(generic[i], 0);
        final var sources = parseEntries(data.sourceEntries(), SourceList.END_AT_SENTINEL);
        yield new MultiplicationChain(i, sources, data.sourcesMaxAgeS());
      }
      case OrcaWhirlpoolAtoB -> new OrcaWhirlpoolAtoB(i, priceAccount, emaTypes);
      case OrcaWhirlpoolBtoA -> new OrcaWhirlpoolBtoA(i, priceAccount, emaTypes);
      case PythLazer -> {
        final var data = PythLazerData.read(generic[i], 0);
        final var band = band(i);
        yield new PythLazer(
            i,
            priceAccount,
            data.feedId(),
            data.exponent(),
            data.bidAskSpreadFactor(),
            data.priceConfidenceFactor(),
            emaTypes,
            band.refPrice(),
            band.toleranceBps()
        );
      }
      case PythLazerEMA -> {
        final var data = PythLazerEmaRefData.read(generic[i], 0);
        yield new PythLazerEMA(i, entry(data.sourceEntry()), emaTypes);
      }
      case PythPull -> {
        final var band = band(i);
        yield new PythPull(i, priceAccount, emaTypes, band.refPrice(), band.toleranceBps());
      }
      case PythPullEMA -> new PythPullEMA(i, priceAccount, emaTypes);
      case RaydiumAmmV3AtoB -> new RaydiumAmmV3AtoB(i, priceAccount, emaTypes);
      case RaydiumAmmV3BtoA -> new RaydiumAmmV3BtoA(i, priceAccount, emaTypes);
      case RedStone -> new RedStone(i, priceAccount, emaTypes);
      case ScopeTwap1h, ScopeTwap8h, ScopeTwap24h, ScopeTwap7d ->
          new ScopeTwap(i, oracleType, entry(twapSourceOrRefPriceToleranceBps[i]));
      case Securitize -> {
        final var band = band(i);
        yield new Securitize(i, priceAccount, emaTypes, band.refPrice(), band.toleranceBps());
      }
      case SplBalance -> new SplBalance(i, priceAccount);
      case SplStake -> new SplStake(i, priceAccount);
      case StakedSolBalance -> new StakedSolBalance(i, priceAccount);
      case Token2022Multiplier -> {
        final var mappingData = Token2022MultiplierMappingData.read(generic[i], 0);
        yield new Token2022Multiplier(i, priceAccount, emaTypes, mappingData.dailyAutoApprovalBps());
      }
      case TotalMintSupply -> new TotalMintSupply(i, priceAccount, emaTypes);
      case SwitchboardOnDemand -> new SwitchboardOnDemand(i, priceAccount, emaTypes);
      case Unused -> new Unused(i);
      default -> {
        if (oracleType.name().startsWith("Deprecated")) {
          yield new Deprecated(i, oracleType);
        } else {
          final var band = band(i);
          yield new NotYetSupported(i, priceAccount, oracleType, emaTypes, band.refPrice(), band.toleranceBps(), generic[i]);
        }
      }
    };
  }

}

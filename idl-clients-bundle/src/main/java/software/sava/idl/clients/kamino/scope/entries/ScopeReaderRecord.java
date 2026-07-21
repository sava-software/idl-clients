package software.sava.idl.clients.kamino.scope.entries;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.kamino.scope.gen.types.*;

import java.util.EnumSet;
import java.util.OptionalInt;
import java.util.Set;

import static software.sava.idl.clients.kamino.scope.gen.types.OracleType.*;

record ScopeReaderRecord(ScopeEntry[] entries,
                         PublicKey[] priceInfoAccounts,
                         byte[] priceTypes,
                         int[] twapSourceOrRefPriceToleranceBps,
                         TwapEnabledBitmask[] twapEnabledBitmasks,
                         int[] refPrice,
                         byte[][] generic,
                         OracleType[] oracleTypes,
                         boolean[] visiting) implements ScopeReader {

  /// Bit 7 of `OracleMappings.price_types[i]` is a frozen flag.
  /// The oracle type is preserved in bits 0-6.
  private static final int ORACLE_TYPE_MASK = 0x7F;
  private static final int NO_REF_PRICE_TOLERANCE = 0xFFFF;

  ScopeEntries readEntries(final PublicKey pubKey, final long slot) {
    for (int i = 0; i < priceInfoAccounts.length; ++i) {
      entries[i] = entry(i);
    }
    return new ScopeEntriesRecord(pubKey, slot, entries);
  }

  private ScopeEntry entry(final OptionalInt i) {
    return i.isPresent() ? entry(i.getAsInt()) : null;
  }

  private ScopeEntry[] parseEntries(final int[] entryIndices) {
    final var entries = new ScopeEntry[entryIndices.length];
    int j = 0;
    for (; j < entryIndices.length; ++j) {
      final var entry = entry(entryIndices[j]);
      if (entry == null) {
        break;
      }
      entries[j] = entry;
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
      // a cyclic refPrice/source index (never produced on-chain) would recurse forever;
      // treat the back-reference as an absent entry instead of overflowing the stack
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

  private ScopeEntry computeEntry(final int i) {
    final var priceAccount = priceInfoAccounts[i];
    final var emaTypes = emaTypes(this.twapEnabledBitmasks[i].bitmask());
    final var refPrice = entry(this.refPrice[i]);
    final var refPriceToleranceBps = refPriceToleranceBps(i, refPrice);
    final int typeOrdinal = priceTypes[i] & ORACLE_TYPE_MASK;
    if (typeOrdinal >= oracleTypes.length) {
      // the on-chain program has deployed an oracle type newer than the generated
      // OracleType enum; degrade until the IDL is re-synced instead of failing the
      // whole mappings parse
      return new NotYetSupported(i, priceAccount, null, emaTypes, refPrice, refPriceToleranceBps, generic[i]);
    }
    final var oracleType = oracleTypes[typeOrdinal];
    return switch (oracleType) {
      case AdrenaLp -> new AdrenaLp(i, priceAccount, emaTypes);
      case CappedFloored -> {
        validateNoEmaTypes(oracleType, emaTypes);
        final var cappedFlooredData = CappedFlooredData.read(generic[i], 0);
        final var sourceEntry = entry(cappedFlooredData.sourceEntry());
        final var capEntry = entry(cappedFlooredData.capEntry());
        final var floorEntry = entry(cappedFlooredData.floorEntry());
        yield new CappedFloored(i, sourceEntry, capEntry, floorEntry);
      }
      case CappedMostRecentOf -> {
        validateNoEmaTypes(oracleType, emaTypes);
        final var cappedMostRecentOf = CappedMostRecentOfData.read(generic[i], 0);
        final var sources = parseEntries(cappedMostRecentOf.sourceEntries());
        final var capEntry = entry(cappedMostRecentOf.capEntry());
        yield new CappedMostRecentOf(i, sources, cappedMostRecentOf.maxDivergenceBps(), cappedMostRecentOf.sourcesMaxAgeS(), capEntry);
      }
      case ChainlinkRWA -> {
        final var cfg = V8V10.read(generic[i], 0);
        yield new ChainlinkRWA(i, priceAccount, cfg.marketStatusBehavior(), emaTypes);
      }
      case ChainlinkX -> {
        final var cfg = V8V10.read(generic[i], 0);
        yield new ChainlinkX(i, priceAccount, cfg.marketStatusBehavior(), emaTypes);
      }
      case Chainlink -> {
        final var cfg = V3.read(generic[i], 0);
        yield new Chainlink(i, priceAccount, cfg.confidenceFactor(), emaTypes, refPrice, refPriceToleranceBps);
      }
      case ChainlinkExchangeRate -> new ChainlinkExchangeRate(i, priceAccount, emaTypes);
      case Conditional -> {
        validateNoRefPrice(oracleType, refPrice);
        validateNoEmaTypes(oracleType, emaTypes);
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
        final var sources = parseEntries(slice);
        yield new Conditional(i, condition, data.toleranceBps(), sources);
      }
      case ChainlinkNAV -> new ChainlinkNAV(i, priceAccount, emaTypes);
      case DiscountToMaturity -> {
        validateNoEmaTypes(oracleType, emaTypes);
        final var dtm = DiscountToMaturityData.read(generic[i], 0);
        yield new DiscountToMaturity(i, dtm.discountPerYearBps(), dtm.maturityTimestamp());
      }
      case FixedPrice -> {
        validateNoEmaTypes(oracleType, emaTypes);
        final var price = Price.read(generic[i], 0);
        yield software.sava.idl.clients.kamino.scope.entries.FixedPrice.createEntry(i, price.value(), Math.toIntExact(price.exp()));
      }
      case FlashtradeLp -> new FlashtradeLp(i, priceAccount, emaTypes);
      case JitoRestaking -> new JitoRestaking(i, priceAccount, emaTypes);
      case JupiterLpFetch -> new JupiterLpFetch(i, priceAccount, emaTypes);
      case KToken -> new KToken(i, priceAccount, emaTypes);
      case MeteoraDlmmAtoB -> new MeteoraDlmmAtoB(i, priceAccount, emaTypes);
      case MeteoraDlmmBtoA -> new MeteoraDlmmBtoA(i, priceAccount, emaTypes);
      case MostRecentOf -> {
        validateNoEmaTypes(oracleType, emaTypes);
        final var mostRecentOf = MostRecentOfData.read(generic[i], 0);
        final var sources = parseEntries(mostRecentOf.sourceEntries());
        yield new MostRecentOfEntry(i, sources, mostRecentOf.maxDivergenceBps(), mostRecentOf.sourcesMaxAgeS(), refPrice, refPriceToleranceBps);
      }
      case MsolStake -> new MsolStake(i, priceAccount, emaTypes);
      case MultiplicationChain -> {
        validateNoEmaTypes(oracleType, emaTypes);
        final var data = MultiplicationChainData.read(generic[i], 0);
        final var sources = parseEntries(data.sourceEntries());
        yield new MultiplicationChain(i, sources, data.sourcesMaxAgeS());
      }
      case OrcaWhirlpoolAtoB -> new OrcaWhirlpoolAtoB(i, priceAccount, emaTypes);
      case OrcaWhirlpoolBtoA -> new OrcaWhirlpoolBtoA(i, priceAccount, emaTypes);
      case PythLazer -> {
        final var data = PythLazerData.read(generic[i], 0);
        yield new PythLazer(
            i,
            priceAccount,
            data.feedId(),
            data.exponent(),
            data.confidenceFactor(),
            emaTypes,
            refPrice,
            refPriceToleranceBps
        );
      }
      case PythLazerEMA -> {
        final var data = PythLazerEmaRefData.read(generic[i], 0);
        yield new PythLazerEMA(i, entry(data.sourceEntry()), emaTypes);
      }
      case PythPull -> new PythPull(i, priceAccount, emaTypes, refPrice, refPriceToleranceBps);
      case PythPullEMA -> new PythPullEMA(i, priceAccount, emaTypes);
      case RaydiumAmmV3AtoB -> new RaydiumAmmV3AtoB(i, priceAccount, emaTypes);
      case RaydiumAmmV3BtoA -> new RaydiumAmmV3BtoA(i, priceAccount, emaTypes);
      case RedStone -> new RedStone(i, priceAccount, emaTypes);
      case ScopeTwap1h -> {
        validateNoEmaTypes(oracleType, emaTypes);
        yield new ScopeTwap(i, ScopeTwap1h, entry(twapSourceOrRefPriceToleranceBps[i]));
      }
      case ScopeTwap8h -> {
        validateNoEmaTypes(oracleType, emaTypes);
        yield new ScopeTwap(i, ScopeTwap8h, entry(twapSourceOrRefPriceToleranceBps[i]));
      }
      case ScopeTwap24h -> {
        validateNoEmaTypes(oracleType, emaTypes);
        yield new ScopeTwap(i, ScopeTwap24h, entry(twapSourceOrRefPriceToleranceBps[i]));
      }
      case ScopeTwap7d -> {
        validateNoEmaTypes(oracleType, emaTypes);
        yield new ScopeTwap(i, ScopeTwap7d, entry(twapSourceOrRefPriceToleranceBps[i]));
      }
      case Securitize -> new Securitize(i, priceAccount, emaTypes, refPrice, refPriceToleranceBps);
      case SplBalance -> {
        validateNoRefPrice(oracleType, refPrice);
        validateNoEmaTypes(oracleType, emaTypes);
        yield new SplBalance(i, priceAccount);
      }
      case SplStake -> {
//        validateNoRefPrice(oracleType, refPrice);
        validateNoEmaTypes(oracleType, emaTypes);
        yield new SplStake(i, priceAccount);
      }
      case StakedSolBalance -> {
        validateNoRefPrice(oracleType, refPrice);
        validateNoEmaTypes(oracleType, emaTypes);
        yield new StakedSolBalance(i, priceAccount);
      }
      case TotalMintSupply -> {
        validateNoRefPrice(oracleType, refPrice);
        yield new TotalMintSupply(i, priceAccount, emaTypes);
      }
      case SwitchboardOnDemand -> new SwitchboardOnDemand(i, priceAccount, emaTypes);
      case Unused -> {
        validateNoEmaTypes(oracleType, emaTypes);
        yield new Unused(i);
      }
      default -> {
        if (oracleType.name().startsWith("Deprecated")) {
          validateNoEmaTypes(oracleType, emaTypes);
          yield new Deprecated(i, oracleType);
        } else {
          yield new NotYetSupported(i, priceAccount, oracleType, emaTypes, refPrice, refPriceToleranceBps, generic[i]);
        }
      }
    };
  }

  private static void validateNoRefPrice(final OracleType oracleType, final ScopeEntry refPrice) {
    if (refPrice != null) {
      throw new IllegalStateException("Unexpected ref price: " + refPrice + " for oracle type: " + oracleType);
    }
  }

  private static void validateNoEmaTypes(final OracleType oracleType, final Set<EmaType> emaTypes) {
    if (emaTypes != null && !emaTypes.isEmpty()) {
      throw new IllegalStateException("Unexpected EMA types: " + emaTypes + " for oracle type:" + oracleType);
    }
  }
}

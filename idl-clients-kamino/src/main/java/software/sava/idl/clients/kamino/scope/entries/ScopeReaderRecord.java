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
                         short[] twapSource,
                         TwapEnabledBitmask[] twapEnabledBitmasks,
                         short[] refPrice,
                         byte[][] generic,
                         OracleType[] oracleTypes) implements ScopeReader {

  ScopeEntries readEntries(final PublicKey pubKey, final long slot) {
    for (int i = 0; i < priceInfoAccounts.length; ++i) {
      entries[i] = entry(i);
    }
    return new ScopeEntriesRecord(pubKey, slot, entries);
  }

  private ScopeEntry entry(final OptionalInt i) {
    return i.isPresent() ? entry(i.getAsInt()) : null;
  }

  private ScopeEntry[] parseEntries(final short[] entryIndices) {
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
      int ordinal;
      for (final var type : EmaType.values()) {
        ordinal = type.ordinal();
        if ((ordinal & bitmask) != ordinal) {
          types.add(type);
        }
      }
      return types;
    } else {
      return Set.of();
    }
  }

  private ScopeEntry entry(final int i) {
    if (i < 0 || i >= priceInfoAccounts.length) {
      return null;
    }
    final var entry = entries[i];
    if (entry != null) {
      return entry;
    }
    final var priceAccount = priceInfoAccounts[i];
    final var oracleType = oracleTypes[Byte.toUnsignedInt(priceTypes[i])];
    final var emaTypes = emaTypes(this.twapEnabledBitmasks[i].bitmask());
    final var refPrice = entry(this.refPrice[i]);
    return switch (oracleType) {
      case AdrenaLp -> new AdrenaLp(priceAccount, emaTypes);
      case CappedFloored -> {
        final var cappedFlooredData = CappedFlooredData.read(generic[i], 0);
        final var sourceEntry = entry(cappedFlooredData.sourceEntry());
        final var capEntry = entry(cappedFlooredData.capEntry());
        final var floorEntry = entry(cappedFlooredData.floorEntry());
        yield new CappedFloored(sourceEntry, capEntry, floorEntry);
      }
      case CappedMostRecentOf -> {
        final var cappedMostRecentOf = CappedMostRecentOfData.read(generic[i], 0);
        final var sources = parseEntries(cappedMostRecentOf.sourceEntries());
        final var capEntry = entry(cappedMostRecentOf.capEntry());
        yield new CappedMostRecentOf(sources, cappedMostRecentOf.maxDivergenceBps(), cappedMostRecentOf.sourcesMaxAgeS(), capEntry);
      }
      case ChainlinkRWA -> {
        final var cfg = V8V10.read(generic[i], 0);
        yield new ChainlinkRWA(priceAccount, cfg.marketStatusBehavior(), emaTypes);
      }
      case ChainlinkX -> {
        final var cfg = V8V10.read(generic[i], 0);
        yield new ChainlinkX(priceAccount, cfg.marketStatusBehavior(), emaTypes);
      }
      case Chainlink -> {
        final var cfg = V3.read(generic[i], 0);
        yield new Chainlink(priceAccount, cfg.confidenceFactor(), emaTypes, refPrice);
      }
      case ChainlinkExchangeRate -> new ChainlinkExchangeRate(priceAccount, emaTypes);
      case ChainlinkNAV -> new ChainlinkNAV(priceAccount, emaTypes);
      case DiscountToMaturity -> {
        final var dtm = DiscountToMaturityData.read(generic[i], 0);
        yield new DiscountToMaturity(dtm.discountPerYearBps(), dtm.maturityTimestamp());
      }
      case FixedPrice -> {
        final var price = Price.read(generic[i], 0);
        yield software.sava.idl.clients.kamino.scope.entries.FixedPrice.createEntry(price.value(), Math.toIntExact(price.exp()));
      }
      case FlashtradeLp -> new FlashtradeLp(priceAccount, emaTypes);
      case JitoRestaking -> new JitoRestaking(priceAccount, emaTypes);
      case JupiterLpFetch -> new JupiterLpFetch(priceAccount, emaTypes);
      case KToken -> new KToken(priceAccount, emaTypes);
      case MeteoraDlmmAtoB -> new MeteoraDlmmAtoB(priceAccount, emaTypes);
      case MeteoraDlmmBtoA -> new MeteoraDlmmBtoA(priceAccount, emaTypes);
      case MostRecentOf -> {
        final var mostRecentOf = MostRecentOfData.read(generic[i], 0);
        final var sources = parseEntries(mostRecentOf.sourceEntries());
        yield new MostRecentOfEntry(sources, mostRecentOf.maxDivergenceBps(), mostRecentOf.sourcesMaxAgeS(), refPrice);
      }
      case MsolStake -> new MsolStake(priceAccount, emaTypes);
      case OrcaWhirlpoolAtoB -> new OrcaWhirlpoolAtoB(priceAccount, emaTypes);
      case OrcaWhirlpoolBtoA -> new OrcaWhirlpoolBtoA(priceAccount, emaTypes);
      case PythLazer -> {
        final var data = PythLazerData.read(generic[i], 0);
        yield new PythLazer(
            priceAccount,
            data.feedId(),
            data.exponent(),
            data.confidenceFactor(),
            emaTypes,
            refPrice
        );
      }
      case PythPull -> new PythPull(priceAccount, emaTypes, refPrice);
      case PythPullEMA -> new PythPullEMA(priceAccount, emaTypes);
      case RaydiumAmmV3AtoB -> new RaydiumAmmV3AtoB(priceAccount, emaTypes);
      case RaydiumAmmV3BtoA -> new RaydiumAmmV3BtoA(priceAccount, emaTypes);
      case RedStone -> new RedStone(priceAccount, emaTypes);
      case ScopeTwap1h -> new ScopeTwap(ScopeTwap1h, entry(twapSource[i]));
      case ScopeTwap8h -> new ScopeTwap(ScopeTwap8h, entry(twapSource[i]));
      case ScopeTwap24h -> new ScopeTwap(ScopeTwap24h, entry(twapSource[i]));
      case Securitize -> new Securitize(priceAccount, emaTypes, refPrice);
      case SplStake -> new SplStake(priceAccount, emaTypes);
      case SwitchboardOnDemand -> new SwitchboardOnDemand(priceAccount, emaTypes);
      case Unused -> software.sava.idl.clients.kamino.scope.entries.Unused.INSTANCE;
      default -> {
        if (oracleType.name().startsWith("Deprecated")) {
          yield Deprecated.INSTANCE;
        } else {
          yield new NotYetSupported(priceAccount, oracleType, entry(i), emaTypes, refPrice, generic[i]);
        }
      }
    };
  }
}

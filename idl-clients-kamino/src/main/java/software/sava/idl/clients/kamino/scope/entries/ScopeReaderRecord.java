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
      case AdrenaLp -> new AdrenaLp(i, priceAccount, emaTypes);
      case CappedFloored -> {
        final var cappedFlooredData = CappedFlooredData.read(generic[i], 0);
        final var sourceEntry = entry(cappedFlooredData.sourceEntry());
        final var capEntry = entry(cappedFlooredData.capEntry());
        final var floorEntry = entry(cappedFlooredData.floorEntry());
        yield new CappedFloored(i, sourceEntry, capEntry, floorEntry);
      }
      case CappedMostRecentOf -> {
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
        yield new Chainlink(i, priceAccount, cfg.confidenceFactor(), emaTypes, refPrice);
      }
      case ChainlinkExchangeRate -> new ChainlinkExchangeRate(i, priceAccount, emaTypes);
      case ChainlinkNAV -> new ChainlinkNAV(i, priceAccount, emaTypes);
      case DiscountToMaturity -> {
        final var dtm = DiscountToMaturityData.read(generic[i], 0);
        yield new DiscountToMaturity(i, dtm.discountPerYearBps(), dtm.maturityTimestamp());
      }
      case FixedPrice -> {
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
        final var mostRecentOf = MostRecentOfData.read(generic[i], 0);
        final var sources = parseEntries(mostRecentOf.sourceEntries());
        yield new MostRecentOfEntry(i, sources, mostRecentOf.maxDivergenceBps(), mostRecentOf.sourcesMaxAgeS(), refPrice);
      }
      case MsolStake -> new MsolStake(i, priceAccount, emaTypes);
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
            refPrice
        );
      }
      case PythPull -> new PythPull(i, priceAccount, emaTypes, refPrice);
      case PythPullEMA -> new PythPullEMA(i, priceAccount, emaTypes);
      case RaydiumAmmV3AtoB -> new RaydiumAmmV3AtoB(i, priceAccount, emaTypes);
      case RaydiumAmmV3BtoA -> new RaydiumAmmV3BtoA(i, priceAccount, emaTypes);
      case RedStone -> new RedStone(i, priceAccount, emaTypes);
      case ScopeTwap1h -> new ScopeTwap(i, ScopeTwap1h, entry(twapSource[i]));
      case ScopeTwap8h -> new ScopeTwap(i, ScopeTwap8h, entry(twapSource[i]));
      case ScopeTwap24h -> new ScopeTwap(i, ScopeTwap24h, entry(twapSource[i]));
      case Securitize -> new Securitize(i, priceAccount, emaTypes, refPrice);
      case SplStake -> new SplStake(i, priceAccount, emaTypes);
      case SwitchboardOnDemand -> new SwitchboardOnDemand(i, priceAccount, emaTypes);
      case Unused -> new Unused(i);
      default -> {
        if (oracleType.name().startsWith("Deprecated")) {
          yield new Deprecated(i, oracleType);
        } else {
          yield new NotYetSupported(i, priceAccount, oracleType, entry(twapSource[i]), emaTypes, refPrice, generic[i]);
        }
      }
    };
  }
}

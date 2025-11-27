package software.sava.idl.clients.kamino.scope.entries;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.kamino.scope.gen.types.*;

import java.util.OptionalInt;

record ScopeReaderRecord(ScopeEntry[] entries,
                         PublicKey[] priceInfoAccounts,
                         byte[] priceTypes,
                         short[] twapSource,
                         byte[] twapEnabled,
                         short[] refPrice,
                         byte[][] generic,
                         OracleType[] oracleTypes) implements ScopeReader {

  ScopeEntries readEntries() {
    for (int i = 0; i < priceInfoAccounts.length; ++i) {
      entries[i] = entry(i);
    }
    return new ScopeEntriesRecord(entries);
  }

  private ScopeEntry entry(final OptionalInt i) {
    return i.isPresent() ? entry(i.getAsInt()) : null;
  }

  private ScopeEntry[] parseEntries(final short[] entryIndicies) {
    final var entries = new ScopeEntry[entryIndicies.length];
    for (int j = 0; j < entryIndicies.length; ++j) {
      entries[j] = entry(entryIndicies[j]);
    }
    return entries;
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
    final boolean twapEnabled = this.twapEnabled[i] != 0;
    final var refPrice = entry(this.refPrice[i]);
    return switch (oracleType) {
      case AdrenaLp -> new AdrenaLp(priceAccount, twapEnabled);
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
        yield new ChainlinkRWA(priceAccount, cfg.marketStatusBehavior(), twapEnabled);
      }
      case ChainlinkX -> {
        final var cfg = V8V10.read(generic[i], 0);
        yield new ChainlinkX(priceAccount, cfg.marketStatusBehavior(), twapEnabled);
      }
      case Chainlink -> {
        final var cfg = V3.read(generic[i], 0);
        yield new Chainlink(priceAccount, cfg.confidenceFactor(), twapEnabled, refPrice);
      }
      case ChainlinkExchangeRate -> new ChainlinkExchangeRate(priceAccount, twapEnabled);
      case ChainlinkNAV -> new ChainlinkNAV(priceAccount, twapEnabled);
      case DiscountToMaturity -> {
        final var dtm = DiscountToMaturityData.read(generic[i], 0);
        yield new DiscountToMaturity(dtm.discountPerYearBps(), dtm.maturityTimestamp());
      }
      case FixedPrice -> {
        final var price = Price.read(generic[i], 0);
        yield new FixedPrice(price.value(), Math.toIntExact(price.exp()));
      }
      case FlashtradeLp -> new FlashtradeLp(priceAccount, twapEnabled);
      case JitoRestaking -> new JitoRestaking(priceAccount, twapEnabled);
      case JupiterLpFetch -> new JupiterLpFetch(priceAccount, twapEnabled);
      case KToken -> new KToken(priceAccount, twapEnabled);
      case MeteoraDlmmAtoB -> new MeteoraDlmmAtoB(priceAccount, twapEnabled);
      case MeteoraDlmmBtoA -> new MeteoraDlmmBtoA(priceAccount, twapEnabled);
      case MostRecentOf -> {
        final var mostRecentOf = MostRecentOfData.read(generic[i], 0);
        final var sources = parseEntries(mostRecentOf.sourceEntries());
        yield new MostRecentOf(sources, mostRecentOf.maxDivergenceBps(), mostRecentOf.sourcesMaxAgeS(), refPrice);
      }
      case MsolStake -> new MsolStake(priceAccount, twapEnabled);
      case OrcaWhirlpoolAtoB -> new OrcaWhirlpoolAtoB(priceAccount, twapEnabled);
      case OrcaWhirlpoolBtoA -> new OrcaWhirlpoolBtoA(priceAccount, twapEnabled);
      case PythLazer -> {
        final var data = PythLazerData.read(generic[i], 0);
        yield new PythLazer(
            priceAccount,
            data.feedId(),
            data.exponent(),
            data.confidenceFactor(),
            twapEnabled,
            refPrice
        );
      }
      case PythPull -> new PythPull(priceAccount, twapEnabled, refPrice);
      case PythPullEMA -> new PythPullEMA(priceAccount, twapEnabled);
      case RaydiumAmmV3AtoB -> new RaydiumAmmV3AtoB(priceAccount, twapEnabled);
      case RaydiumAmmV3BtoA -> new RaydiumAmmV3BtoA(priceAccount, twapEnabled);
      case RedStone -> new RedStone(priceAccount, twapEnabled);
      case ScopeTwap -> new ScopeTwap(entry(twapSource[i]));
      case Securitize -> new Securitize(priceAccount, twapEnabled, refPrice);
      case SplStake -> new SplStake(priceAccount, twapEnabled);
      case SwitchboardOnDemand -> new SwitchboardOnDemand(priceAccount, twapEnabled);
      case Unused -> Unused.INSTANCE;
      default -> {
        if (oracleType.name().startsWith("Deprecated")) {
          yield Deprecated.INSTANCE;
        } else {
          yield new NotYetSupported(priceAccount, oracleType, entry(i), twapEnabled, refPrice, generic[i]);
        }
      }
    };
  }
}

package software.sava.idl.clients.kamino.scope.entries;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.kamino.lend.gen.types.Reserve;
import software.sava.idl.clients.kamino.scope.gen.types.*;

import java.util.Map;
import java.util.OptionalInt;

record ScopeReaderRecord(PublicKey[] priceInfoAccounts,
                         byte[] priceTypes,
                         short[] twapSource,
                         byte[] twapEnabled,
                         short[] refPrice,
                         byte[][] generic,
                         OracleType[] oracleTypes) implements ScopeReader {

  @Override
  public ScopeEntry[] readEntries() {
    final var entries = new ScopeEntry[priceInfoAccounts.length];
    for (int i = 0; i < priceInfoAccounts.length; ++i) {
      entries[i] = parseEntry(i);
    }
    return entries;
  }

  @Override
  public PriceChains readPriceChains(final Map<PublicKey, ScopeEntry[]> oracleMappings, final Reserve reserve) {
    final var mintKey = reserve.liquidity().mintPubkey();
    if (mintKey.equals(PublicKey.NONE)) {
      return null;
    }
    final var scopeConfiguration = reserve.config().tokenInfo().scopeConfiguration();
    final var scopeEntries = oracleMappings.get(scopeConfiguration.priceFeed());
    if (scopeEntries == null) {
      throw new IllegalStateException("Unknown Scope price feed: " + scopeConfiguration.priceFeed());
    }
    final var priceChain = parseChain(scopeConfiguration.priceChain(), scopeEntries);
    final var twapChain = parseChain(scopeConfiguration.twapChain(), scopeEntries);
    return new PriceChains(priceChain, twapChain);
  }

  private ScopeEntry parseEntry(final OptionalInt i) {
    return i.isPresent() ? parseEntry(i.getAsInt()) : null;
  }

  private ScopeEntry[] parseEntries(final short[] entryIndicies) {
    final var entries = new ScopeEntry[entryIndicies.length];
    for (int j = 0; j < entryIndicies.length; ++j) {
      entries[j] = parseEntry(entryIndicies[j]);
    }
    return entries;
  }

  private ScopeEntry parseEntry(final int i) {
    if (i < 0 || i >= priceInfoAccounts.length) {
      return null;
    }
    final var priceAccount = priceInfoAccounts[i];
    final var oracleType = oracleTypes[Byte.toUnsignedInt(priceTypes[i])];
    return switch (oracleType) {
      case ScopeTwap -> {
        final var source = parseEntry(twapSource[i]);
        final boolean enabled = twapEnabled[i] != 0;
        yield new ScopeTwap(source, enabled);
      }
      case CappedFloored -> {
        final var cappedFlooredData = CappedFlooredData.read(generic[i], 0);
        final var sourceEntry = parseEntry(cappedFlooredData.sourceEntry());
        final var capEntry = parseEntry(cappedFlooredData.capEntry());
        final var floorEntry = parseEntry(cappedFlooredData.floorEntry());
        yield new CappedFloored(sourceEntry, capEntry, floorEntry);
      }
      case DiscountToMaturity -> {
        final var dtm = DiscountToMaturityData.read(generic[i], 0);
        final var refEntry = parseEntry(refPrice[i]);
        yield new DiscountToMaturity(refEntry, dtm.discountPerYearBps(), dtm.maturityTimestamp());
      }
      case CappedMostRecentOf -> {
        final var cappedMostRecentOf = CappedMostRecentOfData.read(generic[i], 0);
        final var sources = parseEntries(cappedMostRecentOf.sourceEntries());
        final var capEntry = parseEntry(cappedMostRecentOf.capEntry());
        yield new CappedMostRecentOf(sources, cappedMostRecentOf.maxDivergenceBps(), cappedMostRecentOf.sourcesMaxAgeS(), capEntry);
      }
      case MostRecentOf -> {
        final var mostRecentOf = MostRecentOfData.read(generic[i], 0);
        final var sources = parseEntries(mostRecentOf.sourceEntries());
        yield new MostRecentOf(sources, mostRecentOf.maxDivergenceBps(), mostRecentOf.sourcesMaxAgeS());
      }
      case PythLazer -> {
        final var data = PythLazerData.read(generic[i], 0);
        yield new PythLazerEntry(priceAccount, data.feedId(), data.exponent(), data.confidenceFactor());
      }
      case ChainlinkRWA -> {
        final var cfg = V8V10.read(generic[i], 0);
        yield new ChainlinkRWA(priceAccount, cfg.marketStatusBehavior());
      }
      case ChainlinkX -> {
        final var cfg = V8V10.read(generic[i], 0);
        yield new ChainlinkX(priceAccount, cfg.marketStatusBehavior());
      }
      case Chainlink -> {
        final var cfg = V3.read(generic[i], 0);
        yield new Chainlink(priceAccount, cfg.confidenceFactor());
      }
      case FixedPrice -> {
        final var price = Price.read(generic[i], 0);
        yield new FixedPrice(price.value(), price.exp());
      }
      case PythPull, PythPullEMA, SwitchboardOnDemand -> new OracleEntry(oracleType, priceAccount);
      default -> null;
    };
  }

  private ScopeEntry[] parseChain(final short[] priceChain, final ScopeEntry[] scopeEntries) {
    final var entries = new ScopeEntry[priceChain.length];
    int i = 0;
    for (; i < priceChain.length; ++i) {
      final var priceChainEntry = priceChain[i];
      if (priceChainEntry < 0) {
        break;
      }
      entries[i] = scopeEntries[priceChainEntry];
    }
    if (i < entries.length) {
      final var trimmed = new ScopeEntry[i];
      System.arraycopy(entries, 0, trimmed, 0, i);
      return trimmed;
    } else {
      return entries;
    }
  }
}

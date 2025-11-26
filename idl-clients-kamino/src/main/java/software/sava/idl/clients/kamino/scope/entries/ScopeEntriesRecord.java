package software.sava.idl.clients.kamino.scope.entries;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.kamino.lend.gen.types.Reserve;

record ScopeEntriesRecord(ScopeEntry[] scopeEntries) implements ScopeEntries {

  @Override
  public PriceChains readPriceChains(final Reserve reserve) {
    final var mintKey = reserve.liquidity().mintPubkey();
    if (mintKey.equals(PublicKey.NONE)) {
      return null;
    }
    final var scopeConfiguration = reserve.config().tokenInfo().scopeConfiguration();
    final var priceChain = parseChain(scopeConfiguration.priceChain(), scopeEntries);
    final var twapChain = parseChain(scopeConfiguration.twapChain(), scopeEntries);
    return new PriceChainsRecord(priceChain, twapChain);
  }

  @Override
  public ScopeEntry scopeEntry(final int index) {
    return scopeEntries[index];
  }

  @Override
  public int numEntries() {
    return scopeEntries.length;
  }

  private ScopeEntry[] parseChain(final short[] priceChain, final ScopeEntry[] scopeEntries) {
    final var entries = new ScopeEntry[priceChain.length];
    int i = 0;
    for (; i < priceChain.length; ++i) {
      final var entry = priceChain[i];
      if (entry < 0) {
        break;
      }
      entries[i] = scopeEntries[entry];
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

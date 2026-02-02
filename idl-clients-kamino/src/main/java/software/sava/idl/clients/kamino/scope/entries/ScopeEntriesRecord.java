package software.sava.idl.clients.kamino.scope.entries;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.kamino.lend.gen.types.Reserve;
import software.sava.idl.clients.kamino.lend.gen.types.ScopeConfiguration;

import java.util.Arrays;

record ScopeEntriesRecord(PublicKey pubKey, long slot, ScopeEntry[] scopeEntries) implements ScopeEntries {

  @Override
  public PriceChains readPriceChains(final Reserve reserve) {
    final var mintKey = reserve.liquidity().mintPubkey();
    final var scopeConfiguration = reserve.config().tokenInfo().scopeConfiguration();
    return readPriceChains(mintKey, scopeConfiguration);
  }

  @Override
  public PriceChains readPriceChains(final PublicKey mintKey, final ScopeConfiguration scopeConfiguration) {
    if (mintKey.equals(PublicKey.NONE)) {
      return null;
    }
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
    int j = 0;
    for (int i = 0, entryIndex; i < priceChain.length; ++i) {
      entryIndex = priceChain[i];
      if (entryIndex < 0) {
        break;
      }
      final var entry = scopeEntries[entryIndex];
      if (entry == null) {
        throw new IllegalStateException("Entry not found at index: " + entryIndex);
      }
      entries[j++] = entry;
    }
    if (j < entries.length) {
      final var trimmed = new ScopeEntry[j];
      System.arraycopy(entries, 0, trimmed, 0, j);
      return trimmed;
    } else {
      return entries;
    }
  }

  @Override
  public boolean equals(final Object o) {
    if (!(o instanceof ScopeEntriesRecord(PublicKey key, long oSlot, ScopeEntry[] entries))) return false;
    return slot == oSlot && pubKey.equals(key) && Arrays.equals(scopeEntries, entries);
  }

  @Override
  public int hashCode() {
    int result = pubKey.hashCode();
    result = 31 * result + Long.hashCode(slot);
    result = 31 * result + Arrays.hashCode(scopeEntries);
    return result;
  }

  @Override
  public String toString() {
    return "ScopeEntriesRecord{" +
        "pubKey=" + pubKey +
        ", slot=" + slot +
        ", scopeEntries=" + Arrays.toString(scopeEntries) +
        '}';
  }
}

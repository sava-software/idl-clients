package software.sava.idl.clients.kamino.scope.entries;

import java.util.Arrays;

public record PriceChainsRecord(ScopeEntry[] priceChain, ScopeEntry[] twapChain) implements PriceChains {

  @Override
  public boolean equals(final Object o) {
    if (o instanceof PriceChainsRecord(final ScopeEntry[] price, final ScopeEntry[] twap)) {
      return Arrays.equals(this.priceChain, price) && Arrays.equals(this.twapChain, twap);
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    int result = Arrays.hashCode(priceChain);
    result = 31 * result + Arrays.hashCode(twapChain);
    return result;
  }

  @Override
  public String toString() {
    return "PriceChains{" +
        "priceChain=" + Arrays.toString(priceChain) +
        ", twapChain=" + Arrays.toString(twapChain) +
        '}';
  }
}

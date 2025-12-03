package software.sava.idl.clients.kamino.scope.entries;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.kamino.scope.gen.types.OracleType;

import java.util.Arrays;
import java.util.Objects;

public record NotYetSupported(PublicKey priceAccount,
                              OracleType oracleType,
                              ScopeEntry twapSource,
                              boolean twapEnabled,
                              ScopeEntry refPrice,
                              byte[] generic) implements ScopeEntry {

  @Override
  public boolean equals(final Object o) {
    if (o instanceof NotYetSupported(
        PublicKey account, OracleType type, ScopeEntry source, boolean enabled, ScopeEntry price, byte[] oGeneric
    )) {
      return twapEnabled == enabled
          && Arrays.equals(generic, oGeneric)
          && Objects.equals(refPrice, price)
          && oracleType == type
          && Objects.equals(twapSource, source)
          && Objects.equals(priceAccount, account);
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    int result = Objects.hashCode(priceAccount);
    result = 31 * result + oracleType.hashCode();
    result = 31 * result + Objects.hashCode(twapSource);
    result = 31 * result + Boolean.hashCode(twapEnabled);
    result = 31 * result + Objects.hashCode(refPrice);
    result = 31 * result + Arrays.hashCode(generic);
    return result;
  }
}

package software.sava.idl.clients.kamino.scope.entries;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.kamino.scope.gen.types.EmaType;
import software.sava.idl.clients.kamino.scope.gen.types.OracleType;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

public record NotYetSupported(int index,
                              PublicKey priceAccount,
                              OracleType oracleType,
                              ScopeEntry twapSource,
                              Set<EmaType> emaTypes,
                              ScopeEntry refPrice,
                              byte[] generic) implements ScopeEntry {

  @Override
  public boolean equals(final Object o) {
    if (o instanceof NotYetSupported(
        int i, PublicKey account, OracleType type, ScopeEntry source, Set<EmaType> _emaTypes, ScopeEntry price, byte[] oGeneric
    )) {
      return index == i
          && emaTypes.equals(_emaTypes)
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
    result = 31 * result + emaTypes.hashCode();
    result = 31 * result + Objects.hashCode(refPrice);
    result = 31 * result + Arrays.hashCode(generic);
    return result;
  }
}

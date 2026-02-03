package software.sava.idl.clients.kamino.scope.entries;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.kamino.scope.gen.types.EmaType;
import software.sava.idl.clients.kamino.scope.gen.types.OracleType;

import java.util.Set;

public record Chainlink(int index,
                        PublicKey oracle,
                        int confidenceFactor,
                        Set<EmaType> emaTypes,
                        ScopeEntry refPrice) implements ReferencesEntry {

  @Override
  public OracleType oracleType() {
    return OracleType.Chainlink;
  }
}

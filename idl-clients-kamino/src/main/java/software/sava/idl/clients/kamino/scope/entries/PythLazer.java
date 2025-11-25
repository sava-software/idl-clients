package software.sava.idl.clients.kamino.scope.entries;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.kamino.scope.gen.types.OracleType;

public record PythLazer(PublicKey oracle,
                        int feedId,
                        int exponent,
                        int confidenceFactor,
                        boolean twapEnabled,
                        ScopeEntry refPrice) implements ReferencesEntry, OracleEntry, ScopeEntry {

  @Override
  public OracleType oracleType() {
    return OracleType.PythLazer;
  }
}

package software.sava.idl.clients.kamino.scope.entries;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.kamino.scope.gen.types.OracleType;

public record PythPull(PublicKey oracle, boolean twapEnabled, ScopeEntry refPrice) implements ReferencesEntry, OracleEntry, ScopeEntry {

  @Override
  public OracleType oracleType() {
    return OracleType.PythPull;
  }
}

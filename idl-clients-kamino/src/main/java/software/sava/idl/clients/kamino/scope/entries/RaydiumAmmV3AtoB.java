package software.sava.idl.clients.kamino.scope.entries;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.kamino.scope.gen.types.OracleType;

public record RaydiumAmmV3AtoB(PublicKey oracle, boolean twapEnabled) implements OracleEntry, ScopeEntry {

  @Override
  public OracleType oracleType() {
    return OracleType.RaydiumAmmV3AtoB;
  }
}

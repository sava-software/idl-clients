package software.sava.idl.clients.kamino.scope.entries;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.kamino.scope.gen.types.OracleType;
import software.sava.idl.clients.kamino.scope.gen.types.MarketStatusBehavior;

public record ChainlinkRWA(PublicKey oracle, MarketStatusBehavior marketStatusBehavior) implements ScopeEntry {

  @Override
  public OracleType oracleType() {
    return OracleType.ChainlinkRWA;
  }
}

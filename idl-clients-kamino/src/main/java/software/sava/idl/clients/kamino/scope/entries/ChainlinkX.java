package software.sava.idl.clients.kamino.scope.entries;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.kamino.scope.gen.types.MarketStatusBehavior;
import software.sava.idl.clients.kamino.scope.gen.types.OracleType;

public record ChainlinkX(PublicKey oracle,
                         MarketStatusBehavior marketStatusBehavior,
                         boolean twapEnabled) implements OracleEntry, ScopeEntry {

  @Override
  public OracleType oracleType() {
    return OracleType.ChainlinkX;
  }
}

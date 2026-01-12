package software.sava.idl.clients.kamino.scope.entries;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.kamino.scope.gen.types.EmaType;
import software.sava.idl.clients.kamino.scope.gen.types.MarketStatusBehavior;
import software.sava.idl.clients.kamino.scope.gen.types.OracleType;

import java.util.Set;

public record ChainlinkRWA(PublicKey oracle,
                           MarketStatusBehavior marketStatusBehavior,
                           Set<EmaType> emaTypes) implements ChainlinkStatusEntry {

  @Override
  public OracleType oracleType() {
    return OracleType.ChainlinkRWA;
  }
}

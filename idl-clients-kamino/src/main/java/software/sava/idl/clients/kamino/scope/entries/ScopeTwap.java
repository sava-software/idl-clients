package software.sava.idl.clients.kamino.scope.entries;

import software.sava.idl.clients.kamino.scope.gen.types.OracleType;

public record ScopeTwap(ScopeEntry sourceEntry, boolean enabled) implements ScopeEntry {

  @Override
  public OracleType oracleType() {
    return OracleType.ScopeTwap;
  }
}

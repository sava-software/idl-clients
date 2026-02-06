package software.sava.idl.clients.kamino.scope.entries;

import software.sava.idl.clients.kamino.scope.gen.types.OracleType;

public record Deprecated(int index, OracleType oracleType) implements ScopeEntry {

  @Override
  public OracleType oracleType() {
    return oracleType;
  }
}

package software.sava.idl.clients.kamino.scope.entries;

import software.sava.idl.clients.kamino.scope.gen.types.OracleType;

public record Unused() implements ScopeEntry {

  static final Unused INSTANCE = new Unused();

  @Override
  public OracleType oracleType() {
    return OracleType.Unused;
  }
}

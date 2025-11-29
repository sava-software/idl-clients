package software.sava.idl.clients.kamino.scope.entries;

import software.sava.idl.clients.kamino.scope.gen.types.OracleType;

public record Deprecated() implements ScopeEntry {

  public static final Deprecated INSTANCE = new Deprecated();

  @Override
  public OracleType oracleType() {
    return null;
  }
}

package software.sava.idl.clients.kamino.scope.entries;

import software.sava.idl.clients.kamino.scope.gen.types.OracleType;

public record CappedFloored(int index,
                             ScopeEntry sourceEntry,
                            ScopeEntry capEntry,
                            ScopeEntry flooredEntry) implements ScopeEntry {

  @Override
  public OracleType oracleType() {
    return OracleType.CappedFloored;
  }
}

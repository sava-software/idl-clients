package software.sava.idl.clients.kamino.scope.entries;

import software.sava.idl.clients.kamino.scope.gen.types.OracleType;

public record MultiplicationChain(int index,
                                  ScopeEntry[] sourceEntries,
                                  long sourcesMaxAgeS) implements ScopeEntry {

  @Override
  public OracleType oracleType() {
    return OracleType.MultiplicationChain;
  }
}

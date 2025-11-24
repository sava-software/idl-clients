package software.sava.idl.clients.kamino.scope.entries;

import software.sava.idl.clients.kamino.scope.gen.types.OracleType;

public record MostRecentOf(ScopeEntry[] sources,
                           int maxDivergenceBps,
                           long sourcesMaxAgeS) implements ScopeEntry {

  @Override
  public OracleType oracleType() {
    return OracleType.MostRecentOf;
  }
}

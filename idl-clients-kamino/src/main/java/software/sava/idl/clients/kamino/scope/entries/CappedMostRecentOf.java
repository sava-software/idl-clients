package software.sava.idl.clients.kamino.scope.entries;

import software.sava.idl.clients.kamino.scope.gen.types.OracleType;

public record CappedMostRecentOf(ScopeEntry[] sources,
                                 int maxDivergenceBps,
                                 long sourcesMaxAgeS,
                                 ScopeEntry capEntry) implements MostRecentOf {

  @Override
  public OracleType oracleType() {
    return OracleType.CappedMostRecentOf;
  }
}

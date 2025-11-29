package software.sava.idl.clients.kamino.scope.entries;

import software.sava.idl.clients.kamino.scope.gen.types.OracleType;

public record MostRecentOfRecord(ScopeEntry[] sources,
                                 int maxDivergenceBps,
                                 long sourcesMaxAgeS,
                                 ScopeEntry refPrice) implements MostRecentOf {

  @Override
  public OracleType oracleType() {
    return OracleType.MostRecentOf;
  }
}

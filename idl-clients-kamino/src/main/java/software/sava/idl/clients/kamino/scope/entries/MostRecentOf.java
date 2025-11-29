package software.sava.idl.clients.kamino.scope.entries;

public sealed interface MostRecentOf extends ScopeEntry permits MostRecentOfRecord, CappedMostRecentOf {

  ScopeEntry[] sources();

  int maxDivergenceBps();

  long sourcesMaxAgeS();
}

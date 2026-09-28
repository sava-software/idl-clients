package software.sava.idl.clients.kamino.scope.entries;

public sealed interface MostRecentOf extends ScopeEntry permits MostRecentOfEntry, CappedMostRecentOf {

  ScopeEntry[] sources();

  int maxDivergenceBps();

  /// The age the refresh allows each source to have. For [CappedMostRecentOf] it bounds
  /// the cap entry as well since Scope 0.42.0, which rejects a stale cap
  /// (`CompositeOracleMaxAgeViolated`) where it used to apply it.
  long sourcesMaxAgeS();
}

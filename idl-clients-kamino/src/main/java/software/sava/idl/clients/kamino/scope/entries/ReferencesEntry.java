package software.sava.idl.clients.kamino.scope.entries;

public sealed interface ReferencesEntry
    extends OracleEntry
    permits PythLazer, Chainlink, PythPull, Securitize {

  ScopeEntry refPrice();
}

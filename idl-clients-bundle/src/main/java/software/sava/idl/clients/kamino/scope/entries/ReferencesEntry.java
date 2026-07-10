package software.sava.idl.clients.kamino.scope.entries;

import java.util.OptionalInt;

public sealed interface ReferencesEntry
    extends OracleEntry
    permits PythLazer, Chainlink, PythPull, Securitize {

  ScopeEntry refPrice();

  /// Max divergence from [#refPrice()] tolerated on refresh, in bps.
  /// Empty if no ref price is configured or no tolerance is set.
  OptionalInt refPriceToleranceBps();
}

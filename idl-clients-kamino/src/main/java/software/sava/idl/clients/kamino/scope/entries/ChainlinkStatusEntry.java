package software.sava.idl.clients.kamino.scope.entries;

import software.sava.idl.clients.kamino.scope.gen.types.MarketStatusBehavior;

public sealed interface ChainlinkStatusEntry extends OracleEntry, ScopeEntry permits ChainlinkRWA, ChainlinkX {

  MarketStatusBehavior marketStatusBehavior();
}

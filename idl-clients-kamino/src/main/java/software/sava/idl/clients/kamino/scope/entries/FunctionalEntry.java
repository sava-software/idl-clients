package software.sava.idl.clients.kamino.scope.entries;

import software.sava.core.accounts.PublicKey;

public sealed interface FunctionalEntry extends ScopeEntry permits SplBalance, SplStake, StakedSolBalance {

  PublicKey priceAccount();
}

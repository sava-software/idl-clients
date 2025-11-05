package software.sava.idl.clients.kamino;

import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.idl.clients.kamino.scope.ScopeFeedAccounts;

public record KaminoAccountsRecord(AccountMeta invokedKLendProgram,
                                   PublicKey scopePricesProgram,
                                   ScopeFeedAccounts scopeMainnetHubbleFeed,
                                   ScopeFeedAccounts scopeMainnetKLendFeed,
                                   PublicKey farmProgram,
                                   PublicKey farmsGlobalConfig,
                                   AccountMeta invokedKVaultsProgram,
                                   PublicKey kVaultsEventAuthority) implements KaminoAccounts {


}

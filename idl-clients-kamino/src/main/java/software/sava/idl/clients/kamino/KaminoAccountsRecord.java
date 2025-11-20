package software.sava.idl.clients.kamino;

import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.idl.clients.kamino.scope.ScopeFeedAccounts;

import java.util.Map;

record KaminoAccountsRecord(AccountMeta invokedKLendProgram,
                            PublicKey mainMarketLUT,
                            PublicKey scopePricesProgram,
                            ScopeFeedAccounts scopeMainnetHubbleFeed,
                            ScopeFeedAccounts scopeMainnetKLendFeed,
                            Map<PublicKey, ScopeFeedAccounts> scopeFeeds,
                            PublicKey farmProgram,
                            PublicKey farmsGlobalConfig,
                            AccountMeta invokedKVaultsProgram,
                            PublicKey kVaultsEventAuthority) implements KaminoAccounts {


  @Override
  public ScopeFeedAccounts scopeFeed(final PublicKey priceFeed) {
    return scopeFeeds.get(priceFeed);
  }
}

package software.sava.idl.clients.kamino.scope.entries;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.kamino.lend.gen.types.Reserve;
import software.sava.idl.clients.kamino.lend.gen.types.ScopeConfiguration;

public interface ScopeEntries {

  PublicKey pubKey();

  long slot();

  PriceChains readPriceChains(final Reserve reserve);

  PriceChains readPriceChains(final PublicKey mintKey, final ScopeConfiguration scopeConfiguration);

  ScopeEntry scopeEntry(final int index);

  int numEntries();
}

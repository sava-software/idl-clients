package software.sava.idl.clients.kamino.scope.entries;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.kamino.lend.gen.types.Reserve;

public interface ScopeEntries {

  PublicKey pubKey();

  long slot();

  PriceChains readPriceChains(final Reserve reserve);

  ScopeEntry scopeEntry(final int index);

  int numEntries();
}

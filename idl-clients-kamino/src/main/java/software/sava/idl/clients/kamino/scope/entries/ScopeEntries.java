package software.sava.idl.clients.kamino.scope.entries;

import software.sava.idl.clients.kamino.lend.gen.types.Reserve;

public interface ScopeEntries {

  PriceChains readPriceChains(final Reserve reserve);

  ScopeEntry scopeEntry(final int index);

  int numEntries();
}

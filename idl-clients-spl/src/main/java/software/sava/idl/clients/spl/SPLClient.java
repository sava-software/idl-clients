package software.sava.idl.clients.spl;

import software.sava.core.accounts.SolanaAccounts;

public interface SPLClient {

  static SPLClient create(final SolanaAccounts solanaAccounts) {
    return new SPLClientImpl(solanaAccounts);
  }

  SolanaAccounts solanaAccounts();
}

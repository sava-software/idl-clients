package software.sava.idl.clients.spl;

import software.sava.core.accounts.SolanaAccounts;

final class SPLClientImpl implements SPLClient {

  private final SolanaAccounts solanaAccounts;

  SPLClientImpl(final SolanaAccounts solanaAccounts) {
    this.solanaAccounts = solanaAccounts;
  }

  @Override
  public SolanaAccounts solanaAccounts() {
    return solanaAccounts;
  }
}

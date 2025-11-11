package software.sava.idl.clients.spl;

import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.spl.token.gen.TokenProgram;

record SPLClientImpl(SolanaAccounts solanaAccounts) implements SPLClient {

  @Override
  public SPLAccountClient createAccountClient(final SolanaAccounts accounts,
                                              final PublicKey owner,
                                              final AccountMeta feePayer) {
    return new SPLAccountClientImpl(this, owner, feePayer);
  }

  @Override
  public Instruction syncNative(final PublicKey tokenAccount) {
    return TokenProgram.syncNative(solanaAccounts.invokedTokenProgram(), tokenAccount);
  }
}

package software.sava.idl.clients.spl;

import software.sava.core.accounts.ProgramDerivedAddress;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.tx.Instruction;

public interface SPLClient {

  static SPLClient createClient(final SolanaAccounts solanaAccounts) {
    return new SPLClientImpl(solanaAccounts);
  }

  SolanaAccounts solanaAccounts();

  SPLAccountClient createAccountClient(final SolanaAccounts accounts,
                                       final PublicKey owner,
                                       final AccountMeta feePayer);

  Instruction syncNative(final PublicKey tokenAccount);

  ProgramDerivedAddress findATA(final PublicKey owner, final PublicKey tokenProgram, final PublicKey mint);

  default ProgramDerivedAddress findATA(final PublicKey owner, final PublicKey mint) {
    return findATA(owner, solanaAccounts().tokenProgram(), mint);
  }

  default ProgramDerivedAddress find2022ATA(final PublicKey owner, final PublicKey mint) {
    return findATA(owner, solanaAccounts().token2022Program(), mint);
  }
}

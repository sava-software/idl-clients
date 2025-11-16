package software.sava.idl.clients.spl;

import software.sava.core.accounts.ProgramDerivedAddress;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.tx.Instruction;

import java.util.List;

public interface SPLAccountClient {

  static SPLAccountClient createClient(final SolanaAccounts accounts,
                                       final PublicKey owner,
                                       final AccountMeta feePayer) {
    final var splClient = SPLClient.createClient(accounts);
    return new SPLAccountClientImpl(splClient, owner, feePayer);
  }

  SolanaAccounts solanaAccounts();

  SPLClient splClient();

  AccountMeta feePayer();

  ProgramDerivedAddress wrappedSolPDA();

  Instruction syncNative();

  List<Instruction> wrapSOL(final long lamports);

  Instruction unwrapSOL();

  ProgramDerivedAddress findATA(final PublicKey tokenProgram, final PublicKey mint);

  Instruction transferSolLamports(final PublicKey toPublicKey, final long lamports);


  Instruction transferTokenChecked(final AccountMeta invokedTokenProgram,
                                   final PublicKey fromTokenAccount,
                                   final PublicKey toTokenAccount,
                                   final long scaledAmount,
                                   final int decimals,
                                   final PublicKey tokenMint);

  Instruction closeTokenAccount(final AccountMeta invokedTokenProgram, final PublicKey tokenAccount);

  Instruction createATAForOwnerFundedByFeePayer(final boolean idempotent,
                                                final PublicKey ata,
                                                final PublicKey mint,
                                                final PublicKey tokenProgram);
}

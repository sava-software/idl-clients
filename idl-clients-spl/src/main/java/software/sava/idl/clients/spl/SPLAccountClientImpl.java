package software.sava.idl.clients.spl;

import software.sava.core.accounts.ProgramDerivedAddress;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.spl.associated_token.gen.AssociatedTokenProgram;
import software.sava.idl.clients.spl.system.gen.SystemProgram;
import software.sava.idl.clients.spl.token.gen.TokenProgram;

import java.util.List;

final class SPLAccountClientImpl implements SPLAccountClient {

  private final SPLClient splClient;
  private final PublicKey owner;
  private final AccountMeta feePayer;
  private final ProgramDerivedAddress wrappedSolPDA;

  SPLAccountClientImpl(final SPLClient splClient, final PublicKey owner, final AccountMeta feePayer) {
    this.splClient = splClient;
    this.owner = owner;
    this.feePayer = feePayer;
    final var solanaAccounts = splClient.solanaAccounts();
    this.wrappedSolPDA = findATA(solanaAccounts.tokenProgram(), solanaAccounts.wrappedSolTokenMint());
  }

  @Override
  public SolanaAccounts solanaAccounts() {
    return splClient.solanaAccounts();
  }

  @Override
  public SPLClient splClient() {
    return splClient;
  }

  @Override
  public PublicKey owner() {
    return owner;
  }

  @Override
  public AccountMeta feePayer() {
    return feePayer;
  }

  @Override
  public ProgramDerivedAddress wrappedSolPDA() {
    return wrappedSolPDA;
  }

  @Override
  public Instruction syncNative() {
    return splClient.syncNative(wrappedSolPDA.publicKey());
  }

  @Override
  public Instruction transferSolLamports(final PublicKey toPublicKey, final long lamports) {
    return SystemProgram.transferSol(
        solanaAccounts().invokedSystemProgram(),
        owner,
        toPublicKey,
        lamports
    );
  }

  @Override
  public List<Instruction> wrapSOL(final long lamports) {
    final var wrappedSolKey = wrappedSolPDA.publicKey();
    final var transferIx = transferSolLamports(wrappedSolKey, lamports);
    final var syncNativeIx = splClient.syncNative(wrappedSolKey);
    return List.of(transferIx, syncNativeIx);
  }

  @Override
  public Instruction unwrapSOL() {
    return closeTokenAccount(solanaAccounts().invokedTokenProgram(), wrappedSolPDA.publicKey());
  }

  @Override
  public Instruction transferTokenChecked(final AccountMeta invokedTokenProgram,
                                          final PublicKey fromTokenAccount,
                                          final PublicKey toTokenAccount,
                                          final long scaledAmount,
                                          final int decimals,
                                          final PublicKey tokenMint) {
    return TokenProgram.transferChecked(
        invokedTokenProgram,
        fromTokenAccount,
        tokenMint,
        toTokenAccount,
        owner,
        scaledAmount,
        decimals
    );
  }

  @Override
  public Instruction closeTokenAccount(final AccountMeta invokedTokenProgram, final PublicKey tokenAccount) {
    return TokenProgram.closeAccount(
        invokedTokenProgram,
        tokenAccount,
        owner,
        owner
    );
  }

  @Override
  public Instruction createATAForOwnerFundedByFeePayer(final boolean idempotent,
                                                       final PublicKey ata,
                                                       final PublicKey mint,
                                                       final PublicKey tokenProgram) {
    final var solanaAccounts = splClient.solanaAccounts();
    if (idempotent) {
      return AssociatedTokenProgram.createAssociatedTokenIdempotent(
          solanaAccounts.invokedAssociatedTokenAccountProgram(),
          solanaAccounts,
          feePayer.publicKey(),
          ata,
          owner,
          mint,
          tokenProgram
      );
    } else {
      return AssociatedTokenProgram.createAssociatedToken(
          solanaAccounts.invokedAssociatedTokenAccountProgram(),
          solanaAccounts,
          feePayer.publicKey(),
          ata,
          owner,
          mint,
          tokenProgram
      );
    }
  }
}

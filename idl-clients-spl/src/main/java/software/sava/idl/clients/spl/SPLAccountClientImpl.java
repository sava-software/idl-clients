package software.sava.idl.clients.spl;

import software.sava.core.accounts.ProgramDerivedAddress;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.spl.associated_token.gen.AssociatedTokenPDAs;
import software.sava.idl.clients.spl.associated_token.gen.AssociatedTokenProgram;
import software.sava.idl.clients.spl.token.gen.TokenProgram;
import software.sava.solana.programs.system.SystemProgram;

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
  public ProgramDerivedAddress wrappedSolPDA() {
    return wrappedSolPDA;
  }

  @Override
  public ProgramDerivedAddress findATA(final PublicKey tokenProgram, final PublicKey mint) {
    return AssociatedTokenPDAs.associatedTokenPDA(
        solanaAccounts().associatedTokenAccountProgram(),
        owner,
        tokenProgram,
        mint
    );
  }

  @Override
  public Instruction transferSolLamports(final PublicKey toPublicKey, final long lamports) {
    return SystemProgram.transfer(
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

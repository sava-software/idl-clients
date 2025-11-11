package software.sava.idl.clients.spl;

import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.spl.associated_token.gen.AssociatedTokenProgram;

final class SPLAccountClientImpl implements SPLAccountClient {

  private final SPLClient splClient;
  private final PublicKey owner;
  private final AccountMeta feePayer;

  SPLAccountClientImpl(final SPLClient splClient, final PublicKey owner, final AccountMeta feePayer) {
    this.splClient = splClient;
    this.owner = owner;
    this.feePayer = feePayer;
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

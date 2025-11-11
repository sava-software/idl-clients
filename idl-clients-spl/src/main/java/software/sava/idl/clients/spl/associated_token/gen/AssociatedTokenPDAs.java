package software.sava.idl.clients.spl.associated_token.gen;

import java.util.List;

import software.sava.core.accounts.ProgramDerivedAddress;
import software.sava.core.accounts.PublicKey;

public final class AssociatedTokenPDAs {

  ///
  /// @param owner The wallet address of the associated token account.
  /// @param tokenProgram The address of the token program to use.
  /// @param mint The mint address of the associated token account.
  public static ProgramDerivedAddress associatedTokenPDA(final PublicKey programOwner,
                                                         final PublicKey owner,
                                                         final PublicKey tokenProgram,
                                                         final PublicKey mint) {
    return PublicKey.findProgramAddress(List.of(
      owner.toByteArray(),
      tokenProgram.toByteArray(),
      mint.toByteArray()
    ), programOwner);
  }

  private AssociatedTokenPDAs() {
  }
}

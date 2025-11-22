package software.sava.idl.clients.jupiter.lend_borrow.gen;

import java.util.List;

import software.sava.core.accounts.ProgramDerivedAddress;
import software.sava.core.accounts.PublicKey;

import static java.nio.charset.StandardCharsets.US_ASCII;

public final class LendingPDAs {

  public static ProgramDerivedAddress depositorTokenAccountPDA(final PublicKey program,
                                                               final PublicKey signerAccount,
                                                               final PublicKey tokenProgramAccount,
                                                               final PublicKey mintAccount) {
    return PublicKey.findProgramAddress(List.of(
      signerAccount.toByteArray(),
      tokenProgramAccount.toByteArray(),
      mintAccount.toByteArray()
    ), program);
  }

  public static ProgramDerivedAddress fTokenMintPDA(final PublicKey program,
                                                    final PublicKey mintAccount) {
    return PublicKey.findProgramAddress(List.of(
      "f_token_mint".getBytes(US_ASCII),
      mintAccount.toByteArray()
    ), program);
  }

  public static ProgramDerivedAddress lendingPDA(final PublicKey program,
                                                 final PublicKey mintAccount,
                                                 final PublicKey fTokenMintAccount) {
    return PublicKey.findProgramAddress(List.of(
      "lending".getBytes(US_ASCII),
      mintAccount.toByteArray(),
      fTokenMintAccount.toByteArray()
    ), program);
  }

  public static ProgramDerivedAddress lendingAdminPDA(final PublicKey program) {
    return PublicKey.findProgramAddress(List.of(
      "lending_admin".getBytes(US_ASCII)
    ), program);
  }

  public static ProgramDerivedAddress metadataAccountPDA(final PublicKey program,
                                                         final PublicKey metaqbxxUerdq28cj1RbAWkYQm3ybzjb6a8bt518x1s,
                                                         final PublicKey fTokenMintAccount) {
    return PublicKey.findProgramAddress(List.of(
      "metadata".getBytes(US_ASCII),
      metaqbxxUerdq28cj1RbAWkYQm3ybzjb6a8bt518x1s.toByteArray(),
      fTokenMintAccount.toByteArray()
    ), program);
  }

  private LendingPDAs() {
  }
}

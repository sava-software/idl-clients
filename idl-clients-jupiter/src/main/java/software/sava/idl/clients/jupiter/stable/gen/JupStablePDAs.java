package software.sava.idl.clients.jupiter.stable.gen;

import java.util.List;

import software.sava.core.accounts.ProgramDerivedAddress;
import software.sava.core.accounts.PublicKey;

import static java.nio.charset.StandardCharsets.US_ASCII;

public final class JupStablePDAs {

  public static ProgramDerivedAddress authorityPDA(final PublicKey program) {
    return PublicKey.findProgramAddress(List.of(
      "authority".getBytes(US_ASCII)
    ), program);
  }

  public static ProgramDerivedAddress benefactorPDA(final PublicKey program,
                                                    final PublicKey benefactorAuthorityAccount) {
    return PublicKey.findProgramAddress(List.of(
      "benefactor".getBytes(US_ASCII),
      benefactorAuthorityAccount.toByteArray()
    ), program);
  }

  public static ProgramDerivedAddress configPDA(final PublicKey program) {
    return PublicKey.findProgramAddress(List.of(
      "config".getBytes(US_ASCII)
    ), program);
  }

  public static ProgramDerivedAddress custodianTokenAccountPDA(final PublicKey program,
                                                               final PublicKey custodianAccount,
                                                               final PublicKey vaultTokenProgramAccount,
                                                               final PublicKey vaultMintAccount) {
    return PublicKey.findProgramAddress(List.of(
      custodianAccount.toByteArray(),
      vaultTokenProgramAccount.toByteArray(),
      vaultMintAccount.toByteArray()
    ), program);
  }

  public static ProgramDerivedAddress custodianTokenAccount1PDA(final PublicKey program,
                                                                final PublicKey custodianAccount,
                                                                final PublicKey tokenProgramAccount,
                                                                final PublicKey vaultMintAccount) {
    return PublicKey.findProgramAddress(List.of(
      custodianAccount.toByteArray(),
      tokenProgramAccount.toByteArray(),
      vaultMintAccount.toByteArray()
    ), program);
  }

  public static ProgramDerivedAddress eventAuthorityPDA(final PublicKey program) {
    return PublicKey.findProgramAddress(List.of(
      "__event_authority".getBytes(US_ASCII)
    ), program);
  }

  public static ProgramDerivedAddress newOperatorPDA(final PublicKey program,
                                                     final PublicKey newOperatorAuthorityAccount) {
    return PublicKey.findProgramAddress(List.of(
      "operator".getBytes(US_ASCII),
      newOperatorAuthorityAccount.toByteArray()
    ), program);
  }

  public static ProgramDerivedAddress operatorPDA(final PublicKey program,
                                                  final PublicKey upgradeAuthorityAccount) {
    return PublicKey.findProgramAddress(List.of(
      "operator".getBytes(US_ASCII),
      upgradeAuthorityAccount.toByteArray()
    ), program);
  }

  public static ProgramDerivedAddress tokenAccountPDA(final PublicKey program,
                                                      final PublicKey authorityAccount,
                                                      final PublicKey tokenProgramAccount,
                                                      final PublicKey mintAccount) {
    return PublicKey.findProgramAddress(List.of(
      authorityAccount.toByteArray(),
      tokenProgramAccount.toByteArray(),
      mintAccount.toByteArray()
    ), program);
  }

  public static ProgramDerivedAddress vaultPDA(final PublicKey program,
                                               final PublicKey mintAccount) {
    return PublicKey.findProgramAddress(List.of(
      "vault".getBytes(US_ASCII),
      mintAccount.toByteArray()
    ), program);
  }

  private JupStablePDAs() {
  }
}

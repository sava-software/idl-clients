package software.sava.idl.clients.oracles.switchboard.on_demand.gen;

import java.util.List;

import software.sava.core.accounts.ProgramDerivedAddress;
import software.sava.core.accounts.PublicKey;

import static java.nio.charset.StandardCharsets.US_ASCII;

public final class SbOnDemandPDAs {

  public static ProgramDerivedAddress oraclePDA(final PublicKey program,
                                                final byte[] paramsQueue,
                                                final byte[] paramsSourceOracleKey) {
    return PublicKey.findProgramAddress(List.of(
      "Oracle".getBytes(US_ASCII),
      paramsQueue,
      paramsSourceOracleKey
    ), program);
  }

  public static ProgramDerivedAddress oracleStatsPDA(final PublicKey program,
                                                     final PublicKey oracleAccount) {
    return PublicKey.findProgramAddress(List.of(
      "OracleStats".getBytes(US_ASCII),
      oracleAccount.toByteArray()
    ), program);
  }

  public static ProgramDerivedAddress queuePDA(final PublicKey program,
                                               final byte[] paramsSourceQueueKey) {
    return PublicKey.findProgramAddress(List.of(
      "Queue".getBytes(US_ASCII),
      paramsSourceQueueKey
    ), program);
  }

  public static ProgramDerivedAddress queueEscrowPDA(final PublicKey program,
                                                     final PublicKey queueAccount,
                                                     final PublicKey TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA,
                                                     final PublicKey nativeMintAccount) {
    return PublicKey.findProgramAddress(List.of(
      queueAccount.toByteArray(),
      TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA.toByteArray(),
      nativeMintAccount.toByteArray()
    ), program);
  }

  public static ProgramDerivedAddress rewardEscrowPDA(final PublicKey program,
                                                      final PublicKey pullFeedAccount,
                                                      final PublicKey TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA,
                                                      final PublicKey wrappedSolMintAccount) {
    return PublicKey.findProgramAddress(List.of(
      pullFeedAccount.toByteArray(),
      TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA.toByteArray(),
      wrappedSolMintAccount.toByteArray()
    ), program);
  }

  public static ProgramDerivedAddress rewardEscrow1PDA(final PublicKey program,
                                                       final PublicKey randomnessAccount,
                                                       final PublicKey TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA,
                                                       final PublicKey wrappedSolMintAccount) {
    return PublicKey.findProgramAddress(List.of(
      randomnessAccount.toByteArray(),
      TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA.toByteArray(),
      wrappedSolMintAccount.toByteArray()
    ), program);
  }

  public static ProgramDerivedAddress rewardVaultPDA(final PublicKey program,
                                                     final PublicKey vaultAccount,
                                                     final PublicKey TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA,
                                                     final PublicKey switchMintAccount) {
    return PublicKey.findProgramAddress(List.of(
      vaultAccount.toByteArray(),
      TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA.toByteArray(),
      switchMintAccount.toByteArray()
    ), program);
  }

  public static ProgramDerivedAddress statePDA(final PublicKey program) {
    return PublicKey.findProgramAddress(List.of(
      "STATE".getBytes(US_ASCII)
    ), program);
  }

  public static ProgramDerivedAddress statsPDA(final PublicKey program,
                                               final PublicKey oracleAccount) {
    return PublicKey.findProgramAddress(List.of(
      "OracleRandomnessStats".getBytes(US_ASCII),
      oracleAccount.toByteArray()
    ), program);
  }

  public static ProgramDerivedAddress subsidyVaultPDA(final PublicKey program,
                                                      final PublicKey programStateAccount,
                                                      final PublicKey TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA,
                                                      final PublicKey switchMintAccount) {
    return PublicKey.findProgramAddress(List.of(
      programStateAccount.toByteArray(),
      TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA.toByteArray(),
      switchMintAccount.toByteArray()
    ), program);
  }

  private SbOnDemandPDAs() {
  }
}

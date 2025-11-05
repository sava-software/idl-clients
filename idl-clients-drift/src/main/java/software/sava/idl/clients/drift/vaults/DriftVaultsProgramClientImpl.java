package software.sava.idl.clients.drift.vaults;

import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.drift.DriftAccounts;
import software.sava.idl.clients.drift.vaults.gen.DriftVaultsProgram;
import software.sava.idl.clients.drift.vaults.gen.types.WithdrawUnit;

record DriftVaultsProgramClientImpl(SolanaAccounts solanaAccounts,
                                    DriftAccounts driftAccounts,
                                    PublicKey authority,
                                    PublicKey feePayer) implements DriftVaultsProgramClient {

  @Override
  public PublicKey vaultDepositor(final PublicKey vaultKey) {
    return DriftVaultPDAs.vaultDepositorAddress(
        driftAccounts.driftVaultsProgram(),
        vaultKey,
        authority
    ).publicKey();
  }

  @Override
  public Instruction initializeVaultDepositor(final PublicKey vaultKey,
                                              final PublicKey vaultDepositorKey,
                                              final PublicKey authorityKey,
                                              final PublicKey payerKey) {
    return DriftVaultsProgram.initializeVaultDepositor(
        driftAccounts.invokedDriftVaultsProgram(),
        vaultKey,
        vaultDepositorKey,
        authorityKey,
        payerKey,
        solanaAccounts.rentSysVar(),
        solanaAccounts.systemProgram()
    );
  }

  @Override
  public Instruction deposit(final PublicKey vaultKey,
                             final PublicKey vaultDepositorKey,
                             final PublicKey authorityKey,
                             final PublicKey vaultTokenAccountKey,
                             final PublicKey driftUserStatsKey,
                             final PublicKey driftUserKey,
                             final PublicKey driftSpotMarketVaultKey,
                             final PublicKey userTokenAccountKey,
                             final PublicKey tokenProgramKey,
                             final long amount) {
    return DriftVaultsProgram.deposit(
        driftAccounts.invokedDriftVaultsProgram(),
        vaultKey,
        vaultDepositorKey,
        authorityKey,
        vaultTokenAccountKey,
        driftUserStatsKey,
        driftUserKey,
        driftAccounts.stateKey(),
        driftSpotMarketVaultKey,
        userTokenAccountKey,
        driftAccounts.driftProgram(),
        tokenProgramKey,
        amount
    );
  }

  @Override
  public Instruction requestWithdraw(final PublicKey vaultKey,
                                     final PublicKey vaultDepositorKey,
                                     final PublicKey authorityKey,
                                     final PublicKey driftUserStatsKey,
                                     final PublicKey driftUserKey,
                                     final long withdrawAmount,
                                     final WithdrawUnit withdrawUnit) {
    return DriftVaultsProgram.requestWithdraw(
        driftAccounts.invokedDriftVaultsProgram(),
        vaultKey,
        vaultDepositorKey,
        authorityKey,
        driftUserStatsKey,
        driftUserKey,
        withdrawAmount,
        withdrawUnit
    );
  }

  @Override
  public Instruction cancelRequestWithdraw(final PublicKey vaultKey,
                                           final PublicKey vaultDepositorKey,
                                           final PublicKey authorityKey,
                                           final PublicKey driftUserStatsKey,
                                           final PublicKey driftUserKey) {
    return DriftVaultsProgram.cancelRequestWithdraw(
        driftAccounts.invokedDriftVaultsProgram(),
        vaultKey,
        vaultDepositorKey,
        authorityKey,
        driftUserStatsKey,
        driftUserKey
    );
  }

  @Override
  public Instruction withdraw(final PublicKey vaultKey,
                              final PublicKey vaultDepositorKey,
                              final PublicKey authorityKey,
                              final PublicKey vaultTokenAccountKey,
                              final PublicKey driftUserStatsKey,
                              final PublicKey driftUserKey,
                              final PublicKey driftSpotMarketVaultKey,
                              final PublicKey userTokenAccountKey,
                              final PublicKey tokenProgramKey) {
    return DriftVaultsProgram.withdraw(
        driftAccounts.invokedDriftVaultsProgram(),
        vaultKey,
        vaultDepositorKey,
        authorityKey,
        vaultTokenAccountKey,
        driftUserStatsKey,
        driftUserKey,
        driftAccounts.stateKey(),
        driftSpotMarketVaultKey,
        driftAccounts.driftSignerPDA(),
        userTokenAccountKey,
        driftAccounts.driftProgram(),
        tokenProgramKey
    );
  }
}

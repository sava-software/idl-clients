package software.sava.idl.clients.drift.vaults;

import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.drift.DriftAccounts;
import software.sava.idl.clients.drift.vaults.gen.types.Vault;
import software.sava.idl.clients.drift.vaults.gen.types.WithdrawUnit;
import software.sava.idl.clients.spl.SPLAccountClient;

public interface DriftVaultsProgramClient {

  static DriftVaultsProgramClient createClient(final SPLAccountClient splAccountClient,
                                               final DriftAccounts driftAccounts) {
    return new DriftVaultsProgramClientImpl(
        splAccountClient.solanaAccounts(),
        driftAccounts,
        splAccountClient.owner(),
        splAccountClient.feePayer().publicKey()
    );
  }

  static DriftVaultsProgramClient createClient(final SPLAccountClient splAccountClient) {
    return createClient(splAccountClient, DriftAccounts.MAIN_NET);
  }

  SolanaAccounts solanaAccounts();

  DriftAccounts driftAccounts();

  PublicKey authority();

  PublicKey feePayer();

  PublicKey vaultDepositor(final PublicKey vaultKey);

  default PublicKey vaultDepositor(final Vault vault) {
    return vaultDepositor(vault._address());
  }

  Instruction initializeVaultDepositor(final PublicKey vaultKey,
                                       final PublicKey vaultDepositorKey,
                                       final PublicKey authorityKey,
                                       final PublicKey payerKey);

  default Instruction initializeVaultDepositor(final PublicKey vaultKey) {
    return initializeVaultDepositor(
        vaultKey,
        vaultDepositor(vaultKey),
        authority(),
        feePayer()
    );
  }

  Instruction deposit(final PublicKey vaultKey,
                      final PublicKey vaultDepositorKey,
                      final PublicKey authorityKey,
                      final PublicKey vaultTokenAccountKey,
                      final PublicKey driftUserStatsKey,
                      final PublicKey driftUserKey,
                      final PublicKey driftSpotMarketVaultKey,
                      final PublicKey userTokenAccountKey,
                      final PublicKey tokenProgramKey,
                      final long amount);

  default Instruction deposit(final Vault vault,
                              final PublicKey driftSpotMarketVaultKey,
                              final PublicKey userTokenAccountKey,
                              final PublicKey tokenProgramKey,
                              final long amount) {
    final var vaultKey = vault._address();
    return deposit(
        vaultKey,
        vaultDepositor(vaultKey),
        authority(),
        vault.tokenAccount(),
        vault.userStats(),
        vault.user(),
        driftSpotMarketVaultKey,
        userTokenAccountKey,
        tokenProgramKey,
        amount
    );
  }

  Instruction requestWithdraw(final PublicKey vaultKey,
                              final PublicKey vaultDepositorKey,
                              final PublicKey authorityKey,
                              final PublicKey driftUserStatsKey,
                              final PublicKey driftUserKey,
                              final long withdrawAmount,
                              final WithdrawUnit withdrawUnit);

  default Instruction requestWithdraw(final Vault vault, final long withdrawAmount, final WithdrawUnit withdrawUnit) {
    final var vaultKey = vault._address();
    return requestWithdraw(
        vaultKey,
        vaultDepositor(vaultKey),
        authority(),
        vault.userStats(),
        vault.user(),
        withdrawAmount,
        withdrawUnit
    );
  }

  Instruction cancelRequestWithdraw(final PublicKey vaultKey,
                                    final PublicKey vaultDepositorKey,
                                    final PublicKey authorityKey,
                                    final PublicKey driftUserStatsKey,
                                    final PublicKey driftUserKey);

  default Instruction cancelRequestWithdraw(final Vault vault) {
    final var vaultKey = vault._address();
    return cancelRequestWithdraw(
        vaultKey,
        vaultDepositor(vaultKey),
        authority(),
        vault.userStats(),
        vault.user()
    );
  }

  Instruction withdraw(final PublicKey vaultKey,
                       final PublicKey vaultDepositorKey,
                       final PublicKey authorityKey,
                       final PublicKey vaultTokenAccountKey,
                       final PublicKey driftUserStatsKey,
                       final PublicKey driftUserKey,
                       final PublicKey driftSpotMarketVaultKey,
                       final PublicKey userTokenAccountKey,
                       final PublicKey tokenProgramKey);

  default Instruction withdraw(final Vault vault,
                               final PublicKey driftSpotMarketVaultKey,
                               final PublicKey userTokenAccountKey,
                               final PublicKey tokenProgramKey) {
    final var vaultKey = vault._address();
    return withdraw(
        vaultKey,
        vaultDepositor(vaultKey),
        authority(),
        vault.tokenAccount(),
        vault.userStats(),
        vault.user(),
        driftSpotMarketVaultKey,
        userTokenAccountKey,
        tokenProgramKey
    );
  }
}

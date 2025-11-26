package software.sava.idl.clients.kamino.vaults;

import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.kamino.KaminoAccounts;
import software.sava.idl.clients.kamino.vaults.gen.KaminoVaultProgram;
import software.sava.idl.clients.spl.SPLAccountClient;

public class KaminoVaultsClientImpl implements KaminoVaultsClient {

  private final SPLAccountClient splAccountClient;
  private final SolanaAccounts solanaAccounts;
  private final KaminoAccounts kaminoAccounts;
  private final PublicKey withdrawFromAvailableGlobalConfigKey;
  private final PublicKey owner;
  private final PublicKey feePayer;

  KaminoVaultsClientImpl(final SPLAccountClient SPLAccountClient, final KaminoAccounts kaminoAccounts) {
    this.splAccountClient = SPLAccountClient;
    this.solanaAccounts = SPLAccountClient.solanaAccounts();
    this.kaminoAccounts = kaminoAccounts;
    this.owner = SPLAccountClient.owner();
    this.feePayer = SPLAccountClient.feePayer().publicKey();
    this.withdrawFromAvailableGlobalConfigKey = kaminoAccounts.kVaultGlobalConfig().publicKey();
  }

  @Override
  public SPLAccountClient splAccountClient() {
    return splAccountClient;
  }

  @Override
  public SolanaAccounts solanaAccounts() {
    return solanaAccounts;
  }

  @Override
  public KaminoAccounts kaminoAccounts() {
    return kaminoAccounts;
  }

  @Override
  public PublicKey user() {
    return owner;
  }

  @Override
  public PublicKey feePayer() {
    return feePayer;
  }

  @Override
  public Instruction deposit(final PublicKey vaultStateKey,
                             final PublicKey tokenVaultKey,
                             final PublicKey tokenMintKey,
                             final PublicKey baseVaultAuthorityKey,
                             final PublicKey sharesMintKey,
                             final PublicKey userTokenAtaKey,
                             final PublicKey userSharesAtaKey,
                             final PublicKey tokenProgramKey,
                             final PublicKey sharesTokenProgramKey,
                             final long maxAmount) {
    return KaminoVaultProgram.deposit(
        kaminoAccounts.invokedKVaultsProgram(),
        owner,
        vaultStateKey,
        tokenVaultKey,
        tokenMintKey,
        baseVaultAuthorityKey,
        sharesMintKey,
        userTokenAtaKey,
        userSharesAtaKey,
        kaminoAccounts.kLendProgram(),
        tokenProgramKey,
        sharesTokenProgramKey,
        kaminoAccounts.kVaultsEventAuthority(),
        kaminoAccounts.kVaultsProgram(),
        maxAmount
    );
  }

  @Override
  public Instruction withdraw(final PublicKey withdrawFromAvailableVaultStateKey,
                              final PublicKey withdrawFromAvailableTokenVaultKey,
                              final PublicKey withdrawFromAvailableBaseVaultAuthorityKey,
                              final PublicKey withdrawFromAvailableUserTokenAtaKey,
                              final PublicKey withdrawFromAvailableTokenMintKey,
                              final PublicKey withdrawFromAvailableUserSharesAtaKey,
                              final PublicKey withdrawFromAvailableSharesMintKey,
                              final PublicKey withdrawFromAvailableTokenProgramKey,
                              final PublicKey withdrawFromAvailableSharesTokenProgramKey,
                              final PublicKey withdrawFromReserveAccountsReserveKey,
                              final PublicKey withdrawFromReserveAccountsCtokenVaultKey,
                              final PublicKey withdrawFromReserveAccountsLendingMarketKey,
                              final PublicKey withdrawFromReserveAccountsLendingMarketAuthorityKey,
                              final PublicKey withdrawFromReserveAccountsReserveLiquiditySupplyKey,
                              final PublicKey withdrawFromReserveAccountsReserveCollateralMintKey,
                              final PublicKey withdrawFromReserveAccountsReserveCollateralTokenProgramKey,
                              final long sharesAmount) {
    return KaminoVaultProgram.withdraw(
        kaminoAccounts.invokedKVaultsProgram(),
        owner,
        withdrawFromAvailableVaultStateKey,
        withdrawFromAvailableGlobalConfigKey,
        withdrawFromAvailableTokenVaultKey,
        withdrawFromAvailableBaseVaultAuthorityKey,
        withdrawFromAvailableUserTokenAtaKey,
        withdrawFromAvailableTokenMintKey,
        withdrawFromAvailableUserSharesAtaKey,
        withdrawFromAvailableSharesMintKey,
        withdrawFromAvailableTokenProgramKey,
        withdrawFromAvailableSharesTokenProgramKey,
        kaminoAccounts.kLendProgram(),
        kaminoAccounts.kVaultsEventAuthority(),
        kaminoAccounts.kVaultsProgram(),
        withdrawFromAvailableVaultStateKey,
        withdrawFromReserveAccountsReserveKey,
        withdrawFromReserveAccountsCtokenVaultKey,
        withdrawFromReserveAccountsLendingMarketKey,
        withdrawFromReserveAccountsLendingMarketAuthorityKey,
        withdrawFromReserveAccountsReserveLiquiditySupplyKey,
        withdrawFromReserveAccountsReserveCollateralMintKey,
        withdrawFromReserveAccountsReserveCollateralTokenProgramKey,
        solanaAccounts.instructionsSysVar(),
        kaminoAccounts.kVaultsEventAuthority(),
        kaminoAccounts.kVaultsProgram(),
        sharesAmount
    );
  }
}

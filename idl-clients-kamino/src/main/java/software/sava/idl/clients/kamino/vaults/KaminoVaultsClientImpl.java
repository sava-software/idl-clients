package software.sava.idl.clients.kamino.vaults;

import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.kamino.KaminoAccounts;
import software.sava.idl.clients.kamino.vaults.gen.KaminoVaultProgram;
import software.sava.idl.clients.spl.SPLAccountClient;

final class KaminoVaultsClientImpl implements KaminoVaultsClient {

  private final SPLAccountClient splAccountClient;
  private final SolanaAccounts solanaAccounts;
  private final KaminoAccounts kaminoAccounts;
  private final PublicKey owner;
  private final PublicKey feePayer;

  KaminoVaultsClientImpl(final SPLAccountClient splAccountClient, final KaminoAccounts kaminoAccounts) {
    this.splAccountClient = splAccountClient;
    this.solanaAccounts = splAccountClient.solanaAccounts();
    this.kaminoAccounts = kaminoAccounts;
    this.owner = splAccountClient.owner();
    this.feePayer = splAccountClient.feePayer().publicKey();
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
  public Instruction withdraw(final PublicKey vaultStateKey,
                              final PublicKey tokenVaultKey,
                              final PublicKey baseVaultAuthorityKey,
                              final PublicKey userTokenAtaKey,
                              final PublicKey tokenMintKey,
                              final PublicKey userSharesAtaKey,
                              final PublicKey sharesMintKey,
                              final PublicKey tokenProgramKey,
                              final PublicKey sharesTokenProgramKey,
                              final PublicKey reserveKey,
                              final PublicKey ctokenVaultKey,
                              final PublicKey lendingMarketKey,
                              final PublicKey lendingMarketAuthorityKey,
                              final PublicKey reserveLiquiditySupplyKey,
                              final PublicKey reserveCollateralMintKey,
                              final PublicKey reserveCollateralTokenProgramKey,
                              final long sharesAmount) {
    return KaminoVaultProgram.withdraw(
        kaminoAccounts.invokedKVaultsProgram(),
        owner,
        vaultStateKey,
        kaminoAccounts.kVaultGlobalConfig().publicKey(),
        tokenVaultKey,
        baseVaultAuthorityKey,
        userTokenAtaKey,
        tokenMintKey,
        userSharesAtaKey,
        sharesMintKey,
        tokenProgramKey,
        sharesTokenProgramKey,
        kaminoAccounts.kLendProgram(),
        kaminoAccounts.kVaultsEventAuthority(),
        kaminoAccounts.kVaultsProgram(),
        vaultStateKey,
        reserveKey,
        ctokenVaultKey,
        lendingMarketKey,
        lendingMarketAuthorityKey,
        reserveLiquiditySupplyKey,
        reserveCollateralMintKey,
        reserveCollateralTokenProgramKey,
        solanaAccounts.instructionsSysVar(),
        kaminoAccounts.kVaultsEventAuthority(),
        kaminoAccounts.kVaultsProgram(),
        sharesAmount
    );
  }

  @Override
  public Instruction withdrawFromAvailable(final PublicKey vaultStateKey,
                                           final PublicKey tokenVaultKey,
                                           final PublicKey baseVaultAuthorityKey,
                                           final PublicKey userTokenAtaKey,
                                           final PublicKey tokenMintKey,
                                           final PublicKey userSharesAtaKey,
                                           final PublicKey sharesMintKey,
                                           final PublicKey tokenProgramKey,
                                           final PublicKey sharesTokenProgramKey,
                                           final long sharesAmount) {
    return KaminoVaultProgram.withdrawFromAvailable(
        kaminoAccounts.invokedKVaultsProgram(),
        owner,
        vaultStateKey,
        kaminoAccounts.kVaultGlobalConfig().publicKey(),
        tokenVaultKey,
        baseVaultAuthorityKey,
        userTokenAtaKey,
        tokenMintKey,
        userSharesAtaKey,
        sharesMintKey,
        tokenProgramKey,
        sharesTokenProgramKey,
        kaminoAccounts.kLendProgram(),
        kaminoAccounts.kVaultsEventAuthority(),
        kaminoAccounts.kVaultsProgram(),
        sharesAmount
    );
  }

  @Override
  public Instruction redeemInKind(final PublicKey vaultStateKey,
                                  final PublicKey baseVaultAuthorityKey,
                                  final PublicKey reserveKey,
                                  final PublicKey ctokenVaultKey,
                                  final PublicKey userCtokenTaKey,
                                  final PublicKey ctokenMintKey,
                                  final PublicKey userSharesTaKey,
                                  final PublicKey sharesMintKey,
                                  final PublicKey reserveCollateralTokenProgramKey,
                                  final PublicKey sharesTokenProgramKey,
                                  final long sharesAmount) {
    return KaminoVaultProgram.redeemInKind(
        kaminoAccounts.invokedKVaultsProgram(),
        owner,
        vaultStateKey,
        kaminoAccounts.kVaultGlobalConfig().publicKey(),
        baseVaultAuthorityKey,
        reserveKey,
        ctokenVaultKey,
        userCtokenTaKey,
        ctokenMintKey,
        userSharesTaKey,
        sharesMintKey,
        reserveCollateralTokenProgramKey,
        sharesTokenProgramKey,
        kaminoAccounts.kLendProgram(),
        kaminoAccounts.kVaultsEventAuthority(),
        kaminoAccounts.kVaultsProgram(),
        sharesAmount
    );
  }
}

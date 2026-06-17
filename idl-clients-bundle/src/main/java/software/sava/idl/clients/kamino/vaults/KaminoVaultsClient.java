package software.sava.idl.clients.kamino.vaults;

import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.kamino.KaminoAccounts;
import software.sava.idl.clients.kamino.lend.gen.types.Reserve;
import software.sava.idl.clients.kamino.vaults.gen.types.VaultState;
import software.sava.idl.clients.spl.SPLAccountClient;

public interface KaminoVaultsClient {

  static KaminoVaultsClientImpl createClient(final SPLAccountClient splAccountClient,
                                             final KaminoAccounts kaminoAccounts) {
    return new KaminoVaultsClientImpl(splAccountClient, kaminoAccounts);
  }

  static KaminoVaultsClientImpl createClient(final SPLAccountClient splAccountClient) {
    return createClient(splAccountClient, KaminoAccounts.MAIN_NET);
  }

  SPLAccountClient splAccountClient();

  SolanaAccounts solanaAccounts();

  KaminoAccounts kaminoAccounts();

  PublicKey user();

  PublicKey feePayer();

  /// Deposit `maxAmount` of the vault's underlying token in exchange for shares.
  ///
  /// The handler iterates `ctx.remaining_accounts` for the vault's reserves
  /// (in allocation order) to refresh them via klend before computing the
  /// share price; callers must append those reserves via
  /// `KaminoVaultsRemainingAccounts.appendVaultReserves(instruction, reserves)`.
  Instruction deposit(final PublicKey vaultStateKey,
                      final PublicKey tokenVaultKey,
                      final PublicKey tokenMintKey,
                      final PublicKey baseVaultAuthorityKey,
                      final PublicKey sharesMintKey,
                      final PublicKey userTokenAtaKey,
                      final PublicKey userSharesAtaKey,
                      final PublicKey tokenProgramKey,
                      final PublicKey sharesTokenProgramKey,
                      final long maxAmount);

  default Instruction deposit(final VaultState vaultState,
                              final PublicKey userTokenAtaKey,
                              final PublicKey userSharesAtaKey,
                              final PublicKey sharesTokenProgramKey,
                              final long maxAmount) {

    return deposit(
        vaultState._address(),
        vaultState.tokenVault(),
        vaultState.tokenMint(),
        vaultState.baseVaultAuthority(),
        vaultState.sharesMint(),
        userTokenAtaKey,
        userSharesAtaKey,
        vaultState.tokenProgram(),
        sharesTokenProgramKey,
        maxAmount
    );
  }

  default Instruction deposit(final VaultState vaultState,
                              final PublicKey sharesTokenProgramKey,
                              final long maxAmount) {
    final var tokenMint = vaultState.tokenMint();
    final var tokenProgram = vaultState.tokenProgram();
    final var nativeClient = splAccountClient();
    final var userTokenAtaKey = nativeClient.findATA(tokenProgram, tokenMint).publicKey();
    final var sharesMint = vaultState.sharesMint();
    final var userSharesAtaKey = nativeClient.findATA(sharesTokenProgramKey, sharesMint).publicKey();
    return deposit(
        vaultState._address(),
        vaultState.tokenVault(),
        tokenMint,
        vaultState.baseVaultAuthority(),
        sharesMint,
        userTokenAtaKey,
        userSharesAtaKey,
        tokenProgram,
        sharesTokenProgramKey,
        maxAmount
    );
  }

  /// Burn `sharesAmount` shares for the underlying token. Drains the vault's
  /// available (uninvested) liquidity first and, if not enough, redeems
  /// collateral from the supplied reserve.
  ///
  /// The handler iterates `ctx.remaining_accounts` for the vault's reserves
  /// (in allocation order) to refresh them via klend; callers must append those
  /// reserves via `KaminoVaultsRemainingAccounts.appendVaultReserves(instruction, reserves)`.
  ///
  /// `vaultStateKey` is passed to both the `WithdrawFromAvailable` and
  /// `WithdrawFromInvested` account groups; the program enforces they refer to
  /// the same vault.
  Instruction withdraw(final PublicKey vaultStateKey,
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
                       final long sharesAmount);

  default Instruction withdraw(final VaultState vaultState,
                               final PublicKey userTokenAtaKey,
                               final PublicKey userSharesAtaKey,
                               final PublicKey sharesTokenProgramKey,
                               final PublicKey reserveKey,
                               final PublicKey ctokenVaultKey,
                               final PublicKey lendingMarketKey,
                               final PublicKey lendingMarketAuthorityKey,
                               final PublicKey reserveLiquiditySupplyKey,
                               final PublicKey reserveCollateralMintKey,
                               final PublicKey reserveCollateralTokenProgramKey,
                               final long maxAmount) {

    return withdraw(
        vaultState._address(),
        vaultState.tokenVault(),
        vaultState.baseVaultAuthority(),
        userTokenAtaKey,
        vaultState.tokenMint(),
        userSharesAtaKey,
        vaultState.sharesMint(),
        vaultState.tokenProgram(),
        sharesTokenProgramKey,
        reserveKey,
        ctokenVaultKey,
        lendingMarketKey,
        lendingMarketAuthorityKey,
        reserveLiquiditySupplyKey,
        reserveCollateralMintKey,
        reserveCollateralTokenProgramKey,
        maxAmount
    );
  }

  default Instruction withdraw(final VaultState vaultState,
                               final PublicKey userTokenAtaKey,
                               final PublicKey userSharesAtaKey,
                               final PublicKey sharesTokenProgramKey,
                               final PublicKey reserveCollateralTokenProgramKey,
                               final Reserve reserve,
                               final long maxAmount) {
    final var kaminoAccounts = kaminoAccounts();
    final var lendingMarket = reserve.lendingMarket();
    final var vaultStateKey = vaultState._address();
    final var cTokenVault = kaminoAccounts.cTokenVault(vaultStateKey, reserve._address()).publicKey();
    return withdraw(
        vaultState,
        userTokenAtaKey,
        userSharesAtaKey,
        sharesTokenProgramKey,
        reserve._address(),
        cTokenVault,
        lendingMarket,
        kaminoAccounts.lendingMarketAuthPda(lendingMarket).publicKey(),
        reserve.liquidity().supplyVault(),
        reserve.collateral().mintPubkey(),
        reserveCollateralTokenProgramKey,
        maxAmount
    );
  }

  default Instruction withdraw(final VaultState vaultState,
                               final PublicKey sharesTokenProgramKey,
                               final PublicKey reserveCollateralTokenProgramKey,
                               final Reserve reserve,
                               final long maxAmount) {
    final var tokenMint = vaultState.tokenMint();
    final var tokenProgram = vaultState.tokenProgram();
    final var splAccountClient = splAccountClient();
    final var userTokenAtaKey = splAccountClient.findATA(tokenProgram, tokenMint).publicKey();
    final var sharesMint = vaultState.sharesMint();
    final var userSharesAtaKey = splAccountClient.findATA(sharesTokenProgramKey, sharesMint).publicKey();
    return withdraw(
        vaultState,
        userTokenAtaKey,
        userSharesAtaKey,
        sharesTokenProgramKey,
        reserveCollateralTokenProgramKey,
        reserve,
        maxAmount
    );
  }

  /// Withdraw `sharesAmount` worth of underlying token from the vault's
  /// *available* (uninvested) liquidity only. Cheaper than `withdraw` since it
  /// skips the reserve-redemption leg; will fail if the available liquidity is
  /// insufficient. The handler does not read `remaining_accounts`.
  Instruction withdrawFromAvailable(final PublicKey vaultStateKey,
                                    final PublicKey tokenVaultKey,
                                    final PublicKey baseVaultAuthorityKey,
                                    final PublicKey userTokenAtaKey,
                                    final PublicKey tokenMintKey,
                                    final PublicKey userSharesAtaKey,
                                    final PublicKey sharesMintKey,
                                    final PublicKey tokenProgramKey,
                                    final PublicKey sharesTokenProgramKey,
                                    final long sharesAmount);

  default Instruction withdrawFromAvailable(final VaultState vaultState,
                                            final PublicKey userTokenAtaKey,
                                            final PublicKey userSharesAtaKey,
                                            final PublicKey sharesTokenProgramKey,
                                            final long sharesAmount) {
    return withdrawFromAvailable(
        vaultState._address(),
        vaultState.tokenVault(),
        vaultState.baseVaultAuthority(),
        userTokenAtaKey,
        vaultState.tokenMint(),
        userSharesAtaKey,
        vaultState.sharesMint(),
        vaultState.tokenProgram(),
        sharesTokenProgramKey,
        sharesAmount
    );
  }

  default Instruction withdrawFromAvailable(final VaultState vaultState,
                                            final PublicKey sharesTokenProgramKey,
                                            final long sharesAmount) {
    final var tokenMint = vaultState.tokenMint();
    final var tokenProgram = vaultState.tokenProgram();
    final var splAccountClient = splAccountClient();
    final var userTokenAtaKey = splAccountClient.findATA(tokenProgram, tokenMint).publicKey();
    final var sharesMint = vaultState.sharesMint();
    final var userSharesAtaKey = splAccountClient.findATA(sharesTokenProgramKey, sharesMint).publicKey();
    return withdrawFromAvailable(
        vaultState,
        userTokenAtaKey,
        userSharesAtaKey,
        sharesTokenProgramKey,
        sharesAmount
    );
  }

  /// Burn `sharesAmount` shares and pay out a single reserve's cTokens directly
  /// to the user (bypassing the redeem-to-underlying step). Useful for callers
  /// that want to settle in cTokens.
  ///
  /// The handler iterates `ctx.remaining_accounts` for the vault's reserves
  /// (in allocation order) to refresh them via klend; callers must append those
  /// reserves via `KaminoVaultsRemainingAccounts.appendVaultReserves(instruction, reserves)`.
  Instruction redeemInKind(final PublicKey vaultStateKey,
                           final PublicKey baseVaultAuthorityKey,
                           final PublicKey reserveKey,
                           final PublicKey ctokenVaultKey,
                           final PublicKey userCtokenTaKey,
                           final PublicKey ctokenMintKey,
                           final PublicKey userSharesTaKey,
                           final PublicKey sharesMintKey,
                           final PublicKey reserveCollateralTokenProgramKey,
                           final PublicKey sharesTokenProgramKey,
                           final long sharesAmount);

  default Instruction redeemInKind(final VaultState vaultState,
                                   final Reserve reserve,
                                   final PublicKey userCtokenTaKey,
                                   final PublicKey userSharesTaKey,
                                   final PublicKey reserveCollateralTokenProgramKey,
                                   final PublicKey sharesTokenProgramKey,
                                   final long sharesAmount) {
    final var kaminoAccounts = kaminoAccounts();
    final var vaultStateKey = vaultState._address();
    final var cTokenVault = kaminoAccounts.cTokenVault(vaultStateKey, reserve._address()).publicKey();
    return redeemInKind(
        vaultStateKey,
        vaultState.baseVaultAuthority(),
        reserve._address(),
        cTokenVault,
        userCtokenTaKey,
        reserve.collateral().mintPubkey(),
        userSharesTaKey,
        vaultState.sharesMint(),
        reserveCollateralTokenProgramKey,
        sharesTokenProgramKey,
        sharesAmount
    );
  }

  default Instruction redeemInKind(final VaultState vaultState,
                                   final Reserve reserve,
                                   final PublicKey reserveCollateralTokenProgramKey,
                                   final PublicKey sharesTokenProgramKey,
                                   final long sharesAmount) {
    final var splAccountClient = splAccountClient();
    final var cTokenMint = reserve.collateral().mintPubkey();
    final var userCtokenTaKey = splAccountClient.findATA(reserveCollateralTokenProgramKey, cTokenMint).publicKey();
    final var sharesMint = vaultState.sharesMint();
    final var userSharesTaKey = splAccountClient.findATA(sharesTokenProgramKey, sharesMint).publicKey();
    return redeemInKind(
        vaultState,
        reserve,
        userCtokenTaKey,
        userSharesTaKey,
        reserveCollateralTokenProgramKey,
        sharesTokenProgramKey,
        sharesAmount
    );
  }
}

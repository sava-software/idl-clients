package software.sava.idl.clients.kamino.lend;

import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.kamino.KaminoAccounts;
import software.sava.idl.clients.kamino.farms.gen.types.FarmState;
import software.sava.idl.clients.kamino.lend.gen.types.*;
import software.sava.idl.clients.spl.SPLAccountClient;
import software.sava.rpc.json.http.client.SolanaRpcClient;
import software.sava.rpc.json.http.response.AccountInfo;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface KaminoLendClient {

  static KaminoLendClient createClient(final SPLAccountClient splAccountClient, final KaminoAccounts kaminoAccounts) {
    return new KaminoLendClientImpl(splAccountClient, kaminoAccounts);
  }

  static KaminoLendClient createClient(final SPLAccountClient splAccountClient) {
    return createClient(splAccountClient, KaminoAccounts.MAIN_NET);
  }

  static CompletableFuture<List<AccountInfo<FarmState>>> fetchFarmStateAccounts(final SolanaRpcClient rpcClient,
                                                                                final PublicKey kLendProgram) {
    return rpcClient.getProgramAccounts(
        kLendProgram,
        List.of(FarmState.SIZE_FILTER),
        FarmState.FACTORY
    );
  }

  static CompletableFuture<List<AccountInfo<LendingMarket>>> fetchLendingMarkets(final SolanaRpcClient rpcClient,
                                                                                 final PublicKey kLendProgram) {
    return rpcClient.getProgramAccounts(
        kLendProgram,
        List.of(LendingMarket.SIZE_FILTER),
        LendingMarket.FACTORY
    );
  }

  static CompletableFuture<List<AccountInfo<Reserve>>> fetchReserveAccounts(final SolanaRpcClient rpcClient,
                                                                            final PublicKey kLendProgram) {
    return rpcClient.getProgramAccounts(
        kLendProgram,
        List.of(Reserve.SIZE_FILTER),
        Reserve.FACTORY
    );
  }

  static CompletableFuture<List<AccountInfo<Reserve>>> fetchReserveAccounts(final SolanaRpcClient rpcClient,
                                                                            final PublicKey kLendProgram,
                                                                            final PublicKey lendingMarket) {
    return rpcClient.getProgramAccounts(
        kLendProgram,
        List.of(Reserve.SIZE_FILTER, Reserve.createLendingMarketFilter(lendingMarket)),
        Reserve.FACTORY
    );
  }

  static CompletableFuture<List<AccountInfo<ReferrerState>>> fetchReferrerStateAccounts(final SolanaRpcClient rpcClient,
                                                                                        final PublicKey kLendProgram) {
    return rpcClient.getProgramAccounts(
        kLendProgram,
        List.of(ReferrerState.SIZE_FILTER),
        ReferrerState.FACTORY
    );
  }

  static CompletableFuture<List<AccountInfo<ReferrerTokenState>>> fetchReferrerTokenStateAccounts(final SolanaRpcClient rpcClient,
                                                                                                  final PublicKey kLendProgram) {
    return rpcClient.getProgramAccounts(
        kLendProgram,
        List.of(ReferrerTokenState.SIZE_FILTER),
        ReferrerTokenState.FACTORY
    );
  }

  SolanaAccounts solanaAccounts();

  KaminoAccounts kaminoAccounts();

  PublicKey user();

  Instruction initReferrerTokenState(final PublicKey lendingMarket,
                                     final PublicKey reserveKey,
                                     final PublicKey referrerKey,
                                     final PublicKey referrerTokenStateKey);

  Instruction withdrawReferrerFees(final PublicKey reserveKey,
                                   final KaminoReservePDAs reservePDAs,
                                   final PublicKey referrerKey,
                                   final PublicKey referrerTokenStateKey,
                                   final PublicKey referrerTokenAccountKey);

  Instruction initReferrerStateAndShortUrl(final PublicKey referrerKey,
                                           final PublicKey referrerStateKey,
                                           final PublicKey referrerShortUrlKey,
                                           final PublicKey referrerUserMetadataKey,
                                           final String shortUrl);

  Instruction deleteReferrerStateAndShortUrl(final PublicKey referrerKey,
                                             final PublicKey referrerStateKey,
                                             final PublicKey shortUrlKey);

  Instruction initUserMetadata(final PublicKey referrerUserMetadataKey, final PublicKey userLookupTable);

  Instruction initObligation(final PublicKey lendingMarket,
                             final PublicKey obligationKey,
                             final InitObligationArgs initObligationArgs);

  Instruction initObligationFarmsForReserve(final Reserve reserve,
                                            final KaminoReservePDAs reservePDAs,
                                            final PublicKey obligationKey,
                                            final PublicKey obligationFarmKey,
                                            final int mode);

  Instruction refreshReserve(final PublicKey lendingMarket,
                             final PublicKey reserveKey,
                             final PublicKey pythOracleKey,
                             final PublicKey switchboardPriceOracleKey,
                             final PublicKey switchboardTwapOracleKey,
                             final PublicKey scopePricesKey);

  Instruction refreshReservesBatch(final boolean skipPriceUpdates);

  Instruction refreshObligation(final PublicKey lendingMarket, final PublicKey obligationKey);

  Instruction depositReserveLiquidityAndObligationCollateral(final PublicKey obligationKey,
                                                             final PublicKey reserveKey,
                                                             final KaminoReservePDAs reservePDAs,
                                                             final PublicKey sourceTokenAccount,
                                                             final long liquidityAmount);

  Instruction depositReserveLiquidityAndObligationCollateralV2(final PublicKey obligationKey,
                                                               final PublicKey reserveKey,
                                                               final KaminoReservePDAs reservePDAs,
                                                               final PublicKey sourceTokenAccount,
                                                               final PublicKey farmsAccountsObligationFarmUserStateKey,
                                                               final PublicKey farmsAccountsReserveFarmStateKey,
                                                               final long liquidityAmount);

  Instruction withdrawObligationCollateralAndRedeemReserveCollateralV2(final PublicKey obligationKey,
                                                                       final PublicKey reserveKey,
                                                                       final PublicKey withdrawAccountsReserveSourceCollateralKey,
                                                                       final KaminoReservePDAs reservePDAs,
                                                                       final PublicKey destinationTokenAccount,
                                                                       final PublicKey farmsAccountsObligationFarmUserStateKey,
                                                                       final PublicKey farmsAccountsReserveFarmStateKey,
                                                                       final long collateralAmount);

  // Non-V2 trader-facing instructions.

  Instruction depositReserveLiquidity(final PublicKey reserveKey,
                                      final KaminoReservePDAs reservePDAs,
                                      final PublicKey userSourceLiquidity,
                                      final PublicKey userDestinationCollateral,
                                      final long liquidityAmount);

  Instruction redeemReserveCollateral(final PublicKey reserveKey,
                                      final KaminoReservePDAs reservePDAs,
                                      final PublicKey userSourceCollateral,
                                      final PublicKey userDestinationLiquidity,
                                      final long collateralAmount);

  Instruction depositObligationCollateral(final PublicKey obligationKey,
                                          final PublicKey reserveKey,
                                          final KaminoReservePDAs reservePDAs,
                                          final PublicKey reserveDestinationCollateral,
                                          final PublicKey userSourceCollateral,
                                          final long collateralAmount);

  Instruction depositObligationCollateralV2(final PublicKey obligationKey,
                                            final PublicKey reserveKey,
                                            final KaminoReservePDAs reservePDAs,
                                            final PublicKey reserveDestinationCollateral,
                                            final PublicKey userSourceCollateral,
                                            final PublicKey farmsAccountsObligationFarmUserStateKey,
                                            final PublicKey farmsAccountsReserveFarmStateKey,
                                            final long collateralAmount);

  Instruction withdrawObligationCollateral(final PublicKey obligationKey,
                                           final PublicKey reserveKey,
                                           final KaminoReservePDAs reservePDAs,
                                           final PublicKey reserveSourceCollateral,
                                           final PublicKey userDestinationCollateral,
                                           final long collateralAmount);

  Instruction withdrawObligationCollateralV2(final PublicKey obligationKey,
                                             final PublicKey reserveKey,
                                             final KaminoReservePDAs reservePDAs,
                                             final PublicKey reserveSourceCollateral,
                                             final PublicKey userDestinationCollateral,
                                             final PublicKey farmsAccountsObligationFarmUserStateKey,
                                             final PublicKey farmsAccountsReserveFarmStateKey,
                                             final long collateralAmount);

  Instruction withdrawObligationCollateralAndRedeemReserveCollateral(final PublicKey obligationKey,
                                                                    final PublicKey reserveKey,
                                                                    final PublicKey reserveSourceCollateral,
                                                                    final KaminoReservePDAs reservePDAs,
                                                                    final PublicKey userDestinationLiquidity,
                                                                    final long collateralAmount);

  /// Borrow liquidity against an obligation.
  ///
  /// `referrerTokenState` may be `null` (no referrer); pass the program id as the sentinel.
  /// Caller must prepend a `refreshObligation` (and any required `refreshReserve`s) and append
  /// the obligation's deposit-reserve keys as remaining accounts via
  /// `KaminoLendingRemainingAccounts.appendDepositReserves(...)`, followed by the optional
  /// permission account (see `KaminoLendingRemainingAccounts.appendPermissionAccount(...)`)
  /// when the market enforces `PermissionedOp::BORROW`.
  Instruction borrowObligationLiquidity(final PublicKey obligationKey,
                                        final PublicKey lendingMarket,
                                        final PublicKey borrowReserveKey,
                                        final PublicKey borrowReserveLiquidityMint,
                                        final PublicKey reserveSourceLiquidity,
                                        final PublicKey borrowReserveLiquidityFeeReceiver,
                                        final PublicKey userDestinationLiquidity,
                                        final PublicKey referrerTokenState,
                                        final PublicKey tokenProgram,
                                        final long liquidityAmount);

  /// See `borrowObligationLiquidity`. Append deposit-reserve keys + optional permission via
  /// `KaminoLendingRemainingAccounts`. Farms accounts are part of the IDL account list (not
  /// remaining accounts).
  Instruction borrowObligationLiquidityV2(final PublicKey obligationKey,
                                          final PublicKey lendingMarket,
                                          final PublicKey borrowReserveKey,
                                          final PublicKey borrowReserveLiquidityMint,
                                          final PublicKey reserveSourceLiquidity,
                                          final PublicKey borrowReserveLiquidityFeeReceiver,
                                          final PublicKey userDestinationLiquidity,
                                          final PublicKey referrerTokenState,
                                          final PublicKey tokenProgram,
                                          final PublicKey farmsAccountsObligationFarmUserStateKey,
                                          final PublicKey farmsAccountsReserveFarmStateKey,
                                          final long liquidityAmount);

  /// Repay liquidity owed by an obligation.
  ///
  /// Caller may need to append the optional `PermissionedOp::REPAY` permission account as a
  /// remaining account via `KaminoLendingRemainingAccounts.appendPermissionAccount(...)`.
  Instruction repayObligationLiquidity(final PublicKey obligationKey,
                                       final PublicKey lendingMarket,
                                       final PublicKey repayReserveKey,
                                       final PublicKey reserveLiquidityMint,
                                       final PublicKey reserveDestinationLiquidity,
                                       final PublicKey userSourceLiquidity,
                                       final PublicKey tokenProgram,
                                       final long liquidityAmount);

  Instruction repayObligationLiquidityV2(final PublicKey obligationKey,
                                         final PublicKey lendingMarket,
                                         final PublicKey repayReserveKey,
                                         final PublicKey reserveLiquidityMint,
                                         final PublicKey reserveDestinationLiquidity,
                                         final PublicKey userSourceLiquidity,
                                         final PublicKey tokenProgram,
                                         final PublicKey farmsAccountsObligationFarmUserStateKey,
                                         final PublicKey farmsAccountsReserveFarmStateKey,
                                         final long liquidityAmount);

  Instruction flashBorrowReserveLiquidity(final PublicKey lendingMarket,
                                          final PublicKey reserveKey,
                                          final PublicKey reserveLiquidityMint,
                                          final PublicKey reserveSourceLiquidity,
                                          final PublicKey userDestinationLiquidity,
                                          final PublicKey reserveLiquidityFeeReceiver,
                                          final PublicKey referrerTokenState,
                                          final PublicKey referrerAccount,
                                          final PublicKey tokenProgram,
                                          final long liquidityAmount);

  Instruction flashRepayReserveLiquidity(final PublicKey lendingMarket,
                                         final PublicKey reserveKey,
                                         final PublicKey reserveLiquidityMint,
                                         final PublicKey reserveDestinationLiquidity,
                                         final PublicKey userSourceLiquidity,
                                         final PublicKey reserveLiquidityFeeReceiver,
                                         final PublicKey referrerTokenState,
                                         final PublicKey referrerAccount,
                                         final PublicKey tokenProgram,
                                         final long liquidityAmount,
                                         final int borrowInstructionIndex);

  /// Refresh helper that callers typically prepend to V1 deposit/borrow/repay/withdraw
  /// instructions when farms are enabled (the V2 forms do this internally).
  Instruction refreshObligationFarmsForReserve(final PublicKey obligationKey,
                                               final PublicKey reserveKey,
                                               final PublicKey reserveFarmState,
                                               final PublicKey obligationFarmUserState,
                                               final PublicKey lendingMarket,
                                               final int mode);

  /// Opt the obligation into a new elevation group. Caller must append the new group's
  /// reserves as remaining accounts via
  /// `KaminoLendingRemainingAccounts.appendDepositReserves(...)`.
  Instruction requestElevationGroup(final PublicKey obligationKey,
                                    final PublicKey lendingMarket,
                                    final int elevationGroup);

  /// Set the obligation's stop-loss / take-profit order at `index`.
  Instruction setObligationOrder(final PublicKey obligationKey,
                                 final PublicKey lendingMarket,
                                 final int index,
                                 final ObligationOrder order);

  // Obligation ownership transfer (each side signs separately).

  Instruction initiateObligationOwnershipTransfer(final PublicKey obligationKey, final PublicKey newOwner);

  Instruction acceptObligationOwnership(final PublicKey obligationKey);

  Instruction abortObligationOwnershipTransfer(final PublicKey obligationKey);

  // ===== FarmsProgram (trader-facing) =====

  /// Initialize a user state account on a farm. For non-delegated farms pass `delegatee`
  /// as `null` / `KaminoAccounts.NULL_KEY` and the caller's `owner` will be substituted
  /// (required by the program: payer == owner == delegatee for non-delegated farms, and
  /// `delegatee` is also the user-state PDA seed — derive `userStateKey` accordingly).
  Instruction initializeFarmUser(final PublicKey farmStateKey,
                                 final PublicKey userStateKey,
                                 final PublicKey delegatee);

  /// Refresh a farm's accumulated rewards. `scopePricesKey` may be null/NULL_KEY when the
  /// farm doesn't use a scope price feed; the farm program id is substituted as the
  /// Anchor `Option` sentinel.
  Instruction refreshFarm(final PublicKey farmStateKey, final PublicKey scopePricesKey);

  /// Refresh a user's stake state. `scopePricesKey` is optional (see [#refreshFarm]).
  Instruction refreshFarmUserState(final PublicKey userStateKey,
                                   final PublicKey farmStateKey,
                                   final PublicKey scopePricesKey);

  /// Harvest a single reward (`rewardIndex`) for the calling user from a farm.
  /// `scopePricesKey` is optional (see [#refreshFarm]).
  Instruction harvestFarmReward(final PublicKey userStateKey,
                                final PublicKey farmStateKey,
                                final PublicKey rewardMint,
                                final PublicKey userRewardTokenAccount,
                                final PublicKey rewardsVault,
                                final PublicKey rewardsTreasuryVault,
                                final PublicKey farmVaultsAuthority,
                                final PublicKey scopePricesKey,
                                final PublicKey tokenProgram,
                                final long rewardIndex);

  /// Stake `amount` of the farm's stake token into a user state.
  /// `scopePricesKey` is optional (see [#refreshFarm]).
  Instruction stakeFarm(final PublicKey userStateKey,
                        final PublicKey farmStateKey,
                        final PublicKey farmVault,
                        final PublicKey userAta,
                        final PublicKey tokenMint,
                        final PublicKey scopePricesKey,
                        final PublicKey tokenProgram,
                        final long amount);

  /// Begin unstaking `stakeSharesScaled` shares from a user state.
  /// `scopePricesKey` is optional (see [#refreshFarm]).
  Instruction unstakeFarm(final PublicKey userStateKey,
                          final PublicKey farmStateKey,
                          final PublicKey scopePricesKey,
                          final java.math.BigInteger stakeSharesScaled);

  /// Withdraw any tokens that have completed the unstake cool-down.
  Instruction withdrawUnstakedFarmDeposits(final PublicKey userStateKey,
                                           final PublicKey farmStateKey,
                                           final PublicKey userAta,
                                           final PublicKey farmVault,
                                           final PublicKey farmVaultsAuthority,
                                           final PublicKey tokenProgram);
}

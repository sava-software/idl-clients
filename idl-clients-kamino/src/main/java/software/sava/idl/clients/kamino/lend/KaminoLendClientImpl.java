package software.sava.idl.clients.kamino.lend;

import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.kamino.KaminoAccounts;
import software.sava.idl.clients.kamino.farms.gen.FarmsProgram;
import software.sava.idl.clients.kamino.lend.gen.KaminoLendingProgram;
import software.sava.idl.clients.kamino.lend.gen.types.InitObligationArgs;
import software.sava.idl.clients.kamino.lend.gen.types.ObligationOrder;
import software.sava.idl.clients.kamino.lend.gen.types.Reserve;
import software.sava.idl.clients.spl.SPLAccountClient;

import java.math.BigInteger;

final class KaminoLendClientImpl implements KaminoLendClient {

  private final SolanaAccounts solanaAccounts;
  private final KaminoAccounts kaminoAccounts;
  private final PublicKey owner;
  private final PublicKey ownerMetadata;
  private final PublicKey feePayer;

  KaminoLendClientImpl(final SPLAccountClient splAccountClient, final KaminoAccounts kaminoAccounts) {
    this.solanaAccounts = splAccountClient.solanaAccounts();
    this.kaminoAccounts = kaminoAccounts;
    this.owner = splAccountClient.owner();
    this.ownerMetadata = KaminoAccounts.userMetadataPda(owner, kaminoAccounts.kLendProgram()).publicKey();
    this.feePayer = splAccountClient.feePayer().publicKey();
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
  public Instruction initReferrerTokenState(final PublicKey lendingMarket,
                                            final PublicKey reserveKey,
                                            final PublicKey referrerKey,
                                            final PublicKey referrerTokenStateKey) {
    return KaminoLendingProgram.initReferrerTokenState(
        kaminoAccounts.invokedKLendProgram(),
        feePayer,
        lendingMarket,
        reserveKey,
        referrerKey,
        referrerTokenStateKey,
        solanaAccounts.rentSysVar(),
        solanaAccounts.systemProgram()
    );
  }

  @Override
  public Instruction withdrawReferrerFees(final PublicKey reserveKey,
                                          final KaminoReservePDAs reservePDAs,
                                          final PublicKey referrerKey,
                                          final PublicKey referrerTokenStateKey,
                                          final PublicKey referrerTokenAccountKey) {
    return KaminoLendingProgram.withdrawReferrerFees(
        kaminoAccounts.invokedKLendProgram(),
        referrerKey,
        referrerTokenStateKey,
        reserveKey,
        reservePDAs.collateralMint(),
        reservePDAs.liquiditySupplyVault(),
        referrerTokenAccountKey,
        reservePDAs.market(),
        reservePDAs.marketAuthority(),
        solanaAccounts.tokenProgram()
    );
  }

  @Override
  public Instruction initReferrerStateAndShortUrl(final PublicKey referrerKey,
                                                  final PublicKey referrerStateKey,
                                                  final PublicKey referrerShortUrlKey,
                                                  final PublicKey referrerUserMetadataKey,
                                                  final String shortUrl) {
    return KaminoLendingProgram.initReferrerStateAndShortUrl(
        kaminoAccounts.invokedKLendProgram(),
        referrerKey,
        referrerStateKey,
        referrerShortUrlKey,
        referrerUserMetadataKey,
        solanaAccounts.rentSysVar(),
        solanaAccounts.systemProgram(),
        shortUrl
    );
  }

  @Override
  public Instruction deleteReferrerStateAndShortUrl(final PublicKey referrerKey,
                                                    final PublicKey referrerStateKey,
                                                    final PublicKey shortUrlKey) {
    return KaminoLendingProgram.deleteReferrerStateAndShortUrl(
        kaminoAccounts.invokedKLendProgram(),
        referrerKey,
        referrerStateKey,
        shortUrlKey,
        solanaAccounts.rentSysVar(),
        solanaAccounts.systemProgram()
    );
  }

  @Override
  public Instruction initUserMetadata(final PublicKey referrerUserMetadataKey, final PublicKey userLookupTable) {
    return KaminoLendingProgram.initUserMetadata(
        kaminoAccounts.invokedKLendProgram(),
        owner,
        feePayer,
        ownerMetadata,
        referrerUserMetadataKey,
        solanaAccounts.rentSysVar(),
        solanaAccounts.systemProgram(),
        userLookupTable
    );
  }

  @Override
  public Instruction initObligation(final PublicKey lendingMarket,
                                    final PublicKey obligationKey,
                                    final InitObligationArgs initObligationArgs) {
    return KaminoLendingProgram.initObligation(
        kaminoAccounts.invokedKLendProgram(),
        owner,
        feePayer,
        obligationKey,
        lendingMarket,
        solanaAccounts.systemProgram(),
        solanaAccounts.systemProgram(),
        ownerMetadata,
        solanaAccounts.rentSysVar(),
        solanaAccounts.systemProgram(),
        initObligationArgs
    );
  }

  @Override
  public Instruction initObligationFarmsForReserve(final Reserve reserve,
                                                   final KaminoReservePDAs reservePDAs,
                                                   final PublicKey obligationKey,
                                                   final PublicKey obligationFarmKey,
                                                   final int mode) {
    return initObligationFarmsForReserve(
        reserve._address(),
        reserve.farmCollateral(),
        reservePDAs,
        obligationKey,
        obligationFarmKey,
        mode
    );
  }

  public Instruction initObligationFarmsForReserve(final PublicKey reserveKey,
                                                   final PublicKey reserveFarmStateKey,
                                                   final KaminoReservePDAs reservePDAs,
                                                   final PublicKey obligationKey,
                                                   final PublicKey obligationFarmKey,
                                                   final int mode) {
    return KaminoLendingProgram.initObligationFarmsForReserve(
        kaminoAccounts.invokedKLendProgram(),
        feePayer,
        owner,
        obligationKey,
        reservePDAs.marketAuthority(),
        reserveKey,
        reserveFarmStateKey,
        obligationFarmKey,
        reservePDAs.market(),
        kaminoAccounts.farmProgram(),
        solanaAccounts.rentSysVar(),
        solanaAccounts.systemProgram(),
        mode
    );
  }

  @Override
  public Instruction refreshReserve(final PublicKey lendingMarket,
                                    final PublicKey reserveKey,
                                    final PublicKey pythOracleKey,
                                    final PublicKey switchboardPriceOracleKey,
                                    final PublicKey switchboardTwapOracleKey,
                                    final PublicKey scopePricesKey) {
    final var kLendProgram = kaminoAccounts.kLendProgram();
    return KaminoLendingProgram.refreshReserve(
        kaminoAccounts.invokedKLendProgram(),
        reserveKey,
        lendingMarket,
        KaminoAccounts.isNullKey(pythOracleKey) ? kLendProgram : pythOracleKey,
        KaminoAccounts.isNullKey(switchboardPriceOracleKey) ? kLendProgram : switchboardPriceOracleKey,
        KaminoAccounts.isNullKey(switchboardTwapOracleKey) ? kLendProgram : switchboardTwapOracleKey,
        KaminoAccounts.isNullKey(scopePricesKey) ? kLendProgram : scopePricesKey
    );
  }

  @Override
  public Instruction refreshReservesBatch(final boolean skipPriceUpdates) {
    return KaminoLendingProgram.refreshReservesBatch(kaminoAccounts().invokedKLendProgram(), skipPriceUpdates);
  }

  @Override
  public Instruction refreshObligation(final PublicKey lendingMarket,
                                       final PublicKey obligationKey) {
    return KaminoLendingProgram.refreshObligation(
        kaminoAccounts.invokedKLendProgram(),
        lendingMarket,
        obligationKey
    );
  }

  @Override
  public Instruction depositReserveLiquidityAndObligationCollateral(final PublicKey obligationKey,
                                                                    final PublicKey reserveKey,
                                                                    final KaminoReservePDAs reservePDAs,
                                                                    final PublicKey sourceTokenAccount,
                                                                    final long liquidityAmount) {
    return KaminoLendingProgram.depositReserveLiquidityAndObligationCollateral(
        kaminoAccounts.invokedKLendProgram(),
        owner,
        obligationKey,
        reservePDAs.market(),
        reservePDAs.marketAuthority(),
        reserveKey,
        reservePDAs.mint(),
        reservePDAs.liquiditySupplyVault(),
        reservePDAs.collateralMint(),
        reservePDAs.collateralSupplyVault(),
        sourceTokenAccount,
        kaminoAccounts.kLendProgram(),
        reservePDAs.tokenProgram(),
        reservePDAs.tokenProgram(),
        solanaAccounts.instructionsSysVar(),
        liquidityAmount
    );
  }

  @Override
  public Instruction depositReserveLiquidityAndObligationCollateralV2(final PublicKey obligationKey,
                                                                      final PublicKey reserveKey,
                                                                      final KaminoReservePDAs reservePDAs,
                                                                      final PublicKey sourceTokenAccount,
                                                                      final PublicKey farmsAccountsObligationFarmUserStateKey,
                                                                      final PublicKey farmsAccountsReserveFarmStateKey,
                                                                      final long liquidityAmount) {
    return KaminoLendingProgram.depositReserveLiquidityAndObligationCollateralV2(
        kaminoAccounts.invokedKLendProgram(),
        owner,
        obligationKey,
        reservePDAs.market(),
        reservePDAs.marketAuthority(),
        reserveKey,
        reservePDAs.mint(),
        reservePDAs.liquiditySupplyVault(),
        reservePDAs.collateralMint(),
        reservePDAs.collateralSupplyVault(),
        sourceTokenAccount,
        kaminoAccounts.kLendProgram(),
        reservePDAs.tokenProgram(),
        reservePDAs.tokenProgram(),
        solanaAccounts.instructionsSysVar(),
        farmsAccountsObligationFarmUserStateKey,
        farmsAccountsReserveFarmStateKey,
        kaminoAccounts.farmProgram(),
        liquidityAmount
    );
  }

  @Override
  public Instruction withdrawObligationCollateralAndRedeemReserveCollateralV2(final PublicKey obligationKey,
                                                                              final PublicKey reserveKey,
                                                                              final PublicKey withdrawAccountsReserveSourceCollateralKey,
                                                                              final KaminoReservePDAs reservePDAs,
                                                                              final PublicKey destinationTokenAccount,
                                                                              final PublicKey farmsAccountsObligationFarmUserStateKey,
                                                                              final PublicKey farmsAccountsReserveFarmStateKey,
                                                                              final long collateralAmount) {
    return KaminoLendingProgram.withdrawObligationCollateralAndRedeemReserveCollateralV2(
        kaminoAccounts.invokedKLendProgram(),
        owner,
        obligationKey,
        reservePDAs.market(),
        reservePDAs.marketAuthority(),
        reserveKey,
        reservePDAs.mint(),
        withdrawAccountsReserveSourceCollateralKey,
        reservePDAs.collateralMint(),
        reservePDAs.liquiditySupplyVault(),
        destinationTokenAccount,
        kaminoAccounts.kLendProgram(),
        reservePDAs.tokenProgram(),
        reservePDAs.tokenProgram(),
        solanaAccounts.instructionsSysVar(),
        farmsAccountsObligationFarmUserStateKey,
        farmsAccountsReserveFarmStateKey,
        kaminoAccounts.farmProgram(),
        collateralAmount
    );
  }

  @Override
  public Instruction depositReserveLiquidity(final PublicKey reserveKey,
                                             final KaminoReservePDAs reservePDAs,
                                             final PublicKey userSourceLiquidity,
                                             final PublicKey userDestinationCollateral,
                                             final long liquidityAmount) {
    return KaminoLendingProgram.depositReserveLiquidity(
        kaminoAccounts.invokedKLendProgram(),
        owner,
        reserveKey,
        reservePDAs.market(),
        reservePDAs.marketAuthority(),
        reservePDAs.mint(),
        reservePDAs.liquiditySupplyVault(),
        reservePDAs.collateralMint(),
        userSourceLiquidity,
        userDestinationCollateral,
        reservePDAs.tokenProgram(),
        reservePDAs.tokenProgram(),
        solanaAccounts.instructionsSysVar(),
        liquidityAmount
    );
  }

  @Override
  public Instruction redeemReserveCollateral(final PublicKey reserveKey,
                                             final KaminoReservePDAs reservePDAs,
                                             final PublicKey userSourceCollateral,
                                             final PublicKey userDestinationLiquidity,
                                             final long collateralAmount) {
    return KaminoLendingProgram.redeemReserveCollateral(
        kaminoAccounts.invokedKLendProgram(),
        owner,
        reservePDAs.market(),
        reserveKey,
        reservePDAs.marketAuthority(),
        reservePDAs.mint(),
        reservePDAs.collateralMint(),
        reservePDAs.liquiditySupplyVault(),
        userSourceCollateral,
        userDestinationLiquidity,
        reservePDAs.tokenProgram(),
        reservePDAs.tokenProgram(),
        solanaAccounts.instructionsSysVar(),
        collateralAmount
    );
  }

  @Override
  public Instruction depositObligationCollateral(final PublicKey obligationKey,
                                                 final PublicKey reserveKey,
                                                 final KaminoReservePDAs reservePDAs,
                                                 final PublicKey reserveDestinationCollateral,
                                                 final PublicKey userSourceCollateral,
                                                 final long collateralAmount) {
    return KaminoLendingProgram.depositObligationCollateral(
        kaminoAccounts.invokedKLendProgram(),
        owner,
        obligationKey,
        reservePDAs.market(),
        reserveKey,
        reserveDestinationCollateral,
        userSourceCollateral,
        reservePDAs.tokenProgram(),
        solanaAccounts.instructionsSysVar(),
        collateralAmount
    );
  }

  @Override
  public Instruction depositObligationCollateralV2(final PublicKey obligationKey,
                                                   final PublicKey reserveKey,
                                                   final KaminoReservePDAs reservePDAs,
                                                   final PublicKey reserveDestinationCollateral,
                                                   final PublicKey userSourceCollateral,
                                                   final PublicKey farmsAccountsObligationFarmUserStateKey,
                                                   final PublicKey farmsAccountsReserveFarmStateKey,
                                                   final long collateralAmount) {
    return KaminoLendingProgram.depositObligationCollateralV2(
        kaminoAccounts.invokedKLendProgram(),
        owner,
        obligationKey,
        reservePDAs.market(),
        reserveKey,
        reserveDestinationCollateral,
        userSourceCollateral,
        reservePDAs.tokenProgram(),
        solanaAccounts.instructionsSysVar(),
        reservePDAs.marketAuthority(),
        farmsAccountsObligationFarmUserStateKey,
        farmsAccountsReserveFarmStateKey,
        kaminoAccounts.farmProgram(),
        collateralAmount
    );
  }

  @Override
  public Instruction withdrawObligationCollateral(final PublicKey obligationKey,
                                                  final PublicKey reserveKey,
                                                  final KaminoReservePDAs reservePDAs,
                                                  final PublicKey reserveSourceCollateral,
                                                  final PublicKey userDestinationCollateral,
                                                  final long collateralAmount) {
    return KaminoLendingProgram.withdrawObligationCollateral(
        kaminoAccounts.invokedKLendProgram(),
        owner,
        obligationKey,
        reservePDAs.market(),
        reservePDAs.marketAuthority(),
        reserveKey,
        reserveSourceCollateral,
        userDestinationCollateral,
        reservePDAs.tokenProgram(),
        solanaAccounts.instructionsSysVar(),
        collateralAmount
    );
  }

  @Override
  public Instruction withdrawObligationCollateralV2(final PublicKey obligationKey,
                                                    final PublicKey reserveKey,
                                                    final KaminoReservePDAs reservePDAs,
                                                    final PublicKey reserveSourceCollateral,
                                                    final PublicKey userDestinationCollateral,
                                                    final PublicKey farmsAccountsObligationFarmUserStateKey,
                                                    final PublicKey farmsAccountsReserveFarmStateKey,
                                                    final long collateralAmount) {
    return KaminoLendingProgram.withdrawObligationCollateralV2(
        kaminoAccounts.invokedKLendProgram(),
        owner,
        obligationKey,
        reservePDAs.market(),
        reservePDAs.marketAuthority(),
        reserveKey,
        reserveSourceCollateral,
        userDestinationCollateral,
        reservePDAs.tokenProgram(),
        solanaAccounts.instructionsSysVar(),
        farmsAccountsObligationFarmUserStateKey,
        farmsAccountsReserveFarmStateKey,
        kaminoAccounts.farmProgram(),
        collateralAmount
    );
  }

  @Override
  public Instruction withdrawObligationCollateralAndRedeemReserveCollateral(final PublicKey obligationKey,
                                                                            final PublicKey reserveKey,
                                                                            final PublicKey reserveSourceCollateral,
                                                                            final KaminoReservePDAs reservePDAs,
                                                                            final PublicKey userDestinationLiquidity,
                                                                            final long collateralAmount) {
    return KaminoLendingProgram.withdrawObligationCollateralAndRedeemReserveCollateral(
        kaminoAccounts.invokedKLendProgram(),
        owner,
        obligationKey,
        reservePDAs.market(),
        reservePDAs.marketAuthority(),
        reserveKey,
        reservePDAs.mint(),
        reserveSourceCollateral,
        reservePDAs.collateralMint(),
        reservePDAs.liquiditySupplyVault(),
        userDestinationLiquidity,
        kaminoAccounts.kLendProgram(),
        reservePDAs.tokenProgram(),
        reservePDAs.tokenProgram(),
        solanaAccounts.instructionsSysVar(),
        collateralAmount
    );
  }

  @Override
  public Instruction borrowObligationLiquidity(final PublicKey obligationKey,
                                               final PublicKey lendingMarket,
                                               final PublicKey borrowReserveKey,
                                               final PublicKey borrowReserveLiquidityMint,
                                               final PublicKey reserveSourceLiquidity,
                                               final PublicKey borrowReserveLiquidityFeeReceiver,
                                               final PublicKey userDestinationLiquidity,
                                               final PublicKey referrerTokenState,
                                               final PublicKey tokenProgram,
                                               final long liquidityAmount) {
    return KaminoLendingProgram.borrowObligationLiquidity(
        kaminoAccounts.invokedKLendProgram(),
        owner,
        obligationKey,
        lendingMarket,
        kaminoAccounts.lendingMarketAuthPda(lendingMarket).publicKey(),
        borrowReserveKey,
        borrowReserveLiquidityMint,
        reserveSourceLiquidity,
        borrowReserveLiquidityFeeReceiver,
        userDestinationLiquidity,
        KaminoAccounts.isNullKey(referrerTokenState) ? kaminoAccounts.kLendProgram() : referrerTokenState,
        tokenProgram,
        solanaAccounts.instructionsSysVar(),
        liquidityAmount
    );
  }

  @Override
  public Instruction borrowObligationLiquidityV2(final PublicKey obligationKey,
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
                                                 final long liquidityAmount) {
    return KaminoLendingProgram.borrowObligationLiquidityV2(
        kaminoAccounts.invokedKLendProgram(),
        owner,
        obligationKey,
        lendingMarket,
        kaminoAccounts.lendingMarketAuthPda(lendingMarket).publicKey(),
        borrowReserveKey,
        borrowReserveLiquidityMint,
        reserveSourceLiquidity,
        borrowReserveLiquidityFeeReceiver,
        userDestinationLiquidity,
        KaminoAccounts.isNullKey(referrerTokenState) ? kaminoAccounts.kLendProgram() : referrerTokenState,
        tokenProgram,
        solanaAccounts.instructionsSysVar(),
        farmsAccountsObligationFarmUserStateKey,
        farmsAccountsReserveFarmStateKey,
        kaminoAccounts.farmProgram(),
        liquidityAmount
    );
  }

  @Override
  public Instruction repayObligationLiquidity(final PublicKey obligationKey,
                                              final PublicKey lendingMarket,
                                              final PublicKey repayReserveKey,
                                              final PublicKey reserveLiquidityMint,
                                              final PublicKey reserveDestinationLiquidity,
                                              final PublicKey userSourceLiquidity,
                                              final PublicKey tokenProgram,
                                              final long liquidityAmount) {
    return KaminoLendingProgram.repayObligationLiquidity(
        kaminoAccounts.invokedKLendProgram(),
        owner,
        obligationKey,
        lendingMarket,
        repayReserveKey,
        reserveLiquidityMint,
        reserveDestinationLiquidity,
        userSourceLiquidity,
        tokenProgram,
        solanaAccounts.instructionsSysVar(),
        liquidityAmount
    );
  }

  @Override
  public Instruction repayObligationLiquidityV2(final PublicKey obligationKey,
                                                final PublicKey lendingMarket,
                                                final PublicKey repayReserveKey,
                                                final PublicKey reserveLiquidityMint,
                                                final PublicKey reserveDestinationLiquidity,
                                                final PublicKey userSourceLiquidity,
                                                final PublicKey tokenProgram,
                                                final PublicKey farmsAccountsObligationFarmUserStateKey,
                                                final PublicKey farmsAccountsReserveFarmStateKey,
                                                final long liquidityAmount) {
    return KaminoLendingProgram.repayObligationLiquidityV2(
        kaminoAccounts.invokedKLendProgram(),
        owner,
        obligationKey,
        lendingMarket,
        repayReserveKey,
        reserveLiquidityMint,
        reserveDestinationLiquidity,
        userSourceLiquidity,
        tokenProgram,
        solanaAccounts.instructionsSysVar(),
        farmsAccountsObligationFarmUserStateKey,
        farmsAccountsReserveFarmStateKey,
        kaminoAccounts.lendingMarketAuthPda(lendingMarket).publicKey(),
        kaminoAccounts.farmProgram(),
        liquidityAmount
    );
  }

  @Override
  public Instruction flashBorrowReserveLiquidity(final PublicKey lendingMarket,
                                                 final PublicKey reserveKey,
                                                 final PublicKey reserveLiquidityMint,
                                                 final PublicKey reserveSourceLiquidity,
                                                 final PublicKey userDestinationLiquidity,
                                                 final PublicKey reserveLiquidityFeeReceiver,
                                                 final PublicKey referrerTokenState,
                                                 final PublicKey referrerAccount,
                                                 final PublicKey tokenProgram,
                                                 final long liquidityAmount) {
    return KaminoLendingProgram.flashBorrowReserveLiquidity(
        kaminoAccounts.invokedKLendProgram(),
        owner,
        kaminoAccounts.lendingMarketAuthPda(lendingMarket).publicKey(),
        lendingMarket,
        reserveKey,
        reserveLiquidityMint,
        reserveSourceLiquidity,
        userDestinationLiquidity,
        reserveLiquidityFeeReceiver,
        KaminoAccounts.isNullKey(referrerTokenState) ? kaminoAccounts.kLendProgram() : referrerTokenState,
        KaminoAccounts.isNullKey(referrerAccount) ? kaminoAccounts.kLendProgram() : referrerAccount,
        solanaAccounts.instructionsSysVar(),
        tokenProgram,
        liquidityAmount
    );
  }

  @Override
  public Instruction flashRepayReserveLiquidity(final PublicKey lendingMarket,
                                                final PublicKey reserveKey,
                                                final PublicKey reserveLiquidityMint,
                                                final PublicKey reserveDestinationLiquidity,
                                                final PublicKey userSourceLiquidity,
                                                final PublicKey reserveLiquidityFeeReceiver,
                                                final PublicKey referrerTokenState,
                                                final PublicKey referrerAccount,
                                                final PublicKey tokenProgram,
                                                final long liquidityAmount,
                                                final int borrowInstructionIndex) {
    return KaminoLendingProgram.flashRepayReserveLiquidity(
        kaminoAccounts.invokedKLendProgram(),
        owner,
        kaminoAccounts.lendingMarketAuthPda(lendingMarket).publicKey(),
        lendingMarket,
        reserveKey,
        reserveLiquidityMint,
        reserveDestinationLiquidity,
        userSourceLiquidity,
        reserveLiquidityFeeReceiver,
        KaminoAccounts.isNullKey(referrerTokenState) ? kaminoAccounts.kLendProgram() : referrerTokenState,
        KaminoAccounts.isNullKey(referrerAccount) ? kaminoAccounts.kLendProgram() : referrerAccount,
        solanaAccounts.instructionsSysVar(),
        tokenProgram,
        liquidityAmount,
        borrowInstructionIndex
    );
  }

  @Override
  public Instruction refreshObligationFarmsForReserve(final PublicKey obligationKey,
                                                      final PublicKey reserveKey,
                                                      final PublicKey reserveFarmState,
                                                      final PublicKey obligationFarmUserState,
                                                      final PublicKey lendingMarket,
                                                      final int mode) {
    return KaminoLendingProgram.refreshObligationFarmsForReserve(
        kaminoAccounts.invokedKLendProgram(),
        owner,
        obligationKey,
        kaminoAccounts.lendingMarketAuthPda(lendingMarket).publicKey(),
        reserveKey,
        reserveFarmState,
        obligationFarmUserState,
        lendingMarket,
        kaminoAccounts.farmProgram(),
        solanaAccounts.rentSysVar(),
        solanaAccounts.systemProgram(),
        mode
    );
  }

  @Override
  public Instruction requestElevationGroup(final PublicKey obligationKey,
                                           final PublicKey lendingMarket,
                                           final int elevationGroup) {
    return KaminoLendingProgram.requestElevationGroup(
        kaminoAccounts.invokedKLendProgram(),
        owner,
        obligationKey,
        lendingMarket,
        elevationGroup
    );
  }

  @Override
  public Instruction setObligationOrder(final PublicKey obligationKey,
                                        final PublicKey lendingMarket,
                                        final int index,
                                        final ObligationOrder order) {
    return KaminoLendingProgram.setObligationOrder(
        kaminoAccounts.invokedKLendProgram(),
        owner,
        obligationKey,
        lendingMarket,
        index,
        order
    );
  }

  @Override
  public Instruction initiateObligationOwnershipTransfer(final PublicKey obligationKey, final PublicKey newOwner) {
    return KaminoLendingProgram.initiateObligationOwnershipTransfer(
        kaminoAccounts.invokedKLendProgram(),
        owner,
        obligationKey,
        solanaAccounts.instructionsSysVar(),
        newOwner
    );
  }

  @Override
  public Instruction acceptObligationOwnership(final PublicKey obligationKey) {
    return KaminoLendingProgram.acceptObligationOwnership(
        kaminoAccounts.invokedKLendProgram(),
        owner,
        obligationKey,
        solanaAccounts.instructionsSysVar()
    );
  }

  @Override
  public Instruction abortObligationOwnershipTransfer(final PublicKey obligationKey) {
    return KaminoLendingProgram.abortObligationOwnershipTransfer(
        kaminoAccounts.invokedKLendProgram(),
        owner,
        obligationKey,
        solanaAccounts.instructionsSysVar()
    );
  }

  // ===== FarmsProgram =====

  private PublicKey scopeOrSentinel(final PublicKey scopePricesKey) {
    return KaminoAccounts.isNullKey(scopePricesKey) ? kaminoAccounts.farmProgram() : scopePricesKey;
  }

  @Override
  public Instruction initializeFarmUser(final PublicKey farmStateKey,
                                        final PublicKey userStateKey,
                                        final PublicKey delegatee) {
    return FarmsProgram.initializeUser(
        kaminoAccounts.invokedFarmsProgram(),
        owner,
        feePayer,
        owner,
        KaminoAccounts.isNullKey(delegatee) ? owner : delegatee,
        userStateKey,
        farmStateKey,
        solanaAccounts.systemProgram(),
        solanaAccounts.rentSysVar()
    );
  }

  @Override
  public Instruction refreshFarm(final PublicKey farmStateKey, final PublicKey scopePricesKey) {
    return FarmsProgram.refreshFarm(
        kaminoAccounts.invokedFarmsProgram(),
        farmStateKey,
        scopeOrSentinel(scopePricesKey)
    );
  }

  @Override
  public Instruction refreshFarmUserState(final PublicKey userStateKey,
                                          final PublicKey farmStateKey,
                                          final PublicKey scopePricesKey) {
    return FarmsProgram.refreshUserState(
        kaminoAccounts.invokedFarmsProgram(),
        userStateKey,
        farmStateKey,
        scopeOrSentinel(scopePricesKey)
    );
  }

  @Override
  public Instruction harvestFarmReward(final PublicKey userStateKey,
                                       final PublicKey farmStateKey,
                                       final PublicKey rewardMint,
                                       final PublicKey userRewardTokenAccount,
                                       final PublicKey rewardsVault,
                                       final PublicKey rewardsTreasuryVault,
                                       final PublicKey farmVaultsAuthority,
                                       final PublicKey scopePricesKey,
                                       final PublicKey tokenProgram,
                                       final long rewardIndex) {
    return FarmsProgram.harvestReward(
        kaminoAccounts.invokedFarmsProgram(),
        owner,
        userStateKey,
        farmStateKey,
        kaminoAccounts.farmsGlobalConfig(),
        rewardMint,
        userRewardTokenAccount,
        rewardsVault,
        rewardsTreasuryVault,
        farmVaultsAuthority,
        scopeOrSentinel(scopePricesKey),
        tokenProgram,
        rewardIndex
    );
  }

  @Override
  public Instruction stakeFarm(final PublicKey userStateKey,
                               final PublicKey farmStateKey,
                               final PublicKey farmVault,
                               final PublicKey userAta,
                               final PublicKey tokenMint,
                               final PublicKey scopePricesKey,
                               final PublicKey tokenProgram,
                               final long amount) {
    return FarmsProgram.stake(
        kaminoAccounts.invokedFarmsProgram(),
        owner,
        userStateKey,
        farmStateKey,
        farmVault,
        userAta,
        tokenMint,
        scopeOrSentinel(scopePricesKey),
        tokenProgram,
        amount
    );
  }

  @Override
  public Instruction unstakeFarm(final PublicKey userStateKey,
                                 final PublicKey farmStateKey,
                                 final PublicKey scopePricesKey,
                                 final BigInteger stakeSharesScaled) {
    return FarmsProgram.unstake(
        kaminoAccounts.invokedFarmsProgram(),
        owner,
        userStateKey,
        farmStateKey,
        scopeOrSentinel(scopePricesKey),
        stakeSharesScaled
    );
  }

  @Override
  public Instruction withdrawUnstakedFarmDeposits(final PublicKey userStateKey,
                                                  final PublicKey farmStateKey,
                                                  final PublicKey userAta,
                                                  final PublicKey farmVault,
                                                  final PublicKey farmVaultsAuthority,
                                                  final PublicKey tokenProgram) {
    return FarmsProgram.withdrawUnstakedDeposits(
        kaminoAccounts.invokedFarmsProgram(),
        owner,
        userStateKey,
        farmStateKey,
        userAta,
        farmVault,
        farmVaultsAuthority,
        tokenProgram
    );
  }
}

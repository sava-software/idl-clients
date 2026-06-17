package software.sava.idl.clients.meteora.dlmm;

import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.rpc.Filter;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.meteora.MeteoraAccounts;
import software.sava.idl.clients.meteora.dlmm.gen.LbClmmProgram;
import software.sava.idl.clients.meteora.dlmm.gen.types.*;
import software.sava.rpc.json.http.client.SolanaRpcClient;
import software.sava.rpc.json.http.response.AccountInfo;

import java.util.List;
import java.util.OptionalInt;
import java.util.concurrent.CompletableFuture;

import static java.util.Objects.requireNonNullElse;
import static software.sava.idl.clients.meteora.dlmm.DlmmUtils.NO_REMAINING_ACCOUNTS;

record MeteoraDlmmClientImpl(SolanaAccounts solanaAccounts,
                             MeteoraAccounts meteoraAccounts,
                             PublicKey owner,
                             AccountMeta feePayer) implements MeteoraDlmmClient {

  private PublicKey memoProgram() {
    return solanaAccounts.readMemoProgramV2().publicKey();
  }

  private PublicKey eventAuthority() {
    return meteoraAccounts.eventAuthority().publicKey();
  }

  private PublicKey dlmmProgram() {
    return meteoraAccounts.dlmmProgram();
  }

  private AccountMeta invokedDlmm() {
    return meteoraAccounts.invokedDlmmProgram();
  }

  @Override
  public CompletableFuture<List<AccountInfo<PositionV2>>> fetchPositions(final SolanaRpcClient rpcClient) {
    return rpcClient.getProgramAccounts(
        meteoraAccounts.dlmmProgram(),
        List.of(
            PositionV2.SIZE_FILTER,
            Filter.createMemCompFilter(0, meteoraAccounts.positionV2Discriminator().data()),
            PositionV2.createOwnerFilter(owner)
        ),
        PositionV2.FACTORY
    );
  }

  @Override
  public CompletableFuture<List<AccountInfo<LbPair>>> fetchPairs(final SolanaRpcClient rpcClient) {
    return rpcClient.getProgramAccounts(
        meteoraAccounts.dlmmProgram(),
        List.of(
            LbPair.SIZE_FILTER,
            Filter.createMemCompFilter(0, meteoraAccounts.lbPairDiscriminator().data())
        ),
        LbPair.FACTORY
    );
  }

  @Override
  public CompletableFuture<List<AccountInfo<LbPair>>> fetchPairs(final SolanaRpcClient rpcClient,
                                                                 final PublicKey xMint,
                                                                 final PublicKey yMint) {
    return rpcClient.getProgramAccounts(
        meteoraAccounts.dlmmProgram(),
        List.of(
            LbPair.SIZE_FILTER,
            Filter.createMemCompFilter(0, meteoraAccounts.lbPairDiscriminator().data()),
            LbPair.createTokenXMintFilter(xMint),
            LbPair.createTokenYMintFilter(yMint)
        ),
        LbPair.FACTORY
    );
  }

  @Override
  public Instruction initializePosition(final PublicKey positionKey,
                                        final PublicKey lbPairKey,
                                        final int lowerBinId,
                                        final int width) {
    return LbClmmProgram.initializePosition(
        invokedDlmm(),
        solanaAccounts,
        feePayer.publicKey(),
        positionKey,
        lbPairKey,
        solanaAccounts.rentSysVar(),
        owner,
        eventAuthority(),
        dlmmProgram(),
        lowerBinId,
        width
    );
  }

  @Override
  public Instruction initializePositionWithSeeds(final PublicKey positionKey,
                                                 final PublicKey baseKey,
                                                 final PublicKey lbPairKey,
                                                 final int lowerBinId,
                                                 final int width) {
    return LbClmmProgram.initializePositionPda(
        invokedDlmm(),
        solanaAccounts,
        feePayer.publicKey(),
        baseKey,
        positionKey,
        lbPairKey,
        solanaAccounts.rentSysVar(),
        owner,
        eventAuthority(),
        dlmmProgram(),
        lowerBinId,
        width
    );
  }

  @Override
  public Instruction addLiquidityByStrategy(final PublicKey positionKey,
                                            final PublicKey lbPairKey,
                                            final PublicKey binArrayBitmapExtensionKey,
                                            final PublicKey userTokenXKey,
                                            final PublicKey userTokenYKey,
                                            final PublicKey reserveXKey,
                                            final PublicKey reserveYKey,
                                            final PublicKey tokenXMintKey,
                                            final PublicKey tokenYMintKey,
                                            final PublicKey tokenXProgramKey,
                                            final PublicKey tokenYProgramKey,
                                            final LiquidityParameterByStrategy liquidityParameter,
                                            final RemainingAccountsInfo remainingAccountsInfo) {
    return LbClmmProgram.addLiquidityByStrategy2(
        invokedDlmm(),
        positionKey,
        lbPairKey,
        binArrayBitmapExtensionKey,
        userTokenXKey, userTokenYKey,
        reserveXKey, reserveYKey,
        tokenXMintKey, tokenYMintKey,
        owner,
        tokenXProgramKey, tokenYProgramKey,
        eventAuthority(),
        dlmmProgram(),
        liquidityParameter,
        requireNonNullElse(remainingAccountsInfo, NO_REMAINING_ACCOUNTS)
    );
  }

  @Override
  public Instruction addLiquidity(final PublicKey positionKey,
                                  final PublicKey lbPairKey,
                                  final PublicKey binArrayBitmapExtensionKey,
                                  final PublicKey userTokenXKey,
                                  final PublicKey userTokenYKey,
                                  final PublicKey reserveXKey,
                                  final PublicKey reserveYKey,
                                  final PublicKey tokenXMintKey,
                                  final PublicKey tokenYMintKey,
                                  final PublicKey tokenXProgramKey,
                                  final PublicKey tokenYProgramKey,
                                  final LiquidityParameter liquidityParameter,
                                  final RemainingAccountsInfo remainingAccountsInfo) {
    return LbClmmProgram.addLiquidity2(
        invokedDlmm(),
        positionKey,
        lbPairKey,
        binArrayBitmapExtensionKey,
        userTokenXKey, userTokenYKey,
        reserveXKey, reserveYKey,
        tokenXMintKey, tokenYMintKey,
        owner,
        tokenXProgramKey, tokenYProgramKey,
        eventAuthority(),
        dlmmProgram(),
        liquidityParameter,
        requireNonNullElse(remainingAccountsInfo, NO_REMAINING_ACCOUNTS)
    );
  }

  @Override
  public Instruction addLiquidityOneSidePrecise(final PublicKey positionKey,
                                                final PublicKey lbPairKey,
                                                final PublicKey binArrayBitmapExtensionKey,
                                                final PublicKey userTokenKey,
                                                final PublicKey reserveKey,
                                                final PublicKey tokenMintKey,
                                                final PublicKey tokenProgramKey,
                                                final AddLiquiditySingleSidePreciseParameter2 liquidityParameter,
                                                final RemainingAccountsInfo remainingAccountsInfo) {
    return LbClmmProgram.addLiquidityOneSidePrecise2(
        invokedDlmm(),
        positionKey,
        lbPairKey,
        binArrayBitmapExtensionKey,
        userTokenKey,
        reserveKey,
        tokenMintKey,
        owner,
        tokenProgramKey,
        eventAuthority(),
        dlmmProgram(),
        liquidityParameter,
        requireNonNullElse(remainingAccountsInfo, NO_REMAINING_ACCOUNTS)
    );
  }

  @Override
  public Instruction removeLiquidityByRange(final PublicKey positionKey,
                                            final PublicKey lbPairKey,
                                            final PublicKey binArrayBitmapExtensionKey,
                                            final PublicKey userTokenXKey,
                                            final PublicKey userTokenYKey,
                                            final PublicKey reserveXKey,
                                            final PublicKey reserveYKey,
                                            final PublicKey tokenXMintKey,
                                            final PublicKey tokenYMintKey,
                                            final PublicKey tokenXProgramKey,
                                            final PublicKey tokenYProgramKey,
                                            final int fromBinId,
                                            final int toBinId,
                                            final int bpsToRemove,
                                            final RemainingAccountsInfo remainingAccountsInfo) {
    return LbClmmProgram.removeLiquidityByRange2(
        invokedDlmm(),
        positionKey,
        lbPairKey,
        binArrayBitmapExtensionKey,
        userTokenXKey, userTokenYKey,
        reserveXKey, reserveYKey,
        tokenXMintKey, tokenYMintKey,
        owner,
        tokenXProgramKey, tokenYProgramKey,
        memoProgram(),
        eventAuthority(),
        dlmmProgram(),
        fromBinId, toBinId, bpsToRemove,
        requireNonNullElse(remainingAccountsInfo, NO_REMAINING_ACCOUNTS)
    );
  }

  @Override
  public Instruction removeLiquidity(final PublicKey positionKey,
                                     final PublicKey lbPairKey,
                                     final PublicKey binArrayBitmapExtensionKey,
                                     final PublicKey userTokenXKey,
                                     final PublicKey userTokenYKey,
                                     final PublicKey reserveXKey,
                                     final PublicKey reserveYKey,
                                     final PublicKey tokenXMintKey,
                                     final PublicKey tokenYMintKey,
                                     final PublicKey tokenXProgramKey,
                                     final PublicKey tokenYProgramKey,
                                     final BinLiquidityReduction[] binLiquidityRemoval,
                                     final RemainingAccountsInfo remainingAccountsInfo) {
    return LbClmmProgram.removeLiquidity2(
        invokedDlmm(),
        positionKey,
        lbPairKey,
        binArrayBitmapExtensionKey,
        userTokenXKey, userTokenYKey,
        reserveXKey, reserveYKey,
        tokenXMintKey, tokenYMintKey,
        owner,
        tokenXProgramKey, tokenYProgramKey,
        memoProgram(),
        eventAuthority(),
        dlmmProgram(),
        binLiquidityRemoval,
        requireNonNullElse(remainingAccountsInfo, NO_REMAINING_ACCOUNTS)
    );
  }

  @Override
  public Instruction claimFee(final PublicKey lbPairKey,
                              final PublicKey positionKey,
                              final PublicKey reserveXKey,
                              final PublicKey reserveYKey,
                              final PublicKey userTokenXKey,
                              final PublicKey userTokenYKey,
                              final PublicKey tokenXMintKey,
                              final PublicKey tokenYMintKey,
                              final PublicKey tokenXProgramKey,
                              final PublicKey tokenYProgramKey,
                              final int minBinId,
                              final int maxBinId,
                              final RemainingAccountsInfo remainingAccountsInfo) {
    return LbClmmProgram.claimFee2(
        invokedDlmm(),
        lbPairKey,
        positionKey,
        owner,
        reserveXKey, reserveYKey,
        userTokenXKey, userTokenYKey,
        tokenXMintKey, tokenYMintKey,
        tokenXProgramKey, tokenYProgramKey,
        memoProgram(),
        eventAuthority(),
        dlmmProgram(),
        minBinId, maxBinId,
        requireNonNullElse(remainingAccountsInfo, NO_REMAINING_ACCOUNTS)
    );
  }

  @Override
  public Instruction claimReward(final PublicKey lbPairKey,
                                 final PublicKey positionKey,
                                 final PublicKey rewardVaultKey,
                                 final PublicKey rewardMintKey,
                                 final PublicKey userTokenAccountKey,
                                 final PublicKey tokenProgramKey,
                                 final int rewardIndex,
                                 final int minBinId,
                                 final int maxBinId,
                                 final RemainingAccountsInfo remainingAccountsInfo) {
    return LbClmmProgram.claimReward2(
        invokedDlmm(),
        lbPairKey,
        positionKey,
        owner,
        rewardVaultKey,
        rewardMintKey,
        userTokenAccountKey,
        tokenProgramKey,
        memoProgram(),
        eventAuthority(),
        dlmmProgram(),
        rewardIndex,
        minBinId, maxBinId,
        requireNonNullElse(remainingAccountsInfo, NO_REMAINING_ACCOUNTS)
    );
  }

  @Override
  public Instruction closePosition(final PublicKey positionKey, final PublicKey rentReceiverKey) {
    return LbClmmProgram.closePosition2(
        invokedDlmm(),
        positionKey,
        owner,
        rentReceiverKey,
        eventAuthority(),
        dlmmProgram()
    );
  }

  @Override
  public Instruction closePositionIfEmpty(final PublicKey positionKey, final PublicKey rentReceiverKey) {
    return LbClmmProgram.closePositionIfEmpty(
        invokedDlmm(),
        positionKey,
        owner,
        rentReceiverKey,
        eventAuthority(),
        dlmmProgram()
    );
  }

  @Override
  public Instruction initializePosition2(final PublicKey positionKey,
                                         final PublicKey lbPairKey,
                                         final int lowerBinId,
                                         final int width) {
    return LbClmmProgram.initializePosition2(
        invokedDlmm(),
        solanaAccounts,
        feePayer.publicKey(),
        positionKey,
        lbPairKey,
        owner,
        eventAuthority(),
        dlmmProgram(),
        lowerBinId,
        width
    );
  }

  @Override
  public Instruction increasePositionLength2(final PublicKey funderKey,
                                             final PublicKey lbPairKey,
                                             final PublicKey positionKey,
                                             final int minimumUpperBinId) {
    return LbClmmProgram.increasePositionLength2(
        invokedDlmm(),
        solanaAccounts,
        funderKey,
        lbPairKey,
        positionKey,
        owner,
        eventAuthority(),
        dlmmProgram(),
        minimumUpperBinId
    );
  }

  @Override
  public Instruction decreasePositionLength(final PublicKey positionKey,
                                            final PublicKey rentReceiverKey,
                                            final int lengthToRemove,
                                            final int side) {
    return LbClmmProgram.decreasePositionLength(
        invokedDlmm(),
        solanaAccounts,
        rentReceiverKey,
        positionKey,
        owner,
        eventAuthority(),
        dlmmProgram(),
        lengthToRemove,
        side
    );
  }

  @Override
  public Instruction goToABin(final PublicKey lbPairKey,
                              final PublicKey binArrayBitmapExtensionKey,
                              final PublicKey fromBinArrayKey,
                              final PublicKey toBinArrayKey,
                              final int binId) {
    return LbClmmProgram.goToABin(
        invokedDlmm(),
        lbPairKey,
        binArrayBitmapExtensionKey,
        fromBinArrayKey,
        toBinArrayKey,
        eventAuthority(),
        dlmmProgram(),
        binId
    );
  }

  @Override
  public Instruction addLiquidityByWeight(final PublicKey positionKey,
                                          final PublicKey lbPairKey,
                                          final PublicKey binArrayBitmapExtensionKey,
                                          final PublicKey userTokenXKey,
                                          final PublicKey userTokenYKey,
                                          final PublicKey reserveXKey,
                                          final PublicKey reserveYKey,
                                          final PublicKey tokenXMintKey,
                                          final PublicKey tokenYMintKey,
                                          final PublicKey tokenXProgramKey,
                                          final PublicKey tokenYProgramKey,
                                          final LiquidityParameterByWeight liquidityParameter,
                                          final RemainingAccountsInfo remainingAccountsInfo) {
    return LbClmmProgram.addLiquidityByWeight2(
        invokedDlmm(),
        positionKey,
        lbPairKey,
        binArrayBitmapExtensionKey,
        userTokenXKey, userTokenYKey,
        reserveXKey, reserveYKey,
        tokenXMintKey, tokenYMintKey,
        owner,
        tokenXProgramKey, tokenYProgramKey,
        eventAuthority(),
        dlmmProgram(),
        liquidityParameter,
        requireNonNullElse(remainingAccountsInfo, NO_REMAINING_ACCOUNTS)
    );
  }

  @Override
  public Instruction addLiquidityByStrategyOneSide(final PublicKey positionKey,
                                                   final PublicKey lbPairKey,
                                                   final PublicKey binArrayBitmapExtensionKey,
                                                   final PublicKey userTokenKey,
                                                   final PublicKey reserveKey,
                                                   final PublicKey tokenMintKey,
                                                   final PublicKey binArrayLowerKey,
                                                   final PublicKey binArrayUpperKey,
                                                   final PublicKey tokenProgramKey,
                                                   final LiquidityParameterByStrategyOneSide liquidityParameter) {
    return LbClmmProgram.addLiquidityByStrategyOneSide(
        invokedDlmm(),
        positionKey,
        lbPairKey,
        binArrayBitmapExtensionKey,
        userTokenKey,
        reserveKey,
        tokenMintKey,
        binArrayLowerKey,
        binArrayUpperKey,
        owner,
        tokenProgramKey,
        eventAuthority(),
        dlmmProgram(),
        liquidityParameter
    );
  }

  @Override
  public Instruction addLiquidityOneSide(final PublicKey positionKey,
                                         final PublicKey lbPairKey,
                                         final PublicKey binArrayBitmapExtensionKey,
                                         final PublicKey userTokenKey,
                                         final PublicKey reserveKey,
                                         final PublicKey tokenMintKey,
                                         final PublicKey binArrayLowerKey,
                                         final PublicKey binArrayUpperKey,
                                         final PublicKey tokenProgramKey,
                                         final LiquidityOneSideParameter liquidityParameter) {
    return LbClmmProgram.addLiquidityOneSide(
        invokedDlmm(),
        positionKey,
        lbPairKey,
        binArrayBitmapExtensionKey,
        userTokenKey,
        reserveKey,
        tokenMintKey,
        binArrayLowerKey,
        binArrayUpperKey,
        owner,
        tokenProgramKey,
        eventAuthority(),
        dlmmProgram(),
        liquidityParameter
    );
  }

  @Override
  public Instruction removeAllLiquidity(final PublicKey positionKey,
                                        final PublicKey lbPairKey,
                                        final PublicKey binArrayBitmapExtensionKey,
                                        final PublicKey userTokenXKey,
                                        final PublicKey userTokenYKey,
                                        final PublicKey reserveXKey,
                                        final PublicKey reserveYKey,
                                        final PublicKey tokenXMintKey,
                                        final PublicKey tokenYMintKey,
                                        final PublicKey binArrayLowerKey,
                                        final PublicKey binArrayUpperKey,
                                        final PublicKey tokenXProgramKey,
                                        final PublicKey tokenYProgramKey) {
    return LbClmmProgram.removeAllLiquidity(
        invokedDlmm(),
        positionKey,
        lbPairKey,
        binArrayBitmapExtensionKey,
        userTokenXKey, userTokenYKey,
        reserveXKey, reserveYKey,
        tokenXMintKey, tokenYMintKey,
        binArrayLowerKey, binArrayUpperKey,
        owner,
        tokenXProgramKey, tokenYProgramKey,
        eventAuthority(),
        dlmmProgram()
    );
  }

  @Override
  public Instruction rebalanceLiquidity(final PublicKey positionKey,
                                        final PublicKey lbPairKey,
                                        final PublicKey binArrayBitmapExtensionKey,
                                        final PublicKey userTokenXKey,
                                        final PublicKey userTokenYKey,
                                        final PublicKey reserveXKey,
                                        final PublicKey reserveYKey,
                                        final PublicKey tokenXMintKey,
                                        final PublicKey tokenYMintKey,
                                        final PublicKey rentPayerKey,
                                        final PublicKey tokenXProgramKey,
                                        final PublicKey tokenYProgramKey,
                                        final RebalanceLiquidityParams params,
                                        final RemainingAccountsInfo remainingAccountsInfo) {
    return LbClmmProgram.rebalanceLiquidity(
        invokedDlmm(),
        solanaAccounts,
        positionKey,
        lbPairKey,
        binArrayBitmapExtensionKey,
        userTokenXKey, userTokenYKey,
        reserveXKey, reserveYKey,
        tokenXMintKey, tokenYMintKey,
        owner,
        rentPayerKey,
        tokenXProgramKey, tokenYProgramKey,
        memoProgram(),
        eventAuthority(),
        dlmmProgram(),
        params,
        requireNonNullElse(remainingAccountsInfo, NO_REMAINING_ACCOUNTS)
    );
  }

  @Override
  public Instruction setPairStatusPermissionless(final PublicKey lbPairKey, final int status) {
    return LbClmmProgram.setPairStatusPermissionless(
        invokedDlmm(),
        lbPairKey,
        owner,
        status
    );
  }

  private PublicKey hostFeeInOrSentinel(final PublicKey hostFeeInKey) {
    return hostFeeInKey == null ? dlmmProgram() : hostFeeInKey;
  }

  @Override
  public Instruction swap(final PublicKey lbPairKey,
                          final PublicKey binArrayBitmapExtensionKey,
                          final PublicKey reserveXKey,
                          final PublicKey reserveYKey,
                          final PublicKey userTokenInKey,
                          final PublicKey userTokenOutKey,
                          final PublicKey tokenXMintKey,
                          final PublicKey tokenYMintKey,
                          final PublicKey oracleKey,
                          final PublicKey hostFeeInKey,
                          final PublicKey tokenXProgramKey,
                          final PublicKey tokenYProgramKey,
                          final long amountIn,
                          final long minAmountOut,
                          final RemainingAccountsInfo remainingAccountsInfo) {
    return LbClmmProgram.swap2(
        invokedDlmm(),
        lbPairKey,
        binArrayBitmapExtensionKey,
        reserveXKey, reserveYKey,
        userTokenInKey, userTokenOutKey,
        tokenXMintKey, tokenYMintKey,
        oracleKey,
        hostFeeInOrSentinel(hostFeeInKey),
        owner,
        tokenXProgramKey, tokenYProgramKey,
        memoProgram(),
        eventAuthority(),
        dlmmProgram(),
        amountIn, minAmountOut,
        requireNonNullElse(remainingAccountsInfo, NO_REMAINING_ACCOUNTS)
    );
  }

  @Override
  public Instruction swapExactOut(final PublicKey lbPairKey,
                                  final PublicKey binArrayBitmapExtensionKey,
                                  final PublicKey reserveXKey,
                                  final PublicKey reserveYKey,
                                  final PublicKey userTokenInKey,
                                  final PublicKey userTokenOutKey,
                                  final PublicKey tokenXMintKey,
                                  final PublicKey tokenYMintKey,
                                  final PublicKey oracleKey,
                                  final PublicKey hostFeeInKey,
                                  final PublicKey tokenXProgramKey,
                                  final PublicKey tokenYProgramKey,
                                  final long maxInAmount,
                                  final long outAmount,
                                  final RemainingAccountsInfo remainingAccountsInfo) {
    return LbClmmProgram.swapExactOut2(
        invokedDlmm(),
        lbPairKey,
        binArrayBitmapExtensionKey,
        reserveXKey, reserveYKey,
        userTokenInKey, userTokenOutKey,
        tokenXMintKey, tokenYMintKey,
        oracleKey,
        hostFeeInOrSentinel(hostFeeInKey),
        owner,
        tokenXProgramKey, tokenYProgramKey,
        memoProgram(),
        eventAuthority(),
        dlmmProgram(),
        maxInAmount, outAmount,
        requireNonNullElse(remainingAccountsInfo, NO_REMAINING_ACCOUNTS)
    );
  }

  @Override
  public Instruction swapWithPriceImpact(final PublicKey lbPairKey,
                                         final PublicKey binArrayBitmapExtensionKey,
                                         final PublicKey reserveXKey,
                                         final PublicKey reserveYKey,
                                         final PublicKey userTokenInKey,
                                         final PublicKey userTokenOutKey,
                                         final PublicKey tokenXMintKey,
                                         final PublicKey tokenYMintKey,
                                         final PublicKey oracleKey,
                                         final PublicKey hostFeeInKey,
                                         final PublicKey tokenXProgramKey,
                                         final PublicKey tokenYProgramKey,
                                         final long amountIn,
                                         final OptionalInt activeId,
                                         final int maxPriceImpactBps,
                                         final RemainingAccountsInfo remainingAccountsInfo) {
    return LbClmmProgram.swapWithPriceImpact2(
        invokedDlmm(),
        lbPairKey,
        binArrayBitmapExtensionKey,
        reserveXKey, reserveYKey,
        userTokenInKey, userTokenOutKey,
        tokenXMintKey, tokenYMintKey,
        oracleKey,
        hostFeeInOrSentinel(hostFeeInKey),
        owner,
        tokenXProgramKey, tokenYProgramKey,
        memoProgram(),
        eventAuthority(),
        dlmmProgram(),
        amountIn, activeId, maxPriceImpactBps,
        requireNonNullElse(remainingAccountsInfo, NO_REMAINING_ACCOUNTS)
    );
  }

  @Override
  public Instruction placeLimitOrder(final PublicKey lbPairKey,
                                     final PublicKey binArrayBitmapExtensionKey,
                                     final PublicKey reserveKey,
                                     final PublicKey tokenMintKey,
                                     final PublicKey limitOrderKey,
                                     final PublicKey userTokenKey,
                                     final PublicKey tokenProgramKey,
                                     final PlaceLimitOrderParams params,
                                     final RemainingAccountsInfo remainingAccountsInfo) {
    return LbClmmProgram.placeLimitOrder(
        invokedDlmm(),
        solanaAccounts,
        lbPairKey,
        binArrayBitmapExtensionKey,
        reserveKey,
        tokenMintKey,
        limitOrderKey,
        feePayer.publicKey(),
        owner,
        userTokenKey,
        owner,
        tokenProgramKey,
        eventAuthority(),
        dlmmProgram(),
        params,
        requireNonNullElse(remainingAccountsInfo, NO_REMAINING_ACCOUNTS)
    );
  }

  @Override
  public Instruction cancelLimitOrder(final PublicKey lbPairKey,
                                      final PublicKey binArrayBitmapExtensionKey,
                                      final PublicKey reserveXKey,
                                      final PublicKey reserveYKey,
                                      final PublicKey tokenXMintKey,
                                      final PublicKey tokenYMintKey,
                                      final PublicKey limitOrderKey,
                                      final PublicKey ownerTokenXKey,
                                      final PublicKey ownerTokenYKey,
                                      final PublicKey tokenXProgramKey,
                                      final PublicKey tokenYProgramKey,
                                      final int[] bins,
                                      final RemainingAccountsInfo remainingAccountsInfo) {
    return LbClmmProgram.cancelLimitOrder(
        invokedDlmm(),
        lbPairKey,
        binArrayBitmapExtensionKey,
        reserveXKey, reserveYKey,
        tokenXMintKey, tokenYMintKey,
        limitOrderKey,
        ownerTokenXKey, ownerTokenYKey,
        owner,
        tokenXProgramKey, tokenYProgramKey,
        memoProgram(),
        eventAuthority(),
        dlmmProgram(),
        bins,
        requireNonNullElse(remainingAccountsInfo, NO_REMAINING_ACCOUNTS)
    );
  }

  @Override
  public Instruction closeLimitOrderIfEmpty(final PublicKey limitOrderKey, final PublicKey rentReceiverKey) {
    return LbClmmProgram.closeLimitOrderIfEmpty(
        invokedDlmm(),
        limitOrderKey,
        owner,
        rentReceiverKey,
        eventAuthority(),
        dlmmProgram()
    );
  }
}

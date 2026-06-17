package software.sava.idl.clients.orca;

import java.math.BigInteger;

import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.orca.whirlpools.gen.WhirlpoolProgram;
import software.sava.idl.clients.orca.whirlpools.gen.types.IncreaseLiquidityMethod;
import software.sava.idl.clients.orca.whirlpools.gen.types.LockType;
import software.sava.idl.clients.orca.whirlpools.gen.types.OpenPositionBumps;
import software.sava.idl.clients.orca.whirlpools.gen.types.OpenPositionWithMetadataBumps;
import software.sava.idl.clients.orca.whirlpools.gen.types.RemainingAccountsInfo;
import software.sava.idl.clients.orca.whirlpools.gen.types.RepositionLiquidityMethod;

final class OrcaWhirlpoolsClientImpl implements OrcaWhirlpoolsClient {

  private final SolanaAccounts solanaAccounts;
  private final OrcaAccounts accounts;

  OrcaWhirlpoolsClientImpl(final SolanaAccounts solanaAccounts, final OrcaAccounts accounts) {
    this.solanaAccounts = solanaAccounts;
    this.accounts = accounts;
  }

  @Override
  public SolanaAccounts solanaAccounts() {
    return solanaAccounts;
  }

  @Override
  public OrcaAccounts orcaAccounts() {
    return accounts;
  }

  private PublicKey whirlpoolProgramId() {
    return accounts.invokedWhirlpoolProgram().publicKey();
  }

  @Override
  public Instruction openPosition(final PublicKey funderKey,
                                  final PublicKey ownerKey,
                                  final PublicKey positionKey,
                                  final PublicKey positionMintKey,
                                  final PublicKey positionTokenAccountKey,
                                  final PublicKey whirlpoolKey,
                                  final PublicKey tokenProgramKey,
                                  final OpenPositionBumps bumps,
                                  final int tickLowerIndex,
                                  final int tickUpperIndex) {
    return WhirlpoolProgram.openPosition(
        accounts.invokedWhirlpoolProgram(),
        solanaAccounts,
        funderKey,
        ownerKey,
        positionKey,
        positionMintKey,
        positionTokenAccountKey,
        whirlpoolKey,
        tokenProgramKey,
        whirlpoolProgramId(),
        bumps,
        tickLowerIndex,
        tickUpperIndex
    );
  }

  @Override
  public Instruction openPositionWithMetadata(final PublicKey funderKey,
                                              final PublicKey ownerKey,
                                              final PublicKey positionKey,
                                              final PublicKey positionMintKey,
                                              final PublicKey positionMetadataAccountKey,
                                              final PublicKey positionTokenAccountKey,
                                              final PublicKey whirlpoolKey,
                                              final PublicKey tokenProgramKey,
                                              final PublicKey metadataProgramKey,
                                              final PublicKey metadataUpdateAuthKey,
                                              final OpenPositionWithMetadataBumps bumps,
                                              final int tickLowerIndex,
                                              final int tickUpperIndex) {
    return WhirlpoolProgram.openPositionWithMetadata(
        accounts.invokedWhirlpoolProgram(),
        solanaAccounts,
        funderKey,
        ownerKey,
        positionKey,
        positionMintKey,
        positionMetadataAccountKey,
        positionTokenAccountKey,
        whirlpoolKey,
        tokenProgramKey,
        metadataProgramKey,
        metadataUpdateAuthKey,
        whirlpoolProgramId(),
        bumps,
        tickLowerIndex,
        tickUpperIndex
    );
  }

  @Override
  public Instruction openPositionWithTokenExtensions(final PublicKey funderKey,
                                                     final PublicKey ownerKey,
                                                     final PublicKey positionKey,
                                                     final PublicKey positionMintKey,
                                                     final PublicKey positionTokenAccountKey,
                                                     final PublicKey whirlpoolKey,
                                                     final PublicKey token2022ProgramKey,
                                                     final PublicKey metadataUpdateAuthKey,
                                                     final int tickLowerIndex,
                                                     final int tickUpperIndex,
                                                     final boolean withTokenMetadataExtension) {
    return WhirlpoolProgram.openPositionWithTokenExtensions(
        accounts.invokedWhirlpoolProgram(),
        solanaAccounts,
        funderKey,
        ownerKey,
        positionKey,
        positionMintKey,
        positionTokenAccountKey,
        whirlpoolKey,
        token2022ProgramKey,
        metadataUpdateAuthKey,
        whirlpoolProgramId(),
        tickLowerIndex,
        tickUpperIndex,
        withTokenMetadataExtension
    );
  }

  @Override
  public Instruction closePosition(final PublicKey positionAuthorityKey,
                                   final PublicKey receiverKey,
                                   final PublicKey positionKey,
                                   final PublicKey positionMintKey,
                                   final PublicKey positionTokenAccountKey,
                                   final PublicKey tokenProgramKey) {
    return WhirlpoolProgram.closePosition(
        accounts.invokedWhirlpoolProgram(),
        positionAuthorityKey,
        receiverKey,
        positionKey,
        positionMintKey,
        positionTokenAccountKey,
        tokenProgramKey,
        whirlpoolProgramId()
    );
  }

  @Override
  public Instruction closePositionWithTokenExtensions(final PublicKey positionAuthorityKey,
                                                      final PublicKey receiverKey,
                                                      final PublicKey positionKey,
                                                      final PublicKey positionMintKey,
                                                      final PublicKey positionTokenAccountKey,
                                                      final PublicKey token2022ProgramKey) {
    return WhirlpoolProgram.closePositionWithTokenExtensions(
        accounts.invokedWhirlpoolProgram(),
        positionAuthorityKey,
        receiverKey,
        positionKey,
        positionMintKey,
        positionTokenAccountKey,
        token2022ProgramKey,
        whirlpoolProgramId()
    );
  }

  @Override
  public Instruction resetPositionRange(final PublicKey funderKey,
                                        final PublicKey positionAuthorityKey,
                                        final PublicKey whirlpoolKey,
                                        final PublicKey positionKey,
                                        final PublicKey positionTokenAccountKey,
                                        final int newTickLowerIndex,
                                        final int newTickUpperIndex) {
    return WhirlpoolProgram.resetPositionRange(
        accounts.invokedWhirlpoolProgram(),
        solanaAccounts,
        funderKey,
        positionAuthorityKey,
        whirlpoolKey,
        positionKey,
        positionTokenAccountKey,
        whirlpoolProgramId(),
        newTickLowerIndex,
        newTickUpperIndex
    );
  }

  @Override
  public Instruction lockPosition(final PublicKey funderKey,
                                  final PublicKey positionAuthorityKey,
                                  final PublicKey positionKey,
                                  final PublicKey positionMintKey,
                                  final PublicKey positionTokenAccountKey,
                                  final PublicKey lockConfigKey,
                                  final PublicKey whirlpoolKey,
                                  final PublicKey token2022ProgramKey,
                                  final LockType lockType) {
    return WhirlpoolProgram.lockPosition(
        accounts.invokedWhirlpoolProgram(),
        solanaAccounts,
        funderKey,
        positionAuthorityKey,
        positionKey,
        positionMintKey,
        positionTokenAccountKey,
        lockConfigKey,
        whirlpoolKey,
        token2022ProgramKey,
        whirlpoolProgramId(),
        lockType
    );
  }

  @Override
  public Instruction transferLockedPosition(final PublicKey positionAuthorityKey,
                                            final PublicKey receiverKey,
                                            final PublicKey positionKey,
                                            final PublicKey positionMintKey,
                                            final PublicKey positionTokenAccountKey,
                                            final PublicKey destinationTokenAccountKey,
                                            final PublicKey lockConfigKey,
                                            final PublicKey token2022ProgramKey) {
    return WhirlpoolProgram.transferLockedPosition(
        accounts.invokedWhirlpoolProgram(),
        positionAuthorityKey,
        receiverKey,
        positionKey,
        positionMintKey,
        positionTokenAccountKey,
        destinationTokenAccountKey,
        lockConfigKey,
        token2022ProgramKey,
        whirlpoolProgramId()
    );
  }

  @Override
  public Instruction initializePositionBundle(final PublicKey positionBundleKey,
                                              final PublicKey positionBundleMintKey,
                                              final PublicKey positionBundleTokenAccountKey,
                                              final PublicKey positionBundleOwnerKey,
                                              final PublicKey funderKey,
                                              final PublicKey tokenProgramKey) {
    return WhirlpoolProgram.initializePositionBundle(
        accounts.invokedWhirlpoolProgram(),
        solanaAccounts,
        positionBundleKey,
        positionBundleMintKey,
        positionBundleTokenAccountKey,
        positionBundleOwnerKey,
        funderKey,
        tokenProgramKey,
        whirlpoolProgramId()
    );
  }

  @Override
  public Instruction initializePositionBundleWithMetadata(final PublicKey positionBundleKey,
                                                          final PublicKey positionBundleMintKey,
                                                          final PublicKey positionBundleMetadataKey,
                                                          final PublicKey positionBundleTokenAccountKey,
                                                          final PublicKey positionBundleOwnerKey,
                                                          final PublicKey funderKey,
                                                          final PublicKey metadataUpdateAuthKey,
                                                          final PublicKey tokenProgramKey,
                                                          final PublicKey metadataProgramKey) {
    return WhirlpoolProgram.initializePositionBundleWithMetadata(
        accounts.invokedWhirlpoolProgram(),
        solanaAccounts,
        positionBundleKey,
        positionBundleMintKey,
        positionBundleMetadataKey,
        positionBundleTokenAccountKey,
        positionBundleOwnerKey,
        funderKey,
        metadataUpdateAuthKey,
        tokenProgramKey,
        metadataProgramKey,
        whirlpoolProgramId()
    );
  }

  @Override
  public Instruction deletePositionBundle(final PublicKey positionBundleKey,
                                          final PublicKey positionBundleMintKey,
                                          final PublicKey positionBundleTokenAccountKey,
                                          final PublicKey positionBundleOwnerKey,
                                          final PublicKey receiverKey,
                                          final PublicKey tokenProgramKey) {
    return WhirlpoolProgram.deletePositionBundle(
        accounts.invokedWhirlpoolProgram(),
        positionBundleKey,
        positionBundleMintKey,
        positionBundleTokenAccountKey,
        positionBundleOwnerKey,
        receiverKey,
        tokenProgramKey,
        whirlpoolProgramId()
    );
  }

  @Override
  public Instruction openBundledPosition(final PublicKey bundledPositionKey,
                                         final PublicKey positionBundleKey,
                                         final PublicKey positionBundleTokenAccountKey,
                                         final PublicKey positionBundleAuthorityKey,
                                         final PublicKey whirlpoolKey,
                                         final PublicKey funderKey,
                                         final int bundleIndex,
                                         final int tickLowerIndex,
                                         final int tickUpperIndex) {
    return WhirlpoolProgram.openBundledPosition(
        accounts.invokedWhirlpoolProgram(),
        solanaAccounts,
        bundledPositionKey,
        positionBundleKey,
        positionBundleTokenAccountKey,
        positionBundleAuthorityKey,
        whirlpoolKey,
        funderKey,
        whirlpoolProgramId(),
        bundleIndex,
        tickLowerIndex,
        tickUpperIndex
    );
  }

  @Override
  public Instruction closeBundledPosition(final PublicKey bundledPositionKey,
                                          final PublicKey positionBundleKey,
                                          final PublicKey positionBundleTokenAccountKey,
                                          final PublicKey positionBundleAuthorityKey,
                                          final PublicKey receiverKey,
                                          final int bundleIndex) {
    return WhirlpoolProgram.closeBundledPosition(
        accounts.invokedWhirlpoolProgram(),
        bundledPositionKey,
        positionBundleKey,
        positionBundleTokenAccountKey,
        positionBundleAuthorityKey,
        receiverKey,
        whirlpoolProgramId(),
        bundleIndex
    );
  }

  @Override
  public Instruction initializeTickArray(final PublicKey whirlpoolKey,
                                         final PublicKey funderKey,
                                         final PublicKey tickArrayKey,
                                         final int startTickIndex) {
    return WhirlpoolProgram.initializeTickArray(
        accounts.invokedWhirlpoolProgram(),
        solanaAccounts,
        whirlpoolKey,
        funderKey,
        tickArrayKey,
        whirlpoolProgramId(),
        startTickIndex
    );
  }

  @Override
  public Instruction initializeDynamicTickArray(final PublicKey whirlpoolKey,
                                                final PublicKey funderKey,
                                                final PublicKey tickArrayKey,
                                                final int startTickIndex,
                                                final boolean idempotent) {
    return WhirlpoolProgram.initializeDynamicTickArray(
        accounts.invokedWhirlpoolProgram(),
        solanaAccounts,
        whirlpoolKey,
        funderKey,
        tickArrayKey,
        whirlpoolProgramId(),
        startTickIndex,
        idempotent
    );
  }

  @Override
  public Instruction increaseLiquidity(final PublicKey whirlpoolKey,
                                       final PublicKey tokenProgramKey,
                                       final PublicKey positionAuthorityKey,
                                       final PublicKey positionKey,
                                       final PublicKey positionTokenAccountKey,
                                       final PublicKey tokenOwnerAccountAKey,
                                       final PublicKey tokenOwnerAccountBKey,
                                       final PublicKey tokenVaultAKey,
                                       final PublicKey tokenVaultBKey,
                                       final PublicKey tickArrayLowerKey,
                                       final PublicKey tickArrayUpperKey,
                                       final BigInteger liquidityAmount,
                                       final long tokenMaxA,
                                       final long tokenMaxB) {
    return WhirlpoolProgram.increaseLiquidity(
        accounts.invokedWhirlpoolProgram(),
        whirlpoolKey,
        tokenProgramKey,
        positionAuthorityKey,
        positionKey,
        positionTokenAccountKey,
        tokenOwnerAccountAKey,
        tokenOwnerAccountBKey,
        tokenVaultAKey,
        tokenVaultBKey,
        tickArrayLowerKey,
        tickArrayUpperKey,
        whirlpoolProgramId(),
        liquidityAmount,
        tokenMaxA,
        tokenMaxB
    );
  }

  @Override
  public Instruction increaseLiquidityV2(final PublicKey whirlpoolKey,
                                         final PublicKey tokenProgramAKey,
                                         final PublicKey tokenProgramBKey,
                                         final PublicKey memoProgramKey,
                                         final PublicKey positionAuthorityKey,
                                         final PublicKey positionKey,
                                         final PublicKey positionTokenAccountKey,
                                         final PublicKey tokenMintAKey,
                                         final PublicKey tokenMintBKey,
                                         final PublicKey tokenOwnerAccountAKey,
                                         final PublicKey tokenOwnerAccountBKey,
                                         final PublicKey tokenVaultAKey,
                                         final PublicKey tokenVaultBKey,
                                         final PublicKey tickArrayLowerKey,
                                         final PublicKey tickArrayUpperKey,
                                         final BigInteger liquidityAmount,
                                         final long tokenMaxA,
                                         final long tokenMaxB,
                                         final RemainingAccountsInfo remainingAccountsInfo) {
    return WhirlpoolProgram.increaseLiquidityV2(
        accounts.invokedWhirlpoolProgram(),
        whirlpoolKey,
        tokenProgramAKey,
        tokenProgramBKey,
        memoProgramKey,
        positionAuthorityKey,
        positionKey,
        positionTokenAccountKey,
        tokenMintAKey,
        tokenMintBKey,
        tokenOwnerAccountAKey,
        tokenOwnerAccountBKey,
        tokenVaultAKey,
        tokenVaultBKey,
        tickArrayLowerKey,
        tickArrayUpperKey,
        whirlpoolProgramId(),
        liquidityAmount,
        tokenMaxA,
        tokenMaxB,
        remainingAccountsInfo
    );
  }

  @Override
  public Instruction increaseLiquidityByTokenAmountsV2(final PublicKey whirlpoolKey,
                                                       final PublicKey tokenProgramAKey,
                                                       final PublicKey tokenProgramBKey,
                                                       final PublicKey memoProgramKey,
                                                       final PublicKey positionAuthorityKey,
                                                       final PublicKey positionKey,
                                                       final PublicKey positionTokenAccountKey,
                                                       final PublicKey tokenMintAKey,
                                                       final PublicKey tokenMintBKey,
                                                       final PublicKey tokenOwnerAccountAKey,
                                                       final PublicKey tokenOwnerAccountBKey,
                                                       final PublicKey tokenVaultAKey,
                                                       final PublicKey tokenVaultBKey,
                                                       final PublicKey tickArrayLowerKey,
                                                       final PublicKey tickArrayUpperKey,
                                                       final IncreaseLiquidityMethod method,
                                                       final RemainingAccountsInfo remainingAccountsInfo) {
    return WhirlpoolProgram.increaseLiquidityByTokenAmountsV2(
        accounts.invokedWhirlpoolProgram(),
        whirlpoolKey,
        tokenProgramAKey,
        tokenProgramBKey,
        memoProgramKey,
        positionAuthorityKey,
        positionKey,
        positionTokenAccountKey,
        tokenMintAKey,
        tokenMintBKey,
        tokenOwnerAccountAKey,
        tokenOwnerAccountBKey,
        tokenVaultAKey,
        tokenVaultBKey,
        tickArrayLowerKey,
        tickArrayUpperKey,
        whirlpoolProgramId(),
        method,
        remainingAccountsInfo
    );
  }

  @Override
  public Instruction decreaseLiquidity(final PublicKey whirlpoolKey,
                                       final PublicKey tokenProgramKey,
                                       final PublicKey positionAuthorityKey,
                                       final PublicKey positionKey,
                                       final PublicKey positionTokenAccountKey,
                                       final PublicKey tokenOwnerAccountAKey,
                                       final PublicKey tokenOwnerAccountBKey,
                                       final PublicKey tokenVaultAKey,
                                       final PublicKey tokenVaultBKey,
                                       final PublicKey tickArrayLowerKey,
                                       final PublicKey tickArrayUpperKey,
                                       final BigInteger liquidityAmount,
                                       final long tokenMinA,
                                       final long tokenMinB) {
    return WhirlpoolProgram.decreaseLiquidity(
        accounts.invokedWhirlpoolProgram(),
        whirlpoolKey,
        tokenProgramKey,
        positionAuthorityKey,
        positionKey,
        positionTokenAccountKey,
        tokenOwnerAccountAKey,
        tokenOwnerAccountBKey,
        tokenVaultAKey,
        tokenVaultBKey,
        tickArrayLowerKey,
        tickArrayUpperKey,
        whirlpoolProgramId(),
        liquidityAmount,
        tokenMinA,
        tokenMinB
    );
  }

  @Override
  public Instruction decreaseLiquidityV2(final PublicKey whirlpoolKey,
                                         final PublicKey tokenProgramAKey,
                                         final PublicKey tokenProgramBKey,
                                         final PublicKey memoProgramKey,
                                         final PublicKey positionAuthorityKey,
                                         final PublicKey positionKey,
                                         final PublicKey positionTokenAccountKey,
                                         final PublicKey tokenMintAKey,
                                         final PublicKey tokenMintBKey,
                                         final PublicKey tokenOwnerAccountAKey,
                                         final PublicKey tokenOwnerAccountBKey,
                                         final PublicKey tokenVaultAKey,
                                         final PublicKey tokenVaultBKey,
                                         final PublicKey tickArrayLowerKey,
                                         final PublicKey tickArrayUpperKey,
                                         final BigInteger liquidityAmount,
                                         final long tokenMinA,
                                         final long tokenMinB,
                                         final RemainingAccountsInfo remainingAccountsInfo) {
    return WhirlpoolProgram.decreaseLiquidityV2(
        accounts.invokedWhirlpoolProgram(),
        whirlpoolKey,
        tokenProgramAKey,
        tokenProgramBKey,
        memoProgramKey,
        positionAuthorityKey,
        positionKey,
        positionTokenAccountKey,
        tokenMintAKey,
        tokenMintBKey,
        tokenOwnerAccountAKey,
        tokenOwnerAccountBKey,
        tokenVaultAKey,
        tokenVaultBKey,
        tickArrayLowerKey,
        tickArrayUpperKey,
        whirlpoolProgramId(),
        liquidityAmount,
        tokenMinA,
        tokenMinB,
        remainingAccountsInfo
    );
  }

  @Override
  public Instruction repositionLiquidityV2(final PublicKey whirlpoolKey,
                                           final PublicKey tokenProgramAKey,
                                           final PublicKey tokenProgramBKey,
                                           final PublicKey memoProgramKey,
                                           final PublicKey positionAuthorityKey,
                                           final PublicKey funderKey,
                                           final PublicKey positionKey,
                                           final PublicKey positionTokenAccountKey,
                                           final PublicKey tokenMintAKey,
                                           final PublicKey tokenMintBKey,
                                           final PublicKey tokenOwnerAccountAKey,
                                           final PublicKey tokenOwnerAccountBKey,
                                           final PublicKey tokenVaultAKey,
                                           final PublicKey tokenVaultBKey,
                                           final PublicKey existingTickArrayLowerKey,
                                           final PublicKey existingTickArrayUpperKey,
                                           final PublicKey newTickArrayLowerKey,
                                           final PublicKey newTickArrayUpperKey,
                                           final int newTickLowerIndex,
                                           final int newTickUpperIndex,
                                           final RepositionLiquidityMethod method,
                                           final RemainingAccountsInfo remainingAccountsInfo) {
    return WhirlpoolProgram.repositionLiquidityV2(
        accounts.invokedWhirlpoolProgram(),
        solanaAccounts,
        whirlpoolKey,
        tokenProgramAKey,
        tokenProgramBKey,
        memoProgramKey,
        positionAuthorityKey,
        funderKey,
        positionKey,
        positionTokenAccountKey,
        tokenMintAKey,
        tokenMintBKey,
        tokenOwnerAccountAKey,
        tokenOwnerAccountBKey,
        tokenVaultAKey,
        tokenVaultBKey,
        existingTickArrayLowerKey,
        existingTickArrayUpperKey,
        newTickArrayLowerKey,
        newTickArrayUpperKey,
        whirlpoolProgramId(),
        newTickLowerIndex,
        newTickUpperIndex,
        method,
        remainingAccountsInfo
    );
  }

  @Override
  public Instruction updateFeesAndRewards(final PublicKey whirlpoolKey,
                                          final PublicKey positionKey,
                                          final PublicKey tickArrayLowerKey,
                                          final PublicKey tickArrayUpperKey) {
    return WhirlpoolProgram.updateFeesAndRewards(
        accounts.invokedWhirlpoolProgram(),
        whirlpoolKey,
        positionKey,
        tickArrayLowerKey,
        tickArrayUpperKey,
        whirlpoolProgramId()
    );
  }

  @Override
  public Instruction collectFees(final PublicKey whirlpoolKey,
                                 final PublicKey positionAuthorityKey,
                                 final PublicKey positionKey,
                                 final PublicKey positionTokenAccountKey,
                                 final PublicKey tokenOwnerAccountAKey,
                                 final PublicKey tokenVaultAKey,
                                 final PublicKey tokenOwnerAccountBKey,
                                 final PublicKey tokenVaultBKey,
                                 final PublicKey tokenProgramKey) {
    return WhirlpoolProgram.collectFees(
        accounts.invokedWhirlpoolProgram(),
        whirlpoolKey,
        positionAuthorityKey,
        positionKey,
        positionTokenAccountKey,
        tokenOwnerAccountAKey,
        tokenVaultAKey,
        tokenOwnerAccountBKey,
        tokenVaultBKey,
        tokenProgramKey,
        whirlpoolProgramId()
    );
  }

  @Override
  public Instruction collectFeesV2(final PublicKey whirlpoolKey,
                                   final PublicKey positionAuthorityKey,
                                   final PublicKey positionKey,
                                   final PublicKey positionTokenAccountKey,
                                   final PublicKey tokenMintAKey,
                                   final PublicKey tokenMintBKey,
                                   final PublicKey tokenOwnerAccountAKey,
                                   final PublicKey tokenVaultAKey,
                                   final PublicKey tokenOwnerAccountBKey,
                                   final PublicKey tokenVaultBKey,
                                   final PublicKey tokenProgramAKey,
                                   final PublicKey tokenProgramBKey,
                                   final PublicKey memoProgramKey,
                                   final RemainingAccountsInfo remainingAccountsInfo) {
    return WhirlpoolProgram.collectFeesV2(
        accounts.invokedWhirlpoolProgram(),
        whirlpoolKey,
        positionAuthorityKey,
        positionKey,
        positionTokenAccountKey,
        tokenMintAKey,
        tokenMintBKey,
        tokenOwnerAccountAKey,
        tokenVaultAKey,
        tokenOwnerAccountBKey,
        tokenVaultBKey,
        tokenProgramAKey,
        tokenProgramBKey,
        memoProgramKey,
        whirlpoolProgramId(),
        remainingAccountsInfo
    );
  }

  @Override
  public Instruction collectReward(final PublicKey whirlpoolKey,
                                   final PublicKey positionAuthorityKey,
                                   final PublicKey positionKey,
                                   final PublicKey positionTokenAccountKey,
                                   final PublicKey rewardOwnerAccountKey,
                                   final PublicKey rewardVaultKey,
                                   final PublicKey tokenProgramKey,
                                   final int rewardIndex) {
    return WhirlpoolProgram.collectReward(
        accounts.invokedWhirlpoolProgram(),
        whirlpoolKey,
        positionAuthorityKey,
        positionKey,
        positionTokenAccountKey,
        rewardOwnerAccountKey,
        rewardVaultKey,
        tokenProgramKey,
        whirlpoolProgramId(),
        rewardIndex
    );
  }

  @Override
  public Instruction collectRewardV2(final PublicKey whirlpoolKey,
                                     final PublicKey positionAuthorityKey,
                                     final PublicKey positionKey,
                                     final PublicKey positionTokenAccountKey,
                                     final PublicKey rewardOwnerAccountKey,
                                     final PublicKey rewardMintKey,
                                     final PublicKey rewardVaultKey,
                                     final PublicKey rewardTokenProgramKey,
                                     final PublicKey memoProgramKey,
                                     final int rewardIndex,
                                     final RemainingAccountsInfo remainingAccountsInfo) {
    return WhirlpoolProgram.collectRewardV2(
        accounts.invokedWhirlpoolProgram(),
        whirlpoolKey,
        positionAuthorityKey,
        positionKey,
        positionTokenAccountKey,
        rewardOwnerAccountKey,
        rewardMintKey,
        rewardVaultKey,
        rewardTokenProgramKey,
        memoProgramKey,
        whirlpoolProgramId(),
        rewardIndex,
        remainingAccountsInfo
    );
  }

  @Override
  public Instruction swap(final PublicKey tokenProgramKey,
                          final PublicKey tokenAuthorityKey,
                          final PublicKey whirlpoolKey,
                          final PublicKey tokenOwnerAccountAKey,
                          final PublicKey tokenVaultAKey,
                          final PublicKey tokenOwnerAccountBKey,
                          final PublicKey tokenVaultBKey,
                          final PublicKey tickArray0Key,
                          final PublicKey tickArray1Key,
                          final PublicKey tickArray2Key,
                          final PublicKey oracleKey,
                          final long amount,
                          final long otherAmountThreshold,
                          final BigInteger sqrtPriceLimit,
                          final boolean amountSpecifiedIsInput,
                          final boolean aToB) {
    return WhirlpoolProgram.swap(
        accounts.invokedWhirlpoolProgram(),
        tokenProgramKey,
        tokenAuthorityKey,
        whirlpoolKey,
        tokenOwnerAccountAKey,
        tokenVaultAKey,
        tokenOwnerAccountBKey,
        tokenVaultBKey,
        tickArray0Key,
        tickArray1Key,
        tickArray2Key,
        oracleKey,
        whirlpoolProgramId(),
        amount,
        otherAmountThreshold,
        sqrtPriceLimit,
        amountSpecifiedIsInput,
        aToB
    );
  }

  @Override
  public Instruction swapV2(final PublicKey tokenProgramAKey,
                            final PublicKey tokenProgramBKey,
                            final PublicKey memoProgramKey,
                            final PublicKey tokenAuthorityKey,
                            final PublicKey whirlpoolKey,
                            final PublicKey tokenMintAKey,
                            final PublicKey tokenMintBKey,
                            final PublicKey tokenOwnerAccountAKey,
                            final PublicKey tokenVaultAKey,
                            final PublicKey tokenOwnerAccountBKey,
                            final PublicKey tokenVaultBKey,
                            final PublicKey tickArray0Key,
                            final PublicKey tickArray1Key,
                            final PublicKey tickArray2Key,
                            final PublicKey oracleKey,
                            final long amount,
                            final long otherAmountThreshold,
                            final BigInteger sqrtPriceLimit,
                            final boolean amountSpecifiedIsInput,
                            final boolean aToB,
                            final RemainingAccountsInfo remainingAccountsInfo) {
    return WhirlpoolProgram.swapV2(
        accounts.invokedWhirlpoolProgram(),
        tokenProgramAKey,
        tokenProgramBKey,
        memoProgramKey,
        tokenAuthorityKey,
        whirlpoolKey,
        tokenMintAKey,
        tokenMintBKey,
        tokenOwnerAccountAKey,
        tokenVaultAKey,
        tokenOwnerAccountBKey,
        tokenVaultBKey,
        tickArray0Key,
        tickArray1Key,
        tickArray2Key,
        oracleKey,
        whirlpoolProgramId(),
        amount,
        otherAmountThreshold,
        sqrtPriceLimit,
        amountSpecifiedIsInput,
        aToB,
        remainingAccountsInfo
    );
  }

  @Override
  public Instruction twoHopSwap(final PublicKey tokenProgramKey,
                                final PublicKey tokenAuthorityKey,
                                final PublicKey whirlpoolOneKey,
                                final PublicKey whirlpoolTwoKey,
                                final PublicKey tokenOwnerAccountOneAKey,
                                final PublicKey tokenVaultOneAKey,
                                final PublicKey tokenOwnerAccountOneBKey,
                                final PublicKey tokenVaultOneBKey,
                                final PublicKey tokenOwnerAccountTwoAKey,
                                final PublicKey tokenVaultTwoAKey,
                                final PublicKey tokenOwnerAccountTwoBKey,
                                final PublicKey tokenVaultTwoBKey,
                                final PublicKey tickArrayOne0Key,
                                final PublicKey tickArrayOne1Key,
                                final PublicKey tickArrayOne2Key,
                                final PublicKey tickArrayTwo0Key,
                                final PublicKey tickArrayTwo1Key,
                                final PublicKey tickArrayTwo2Key,
                                final PublicKey oracleOneKey,
                                final PublicKey oracleTwoKey,
                                final long amount,
                                final long otherAmountThreshold,
                                final boolean amountSpecifiedIsInput,
                                final boolean aToBOne,
                                final boolean aToBTwo,
                                final BigInteger sqrtPriceLimitOne,
                                final BigInteger sqrtPriceLimitTwo) {
    return WhirlpoolProgram.twoHopSwap(
        accounts.invokedWhirlpoolProgram(),
        tokenProgramKey,
        tokenAuthorityKey,
        whirlpoolOneKey,
        whirlpoolTwoKey,
        tokenOwnerAccountOneAKey,
        tokenVaultOneAKey,
        tokenOwnerAccountOneBKey,
        tokenVaultOneBKey,
        tokenOwnerAccountTwoAKey,
        tokenVaultTwoAKey,
        tokenOwnerAccountTwoBKey,
        tokenVaultTwoBKey,
        tickArrayOne0Key,
        tickArrayOne1Key,
        tickArrayOne2Key,
        tickArrayTwo0Key,
        tickArrayTwo1Key,
        tickArrayTwo2Key,
        oracleOneKey,
        oracleTwoKey,
        whirlpoolProgramId(),
        amount,
        otherAmountThreshold,
        amountSpecifiedIsInput,
        aToBOne,
        aToBTwo,
        sqrtPriceLimitOne,
        sqrtPriceLimitTwo
    );
  }

  @Override
  public Instruction twoHopSwapV2(final PublicKey whirlpoolOneKey,
                                  final PublicKey whirlpoolTwoKey,
                                  final PublicKey tokenMintInputKey,
                                  final PublicKey tokenMintIntermediateKey,
                                  final PublicKey tokenMintOutputKey,
                                  final PublicKey tokenProgramInputKey,
                                  final PublicKey tokenProgramIntermediateKey,
                                  final PublicKey tokenProgramOutputKey,
                                  final PublicKey tokenOwnerAccountInputKey,
                                  final PublicKey tokenVaultOneInputKey,
                                  final PublicKey tokenVaultOneIntermediateKey,
                                  final PublicKey tokenVaultTwoIntermediateKey,
                                  final PublicKey tokenVaultTwoOutputKey,
                                  final PublicKey tokenOwnerAccountOutputKey,
                                  final PublicKey tokenAuthorityKey,
                                  final PublicKey tickArrayOne0Key,
                                  final PublicKey tickArrayOne1Key,
                                  final PublicKey tickArrayOne2Key,
                                  final PublicKey tickArrayTwo0Key,
                                  final PublicKey tickArrayTwo1Key,
                                  final PublicKey tickArrayTwo2Key,
                                  final PublicKey oracleOneKey,
                                  final PublicKey oracleTwoKey,
                                  final PublicKey memoProgramKey,
                                  final long amount,
                                  final long otherAmountThreshold,
                                  final boolean amountSpecifiedIsInput,
                                  final boolean aToBOne,
                                  final boolean aToBTwo,
                                  final BigInteger sqrtPriceLimitOne,
                                  final BigInteger sqrtPriceLimitTwo,
                                  final RemainingAccountsInfo remainingAccountsInfo) {
    return WhirlpoolProgram.twoHopSwapV2(
        accounts.invokedWhirlpoolProgram(),
        whirlpoolOneKey,
        whirlpoolTwoKey,
        tokenMintInputKey,
        tokenMintIntermediateKey,
        tokenMintOutputKey,
        tokenProgramInputKey,
        tokenProgramIntermediateKey,
        tokenProgramOutputKey,
        tokenOwnerAccountInputKey,
        tokenVaultOneInputKey,
        tokenVaultOneIntermediateKey,
        tokenVaultTwoIntermediateKey,
        tokenVaultTwoOutputKey,
        tokenOwnerAccountOutputKey,
        tokenAuthorityKey,
        tickArrayOne0Key,
        tickArrayOne1Key,
        tickArrayOne2Key,
        tickArrayTwo0Key,
        tickArrayTwo1Key,
        tickArrayTwo2Key,
        oracleOneKey,
        oracleTwoKey,
        memoProgramKey,
        whirlpoolProgramId(),
        amount,
        otherAmountThreshold,
        amountSpecifiedIsInput,
        aToBOne,
        aToBTwo,
        sqrtPriceLimitOne,
        sqrtPriceLimitTwo,
        remainingAccountsInfo
    );
  }
}

package software.sava.idl.clients.orca;

import java.math.BigInteger;

import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.orca.whirlpools.gen.WhirlpoolPDAs;
import software.sava.idl.clients.orca.whirlpools.gen.types.IncreaseLiquidityMethod;
import software.sava.idl.clients.orca.whirlpools.gen.types.LockType;
import software.sava.idl.clients.orca.whirlpools.gen.types.OpenPositionBumps;
import software.sava.idl.clients.orca.whirlpools.gen.types.OpenPositionWithMetadataBumps;
import software.sava.idl.clients.orca.whirlpools.gen.types.RemainingAccountsInfo;
import software.sava.idl.clients.orca.whirlpools.gen.types.RepositionLiquidityMethod;
import software.sava.idl.clients.spl.associated_token.gen.AssociatedTokenPDAs;

public interface OrcaWhirlpoolsClient {

  static OrcaWhirlpoolsClient createClient(final SolanaAccounts solanaAccounts, final OrcaAccounts accounts) {
    return new OrcaWhirlpoolsClientImpl(solanaAccounts, accounts);
  }

  static OrcaWhirlpoolsClient createClient(final OrcaAccounts accounts) {
    return createClient(SolanaAccounts.MAIN_NET, accounts);
  }

  SolanaAccounts solanaAccounts();

  OrcaAccounts orcaAccounts();

  // Position lifecycle

  Instruction openPosition(final PublicKey funderKey,
                           final PublicKey ownerKey,
                           final PublicKey positionKey,
                           final PublicKey positionMintKey,
                           final PublicKey positionTokenAccountKey,
                           final PublicKey whirlpoolKey,
                           final PublicKey tokenProgramKey,
                           final OpenPositionBumps bumps,
                           final int tickLowerIndex,
                           final int tickUpperIndex);

  Instruction openPositionWithMetadata(final PublicKey funderKey,
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
                                       final int tickUpperIndex);

  Instruction openPositionWithTokenExtensions(final PublicKey funderKey,
                                              final PublicKey ownerKey,
                                              final PublicKey positionKey,
                                              final PublicKey positionMintKey,
                                              final PublicKey positionTokenAccountKey,
                                              final PublicKey whirlpoolKey,
                                              final PublicKey token2022ProgramKey,
                                              final PublicKey metadataUpdateAuthKey,
                                              final int tickLowerIndex,
                                              final int tickUpperIndex,
                                              final boolean withTokenMetadataExtension);

  Instruction closePosition(final PublicKey positionAuthorityKey,
                            final PublicKey receiverKey,
                            final PublicKey positionKey,
                            final PublicKey positionMintKey,
                            final PublicKey positionTokenAccountKey,
                            final PublicKey tokenProgramKey);

  Instruction closePositionWithTokenExtensions(final PublicKey positionAuthorityKey,
                                               final PublicKey receiverKey,
                                               final PublicKey positionKey,
                                               final PublicKey positionMintKey,
                                               final PublicKey positionTokenAccountKey,
                                               final PublicKey token2022ProgramKey);

  Instruction resetPositionRange(final PublicKey funderKey,
                                 final PublicKey positionAuthorityKey,
                                 final PublicKey whirlpoolKey,
                                 final PublicKey positionKey,
                                 final PublicKey positionTokenAccountKey,
                                 final int newTickLowerIndex,
                                 final int newTickUpperIndex);

  Instruction lockPosition(final PublicKey funderKey,
                           final PublicKey positionAuthorityKey,
                           final PublicKey positionKey,
                           final PublicKey positionMintKey,
                           final PublicKey positionTokenAccountKey,
                           final PublicKey lockConfigKey,
                           final PublicKey whirlpoolKey,
                           final PublicKey token2022ProgramKey,
                           final LockType lockType);

  Instruction transferLockedPosition(final PublicKey positionAuthorityKey,
                                     final PublicKey receiverKey,
                                     final PublicKey positionKey,
                                     final PublicKey positionMintKey,
                                     final PublicKey positionTokenAccountKey,
                                     final PublicKey destinationTokenAccountKey,
                                     final PublicKey lockConfigKey,
                                     final PublicKey token2022ProgramKey);

  // Position bundles

  Instruction initializePositionBundle(final PublicKey positionBundleKey,
                                       final PublicKey positionBundleMintKey,
                                       final PublicKey positionBundleTokenAccountKey,
                                       final PublicKey positionBundleOwnerKey,
                                       final PublicKey funderKey,
                                       final PublicKey tokenProgramKey);

  Instruction initializePositionBundleWithMetadata(final PublicKey positionBundleKey,
                                                   final PublicKey positionBundleMintKey,
                                                   final PublicKey positionBundleMetadataKey,
                                                   final PublicKey positionBundleTokenAccountKey,
                                                   final PublicKey positionBundleOwnerKey,
                                                   final PublicKey funderKey,
                                                   final PublicKey metadataUpdateAuthKey,
                                                   final PublicKey tokenProgramKey,
                                                   final PublicKey metadataProgramKey);

  Instruction deletePositionBundle(final PublicKey positionBundleKey,
                                   final PublicKey positionBundleMintKey,
                                   final PublicKey positionBundleTokenAccountKey,
                                   final PublicKey positionBundleOwnerKey,
                                   final PublicKey receiverKey,
                                   final PublicKey tokenProgramKey);

  Instruction openBundledPosition(final PublicKey bundledPositionKey,
                                  final PublicKey positionBundleKey,
                                  final PublicKey positionBundleTokenAccountKey,
                                  final PublicKey positionBundleAuthorityKey,
                                  final PublicKey whirlpoolKey,
                                  final PublicKey funderKey,
                                  final int bundleIndex,
                                  final int tickLowerIndex,
                                  final int tickUpperIndex);

  Instruction closeBundledPosition(final PublicKey bundledPositionKey,
                                   final PublicKey positionBundleKey,
                                   final PublicKey positionBundleTokenAccountKey,
                                   final PublicKey positionBundleAuthorityKey,
                                   final PublicKey receiverKey,
                                   final int bundleIndex);

  // Tick arrays

  Instruction initializeTickArray(final PublicKey whirlpoolKey,
                                  final PublicKey funderKey,
                                  final PublicKey tickArrayKey,
                                  final int startTickIndex);

  Instruction initializeDynamicTickArray(final PublicKey whirlpoolKey,
                                         final PublicKey funderKey,
                                         final PublicKey tickArrayKey,
                                         final int startTickIndex,
                                         final boolean idempotent);

  // Liquidity

  Instruction increaseLiquidity(final PublicKey whirlpoolKey,
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
                                final long tokenMaxB);

  Instruction increaseLiquidityV2(final PublicKey whirlpoolKey,
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
                                  final RemainingAccountsInfo remainingAccountsInfo);

  Instruction increaseLiquidityByTokenAmountsV2(final PublicKey whirlpoolKey,
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
                                                final RemainingAccountsInfo remainingAccountsInfo);

  Instruction decreaseLiquidity(final PublicKey whirlpoolKey,
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
                                final long tokenMinB);

  Instruction decreaseLiquidityV2(final PublicKey whirlpoolKey,
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
                                  final RemainingAccountsInfo remainingAccountsInfo);

  Instruction repositionLiquidityV2(final PublicKey whirlpoolKey,
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
                                    final RemainingAccountsInfo remainingAccountsInfo);

  // Fees and rewards

  Instruction updateFeesAndRewards(final PublicKey whirlpoolKey,
                                   final PublicKey positionKey,
                                   final PublicKey tickArrayLowerKey,
                                   final PublicKey tickArrayUpperKey);

  Instruction collectFees(final PublicKey whirlpoolKey,
                          final PublicKey positionAuthorityKey,
                          final PublicKey positionKey,
                          final PublicKey positionTokenAccountKey,
                          final PublicKey tokenOwnerAccountAKey,
                          final PublicKey tokenVaultAKey,
                          final PublicKey tokenOwnerAccountBKey,
                          final PublicKey tokenVaultBKey,
                          final PublicKey tokenProgramKey);

  Instruction collectFeesV2(final PublicKey whirlpoolKey,
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
                            final RemainingAccountsInfo remainingAccountsInfo);

  Instruction collectReward(final PublicKey whirlpoolKey,
                            final PublicKey positionAuthorityKey,
                            final PublicKey positionKey,
                            final PublicKey positionTokenAccountKey,
                            final PublicKey rewardOwnerAccountKey,
                            final PublicKey rewardVaultKey,
                            final PublicKey tokenProgramKey,
                            final int rewardIndex);

  Instruction collectRewardV2(final PublicKey whirlpoolKey,
                              final PublicKey positionAuthorityKey,
                              final PublicKey positionKey,
                              final PublicKey positionTokenAccountKey,
                              final PublicKey rewardOwnerAccountKey,
                              final PublicKey rewardMintKey,
                              final PublicKey rewardVaultKey,
                              final PublicKey rewardTokenProgramKey,
                              final PublicKey memoProgramKey,
                              final int rewardIndex,
                              final RemainingAccountsInfo remainingAccountsInfo);

  // Swaps

  Instruction swap(final PublicKey tokenProgramKey,
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
                   final boolean aToB);

  Instruction swapV2(final PublicKey tokenProgramAKey,
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
                     final RemainingAccountsInfo remainingAccountsInfo);

  Instruction twoHopSwap(final PublicKey tokenProgramKey,
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
                         final BigInteger sqrtPriceLimitTwo);

  Instruction twoHopSwapV2(final PublicKey whirlpoolOneKey,
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
                           final RemainingAccountsInfo remainingAccountsInfo);

  // --- Convenience derivations ---
  //
  // The methods below derive program-seeded PDAs and associated token accounts
  // from caller-provided context, then delegate to the explicit-account methods
  // declared above. Use these to avoid wiring obviously-derivable accounts by
  // hand; use the explicit methods when the caller already has the derived
  // keys or needs to pass a non-default account (e.g. a non-ATA token account).

  default PublicKey whirlpoolProgramKey() {
    return orcaAccounts().invokedWhirlpoolProgram().publicKey();
  }

  default PublicKey derivePositionKey(final PublicKey positionMintKey) {
    return WhirlpoolPDAs.positionPDA(whirlpoolProgramKey(), positionMintKey).publicKey();
  }

  default PublicKey deriveOracleKey(final PublicKey whirlpoolKey) {
    return WhirlpoolPDAs.oraclePDA(whirlpoolProgramKey(), whirlpoolKey).publicKey();
  }

  default PublicKey deriveLockConfigKey(final PublicKey positionKey) {
    return WhirlpoolPDAs.lockConfigPDA(whirlpoolProgramKey(), positionKey).publicKey();
  }

  default PublicKey derivePositionBundleKey(final PublicKey positionBundleMintKey) {
    return WhirlpoolPDAs.positionBundlePDA(whirlpoolProgramKey(), positionBundleMintKey).publicKey();
  }

  default PublicKey deriveBundledPositionKey(final PublicKey positionBundleMintKey, final byte[] bundleIndexBytes) {
    return WhirlpoolPDAs.bundledPositionPDA(whirlpoolProgramKey(), positionBundleMintKey, bundleIndexBytes).publicKey();
  }

  default PublicKey deriveATA(final PublicKey ownerKey,
                              final PublicKey tokenProgramKey,
                              final PublicKey mintKey) {
    return AssociatedTokenPDAs.associatedTokenPDA(
        solanaAccounts().associatedTokenAccountProgram(),
        ownerKey,
        tokenProgramKey,
        mintKey
    ).publicKey();
  }

  // Position lifecycle

  default Instruction openPosition(final PublicKey funderKey,
                                   final PublicKey ownerKey,
                                   final PublicKey positionMintKey,
                                   final PublicKey whirlpoolKey,
                                   final OpenPositionBumps bumps,
                                   final int tickLowerIndex,
                                   final int tickUpperIndex) {
    final var tokenProgramKey = solanaAccounts().tokenProgram();
    return openPosition(
        funderKey,
        ownerKey,
        derivePositionKey(positionMintKey),
        positionMintKey,
        deriveATA(ownerKey, tokenProgramKey, positionMintKey),
        whirlpoolKey,
        tokenProgramKey,
        bumps,
        tickLowerIndex,
        tickUpperIndex
    );
  }

  default Instruction openPositionWithMetadata(final PublicKey funderKey,
                                               final PublicKey ownerKey,
                                               final PublicKey positionMintKey,
                                               final PublicKey positionMetadataAccountKey,
                                               final PublicKey whirlpoolKey,
                                               final OpenPositionWithMetadataBumps bumps,
                                               final int tickLowerIndex,
                                               final int tickUpperIndex) {
    final var tokenProgramKey = solanaAccounts().tokenProgram();
    return openPositionWithMetadata(
        funderKey,
        ownerKey,
        derivePositionKey(positionMintKey),
        positionMintKey,
        positionMetadataAccountKey,
        deriveATA(ownerKey, tokenProgramKey, positionMintKey),
        whirlpoolKey,
        tokenProgramKey,
        orcaAccounts().tokenMetadataProgram(),
        orcaAccounts().whirlpoolNftUpdateAuthority(),
        bumps,
        tickLowerIndex,
        tickUpperIndex
    );
  }

  default Instruction openPositionWithTokenExtensions(final PublicKey funderKey,
                                                      final PublicKey ownerKey,
                                                      final PublicKey positionMintKey,
                                                      final PublicKey whirlpoolKey,
                                                      final int tickLowerIndex,
                                                      final int tickUpperIndex,
                                                      final boolean withTokenMetadataExtension) {
    final var token2022ProgramKey = solanaAccounts().token2022Program();
    return openPositionWithTokenExtensions(
        funderKey,
        ownerKey,
        derivePositionKey(positionMintKey),
        positionMintKey,
        deriveATA(ownerKey, token2022ProgramKey, positionMintKey),
        whirlpoolKey,
        token2022ProgramKey,
        orcaAccounts().whirlpoolNftUpdateAuthority(),
        tickLowerIndex,
        tickUpperIndex,
        withTokenMetadataExtension
    );
  }

  default Instruction closePosition(final PublicKey positionAuthorityKey,
                                    final PublicKey receiverKey,
                                    final PublicKey positionMintKey) {
    final var tokenProgramKey = solanaAccounts().tokenProgram();
    return closePosition(
        positionAuthorityKey,
        receiverKey,
        derivePositionKey(positionMintKey),
        positionMintKey,
        deriveATA(positionAuthorityKey, tokenProgramKey, positionMintKey),
        tokenProgramKey
    );
  }

  default Instruction closePositionWithTokenExtensions(final PublicKey positionAuthorityKey,
                                                       final PublicKey receiverKey,
                                                       final PublicKey positionMintKey) {
    final var token2022ProgramKey = solanaAccounts().token2022Program();
    return closePositionWithTokenExtensions(
        positionAuthorityKey,
        receiverKey,
        derivePositionKey(positionMintKey),
        positionMintKey,
        deriveATA(positionAuthorityKey, token2022ProgramKey, positionMintKey),
        token2022ProgramKey
    );
  }

  default Instruction lockPosition(final PublicKey funderKey,
                                   final PublicKey positionAuthorityKey,
                                   final PublicKey positionMintKey,
                                   final PublicKey whirlpoolKey,
                                   final LockType lockType) {
    final var token2022ProgramKey = solanaAccounts().token2022Program();
    final var positionKey = derivePositionKey(positionMintKey);
    return lockPosition(
        funderKey,
        positionAuthorityKey,
        positionKey,
        positionMintKey,
        deriveATA(positionAuthorityKey, token2022ProgramKey, positionMintKey),
        deriveLockConfigKey(positionKey),
        whirlpoolKey,
        token2022ProgramKey,
        lockType
    );
  }

  default Instruction transferLockedPosition(final PublicKey positionAuthorityKey,
                                             final PublicKey receiverKey,
                                             final PublicKey positionMintKey,
                                             final PublicKey destinationOwnerKey) {
    final var token2022ProgramKey = solanaAccounts().token2022Program();
    final var positionKey = derivePositionKey(positionMintKey);
    return transferLockedPosition(
        positionAuthorityKey,
        receiverKey,
        positionKey,
        positionMintKey,
        deriveATA(positionAuthorityKey, token2022ProgramKey, positionMintKey),
        deriveATA(destinationOwnerKey, token2022ProgramKey, positionMintKey),
        deriveLockConfigKey(positionKey),
        token2022ProgramKey
    );
  }

  // Position bundles

  default Instruction openBundledPosition(final PublicKey positionBundleMintKey,
                                          final PublicKey positionBundleOwnerKey,
                                          final PublicKey positionBundleAuthorityKey,
                                          final PublicKey whirlpoolKey,
                                          final PublicKey funderKey,
                                          final int bundleIndex,
                                          final byte[] bundleIndexBytes,
                                          final int tickLowerIndex,
                                          final int tickUpperIndex) {
    final var tokenProgramKey = solanaAccounts().tokenProgram();
    return openBundledPosition(
        deriveBundledPositionKey(positionBundleMintKey, bundleIndexBytes),
        derivePositionBundleKey(positionBundleMintKey),
        deriveATA(positionBundleOwnerKey, tokenProgramKey, positionBundleMintKey),
        positionBundleAuthorityKey,
        whirlpoolKey,
        funderKey,
        bundleIndex,
        tickLowerIndex,
        tickUpperIndex
    );
  }

  default Instruction closeBundledPosition(final PublicKey positionBundleMintKey,
                                           final PublicKey positionBundleOwnerKey,
                                           final PublicKey positionBundleAuthorityKey,
                                           final PublicKey receiverKey,
                                           final int bundleIndex,
                                           final byte[] bundleIndexBytes) {
    final var tokenProgramKey = solanaAccounts().tokenProgram();
    return closeBundledPosition(
        deriveBundledPositionKey(positionBundleMintKey, bundleIndexBytes),
        derivePositionBundleKey(positionBundleMintKey),
        deriveATA(positionBundleOwnerKey, tokenProgramKey, positionBundleMintKey),
        positionBundleAuthorityKey,
        receiverKey,
        bundleIndex
    );
  }

  // Swaps

  default Instruction swap(final PublicKey tokenProgramKey,
                           final PublicKey tokenAuthorityKey,
                           final PublicKey whirlpoolKey,
                           final PublicKey tokenOwnerAccountAKey,
                           final PublicKey tokenVaultAKey,
                           final PublicKey tokenOwnerAccountBKey,
                           final PublicKey tokenVaultBKey,
                           final PublicKey tickArray0Key,
                           final PublicKey tickArray1Key,
                           final PublicKey tickArray2Key,
                           final long amount,
                           final long otherAmountThreshold,
                           final BigInteger sqrtPriceLimit,
                           final boolean amountSpecifiedIsInput,
                           final boolean aToB) {
    return swap(
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
        deriveOracleKey(whirlpoolKey),
        amount,
        otherAmountThreshold,
        sqrtPriceLimit,
        amountSpecifiedIsInput,
        aToB
    );
  }

  default Instruction swapV2(final PublicKey tokenProgramAKey,
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
                             final long amount,
                             final long otherAmountThreshold,
                             final BigInteger sqrtPriceLimit,
                             final boolean amountSpecifiedIsInput,
                             final boolean aToB,
                             final RemainingAccountsInfo remainingAccountsInfo) {
    return swapV2(
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
        deriveOracleKey(whirlpoolKey),
        amount,
        otherAmountThreshold,
        sqrtPriceLimit,
        amountSpecifiedIsInput,
        aToB,
        remainingAccountsInfo
    );
  }
}

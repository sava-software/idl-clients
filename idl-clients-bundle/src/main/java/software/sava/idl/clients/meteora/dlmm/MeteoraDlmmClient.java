package software.sava.idl.clients.meteora.dlmm;

import software.sava.core.accounts.ProgramDerivedAddress;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.meteora.MeteoraAccounts;
import software.sava.idl.clients.meteora.MeteoraPDAs;
import software.sava.idl.clients.meteora.dlmm.gen.types.*;
import software.sava.idl.clients.spl.SPLAccountClient;
import software.sava.rpc.json.http.client.SolanaRpcClient;
import software.sava.rpc.json.http.response.AccountInfo;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

import static software.sava.idl.clients.meteora.dlmm.DlmmUtils.binIdToArrayIndex;


public interface MeteoraDlmmClient {

  static MeteoraDlmmClient createClient(final SolanaAccounts solanaAccounts,
                                        final MeteoraAccounts meteoraAccounts,
                                        final PublicKey owner,
                                        final AccountMeta feePayer) {
    return new MeteoraDlmmClientImpl(
        solanaAccounts,
        meteoraAccounts,
        owner,
        feePayer
    );
  }

  static MeteoraDlmmClient createClient(final PublicKey owner, final AccountMeta feePayer) {
    return createClient(
        SolanaAccounts.MAIN_NET,
        MeteoraAccounts.MAIN_NET,
        owner,
        feePayer
    );
  }

  static MeteoraDlmmClient createClient(final SPLAccountClient splAccountClient,
                                        final MeteoraAccounts meteoraAccounts) {
    return createClient(
        splAccountClient.solanaAccounts(),
        meteoraAccounts,
        splAccountClient.owner(),
        splAccountClient.feePayer()
    );
  }

  static MeteoraDlmmClient createClient(final SPLAccountClient splAccountClient) {
    return createClient(splAccountClient, MeteoraAccounts.MAIN_NET);
  }

  static List<AccountMeta> deriveBinAccounts(final PublicKey programId,
                                             final PublicKey lbPairKey,
                                             final int lowerBinId,
                                             final int upperBinId) {
    final int lowerBinIndex = binIdToArrayIndex(lowerBinId);
    final int upperBinIndex = binIdToArrayIndex(upperBinId);
    if (lowerBinIndex == upperBinIndex) {
      return List.of(AccountMeta.createWrite(MeteoraPDAs.binArrayPdA(lbPairKey, lowerBinIndex, programId).publicKey()));
    } else {
      return IntStream.rangeClosed(lowerBinIndex, upperBinIndex)
          .mapToObj(binIndex -> MeteoraPDAs.binArrayPdA(lbPairKey, binIndex, programId).publicKey())
          .map(AccountMeta.CREATE_WRITE)
          .toList();
    }
  }

  static List<AccountMeta> deriveBinAccounts(final PublicKey programId,
                                             final PositionV2 positionV2) {
    return deriveBinAccounts(programId, positionV2.lbPair(), positionV2.lowerBinId(), positionV2.upperBinId());
  }

  static Instruction appendBinAccounts(final PublicKey programId,
                                       final PublicKey lbPairKey,
                                       final int lowerBinId,
                                       final int upperBinId,
                                       final Instruction instruction) {
    return instruction.extraAccounts(deriveBinAccounts(programId, lbPairKey, lowerBinId, upperBinId));
  }

  default Instruction appendBinAccounts(final PublicKey lbPairKey,
                                        final int lowerBinId,
                                        final int upperBinId,
                                        final Instruction instruction) {
    final var programId = meteoraAccounts().dlmmProgram();
    return appendBinAccounts(programId, lbPairKey, lowerBinId, upperBinId, instruction);
  }

  SolanaAccounts solanaAccounts();

  MeteoraAccounts meteoraAccounts();

  PublicKey owner();

  AccountMeta feePayer();

  default List<AccountMeta> deriveBinAccounts(final PublicKey lbPairKey,
                                              final int lowerBinId,
                                              final int upperBinId) {
    return deriveBinAccounts(meteoraAccounts().dlmmProgram(), lbPairKey, lowerBinId, upperBinId);
  }


  default List<AccountMeta> deriveBinAccounts(final PositionV2 positionV2) {
    return deriveBinAccounts(positionV2.lbPair(), positionV2.lowerBinId(), positionV2.upperBinId());
  }

  CompletableFuture<List<AccountInfo<PositionV2>>> fetchPositions(final SolanaRpcClient rpcClient);

  CompletableFuture<List<AccountInfo<LbPair>>> fetchPairs(final SolanaRpcClient rpcClient);

  CompletableFuture<List<AccountInfo<LbPair>>> fetchPairs(final SolanaRpcClient rpcClient,
                                                          final PublicKey xMint,
                                                          final PublicKey yMint);

  Instruction initializePosition(final PublicKey positionKey,
                                 final PublicKey lbPairKey,
                                 final int lowerBinId,
                                 final int width);

  default ProgramDerivedAddress derivePositionAccount(final PublicKey lbPairKey,
                                                      final PublicKey baseKey,
                                                      final int lowerBinId,
                                                      final int width) {
    return MeteoraPDAs.positionPDA(
        lbPairKey,
        baseKey,
        lowerBinId,
        width,
        meteoraAccounts().dlmmProgram()
    );
  }


  default ProgramDerivedAddress derivePositionAccount(final PublicKey lbPairKey,
                                                      final int lowerBinId,
                                                      final int width) {
    return derivePositionAccount(
        lbPairKey,
        feePayer().publicKey(),
        lowerBinId,
        width
    );
  }

  Instruction initializePositionWithSeeds(final PublicKey positionKey,
                                          final PublicKey baseKey,
                                          final PublicKey lbPairKey,
                                          final int lowerBinId,
                                          final int width);

  default Instruction initializePositionWithSeeds(final PublicKey positionKey,
                                                  final PublicKey lbPairKey,
                                                  final int lowerBinId,
                                                  final int width) {
    return initializePositionWithSeeds(
        positionKey,
        feePayer().publicKey(),
        lbPairKey,
        lowerBinId,
        width
    );
  }

  Instruction addLiquidityByStrategy(final PublicKey positionKey,
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
                                     final RemainingAccountsInfo remainingAccountsInfo);

  default Instruction addLiquidityByStrategy(final PublicKey positionKey,
                                             final LbPair lbPair,
                                             final PublicKey binArrayBitmapExtensionKey,
                                             final PublicKey userTokenXKey,
                                             final PublicKey userTokenYKey,
                                             final PublicKey tokenXProgramKey,
                                             final PublicKey tokenYProgramKey,
                                             final LiquidityParameterByStrategy liquidityParameter,
                                             final RemainingAccountsInfo remainingAccountsInfo) {
    return addLiquidityByStrategy(
        positionKey,
        lbPair._address(),
        binArrayBitmapExtensionKey,
        userTokenXKey, userTokenYKey,
        lbPair.reserveX(), lbPair.reserveY(),
        lbPair.tokenXMint(), lbPair.tokenYMint(),
        tokenXProgramKey, tokenYProgramKey,
        liquidityParameter,
        remainingAccountsInfo
    );
  }

  default Instruction addLiquidityByStrategy(final PublicKey positionKey,
                                             final PublicKey lbPairKey,
                                             final PublicKey binArrayBitmapExtensionKey,
                                             final PublicKey userTokenXKey,
                                             final PublicKey userTokenYKey,
                                             final PublicKey tokenXMintKey,
                                             final PublicKey tokenYMintKey,
                                             final PublicKey tokenXProgramKey,
                                             final PublicKey tokenYProgramKey,
                                             final LiquidityParameterByStrategy liquidityParameter,
                                             final RemainingAccountsInfo remainingAccountsInfo) {
    final var programId = meteoraAccounts().dlmmProgram();
    final var reserveXKey = MeteoraPDAs.reservePDA(lbPairKey, tokenXMintKey, programId);
    final var reserveYKey = MeteoraPDAs.reservePDA(lbPairKey, tokenYMintKey, programId);

    return addLiquidityByStrategy(
        positionKey,
        lbPairKey,
        binArrayBitmapExtensionKey,
        userTokenXKey, userTokenYKey,
        reserveXKey.publicKey(), reserveYKey.publicKey(),
        tokenXMintKey, tokenYMintKey,
        tokenXProgramKey, tokenYProgramKey,
        liquidityParameter,
        remainingAccountsInfo
    );
  }

  Instruction addLiquidity(final PublicKey positionKey,
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
                           final RemainingAccountsInfo remainingAccountsInfo);

  default Instruction addLiquidity(final PublicKey positionKey,
                                   final LbPair lbPair,
                                   final PublicKey binArrayBitmapExtensionKey,
                                   final PublicKey userTokenXKey,
                                   final PublicKey userTokenYKey,
                                   final PublicKey tokenXProgramKey,
                                   final PublicKey tokenYProgramKey,
                                   final LiquidityParameter liquidityParameter,
                                   final RemainingAccountsInfo remainingAccountsInfo) {
    return addLiquidity(
        positionKey,
        lbPair._address(),
        binArrayBitmapExtensionKey,
        userTokenXKey, userTokenYKey,
        lbPair.reserveX(), lbPair.reserveY(),
        lbPair.tokenXMint(), lbPair.tokenYMint(),
        tokenXProgramKey, tokenYProgramKey,
        liquidityParameter,
        remainingAccountsInfo
    );
  }

  default Instruction addLiquidity(final PublicKey positionKey,
                                   final PublicKey lbPairKey,
                                   final PublicKey binArrayBitmapExtensionKey,
                                   final PublicKey userTokenXKey,
                                   final PublicKey userTokenYKey,
                                   final PublicKey tokenXMintKey,
                                   final PublicKey tokenYMintKey,
                                   final PublicKey tokenXProgramKey,
                                   final PublicKey tokenYProgramKey,
                                   final LiquidityParameter liquidityParameter,
                                   final RemainingAccountsInfo remainingAccountsInfo) {
    final var programId = meteoraAccounts().dlmmProgram();
    final var reserveXKey = MeteoraPDAs.reservePDA(lbPairKey, tokenXMintKey, programId);
    final var reserveYKey = MeteoraPDAs.reservePDA(lbPairKey, tokenYMintKey, programId);

    return addLiquidity(
        positionKey,
        lbPairKey,
        binArrayBitmapExtensionKey,
        userTokenXKey, userTokenYKey,
        reserveXKey.publicKey(), reserveYKey.publicKey(),
        tokenXMintKey, tokenYMintKey,
        tokenXProgramKey, tokenYProgramKey,
        liquidityParameter,
        remainingAccountsInfo
    );
  }

  Instruction addLiquidityOneSidePrecise(final PublicKey positionKey,
                                         final PublicKey lbPairKey,
                                         final PublicKey binArrayBitmapExtensionKey,
                                         final PublicKey userTokenKey,
                                         final PublicKey reserveKey,
                                         final PublicKey tokenMintKey,
                                         final PublicKey tokenProgramKey,
                                         final AddLiquiditySingleSidePreciseParameter2 liquidityParameter,
                                         final RemainingAccountsInfo remainingAccountsInfo);

  default Instruction bidLiquidityPrecise(final PublicKey positionKey,
                                          final LbPair lbPair,
                                          final PublicKey binArrayBitmapExtensionKey,
                                          final PublicKey userTokenKey,
                                          final PublicKey tokenProgramKey,
                                          final AddLiquiditySingleSidePreciseParameter2 liquidityParameter,
                                          final RemainingAccountsInfo remainingAccountsInfo) {
    return addLiquidityOneSidePrecise(
        positionKey,
        lbPair._address(),
        binArrayBitmapExtensionKey,
        userTokenKey,
        lbPair.reserveY(),
        lbPair.tokenYMint(),
        tokenProgramKey,
        liquidityParameter,
        remainingAccountsInfo
    );
  }

  default Instruction askLiquidityPrecise(final PublicKey positionKey,
                                          final LbPair lbPair,
                                          final PublicKey binArrayBitmapExtensionKey,
                                          final PublicKey userTokenKey,
                                          final PublicKey tokenProgramKey,
                                          final AddLiquiditySingleSidePreciseParameter2 liquidityParameter,
                                          final RemainingAccountsInfo remainingAccountsInfo) {
    return addLiquidityOneSidePrecise(
        positionKey,
        lbPair._address(),
        binArrayBitmapExtensionKey,
        userTokenKey,
        lbPair.reserveX(),
        lbPair.tokenXMint(),
        tokenProgramKey,
        liquidityParameter,
        remainingAccountsInfo
    );
  }

  Instruction removeLiquidityByRange(final PublicKey positionKey,
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
                                     final RemainingAccountsInfo remainingAccountsInfo);

  default Instruction removeLiquidityByRange(final PublicKey positionKey,
                                             final LbPair lbPair,
                                             final PublicKey binArrayBitmapExtensionKey,
                                             final PublicKey userTokenXKey,
                                             final PublicKey userTokenYKey,
                                             final PublicKey tokenXProgramKey,
                                             final PublicKey tokenYProgramKey,
                                             final int fromBinId,
                                             final int toBinId,
                                             final int bpsToRemove,
                                             final RemainingAccountsInfo remainingAccountsInfo) {
    final var lbPairKey = lbPair._address();
    return removeLiquidityByRange(
        positionKey,
        lbPairKey,
        binArrayBitmapExtensionKey,
        userTokenXKey, userTokenYKey,
        lbPair.reserveX(), lbPair.reserveY(),
        lbPair.tokenXMint(), lbPair.tokenYMint(),
        tokenXProgramKey, tokenYProgramKey,
        fromBinId, toBinId, bpsToRemove,
        remainingAccountsInfo
    );
  }

  default Instruction removeLiquidityByRange(final PublicKey positionKey,
                                             final PublicKey lbPairKey,
                                             final PublicKey binArrayBitmapExtensionKey,
                                             final PublicKey userTokenXKey,
                                             final PublicKey userTokenYKey,
                                             final PublicKey tokenXMintKey,
                                             final PublicKey tokenYMintKey,
                                             final PublicKey tokenXProgramKey,
                                             final PublicKey tokenYProgramKey,
                                             final int fromBinId,
                                             final int toBinId,
                                             final int bpsToRemove,
                                             final RemainingAccountsInfo remainingAccountsInfo) {
    final var programId = meteoraAccounts().dlmmProgram();

    final var reserveXKey = MeteoraPDAs.reservePDA(lbPairKey, tokenXMintKey, programId);
    final var reserveYKey = MeteoraPDAs.reservePDA(lbPairKey, tokenYMintKey, programId);

    return removeLiquidityByRange(
        positionKey,
        lbPairKey,
        binArrayBitmapExtensionKey,
        userTokenXKey, userTokenYKey,
        reserveXKey.publicKey(), reserveYKey.publicKey(),
        tokenXMintKey, tokenYMintKey,
        tokenXProgramKey, tokenYProgramKey,
        fromBinId, toBinId, bpsToRemove,
        remainingAccountsInfo
    );
  }

  default Instruction removeLiquidityByRange(final PositionV2 positionV2,
                                             final PublicKey binArrayBitmapExtensionKey,
                                             final PublicKey userTokenXKey,
                                             final PublicKey userTokenYKey,
                                             final PublicKey tokenXMintKey,
                                             final PublicKey tokenYMintKey,
                                             final PublicKey tokenXProgramKey,
                                             final PublicKey tokenYProgramKey,
                                             final int bpsToRemove,
                                             final RemainingAccountsInfo remainingAccountsInfo) {
    final var programId = meteoraAccounts().dlmmProgram();

    final var lbPairKey = positionV2.lbPair();
    final var reserveXKey = MeteoraPDAs.reservePDA(lbPairKey, tokenXMintKey, programId);
    final var reserveYKey = MeteoraPDAs.reservePDA(lbPairKey, tokenYMintKey, programId);

    return removeLiquidityByRange(
        positionV2._address(),
        lbPairKey,
        binArrayBitmapExtensionKey,
        userTokenXKey, userTokenYKey,
        reserveXKey.publicKey(), reserveYKey.publicKey(),
        tokenXMintKey, tokenYMintKey,
        tokenXProgramKey, tokenYProgramKey,
        positionV2.lowerBinId(), positionV2.upperBinId(), bpsToRemove,
        remainingAccountsInfo
    );
  }

  default Instruction removeLiquidityByRange(final PositionV2 positionV2,
                                             final LbPair lbPair,
                                             final PublicKey binArrayBitmapExtensionKey,
                                             final PublicKey userTokenXKey,
                                             final PublicKey userTokenYKey,
                                             final PublicKey tokenXProgramKey,
                                             final PublicKey tokenYProgramKey,
                                             final int bpsToRemove,
                                             final RemainingAccountsInfo remainingAccountsInfo) {
    return removeLiquidityByRange(
        positionV2._address(),
        positionV2.lbPair(),
        binArrayBitmapExtensionKey,
        userTokenXKey, userTokenYKey,
        lbPair.reserveX(), lbPair.reserveY(),
        lbPair.tokenXMint(), lbPair.tokenYMint(),
        tokenXProgramKey, tokenYProgramKey,
        positionV2.lowerBinId(), positionV2.upperBinId(), bpsToRemove,
        remainingAccountsInfo
    );
  }

  Instruction removeLiquidity(final PublicKey positionKey,
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
                              final RemainingAccountsInfo remainingAccountsInfo);

  default Instruction removeLiquidity(final PublicKey positionKey,
                                      final LbPair lbPair,
                                      final PublicKey binArrayBitmapExtensionKey,
                                      final PublicKey userTokenXKey,
                                      final PublicKey userTokenYKey,
                                      final PublicKey tokenXProgramKey,
                                      final PublicKey tokenYProgramKey,
                                      final BinLiquidityReduction[] binLiquidityRemoval,
                                      final RemainingAccountsInfo remainingAccountsInfo) {
    return removeLiquidity(
        positionKey,
        lbPair._address(),
        binArrayBitmapExtensionKey,
        userTokenXKey, userTokenYKey,
        lbPair.reserveX(), lbPair.reserveY(),
        lbPair.tokenXMint(), lbPair.tokenYMint(),
        tokenXProgramKey, tokenYProgramKey,
        binLiquidityRemoval,
        remainingAccountsInfo
    );
  }

  default Instruction removeLiquidity(final PublicKey positionKey,
                                      final PublicKey lbPairKey,
                                      final PublicKey binArrayBitmapExtensionKey,
                                      final PublicKey userTokenXKey,
                                      final PublicKey userTokenYKey,
                                      final PublicKey tokenXMintKey,
                                      final PublicKey tokenYMintKey,
                                      final PublicKey tokenXProgramKey,
                                      final PublicKey tokenYProgramKey,
                                      final BinLiquidityReduction[] binLiquidityRemoval,
                                      final RemainingAccountsInfo remainingAccountsInfo) {
    final var programId = meteoraAccounts().dlmmProgram();
    final var reserveXKey = MeteoraPDAs.reservePDA(lbPairKey, tokenXMintKey, programId);
    final var reserveYKey = MeteoraPDAs.reservePDA(lbPairKey, tokenYMintKey, programId);

    return removeLiquidity(
        positionKey,
        lbPairKey,
        binArrayBitmapExtensionKey,
        userTokenXKey, userTokenYKey,
        reserveXKey.publicKey(), reserveYKey.publicKey(),
        tokenXMintKey, tokenYMintKey,
        tokenXProgramKey, tokenYProgramKey,
        binLiquidityRemoval,
        remainingAccountsInfo
    );
  }

  Instruction claimFee(final PublicKey lbPairKey,
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
                       final RemainingAccountsInfo remainingAccountsInfo);

  default Instruction claimFee(final PublicKey lbPairKey,
                               final PublicKey positionKey,
                               final PublicKey userTokenXKey,
                               final PublicKey userTokenYKey,
                               final PublicKey tokenXMintKey,
                               final PublicKey tokenYMintKey,
                               final PublicKey tokenXProgramKey,
                               final PublicKey tokenYProgramKey,
                               final int minBinId,
                               final int maxBinId,
                               final RemainingAccountsInfo remainingAccountsInfo) {
    final var programId = meteoraAccounts().dlmmProgram();
    final var reserveXKey = MeteoraPDAs.reservePDA(lbPairKey, tokenXMintKey, programId);
    final var reserveYKey = MeteoraPDAs.reservePDA(lbPairKey, tokenYMintKey, programId);

    return claimFee(
        lbPairKey,
        positionKey,
        reserveXKey.publicKey(), reserveYKey.publicKey(),
        userTokenXKey, userTokenYKey,
        tokenXMintKey, tokenYMintKey,
        tokenXProgramKey, tokenYProgramKey,
        minBinId, maxBinId,
        remainingAccountsInfo
    );
  }

  default Instruction claimFee(final PositionV2 position,
                               final PublicKey userTokenXKey,
                               final PublicKey userTokenYKey,
                               final PublicKey tokenXMintKey,
                               final PublicKey tokenYMintKey,
                               final PublicKey tokenXProgramKey,
                               final PublicKey tokenYProgramKey,
                               final RemainingAccountsInfo remainingAccountsInfo) {
    final var programId = meteoraAccounts().dlmmProgram();
    final var lbPairKey = position.lbPair();
    final var reserveXKey = MeteoraPDAs.reservePDA(lbPairKey, tokenXMintKey, programId);
    final var reserveYKey = MeteoraPDAs.reservePDA(lbPairKey, tokenYMintKey, programId);

    return claimFee(
        lbPairKey,
        position._address(),
        reserveXKey.publicKey(), reserveYKey.publicKey(),
        userTokenXKey, userTokenYKey,
        tokenXMintKey, tokenYMintKey,
        tokenXProgramKey, tokenYProgramKey,
        position.lowerBinId(), position.upperBinId(),
        remainingAccountsInfo
    );
  }

  default Instruction claimFee(final LbPair lbPair,
                               final PositionV2 position,
                               final PublicKey userTokenXKey,
                               final PublicKey userTokenYKey,
                               final PublicKey tokenXProgramKey,
                               final PublicKey tokenYProgramKey,
                               final RemainingAccountsInfo remainingAccountsInfo) {
    return claimFee(
        position.lbPair(),
        position._address(),
        lbPair.reserveX(), lbPair.reserveY(),
        userTokenXKey, userTokenYKey,
        lbPair.tokenXMint(), lbPair.tokenYMint(),
        tokenXProgramKey, tokenYProgramKey,
        position.lowerBinId(), position.upperBinId(),
        remainingAccountsInfo
    );
  }

  Instruction claimReward(final PublicKey lbPairKey,
                          final PublicKey positionKey,
                          final PublicKey rewardVaultKey,
                          final PublicKey rewardMintKey,
                          final PublicKey userTokenAccountKey,
                          final PublicKey tokenProgramKey,
                          final int rewardIndex,
                          final int minBinId,
                          final int maxBinId,
                          final RemainingAccountsInfo remainingAccountsInfo);

  default Instruction claimReward(final PublicKey lbPairKey,
                                  final PublicKey positionKey,
                                  final PublicKey rewardMintKey,
                                  final PublicKey userTokenAccountKey,
                                  final PublicKey tokenProgramKey,
                                  final int rewardIndex,
                                  final int minBinId,
                                  final int maxBinId,
                                  final RemainingAccountsInfo remainingAccountsInfo) {
    final var programId = meteoraAccounts().dlmmProgram();
    final var rewardVaultKey = MeteoraPDAs.rewardVaultPDA(lbPairKey, rewardIndex, programId).publicKey();

    return claimReward(
        lbPairKey,
        positionKey,
        rewardVaultKey, rewardMintKey, userTokenAccountKey, tokenProgramKey,
        rewardIndex,
        minBinId, maxBinId,
        remainingAccountsInfo
    );
  }

  default Instruction claimReward(final PositionV2 position,
                                  final PublicKey rewardMintKey,
                                  final PublicKey userTokenAccountKey,
                                  final PublicKey tokenProgramKey,
                                  final int rewardIndex,
                                  final RemainingAccountsInfo remainingAccountsInfo) {
    return claimReward(
        position.lbPair(),
        position._address(),
        rewardMintKey,
        userTokenAccountKey,
        tokenProgramKey,
        rewardIndex,
        position.lowerBinId(), position.upperBinId(),
        remainingAccountsInfo
    );
  }

  Instruction closePosition(final PublicKey positionKey, final PublicKey rentReceiverKey);

  default Instruction closePosition(final PublicKey positionKey) {
    return closePosition(positionKey, feePayer().publicKey());
  }

  default Instruction closePosition(final PositionV2 position, final PublicKey rentReceiverKey) {
    return closePosition(position._address(), rentReceiverKey);
  }

  default Instruction closePosition(final PositionV2 position) {
    return closePosition(position, feePayer().publicKey());
  }

  /// `closePosition2` only allows closing positions that already have all liquidity removed.
  /// `closePositionIfEmpty` is a no-fail variant that closes the position iff it currently has no
  /// liquidity; useful for GC after a full withdraw without risking an on-chain reject.
  Instruction closePositionIfEmpty(final PublicKey positionKey, final PublicKey rentReceiverKey);

  default Instruction closePositionIfEmpty(final PublicKey positionKey) {
    return closePositionIfEmpty(positionKey, feePayer().publicKey());
  }

  default Instruction closePositionIfEmpty(final PositionV2 position, final PublicKey rentReceiverKey) {
    return closePositionIfEmpty(position._address(), rentReceiverKey);
  }

  default Instruction closePositionIfEmpty(final PositionV2 position) {
    return closePositionIfEmpty(position, feePayer().publicKey());
  }

  /// V2 variant of `initializePosition` (no rent sysvar in the account list) — used by callers
  /// targeting dynamic-position / Token-2022 friendly position layouts.
  Instruction initializePosition2(final PublicKey positionKey,
                                  final PublicKey lbPairKey,
                                  final int lowerBinId,
                                  final int width);

  /// Dynamically extend a position's upper bin id (must already be initialized via `initializePosition2`).
  Instruction increasePositionLength2(final PublicKey funderKey,
                                      final PublicKey lbPairKey,
                                      final PublicKey positionKey,
                                      final int minimumUpperBinId);

  default Instruction increasePositionLength2(final PublicKey lbPairKey,
                                              final PublicKey positionKey,
                                              final int minimumUpperBinId) {
    return increasePositionLength2(feePayer().publicKey(), lbPairKey, positionKey, minimumUpperBinId);
  }

  /// Dynamically shrink a position. `side` follows the program enum (`0` = lower, `1` = upper);
  /// `lengthToRemove` is the number of bins to drop from the chosen side.
  Instruction decreasePositionLength(final PublicKey positionKey,
                                     final PublicKey rentReceiverKey,
                                     final int lengthToRemove,
                                     final int side);

  default Instruction decreasePositionLength(final PublicKey positionKey,
                                             final int lengthToRemove,
                                             final int side) {
    return decreasePositionLength(positionKey, feePayer().publicKey(), lengthToRemove, side);
  }

  /// Bitmap maintenance — advances the active-bin pointer to `binId` by walking from
  /// `fromBinArray` to `toBinArray`. Callable by anyone; useful when the active bin is stale
  /// and a swap would otherwise fail.
  Instruction goToABin(final PublicKey lbPairKey,
                       final PublicKey binArrayBitmapExtensionKey,
                       final PublicKey fromBinArrayKey,
                       final PublicKey toBinArrayKey,
                       final int binId);

  /// Add liquidity weighted across the given bins. Pairs with `addLiquidityByStrategy` — use this
  /// when caller already has a per-bin weight schedule rather than a strategy descriptor.
  Instruction addLiquidityByWeight(final PublicKey positionKey,
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
                                   final RemainingAccountsInfo remainingAccountsInfo);

  default Instruction addLiquidityByWeight(final PublicKey positionKey,
                                           final LbPair lbPair,
                                           final PublicKey binArrayBitmapExtensionKey,
                                           final PublicKey userTokenXKey,
                                           final PublicKey userTokenYKey,
                                           final PublicKey tokenXProgramKey,
                                           final PublicKey tokenYProgramKey,
                                           final LiquidityParameterByWeight liquidityParameter,
                                           final RemainingAccountsInfo remainingAccountsInfo) {
    return addLiquidityByWeight(
        positionKey,
        lbPair._address(),
        binArrayBitmapExtensionKey,
        userTokenXKey, userTokenYKey,
        lbPair.reserveX(), lbPair.reserveY(),
        lbPair.tokenXMint(), lbPair.tokenYMint(),
        tokenXProgramKey, tokenYProgramKey,
        liquidityParameter,
        remainingAccountsInfo
    );
  }

  /// V1 single-sided add-liquidity using a strategy descriptor. V1 has no remaining-accounts
  /// payload — bin arrays still go in via `appendBinAccounts(...)` if applicable.
  /// `binArrayLowerKey` / `binArrayUpperKey` are the two bin arrays covering the strategy range.
  Instruction addLiquidityByStrategyOneSide(final PublicKey positionKey,
                                            final PublicKey lbPairKey,
                                            final PublicKey binArrayBitmapExtensionKey,
                                            final PublicKey userTokenKey,
                                            final PublicKey reserveKey,
                                            final PublicKey tokenMintKey,
                                            final PublicKey binArrayLowerKey,
                                            final PublicKey binArrayUpperKey,
                                            final PublicKey tokenProgramKey,
                                            final LiquidityParameterByStrategyOneSide liquidityParameter);

  /// V1 single-sided add-liquidity with explicit per-bin amounts (not strategy-derived).
  Instruction addLiquidityOneSide(final PublicKey positionKey,
                                  final PublicKey lbPairKey,
                                  final PublicKey binArrayBitmapExtensionKey,
                                  final PublicKey userTokenKey,
                                  final PublicKey reserveKey,
                                  final PublicKey tokenMintKey,
                                  final PublicKey binArrayLowerKey,
                                  final PublicKey binArrayUpperKey,
                                  final PublicKey tokenProgramKey,
                                  final LiquidityOneSideParameter liquidityParameter);

  /// Force-removes **all** liquidity from a position (no per-bin reduction). The two bin-array
  /// accounts must be the two arrays the position spans.
  Instruction removeAllLiquidity(final PublicKey positionKey,
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
                                 final PublicKey tokenYProgramKey);

  default Instruction removeAllLiquidity(final PositionV2 position,
                                         final LbPair lbPair,
                                         final PublicKey binArrayBitmapExtensionKey,
                                         final PublicKey userTokenXKey,
                                         final PublicKey userTokenYKey,
                                         final PublicKey binArrayLowerKey,
                                         final PublicKey binArrayUpperKey,
                                         final PublicKey tokenXProgramKey,
                                         final PublicKey tokenYProgramKey) {
    return removeAllLiquidity(
        position._address(),
        position.lbPair(),
        binArrayBitmapExtensionKey,
        userTokenXKey, userTokenYKey,
        lbPair.reserveX(), lbPair.reserveY(),
        lbPair.tokenXMint(), lbPair.tokenYMint(),
        binArrayLowerKey, binArrayUpperKey,
        tokenXProgramKey, tokenYProgramKey
    );
  }

  /// Rebalance an existing position by atomically removing and re-depositing liquidity at a new
  /// range. Used by the official market-making crate. The `RebalanceLiquidityParams` arg encodes
  /// the new range / strategy; bin arrays for both legs go in via `extraAccounts(...)`.
  Instruction rebalanceLiquidity(final PublicKey positionKey,
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
                                 final RemainingAccountsInfo remainingAccountsInfo);

  default Instruction rebalanceLiquidity(final PublicKey positionKey,
                                         final LbPair lbPair,
                                         final PublicKey binArrayBitmapExtensionKey,
                                         final PublicKey userTokenXKey,
                                         final PublicKey userTokenYKey,
                                         final PublicKey tokenXProgramKey,
                                         final PublicKey tokenYProgramKey,
                                         final RebalanceLiquidityParams params,
                                         final RemainingAccountsInfo remainingAccountsInfo) {
    return rebalanceLiquidity(
        positionKey,
        lbPair._address(),
        binArrayBitmapExtensionKey,
        userTokenXKey, userTokenYKey,
        lbPair.reserveX(), lbPair.reserveY(),
        lbPair.tokenXMint(), lbPair.tokenYMint(),
        feePayer().publicKey(),
        tokenXProgramKey, tokenYProgramKey,
        params,
        remainingAccountsInfo
    );
  }

  /// Permissionless pair-status setter. Document on-chain constraints: only callable when the
  /// program's permissionless-operation bits allow it; otherwise rejected.
  Instruction setPairStatusPermissionless(final PublicKey lbPairKey, final int status);

  // ---------------------------------------------------------------------------
  // Swaps
  // ---------------------------------------------------------------------------

  /// V2 exact-in swap. Bin arrays covering the active-id walk must be appended via
  /// `appendBinAccounts(...)` / `MeteoraDlmmRemainingAccounts.appendBinAccounts(...)` after
  /// building the instruction. `hostFeeInKey == null` is treated as the program-id sentinel
  /// (Anchor `Option`). `oracleKey` defaults to `LbPair.oracle()` when using the `LbPair` overload.
  Instruction swap(final PublicKey lbPairKey,
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
                   final RemainingAccountsInfo remainingAccountsInfo);

  default Instruction swap(final LbPair lbPair,
                           final PublicKey binArrayBitmapExtensionKey,
                           final PublicKey userTokenInKey,
                           final PublicKey userTokenOutKey,
                           final PublicKey hostFeeInKey,
                           final PublicKey tokenXProgramKey,
                           final PublicKey tokenYProgramKey,
                           final long amountIn,
                           final long minAmountOut,
                           final RemainingAccountsInfo remainingAccountsInfo) {
    return swap(
        lbPair._address(),
        binArrayBitmapExtensionKey,
        lbPair.reserveX(), lbPair.reserveY(),
        userTokenInKey, userTokenOutKey,
        lbPair.tokenXMint(), lbPair.tokenYMint(),
        lbPair.oracle(),
        hostFeeInKey,
        tokenXProgramKey, tokenYProgramKey,
        amountIn, minAmountOut,
        remainingAccountsInfo
    );
  }

  /// V2 exact-out swap.
  Instruction swapExactOut(final PublicKey lbPairKey,
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
                           final RemainingAccountsInfo remainingAccountsInfo);

  default Instruction swapExactOut(final LbPair lbPair,
                                   final PublicKey binArrayBitmapExtensionKey,
                                   final PublicKey userTokenInKey,
                                   final PublicKey userTokenOutKey,
                                   final PublicKey hostFeeInKey,
                                   final PublicKey tokenXProgramKey,
                                   final PublicKey tokenYProgramKey,
                                   final long maxInAmount,
                                   final long outAmount,
                                   final RemainingAccountsInfo remainingAccountsInfo) {
    return swapExactOut(
        lbPair._address(),
        binArrayBitmapExtensionKey,
        lbPair.reserveX(), lbPair.reserveY(),
        userTokenInKey, userTokenOutKey,
        lbPair.tokenXMint(), lbPair.tokenYMint(),
        lbPair.oracle(),
        hostFeeInKey,
        tokenXProgramKey, tokenYProgramKey,
        maxInAmount, outAmount,
        remainingAccountsInfo
    );
  }

  /// V2 exact-in swap with a price-impact guard. `activeId` is the caller's expected active bin
  /// (use `OptionalInt.empty()` to skip the check); `maxPriceImpactBps` rejects swaps that move
  /// the active bin by more than the given basis points.
  Instruction swapWithPriceImpact(final PublicKey lbPairKey,
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
                                  final java.util.OptionalInt activeId,
                                  final int maxPriceImpactBps,
                                  final RemainingAccountsInfo remainingAccountsInfo);

  default Instruction swapWithPriceImpact(final LbPair lbPair,
                                          final PublicKey binArrayBitmapExtensionKey,
                                          final PublicKey userTokenInKey,
                                          final PublicKey userTokenOutKey,
                                          final PublicKey hostFeeInKey,
                                          final PublicKey tokenXProgramKey,
                                          final PublicKey tokenYProgramKey,
                                          final long amountIn,
                                          final java.util.OptionalInt activeId,
                                          final int maxPriceImpactBps,
                                          final RemainingAccountsInfo remainingAccountsInfo) {
    return swapWithPriceImpact(
        lbPair._address(),
        binArrayBitmapExtensionKey,
        lbPair.reserveX(), lbPair.reserveY(),
        userTokenInKey, userTokenOutKey,
        lbPair.tokenXMint(), lbPair.tokenYMint(),
        lbPair.oracle(),
        hostFeeInKey,
        tokenXProgramKey, tokenYProgramKey,
        amountIn, activeId, maxPriceImpactBps,
        remainingAccountsInfo
    );
  }

  // ---------------------------------------------------------------------------
  // Limit orders
  // ---------------------------------------------------------------------------

  /// Place a limit order. `limitOrderKey` is a caller-supplied fresh keypair (NOT a PDA) — the
  /// caller must also sign with that keypair when submitting the transaction.
  Instruction placeLimitOrder(final PublicKey lbPairKey,
                              final PublicKey binArrayBitmapExtensionKey,
                              final PublicKey reserveKey,
                              final PublicKey tokenMintKey,
                              final PublicKey limitOrderKey,
                              final PublicKey userTokenKey,
                              final PublicKey tokenProgramKey,
                              final PlaceLimitOrderParams params,
                              final RemainingAccountsInfo remainingAccountsInfo);

  /// Cancel one or more bins of an existing limit order. `bins` are the bin ids to cancel.
  Instruction cancelLimitOrder(final PublicKey lbPairKey,
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
                               final RemainingAccountsInfo remainingAccountsInfo);

  default Instruction cancelLimitOrder(final LbPair lbPair,
                                       final PublicKey binArrayBitmapExtensionKey,
                                       final PublicKey limitOrderKey,
                                       final PublicKey ownerTokenXKey,
                                       final PublicKey ownerTokenYKey,
                                       final PublicKey tokenXProgramKey,
                                       final PublicKey tokenYProgramKey,
                                       final int[] bins,
                                       final RemainingAccountsInfo remainingAccountsInfo) {
    return cancelLimitOrder(
        lbPair._address(),
        binArrayBitmapExtensionKey,
        lbPair.reserveX(), lbPair.reserveY(),
        lbPair.tokenXMint(), lbPair.tokenYMint(),
        limitOrderKey,
        ownerTokenXKey, ownerTokenYKey,
        tokenXProgramKey, tokenYProgramKey,
        bins,
        remainingAccountsInfo
    );
  }

  /// GC an empty limit-order account after all of its bins have been cancelled / filled.
  Instruction closeLimitOrderIfEmpty(final PublicKey limitOrderKey, final PublicKey rentReceiverKey);

  default Instruction closeLimitOrderIfEmpty(final PublicKey limitOrderKey) {
    return closeLimitOrderIfEmpty(limitOrderKey, feePayer().publicKey());
  }
}

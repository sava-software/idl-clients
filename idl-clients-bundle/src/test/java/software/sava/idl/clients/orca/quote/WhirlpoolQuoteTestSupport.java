package software.sava.idl.clients.orca.quote;

import software.sava.idl.clients.orca.whirlpools.gen.types.Position;
import software.sava.idl.clients.orca.whirlpools.gen.types.PositionRewardInfo;
import software.sava.idl.clients.orca.whirlpools.gen.types.Tick;
import software.sava.idl.clients.orca.whirlpools.gen.types.Whirlpool;
import software.sava.idl.clients.orca.whirlpools.gen.types.WhirlpoolRewardInfo;

import java.math.BigInteger;

/// Test fixtures mirroring the Rust SDK's `*Facade::default()` patterns. The
/// generated `Whirlpool`/`Position`/`Tick` records carry far more fields than
/// the quote functions read; these builders fill the rest with zero/null so
/// tests can focus on the relevant inputs.
final class WhirlpoolQuoteTestSupport {

  private WhirlpoolQuoteTestSupport() {
  }

  static final BigInteger ZERO = BigInteger.ZERO;

  static Whirlpool whirlpool(final int tickCurrentIndex,
                             final BigInteger feeGrowthGlobalA,
                             final BigInteger feeGrowthGlobalB) {
    return new Whirlpool(
        null, null, null, null, 0, null, 0, 0,
        ZERO,                       // liquidity
        ZERO,                       // sqrtPrice
        tickCurrentIndex,
        0L, 0L,                     // protocolFeeOwedA/B
        null,                       // tokenMintA
        null,                       // tokenVaultA
        feeGrowthGlobalA,
        null,                       // tokenMintB
        null,                       // tokenVaultB
        feeGrowthGlobalB,
        0L,                         // rewardLastUpdatedTimestamp
        new WhirlpoolRewardInfo[]{rewardInfo(ZERO, ZERO), rewardInfo(ZERO, ZERO), rewardInfo(ZERO, ZERO)});
  }

  static Whirlpool whirlpoolForRewards(final int tickCurrentIndex,
                                       final long rewardLastUpdatedTimestamp,
                                       final BigInteger[] growthGlobals,
                                       final BigInteger[] emissionsPerSecond,
                                       final BigInteger liquidity) {
    return new Whirlpool(
        null, null, null, null, 0, null, 0, 0,
        liquidity,
        ZERO,
        tickCurrentIndex,
        0L, 0L, null, null,
        ZERO,
        null, null,
        ZERO,
        rewardLastUpdatedTimestamp,
        new WhirlpoolRewardInfo[]{
            rewardInfo(emissionsPerSecond[0], growthGlobals[0]),
            rewardInfo(emissionsPerSecond[1], growthGlobals[1]),
            rewardInfo(emissionsPerSecond[2], growthGlobals[2])
        });
  }

  static WhirlpoolRewardInfo rewardInfo(final BigInteger emissionsPerSecondX64,
                                        final BigInteger growthGlobalX64) {
    return new WhirlpoolRewardInfo(null, null, new byte[32], emissionsPerSecondX64, growthGlobalX64);
  }

  static Position position(final BigInteger liquidity,
                           final int tickLower,
                           final int tickUpper,
                           final BigInteger feeGrowthCheckpointA,
                           final long feeOwedA,
                           final BigInteger feeGrowthCheckpointB,
                           final long feeOwedB) {
    return new Position(
        null, null, null, null,
        liquidity, tickLower, tickUpper,
        feeGrowthCheckpointA, feeOwedA,
        feeGrowthCheckpointB, feeOwedB,
        new PositionRewardInfo[]{
            new PositionRewardInfo(ZERO, 0L),
            new PositionRewardInfo(ZERO, 0L),
            new PositionRewardInfo(ZERO, 0L)
        });
  }

  static Position positionForRewards(final BigInteger liquidity,
                                     final int tickLower,
                                     final int tickUpper,
                                     final BigInteger[] growthInsideCheckpoints,
                                     final long[] amountsOwed) {
    return new Position(
        null, null, null, null,
        liquidity, tickLower, tickUpper,
        ZERO, 0L,
        ZERO, 0L,
        new PositionRewardInfo[]{
            new PositionRewardInfo(growthInsideCheckpoints[0], amountsOwed[0]),
            new PositionRewardInfo(growthInsideCheckpoints[1], amountsOwed[1]),
            new PositionRewardInfo(growthInsideCheckpoints[2], amountsOwed[2])
        });
  }

  static Tick tick(final BigInteger feeGrowthOutsideA, final BigInteger feeGrowthOutsideB) {
    return new Tick(true, ZERO, ZERO, feeGrowthOutsideA, feeGrowthOutsideB,
        new BigInteger[]{ZERO, ZERO, ZERO});
  }

  static Tick tickRewards(final BigInteger[] rewardGrowthsOutside) {
    return new Tick(true, ZERO, ZERO, ZERO, ZERO, rewardGrowthsOutside);
  }

  static BigInteger bi(final String s) {
    return new BigInteger(s);
  }

  static BigInteger bi(final long v) {
    return BigInteger.valueOf(v);
  }
}

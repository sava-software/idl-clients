package software.sava.idl.clients.orca.quote;

import org.junit.jupiter.api.Test;
import software.sava.idl.clients.orca.whirlpools.gen.types.Position;
import software.sava.idl.clients.orca.whirlpools.gen.types.Tick;
import software.sava.idl.clients.orca.whirlpools.gen.types.Whirlpool;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static software.sava.idl.clients.orca.quote.WhirlpoolQuoteTestSupport.*;

/// Ported from `rust-sdk/core/src/quote/rewards.rs#tests`.
final class CollectRewardsQuoteTests {

  private static final BigInteger[] DEFAULT_GROWTH_GLOBALS = {
      bi(500).shiftLeft(64),
      bi(600).shiftLeft(64),
      bi(700).shiftLeft(64)
  };
  private static final BigInteger[] DEFAULT_EMISSIONS = {bi(1), bi(2), bi(3)};
  private static final BigInteger[] DEFAULT_TICK_OUTSIDES = {bi(10), bi(20), bi(30)};

  private static Whirlpool defaultPool(final int tickCurrent) {
    return whirlpoolForRewards(tickCurrent, 0L, DEFAULT_GROWTH_GLOBALS, DEFAULT_EMISSIONS, bi(50));
  }

  private static Position defaultPos() {
    return positionForRewards(bi(50), 5, 10,
        new BigInteger[]{ZERO, ZERO, ZERO},
        new long[]{100L, 200L, 300L});
  }

  private static Tick defaultTick() {
    return tickRewards(DEFAULT_TICK_OUTSIDES);
  }

  @Test
  void belowRange() {
    final var q = WhirlpoolQuote.collectRewardsQuote(defaultPool(0), defaultPos(),
        defaultTick(), defaultTick(), 10L, null, null, null);
    assertEquals(100L, q.rewards()[0].rewardsOwed());
    assertEquals(200L, q.rewards()[1].rewardsOwed());
    assertEquals(300L, q.rewards()[2].rewardsOwed());
  }

  @Test
  void inRange() {
    final var q = WhirlpoolQuote.collectRewardsQuote(defaultPool(7), defaultPos(),
        defaultTick(), defaultTick(), 10L, null, null, null);
    assertEquals(25099L, q.rewards()[0].rewardsOwed());
    assertEquals(30199L, q.rewards()[1].rewardsOwed());
    assertEquals(35299L, q.rewards()[2].rewardsOwed());
  }

  @Test
  void aboveRange() {
    final var q = WhirlpoolQuote.collectRewardsQuote(defaultPool(15), defaultPos(),
        defaultTick(), defaultTick(), 10L, null, null, null);
    assertEquals(100L, q.rewards()[0].rewardsOwed());
    assertEquals(200L, q.rewards()[1].rewardsOwed());
    assertEquals(300L, q.rewards()[2].rewardsOwed());
  }

  @Test
  void onRangeLower() {
    final var q = WhirlpoolQuote.collectRewardsQuote(defaultPool(5), defaultPos(),
        defaultTick(), defaultTick(), 10L, null, null, null);
    assertEquals(25099L, q.rewards()[0].rewardsOwed());
    assertEquals(30199L, q.rewards()[1].rewardsOwed());
    assertEquals(35299L, q.rewards()[2].rewardsOwed());
  }

  @Test
  void onRangeUpper() {
    final var q = WhirlpoolQuote.collectRewardsQuote(defaultPool(10), defaultPos(),
        defaultTick(), defaultTick(), 10L, null, null, null);
    assertEquals(100L, q.rewards()[0].rewardsOwed());
    assertEquals(200L, q.rewards()[1].rewardsOwed());
    assertEquals(300L, q.rewards()[2].rewardsOwed());
  }

  /// The reward accrual depends only on `currentTimestamp - rewardLastUpdatedTimestamp`:
  /// the same delta from a different origin must quote identically. The default fixtures
  /// all use `lastUpdated = 0`, where a sign flip in the subtraction is invisible.
  @Test
  void rewardAccrualDependsOnlyOnTheTimestampDelta() {
    final var fromZero = WhirlpoolQuote.collectRewardsQuote(
        whirlpoolForRewards(7, 0L, DEFAULT_GROWTH_GLOBALS, DEFAULT_EMISSIONS, bi(50)),
        defaultPos(), defaultTick(), defaultTick(), 6L, null, null, null);
    final var fromFour = WhirlpoolQuote.collectRewardsQuote(
        whirlpoolForRewards(7, 4L, DEFAULT_GROWTH_GLOBALS, DEFAULT_EMISSIONS, bi(50)),
        defaultPos(), defaultTick(), defaultTick(), 10L, null, null, null);

    for (int i = 0; i < 3; i++) {
      assertEquals(fromZero.rewards()[i].rewardsOwed(), fromFour.rewards()[i].rewardsOwed(), "reward " + i);
    }
    // and a larger delta accrues more once emissions are large enough to survive
    // the integer division by the pool liquidity
    final BigInteger[] bigEmissions = {bi(1).shiftLeft(64), bi(2).shiftLeft(64), bi(3).shiftLeft(64)};
    final var shortDelta = WhirlpoolQuote.collectRewardsQuote(
        whirlpoolForRewards(7, 0L, DEFAULT_GROWTH_GLOBALS, bigEmissions, bi(50)),
        defaultPos(), defaultTick(), defaultTick(), 6L, null, null, null);
    final var longDelta = WhirlpoolQuote.collectRewardsQuote(
        whirlpoolForRewards(7, 0L, DEFAULT_GROWTH_GLOBALS, bigEmissions, bi(50)),
        defaultPos(), defaultTick(), defaultTick(), 14L, null, null, null);
    assertTrue(Long.compareUnsigned(longDelta.rewards()[0].rewardsOwed(), shortDelta.rewards()[0].rewardsOwed()) > 0);
  }

  @Test
  void withTransferFees() {
    final var q = WhirlpoolQuote.collectRewardsQuote(defaultPool(7), defaultPos(),
        defaultTick(), defaultTick(), 10L,
        TransferFee.of(1000), TransferFee.of(2000), TransferFee.of(3000));
    assertEquals(22589L, q.rewards()[0].rewardsOwed());
    assertEquals(24159L, q.rewards()[1].rewardsOwed());
    assertEquals(24709L, q.rewards()[2].rewardsOwed());
  }

  @Test
  void cyclicGrowthCheckpoint() {
    final var p = positionForRewards(bi(91354442895L), 15168, 19648,
        new BigInteger[]{
            bi("340282366920938463463374607431768211400"),
            bi("340282366920938463463374607431768211000"),
            ZERO},
        new long[]{0L, 0L, 0L});
    final var pool = whirlpoolForRewards(18158, 0L,
        new BigInteger[]{ZERO, ZERO, ZERO}, new BigInteger[]{ZERO, ZERO, ZERO}, ZERO);
    final var tl = tickRewards(new BigInteger[]{ZERO, ZERO, ZERO});
    final var tu = tickRewards(new BigInteger[]{ZERO, ZERO, ZERO});
    final var r = WhirlpoolQuote.collectRewardsQuote(pool, p, tl, tu, 10L, null, null, null);
    assertEquals(0L, r.rewards()[0].rewardsOwed());
    assertEquals(0L, r.rewards()[1].rewardsOwed());
    assertEquals(0L, r.rewards()[2].rewardsOwed());
  }

  /// The per-liquidity division is the only thing separating "reward per unit
  /// of liquidity" from "total reward". The other fixtures cannot see it
  /// dropped: their raw (sub-X64) emissions leave a quotient — divided or not —
  /// that truncates to zero in the final `>> 64`. X64-scaled emissions over a
  /// small pool make the quotient exact: 8*2^64/s x 5s / 4 liquidity, held by
  /// 3 position liquidity = (10*2^64 * 3) >> 64 = 30.
  @Test
  void emissionsAccrueDividedByPoolLiquidity() {
    final var pool = whirlpoolForRewards(5, 10L,
        new BigInteger[]{ZERO, ZERO, ZERO},
        new BigInteger[]{bi(8).shiftLeft(64), ZERO, ZERO}, bi(4));
    final var p = positionForRewards(bi(3), 0, 10,
        new BigInteger[]{ZERO, ZERO, ZERO}, new long[]{0L, 0L, 0L});
    final var tl = tickRewards(new BigInteger[]{ZERO, ZERO, ZERO});
    final var tu = tickRewards(new BigInteger[]{ZERO, ZERO, ZERO});

    final var r = WhirlpoolQuote.collectRewardsQuote(pool, p, tl, tu, 15L, null, null, null);
    assertEquals(30L, r.rewards()[0].rewardsOwed());
    assertEquals(0L, r.rewards()[1].rewardsOwed());
    assertEquals(0L, r.rewards()[2].rewardsOwed());
  }

  @Test
  void forceProductOverflow() {
    final BigInteger half = OrcaUtilU128Max().shiftRight(1);
    final var pool = whirlpoolForRewards(5, 50L,
        new BigInteger[]{half, ZERO, ZERO},
        new BigInteger[]{bi(1), ZERO, ZERO}, bi(59));
    final var p = positionForRewards(OrcaUtilU128Max(), 0, 10,
        new BigInteger[]{ZERO, ZERO, ZERO}, new long[]{0L, 0L, 0L});
    final var tl = tickRewards(new BigInteger[]{ZERO, ZERO, ZERO});
    final var tu = tickRewards(new BigInteger[]{ZERO, ZERO, ZERO});
    final var r = WhirlpoolQuote.collectRewardsQuote(pool, p, tl, tu, 1746011244L, null, null, null);
    assertEquals(0L, r.rewards()[0].rewardsOwed());
    assertEquals(0L, r.rewards()[1].rewardsOwed());
    assertEquals(0L, r.rewards()[2].rewardsOwed());
  }

  private static BigInteger OrcaUtilU128Max() {
    return software.sava.idl.clients.orca.OrcaUtil.U128_MASK;
  }
}

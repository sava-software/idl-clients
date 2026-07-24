package software.sava.idl.clients.orca.quote;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

/// Ported from `rust-sdk/core/src/quote/liquidity.rs#tests`.
final class LiquidityQuoteTests {

  private static final BigInteger BELOW = new BigInteger("18354745142194483561");
  private static final BigInteger IN = new BigInteger("18446744073709551616");
  private static final BigInteger ABOVE = new BigInteger("18539204128674405812");

  // ----- decrease_liquidity_quote -----

  @Test
  void decreaseLiquidityQuote_below() {
    final var r = WhirlpoolQuote.decreaseLiquidityQuote(BigInteger.valueOf(1_000_000),
        100, BELOW, -10, 10, null, null);
    assertEquals(BigInteger.valueOf(1_000_000), r.liquidityDelta());
    assertEquals(999L, r.tokenEstA());
    assertEquals(0L, r.tokenEstB());
    assertEquals(989L, r.tokenMinA());
    assertEquals(0L, r.tokenMinB());
  }

  @Test
  void decreaseLiquidityQuote_inRange() {
    final var r = WhirlpoolQuote.decreaseLiquidityQuote(BigInteger.valueOf(1_000_000),
        100, IN, -10, 10, null, null);
    assertEquals(499L, r.tokenEstA());
    assertEquals(499L, r.tokenEstB());
    assertEquals(494L, r.tokenMinA());
    assertEquals(494L, r.tokenMinB());
  }

  @Test
  void decreaseLiquidityQuote_above() {
    final var r = WhirlpoolQuote.decreaseLiquidityQuote(BigInteger.valueOf(1_000_000),
        100, ABOVE, -10, 10, null, null);
    assertEquals(0L, r.tokenEstA());
    assertEquals(999L, r.tokenEstB());
    assertEquals(0L, r.tokenMinA());
    assertEquals(989L, r.tokenMinB());
  }

  @Test
  void decreaseLiquidityQuote_zero() {
    final var r = WhirlpoolQuote.decreaseLiquidityQuote(BigInteger.ZERO,
        100, IN, -10, 10, null, null);
    assertEquals(BigInteger.ZERO, r.liquidityDelta());
    assertEquals(0L, r.tokenEstA());
    assertEquals(0L, r.tokenEstB());
  }

  // ----- decrease_liquidity_quote_a -----

  @Test
  void decreaseLiquidityQuoteA_below() {
    final var r = WhirlpoolQuote.decreaseLiquidityQuoteA(1000L, 100, BELOW, -10, 10, null, null);
    assertEquals(BigInteger.valueOf(1_000_049L), r.liquidityDelta());
    assertEquals(999L, r.tokenEstA());
  }

  @Test
  void decreaseLiquidityQuoteA_inRange() {
    final var r = WhirlpoolQuote.decreaseLiquidityQuoteA(500L, 100, IN, -10, 10, null, null);
    assertEquals(BigInteger.valueOf(1_000_300L), r.liquidityDelta());
    assertEquals(499L, r.tokenEstA());
    assertEquals(499L, r.tokenEstB());
  }

  @Test
  void decreaseLiquidityQuoteA_above() {
    final var r = WhirlpoolQuote.decreaseLiquidityQuoteA(1000L, 100, ABOVE, -10, 10, null, null);
    assertEquals(BigInteger.ZERO, r.liquidityDelta());
  }

  // ----- decrease_liquidity_quote_b -----

  @Test
  void decreaseLiquidityQuoteB_below() {
    final var r = WhirlpoolQuote.decreaseLiquidityQuoteB(1000L, 100, BELOW, -10, 10, null, null);
    assertEquals(BigInteger.ZERO, r.liquidityDelta());
  }

  @Test
  void decreaseLiquidityQuoteB_inRange() {
    final var r = WhirlpoolQuote.decreaseLiquidityQuoteB(500L, 100, IN, -10, 10, null, null);
    assertEquals(BigInteger.valueOf(1_000_300L), r.liquidityDelta());
  }

  @Test
  void decreaseLiquidityQuoteB_above() {
    final var r = WhirlpoolQuote.decreaseLiquidityQuoteB(1000L, 100, ABOVE, -10, 10, null, null);
    assertEquals(BigInteger.valueOf(1_000_049L), r.liquidityDelta());
    assertEquals(999L, r.tokenEstB());
  }

  // ----- increase_liquidity_quote -----

  @Test
  void increaseLiquidityQuote_below() {
    final var r = WhirlpoolQuote.increaseLiquidityQuote(BigInteger.valueOf(1_000_000),
        100, BELOW, -10, 10, null, null);
    assertEquals(1000L, r.tokenEstA());
    assertEquals(0L, r.tokenEstB());
    assertEquals(1010L, r.tokenMaxA());
    assertEquals(0L, r.tokenMaxB());
  }

  @Test
  void increaseLiquidityQuote_inRange() {
    final var r = WhirlpoolQuote.increaseLiquidityQuote(BigInteger.valueOf(1_000_000),
        100, IN, -10, 10, null, null);
    assertEquals(500L, r.tokenEstA());
    assertEquals(500L, r.tokenEstB());
    assertEquals(505L, r.tokenMaxA());
    assertEquals(505L, r.tokenMaxB());
  }

  @Test
  void increaseLiquidityQuote_above() {
    final var r = WhirlpoolQuote.increaseLiquidityQuote(BigInteger.valueOf(1_000_000),
        100, ABOVE, -10, 10, null, null);
    assertEquals(0L, r.tokenEstA());
    assertEquals(1000L, r.tokenEstB());
    assertEquals(0L, r.tokenMaxA());
    assertEquals(1010L, r.tokenMaxB());
  }

  // ----- increase_liquidity_quote_a -----

  @Test
  void increaseLiquidityQuoteA_below() {
    final var r = WhirlpoolQuote.increaseLiquidityQuoteA(1000L, 100, BELOW, -10, 10, null, null);
    assertEquals(BigInteger.valueOf(1_000_049L), r.liquidityDelta());
    assertEquals(1000L, r.tokenEstA());
    assertEquals(1010L, r.tokenMaxA());
  }

  @Test
  void increaseLiquidityQuoteA_inRange() {
    final var r = WhirlpoolQuote.increaseLiquidityQuoteA(500L, 100, IN, -10, 10, null, null);
    assertEquals(BigInteger.valueOf(1_000_300L), r.liquidityDelta());
    assertEquals(500L, r.tokenEstA());
    assertEquals(500L, r.tokenEstB());
  }

  @Test
  void increaseLiquidityQuoteA_above() {
    final var r = WhirlpoolQuote.increaseLiquidityQuoteA(1000L, 100, ABOVE, -10, 10, null, null);
    assertEquals(BigInteger.ZERO, r.liquidityDelta());
  }

  // ----- increase_liquidity_quote_b -----

  @Test
  void increaseLiquidityQuoteB_below() {
    final var r = WhirlpoolQuote.increaseLiquidityQuoteB(1000L, 100, BELOW, -10, 10, null, null);
    assertEquals(BigInteger.ZERO, r.liquidityDelta());
  }

  @Test
  void increaseLiquidityQuoteB_inRange() {
    final var r = WhirlpoolQuote.increaseLiquidityQuoteB(500L, 100, IN, -10, 10, null, null);
    assertEquals(BigInteger.valueOf(1_000_300L), r.liquidityDelta());
  }

  @Test
  void increaseLiquidityQuoteB_above() {
    final var r = WhirlpoolQuote.increaseLiquidityQuoteB(1000L, 100, ABOVE, -10, 10, null, null);
    assertEquals(BigInteger.valueOf(1_000_049L), r.liquidityDelta());
    assertEquals(1000L, r.tokenEstB());
  }

  // ----- guards and boundaries -----

  /// The zero fast paths return the shared `ZERO` constants without touching
  /// the math — pinned by identity, matching the spl `Fee.toRatio` precedent.
  @Test
  void zeroInputsReturnTheSharedZeroQuotes() {
    assertSame(DecreaseLiquidityQuote.ZERO,
        WhirlpoolQuote.decreaseLiquidityQuote(BigInteger.ZERO, 100, IN, -10, 10, null, null));
    assertSame(IncreaseLiquidityQuote.ZERO,
        WhirlpoolQuote.increaseLiquidityQuote(BigInteger.ZERO, 100, IN, -10, 10, null, null));
    assertSame(DecreaseLiquidityQuote.ZERO,
        WhirlpoolQuote.decreaseLiquidityQuoteA(0L, 100, IN, -10, 10, null, null));
    assertSame(DecreaseLiquidityQuote.ZERO,
        WhirlpoolQuote.decreaseLiquidityQuoteB(0L, 100, IN, -10, 10, null, null));
    assertSame(IncreaseLiquidityQuote.ZERO,
        WhirlpoolQuote.increaseLiquidityQuoteA(0L, 100, IN, -10, 10, null, null));
    assertSame(IncreaseLiquidityQuote.ZERO,
        WhirlpoolQuote.increaseLiquidityQuoteB(0L, 100, IN, -10, 10, null, null));
  }

  /// The tick arguments are order-insensitive: every quote sorts them before
  /// deriving prices. An unsorted range would put the larger sqrt price in the
  /// lower slot and blow up the estimates.
  @Test
  void tickOrderDoesNotMatter() {
    assertEquals(
        WhirlpoolQuote.decreaseLiquidityQuote(BigInteger.valueOf(1_000_000), 100, IN, -10, 10, null, null),
        WhirlpoolQuote.decreaseLiquidityQuote(BigInteger.valueOf(1_000_000), 100, IN, 10, -10, null, null));
    assertEquals(
        WhirlpoolQuote.decreaseLiquidityQuoteA(1000L, 100, BELOW, -10, 10, null, null),
        WhirlpoolQuote.decreaseLiquidityQuoteA(1000L, 100, BELOW, 10, -10, null, null));
    assertEquals(
        WhirlpoolQuote.increaseLiquidityQuote(BigInteger.valueOf(1_000_000), 100, IN, -10, 10, null, null),
        WhirlpoolQuote.increaseLiquidityQuote(BigInteger.valueOf(1_000_000), 100, IN, 10, -10, null, null));
    assertEquals(
        WhirlpoolQuote.increaseLiquidityQuoteB(1000L, 100, ABOVE, -10, 10, null, null),
        WhirlpoolQuote.increaseLiquidityQuoteB(1000L, 100, ABOVE, 10, -10, null, null));
  }

  /// A same-tick range has no width: the estimator reports it as invalid and
  /// returns zero for both tokens rather than deriving prices from it.
  @Test
  void equalTicksAreAnInvalidRange() {
    assertArrayEquals(new long[]{0L, 0L},
        WhirlpoolQuote.tryGetTokenEstimatesFromLiquidity(BigInteger.valueOf(1_000_000), IN, 10, 10, false));
    assertArrayEquals(new long[]{0L, 0L},
        WhirlpoolQuote.tryGetTokenEstimatesFromLiquidity(BigInteger.valueOf(1_000_000), IN, 10, 10, true));
  }

  /// An increase quote's estimates are grossed *up* for a transfer fee — the
  /// caller must send more so the pool receives the estimate.
  @Test
  void transferFeeGrossesUpAnIncreaseEstimate() {
    final var noFee = WhirlpoolQuote.increaseLiquidityQuote(
        BigInteger.valueOf(1_000_000), 100, IN, -10, 10, null, null);
    final var withFee = WhirlpoolQuote.increaseLiquidityQuote(
        BigInteger.valueOf(1_000_000), 100, IN, -10, 10, TransferFee.of(100), null);
    assertTrue(Long.compareUnsigned(withFee.tokenEstA(), noFee.tokenEstA()) > 0,
        "a 1% transfer fee on mint A must inflate the token A estimate");
    assertEquals(noFee.tokenEstB(), withFee.tokenEstB(), "mint B carries no fee");
  }

  /// Round-up must only fire on an inexact division. `delta = sqrtPriceUpper`
  /// with `sqrtPriceLower = 2^64` (tick 0) makes `tokenA = delta * diff * 2^64
  /// / (upper * lower)` collapse to exactly `diff`, so the rounded-up and
  /// truncated results agree.
  @Test
  void roundUpLeavesAnExactDivisionAlone() {
    final var lower = software.sava.idl.clients.orca.OrcaUtil.tickIndexToSqrtPriceX64(0);
    assertEquals(BigInteger.ONE.shiftLeft(64), lower, "tick 0 is exactly 1.0 in X64");
    final var upper = software.sava.idl.clients.orca.OrcaUtil.tickIndexToSqrtPriceX64(64);
    final var diff = upper.subtract(lower);

    final long[] roundedUp = WhirlpoolQuote.tryGetTokenEstimatesFromLiquidity(
        upper, lower.shiftRight(1), 0, 64, true);
    final long[] truncated = WhirlpoolQuote.tryGetTokenEstimatesFromLiquidity(
        upper, lower.shiftRight(1), 0, 64, false);
    assertEquals(diff.longValueExact(), roundedUp[0]);
    assertEquals(diff.longValueExact(), truncated[0], "the division is exact, so both roundings agree");
    assertEquals(0L, roundedUp[1]);
  }
}

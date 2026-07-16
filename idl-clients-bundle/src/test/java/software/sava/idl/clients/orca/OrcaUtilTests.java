package software.sava.idl.clients.orca;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.OptionalInt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/// Vectors ported from the Orca Whirlpools Rust sources (ground truth):
///
/// - `rust-sdk/core/src/math/tick.rs` — tick <-> sqrt-price conversions,
///   full-range indexes, tick-array start indexes.
/// - `rust-sdk/core/src/math/token.rs` — amount deltas, next sqrt-price,
///   swap/transfer fees, slippage amounts.
/// - `rust-sdk/core/src/math/price.rs` — price conversions and sqrt-price
///   slippage bounds (integer sqrt).
/// - `rust-sdk/core/src/math/bundle.rs` — position-bundle bitmap helpers.
/// - `programs/whirlpool/src/state/tick.rs` — `check_is_valid_start_tick`.
///
/// Where the Rust tests only assert relative properties, exact expected
/// values were derived by executing the Rust algorithm independently
/// (arbitrary-precision integer / IEEE-754 double semantics) rather than by
/// running the Java implementation under test.
final class OrcaUtilTests {

  private static final BigInteger TWO_POW_64 = BigInteger.ONE.shiftLeft(64);

  private static BigInteger bi(final String value) {
    return new BigInteger(value);
  }

  // --------------------------------------------------------------------------
  // tickIndexToSqrtPriceX64 (rust-sdk/core/src/math/tick.rs
  // `test_tick_index_to_sqrt_price`)
  // --------------------------------------------------------------------------

  private static void assertTickSqrtPrice(final int tick, final String positive, final String negative) {
    assertEquals(bi(positive), OrcaUtil.tickIndexToSqrtPriceX64(tick), "tick " + tick);
    assertEquals(bi(negative), OrcaUtil.tickIndexToSqrtPriceX64(-tick), "tick " + -tick);
  }

  @Test
  void tickIndexToSqrtPriceX64ExactBitValues() {
    assertTickSqrtPrice(0, "18446744073709551616", "18446744073709551616");
    assertTickSqrtPrice(1, "18447666387855959850", "18445821805675392311");
    assertTickSqrtPrice(2, "18448588748116922571", "18444899583751176498");
    assertTickSqrtPrice(4, "18450433606991734263", "18443055278223354162");
    assertTickSqrtPrice(8, "18454123878217468680", "18439367220385604838");
    assertTickSqrtPrice(16, "18461506635090006701", "18431993317065449817");
    assertTickSqrtPrice(32, "18476281010653910144", "18417254355718160513");
    assertTickSqrtPrice(64, "18505865242158250041", "18387811781193591352");
    assertTickSqrtPrice(128, "18565175891880433522", "18329067761203520168");
    assertTickSqrtPrice(256, "18684368066214940582", "18212142134806087854");
    assertTickSqrtPrice(512, "18925053041275764671", "17980523815641551639");
    assertTickSqrtPrice(1024, "19415764168677886926", "17526086738831147013");
    assertTickSqrtPrice(2048, "20435687552633177494", "16651378430235024244");
    assertTickSqrtPrice(4096, "22639080592224303007", "15030750278693429944");
    assertTickSqrtPrice(8192, "27784196929998399742", "12247334978882834399");
    assertTickSqrtPrice(16384, "41848122137994986128", "8131365268884726200");
    assertTickSqrtPrice(32768, "94936283578220370716", "3584323654723342297");
    assertTickSqrtPrice(65536, "488590176327622479860", "696457651847595233");
    assertTickSqrtPrice(131072, "12941056668319229769860", "26294789957452057");
    assertTickSqrtPrice(262144, "9078618265828848800676189", "37481735321082");
  }

  @Test
  void tickIndexToSqrtPriceX64AtBounds() {
    assertEquals(OrcaUtil.MAX_SQRT_PRICE_X64, OrcaUtil.tickIndexToSqrtPriceX64(OrcaUtil.MAX_TICK_INDEX));
    assertEquals(OrcaUtil.MIN_SQRT_PRICE_X64, OrcaUtil.tickIndexToSqrtPriceX64(OrcaUtil.MIN_TICK_INDEX));
    // `test_tick_below_min`: still monotonic just below the supported range.
    assertTrue(OrcaUtil.tickIndexToSqrtPriceX64(OrcaUtil.MIN_TICK_INDEX - 1)
        .compareTo(OrcaUtil.MIN_SQRT_PRICE_X64) < 0);
  }

  @Test
  void tickIndexToSqrtPriceX64Checked() {
    assertEquals(OrcaUtil.MAX_SQRT_PRICE_X64, OrcaUtil.tickIndexToSqrtPriceX64Checked(OrcaUtil.MAX_TICK_INDEX));
    assertEquals(OrcaUtil.MIN_SQRT_PRICE_X64, OrcaUtil.tickIndexToSqrtPriceX64Checked(OrcaUtil.MIN_TICK_INDEX));
    assertThrows(IllegalArgumentException.class,
        () -> OrcaUtil.tickIndexToSqrtPriceX64Checked(OrcaUtil.MIN_TICK_INDEX - 1));
    assertThrows(IllegalArgumentException.class,
        () -> OrcaUtil.tickIndexToSqrtPriceX64Checked(OrcaUtil.MAX_TICK_INDEX + 1));
  }

  // --------------------------------------------------------------------------
  // sqrtPriceX64ToTickIndex (rust-sdk/core/src/math/tick.rs
  // `test_sqrt_price_to_tick_index`)
  // --------------------------------------------------------------------------

  @Test
  void sqrtPriceX64ToTickIndexAtBounds() {
    assertEquals(OrcaUtil.MAX_TICK_INDEX, OrcaUtil.sqrtPriceX64ToTickIndex(OrcaUtil.MAX_SQRT_PRICE_X64));
    assertEquals(OrcaUtil.MIN_TICK_INDEX, OrcaUtil.sqrtPriceX64ToTickIndex(OrcaUtil.MIN_SQRT_PRICE_X64));
    assertEquals(OrcaUtil.MIN_TICK_INDEX,
        OrcaUtil.sqrtPriceX64ToTickIndex(OrcaUtil.MIN_SQRT_PRICE_X64.add(BigInteger.ONE)));
    assertEquals(OrcaUtil.MAX_TICK_INDEX - 1,
        OrcaUtil.sqrtPriceX64ToTickIndex(OrcaUtil.MAX_SQRT_PRICE_X64.subtract(BigInteger.ONE)));
    assertTrue(OrcaUtil.sqrtPriceX64ToTickIndex(OrcaUtil.MIN_SQRT_PRICE_X64.subtract(BigInteger.ONE))
        < OrcaUtil.MIN_TICK_INDEX);
  }

  @Test
  void sqrtPriceX64ToTickIndexAroundOne() {
    assertEquals(0, OrcaUtil.sqrtPriceX64ToTickIndex(TWO_POW_64));
    assertEquals(0, OrcaUtil.sqrtPriceX64ToTickIndex(TWO_POW_64.add(BigInteger.ONE)));
    assertEquals(-1, OrcaUtil.sqrtPriceX64ToTickIndex(TWO_POW_64.subtract(BigInteger.ONE)));
  }

  /// Exercises the `tickLow == tickHigh` early return (the estimates agree
  /// for this sqrt-price; verified against the Rust algorithm:
  /// sqrt-price(-443303) <= 4367167935 < sqrt-price(-443302)).
  @Test
  void sqrtPriceX64ToTickIndexEstimatesAgree() {
    assertEquals(-443_303, OrcaUtil.sqrtPriceX64ToTickIndex(BigInteger.valueOf(4_367_167_935L)));
  }

  @Test
  void sqrtPriceX64ToTickIndexRejectsNonPositive() {
    assertThrows(IllegalArgumentException.class, () -> OrcaUtil.sqrtPriceX64ToTickIndex(BigInteger.ZERO));
    assertThrows(IllegalArgumentException.class, () -> OrcaUtil.sqrtPriceX64ToTickIndex(BigInteger.valueOf(-1)));
  }

  /// Mirrors the Rust proptest properties `test_tick_index_and_sqrt_price_symmetry`
  /// and `test_sqrt_price_from_tick_index_is_sequence` on a deterministic sample.
  @Test
  void tickSqrtPriceRoundTripAndMonotonic() {
    var previous = OrcaUtil.tickIndexToSqrtPriceX64(OrcaUtil.MIN_TICK_INDEX - 1);
    for (int tick = OrcaUtil.MIN_TICK_INDEX; tick <= OrcaUtil.MAX_TICK_INDEX; tick += 991) {
      final var sqrtPrice = OrcaUtil.tickIndexToSqrtPriceX64(tick);
      assertTrue(previous.compareTo(sqrtPrice) < 0, "not monotonic at tick " + tick);
      assertEquals(tick, OrcaUtil.sqrtPriceX64ToTickIndex(sqrtPrice), "round trip at tick " + tick);
      previous = sqrtPrice;
    }
    assertEquals(OrcaUtil.MAX_TICK_INDEX,
        OrcaUtil.sqrtPriceX64ToTickIndex(OrcaUtil.tickIndexToSqrtPriceX64(OrcaUtil.MAX_TICK_INDEX)));
  }

  // --------------------------------------------------------------------------
  // getSqrtPriceSlippageBounds / sqrtFloor
  // (rust-sdk/core/src/math/price.rs `test_get_sqrt_price_slippage_bounds`;
  // exact expected values derived from the Rust integer algorithm.)
  // --------------------------------------------------------------------------

  private static void assertSlippageBounds(final BigInteger sqrtPrice,
                                           final int bps,
                                           final String expectedMin,
                                           final String expectedMax) {
    final var bounds = OrcaUtil.getSqrtPriceSlippageBounds(sqrtPrice, bps);
    assertEquals(bi(expectedMin), bounds.minSqrtPrice(), "min at bps " + bps);
    assertEquals(bi(expectedMax), bounds.maxSqrtPrice(), "max at bps " + bps);
  }

  @Test
  void getSqrtPriceSlippageBoundsVectors() {
    // sqrt_price for price 1_000_000.0 with equal decimals, from the Rust test.
    final var sqrtPrice = bi("18446744073709551616000");
    assertSlippageBounds(sqrtPrice, 0, "18446744073709551616000", "18446744073709551616000");
    assertSlippageBounds(sqrtPrice, 1, "18445637269065129042903", "18447666410913237093580");
    assertSlippageBounds(sqrtPrice, 10, "18437336234231959744675", "18455967445746406391808");
    assertSlippageBounds(sqrtPrice, 100, "18354141418459529666887", "18538793326637362278563");
    assertSlippageBounds(sqrtPrice, 1000, "17500057167846777427066", "19347129651947314830376");
    assertSlippageBounds(sqrtPrice, 5000, "13043692734520023947673", "22592649804275773341696");
    assertSlippageBounds(sqrtPrice, 10000, "4295048016", "26087754403921522086379");
    // bps above the denominator are clamped to 10_000.
    assertSlippageBounds(sqrtPrice, 20000, "4295048016", "26087754403921522086379");
  }

  @Test
  void getSqrtPriceSlippageBoundsClampsAndValidates() {
    assertSlippageBounds(TWO_POW_64, 100, "18354141418459529666", "18538793326637362278");
    assertSlippageBounds(OrcaUtil.MIN_SQRT_PRICE_X64, 100, "4295048016", "4316480305");
    assertSlippageBounds(OrcaUtil.MAX_SQRT_PRICE_X64, 100,
        "78828955614353965566885492208", "79226673515401279992447579055");
    assertThrows(IllegalArgumentException.class,
        () -> OrcaUtil.getSqrtPriceSlippageBounds(TWO_POW_64, -1));
  }

  @Test
  void sqrtFloor() {
    assertEquals(BigInteger.ZERO, OrcaUtil.sqrtFloor(BigInteger.ZERO));
    assertEquals(BigInteger.ONE, OrcaUtil.sqrtFloor(BigInteger.ONE));
    assertEquals(BigInteger.ONE, OrcaUtil.sqrtFloor(BigInteger.TWO));
    assertEquals(BigInteger.ONE, OrcaUtil.sqrtFloor(BigInteger.valueOf(3)));
    assertEquals(BigInteger.TWO, OrcaUtil.sqrtFloor(BigInteger.valueOf(4)));
    assertEquals(BigInteger.valueOf(3), OrcaUtil.sqrtFloor(BigInteger.valueOf(15)));
    assertEquals(BigInteger.valueOf(4), OrcaUtil.sqrtFloor(BigInteger.valueOf(16)));
    assertEquals(BigInteger.valueOf(4), OrcaUtil.sqrtFloor(BigInteger.valueOf(17)));
    assertEquals(BigInteger.valueOf(1_000_000_000L), OrcaUtil.sqrtFloor(bi("1000000000000000000")));
    assertEquals(BigInteger.valueOf(1_000_000_000L), OrcaUtil.sqrtFloor(bi("1000000000000000001")));
    // isqrt(2^128 - 1) == 2^64 - 1.
    assertEquals(bi("18446744073709551615"), OrcaUtil.sqrtFloor(OrcaUtil.U128_MASK));
    assertThrows(IllegalArgumentException.class, () -> OrcaUtil.sqrtFloor(BigInteger.valueOf(-1)));
  }

  // --------------------------------------------------------------------------
  // tryGetAmountDeltaA / tryGetAmountDeltaB
  // (rust-sdk/core/src/math/token.rs `test_get_amount_delta_a/b`.)
  // --------------------------------------------------------------------------

  private static final BigInteger FOUR_X64 = BigInteger.valueOf(4).shiftLeft(64);
  private static final BigInteger TWO_X64 = BigInteger.TWO.shiftLeft(64);
  // Sqrt-prices for ticks -100 / 0 / 100 (see LiquidityQuoteTests).
  private static final BigInteger SQRT_PRICE_TICK_NEG_100 = bi("18354745142194483561");
  private static final BigInteger SQRT_PRICE_TICK_100 = bi("18539204128674405812");

  @Test
  void tryGetAmountDeltaA() {
    final var four = BigInteger.valueOf(4);
    assertEquals(BigInteger.ONE, OrcaUtil.tryGetAmountDeltaA(FOUR_X64, TWO_X64, four, true));
    assertEquals(BigInteger.ONE, OrcaUtil.tryGetAmountDeltaA(FOUR_X64, TWO_X64, four, false));
    assertEquals(BigInteger.ZERO, OrcaUtil.tryGetAmountDeltaA(FOUR_X64, FOUR_X64, four, true));
    assertEquals(BigInteger.ZERO, OrcaUtil.tryGetAmountDeltaA(FOUR_X64, FOUR_X64, four, false));
    // Rounding split (liquidity 5 leaves a remainder of 1/4).
    assertEquals(BigInteger.TWO, OrcaUtil.tryGetAmountDeltaA(FOUR_X64, TWO_X64, BigInteger.valueOf(5), true));
    assertEquals(BigInteger.ONE, OrcaUtil.tryGetAmountDeltaA(FOUR_X64, TWO_X64, BigInteger.valueOf(5), false));
    // Tick range 0..100 with liquidity 1e6 (same fixture as the liquidity quotes).
    final var liquidity = BigInteger.valueOf(1_000_000L);
    assertEquals(BigInteger.valueOf(4988L),
        OrcaUtil.tryGetAmountDeltaA(TWO_POW_64, SQRT_PRICE_TICK_100, liquidity, true));
    assertEquals(BigInteger.valueOf(4987L),
        OrcaUtil.tryGetAmountDeltaA(TWO_POW_64, SQRT_PRICE_TICK_100, liquidity, false));
  }

  @Test
  void tryGetAmountDeltaAErrors() {
    assertThrows(IllegalArgumentException.class,
        () -> OrcaUtil.tryGetAmountDeltaA(BigInteger.valueOf(-1), TWO_X64, BigInteger.ONE, false));
    assertThrows(IllegalArgumentException.class,
        () -> OrcaUtil.tryGetAmountDeltaA(TWO_X64, TWO_X64, OrcaUtil.U128_MASK.add(BigInteger.ONE), false));
    assertThrows(ArithmeticException.class,
        () -> OrcaUtil.tryGetAmountDeltaA(BigInteger.ZERO, TWO_X64, BigInteger.ONE, false));
  }

  /// The Rust impl errors with `AMOUNT_EXCEEDS_MAX_U64` when the delta
  /// overflows u64; the Java port intentionally exposes the full
  /// `BigInteger` and leaves range checking to the caller.
  @Test
  void tryGetAmountDeltaAExceedingU64() {
    final var min = OrcaUtil.MIN_SQRT_PRICE_X64;
    assertEquals(bi("39613336760533489628789124045"),
        OrcaUtil.tryGetAmountDeltaA(min, min.multiply(BigInteger.TWO), TWO_POW_64, false));
  }

  @Test
  void tryGetAmountDeltaB() {
    final var four = BigInteger.valueOf(4);
    assertEquals(BigInteger.valueOf(8), OrcaUtil.tryGetAmountDeltaB(FOUR_X64, TWO_X64, four, true));
    assertEquals(BigInteger.valueOf(8), OrcaUtil.tryGetAmountDeltaB(FOUR_X64, TWO_X64, four, false));
    assertEquals(BigInteger.ZERO, OrcaUtil.tryGetAmountDeltaB(FOUR_X64, FOUR_X64, four, true));
    assertEquals(BigInteger.ZERO, OrcaUtil.tryGetAmountDeltaB(FOUR_X64, FOUR_X64, four, false));
    // Exact product (low 64 bits zero): round-up must not bump the result.
    assertEquals(BigInteger.TEN, OrcaUtil.tryGetAmountDeltaB(FOUR_X64, TWO_X64, BigInteger.valueOf(5), true));
    // Tick range -100..0 with liquidity 1e6: remainder forces the rounding split.
    final var liquidity = BigInteger.valueOf(1_000_000L);
    assertEquals(BigInteger.valueOf(4988L),
        OrcaUtil.tryGetAmountDeltaB(SQRT_PRICE_TICK_NEG_100, TWO_POW_64, liquidity, true));
    assertEquals(BigInteger.valueOf(4987L),
        OrcaUtil.tryGetAmountDeltaB(SQRT_PRICE_TICK_NEG_100, TWO_POW_64, liquidity, false));
    assertThrows(IllegalArgumentException.class,
        () -> OrcaUtil.tryGetAmountDeltaB(BigInteger.valueOf(-1), TWO_X64, BigInteger.ONE, false));
    // u128 range edges are accepted: liquidity of 0 and u128::MAX.
    assertEquals(BigInteger.ZERO, OrcaUtil.tryGetAmountDeltaB(FOUR_X64, TWO_X64, BigInteger.ZERO, false));
    // (2^128 - 1) * (2 << 64) >> 64 == 2^129 - 2.
    assertEquals(BigInteger.ONE.shiftLeft(129).subtract(BigInteger.TWO),
        OrcaUtil.tryGetAmountDeltaB(FOUR_X64, TWO_X64, OrcaUtil.U128_MASK, false));
  }

  // --------------------------------------------------------------------------
  // tryGetNextSqrtPriceFromA / tryGetNextSqrtPriceFromB
  // (rust-sdk/core/src/math/token.rs `test_get_next_sqrt_price_from_a/b`.)
  // --------------------------------------------------------------------------

  @Test
  void tryGetNextSqrtPriceFromA() {
    final var four = BigInteger.valueOf(4);
    assertEquals(TWO_X64, OrcaUtil.tryGetNextSqrtPriceFromA(FOUR_X64, four, 1L, true));
    assertEquals(FOUR_X64, OrcaUtil.tryGetNextSqrtPriceFromA(TWO_X64, four, 1L, false));
    assertEquals(FOUR_X64, OrcaUtil.tryGetNextSqrtPriceFromA(FOUR_X64, four, 0L, true));
    assertEquals(FOUR_X64, OrcaUtil.tryGetNextSqrtPriceFromA(FOUR_X64, four, 0L, false));
    // Liquidity 1e18, amount 1e6 around price 1 (always rounded up).
    final var liquidity = bi("1000000000000000000");
    assertEquals(bi("18446744073691104872"),
        OrcaUtil.tryGetNextSqrtPriceFromA(TWO_POW_64, liquidity, 1_000_000L, true));
    assertEquals(bi("18446744073727998361"),
        OrcaUtil.tryGetNextSqrtPriceFromA(TWO_POW_64, liquidity, 1_000_000L, false));
  }

  @Test
  void tryGetNextSqrtPriceFromAErrors() {
    // specified_input == false with p > liquidity << 64 leaves a negative denominator.
    final var negative = assertThrows(ArithmeticException.class,
        () -> OrcaUtil.tryGetNextSqrtPriceFromA(TWO_X64, BigInteger.ONE, 3L, false));
    assertEquals("denominator is non-positive", negative.getMessage());
    // p == liquidity << 64 leaves a zero denominator.
    final var zero = assertThrows(ArithmeticException.class,
        () -> OrcaUtil.tryGetNextSqrtPriceFromA(TWO_X64, BigInteger.TWO, 1L, false));
    assertEquals("denominator is non-positive", zero.getMessage());
    // Result below MIN_SQRT_PRICE_X64.
    assertThrows(ArithmeticException.class,
        () -> OrcaUtil.tryGetNextSqrtPriceFromA(OrcaUtil.MIN_SQRT_PRICE_X64, BigInteger.ONE, 1L << 32, true));
    assertThrows(IllegalArgumentException.class,
        () -> OrcaUtil.tryGetNextSqrtPriceFromA(BigInteger.valueOf(-1), BigInteger.ONE, 1L, true));
  }

  @Test
  void tryGetNextSqrtPriceFromB() {
    final var four = BigInteger.valueOf(4);
    assertEquals(FOUR_X64, OrcaUtil.tryGetNextSqrtPriceFromB(TWO_X64, four, 8L, true));
    assertEquals(TWO_X64, OrcaUtil.tryGetNextSqrtPriceFromB(FOUR_X64, four, 8L, false));
    assertEquals(FOUR_X64, OrcaUtil.tryGetNextSqrtPriceFromB(FOUR_X64, four, 0L, true));
    assertEquals(FOUR_X64, OrcaUtil.tryGetNextSqrtPriceFromB(FOUR_X64, four, 0L, false));
    // Liquidity 1e18, amount 1e6: round down on input, up on output.
    final var liquidity = bi("1000000000000000000");
    assertEquals(bi("18446744073727998360"),
        OrcaUtil.tryGetNextSqrtPriceFromB(TWO_POW_64, liquidity, 1_000_000L, true));
    assertEquals(bi("18446744073691104871"),
        OrcaUtil.tryGetNextSqrtPriceFromB(TWO_POW_64, liquidity, 1_000_000L, false));
    // Results landing exactly on the sqrt-price bounds are accepted.
    assertEquals(OrcaUtil.MIN_SQRT_PRICE_X64,
        OrcaUtil.tryGetNextSqrtPriceFromB(OrcaUtil.MIN_SQRT_PRICE_X64.add(BigInteger.ONE), TWO_POW_64, 1L, false));
    assertEquals(OrcaUtil.MAX_SQRT_PRICE_X64,
        OrcaUtil.tryGetNextSqrtPriceFromB(OrcaUtil.MAX_SQRT_PRICE_X64.subtract(BigInteger.ONE), TWO_POW_64, 1L, true));
  }

  @Test
  void tryGetNextSqrtPriceFromBErrors() {
    final var zeroLiquidity = assertThrows(ArithmeticException.class,
        () -> OrcaUtil.tryGetNextSqrtPriceFromB(TWO_POW_64, BigInteger.ZERO, 1L, true));
    assertEquals("currentLiquidity is zero", zeroLiquidity.getMessage());
    // Below MIN_SQRT_PRICE_X64 / above MAX_SQRT_PRICE_X64.
    assertThrows(ArithmeticException.class,
        () -> OrcaUtil.tryGetNextSqrtPriceFromB(OrcaUtil.MIN_SQRT_PRICE_X64, BigInteger.ONE, 1L, false));
    assertThrows(ArithmeticException.class,
        () -> OrcaUtil.tryGetNextSqrtPriceFromB(OrcaUtil.MAX_SQRT_PRICE_X64, BigInteger.ONE, 1L, true));
    assertThrows(IllegalArgumentException.class,
        () -> OrcaUtil.tryGetNextSqrtPriceFromB(TWO_POW_64, BigInteger.valueOf(-1), 1L, true));
  }

  // --------------------------------------------------------------------------
  // applySwapFee / reverseApplySwapFee
  // (rust-sdk/core/src/math/token.rs `test_apply_swap_fee` /
  // `test_reverse_apply_swap_fee`; FEE_RATE_HARD_LIMIT == 100_000.)
  // --------------------------------------------------------------------------

  @Test
  void applySwapFee() {
    assertEquals(0L, OrcaUtil.applySwapFee(0L, 1000));
    assertEquals(10000L, OrcaUtil.applySwapFee(10000L, 0));
    assertEquals(9990L, OrcaUtil.applySwapFee(10000L, 1000));
    assertEquals(9900L, OrcaUtil.applySwapFee(10000L, 10000));
    assertEquals(9344L, OrcaUtil.applySwapFee(10000L, 65535));
    assertEquals(9000L, OrcaUtil.applySwapFee(10000L, 100_000));
    // Round-down check (9999 * 0.999 = 9989.001).
    assertEquals(9989L, OrcaUtil.applySwapFee(9999L, 1000));
    // Whole amount taken as fee.
    assertEquals(0L, OrcaUtil.applySwapFee(10000L, OrcaUtil.FEE_RATE_DENOMINATOR));
    assertThrows(IllegalArgumentException.class, () -> OrcaUtil.applySwapFee(10000L, -1));
    assertThrows(IllegalArgumentException.class,
        () -> OrcaUtil.applySwapFee(10000L, OrcaUtil.FEE_RATE_DENOMINATOR + 1));
  }

  @Test
  void reverseApplySwapFee() {
    assertEquals(0L, OrcaUtil.reverseApplySwapFee(0L, 1000));
    assertEquals(10000L, OrcaUtil.reverseApplySwapFee(10000L, 0));
    assertEquals(10000L, OrcaUtil.reverseApplySwapFee(9990L, 1000));
    assertEquals(10000L, OrcaUtil.reverseApplySwapFee(9900L, 10000));
    assertEquals(10000L, OrcaUtil.reverseApplySwapFee(9344L, 65535));
    assertEquals(10000L, OrcaUtil.reverseApplySwapFee(9000L, 100_000));
    // Round-up check (9991 / 0.999 = 10001.001...).
    assertEquals(10002L, OrcaUtil.reverseApplySwapFee(9991L, 1000));
    assertThrows(IllegalArgumentException.class, () -> OrcaUtil.reverseApplySwapFee(10000L, -1));
    assertThrows(IllegalArgumentException.class,
        () -> OrcaUtil.reverseApplySwapFee(10000L, OrcaUtil.FEE_RATE_DENOMINATOR + 1));
    // feeRate == denominator passes validation but divides by zero (the Rust
    // impl panics on the same input).
    assertThrows(ArithmeticException.class,
        () -> OrcaUtil.reverseApplySwapFee(10000L, OrcaUtil.FEE_RATE_DENOMINATOR));
  }

  // --------------------------------------------------------------------------
  // Transfer fee / slippage amount boundaries
  // (rust-sdk/core/src/math/token.rs; supplements the indirect quote coverage.)
  // --------------------------------------------------------------------------

  @Test
  void applyTransferFeeBoundaries() {
    final long noCap = Long.MAX_VALUE;
    assertEquals(0L, OrcaUtil.applyTransferFee(0L, 100, noCap));
    assertEquals(10000L, OrcaUtil.applyTransferFee(10000L, 0, noCap));
    assertEquals(9900L, OrcaUtil.applyTransferFee(10000L, 100, noCap));
    assertEquals(0L, OrcaUtil.applyTransferFee(10000L, 10000, noCap));
    // div_ceil: fee on 9999 at 100 bps is ceil(99.99) == 100.
    assertEquals(9899L, OrcaUtil.applyTransferFee(9999L, 100, noCap));
    // max_fee cap (Rust `test_apply_transfer_fee_with_max`).
    assertEquals(9500L, OrcaUtil.applyTransferFee(10000L, 1000, 500L));
    assertEquals(9500L, OrcaUtil.applyTransferFee(10000L, 10000, 500L));
    assertThrows(IllegalArgumentException.class, () -> OrcaUtil.applyTransferFee(10000L, 10001, noCap));
    assertThrows(IllegalArgumentException.class, () -> OrcaUtil.applyTransferFee(10000L, -1, noCap));
  }

  @Test
  void reverseApplyTransferFeeBoundaries() {
    final long noCap = Long.MAX_VALUE;
    assertEquals(0L, OrcaUtil.reverseApplyTransferFee(0L, 100, noCap));
    assertEquals(10000L, OrcaUtil.reverseApplyTransferFee(10000L, 0, noCap));
    assertEquals(10000L, OrcaUtil.reverseApplyTransferFee(9900L, 100, noCap));
    assertEquals(10000L, OrcaUtil.reverseApplyTransferFee(9000L, 1000, noCap));
    // max_fee cap (Rust `test_reverse_apply_transfer_fee_with_max`).
    assertEquals(10000L, OrcaUtil.reverseApplyTransferFee(9500L, 1000, 500L));
    assertEquals(10000L, OrcaUtil.reverseApplyTransferFee(9500L, 10000, 500L));
    // 100% fee: pre-fee amount is amount + max_fee.
    assertEquals(600L, OrcaUtil.reverseApplyTransferFee(100L, 10000, 500L));
    assertEquals(0L, OrcaUtil.reverseApplyTransferFee(0L, 10000, 500L));
    assertThrows(IllegalArgumentException.class, () -> OrcaUtil.reverseApplyTransferFee(10000L, 10001, noCap));
    assertThrows(IllegalArgumentException.class, () -> OrcaUtil.reverseApplyTransferFee(10000L, -1, noCap));
  }

  @Test
  void slippageToleranceAmountBoundaries() {
    assertEquals(0L, OrcaUtil.maxAmountWithSlippageTolerance(0L, 100));
    assertEquals(10000L, OrcaUtil.maxAmountWithSlippageTolerance(10000L, 0));
    assertEquals(10100L, OrcaUtil.maxAmountWithSlippageTolerance(10000L, 100));
    assertEquals(20000L, OrcaUtil.maxAmountWithSlippageTolerance(10000L, 10000));
    // Round-up: 9999 * 1.01 = 10098.99.
    assertEquals(10099L, OrcaUtil.maxAmountWithSlippageTolerance(9999L, 100));
    assertEquals(0L, OrcaUtil.minAmountWithSlippageTolerance(0L, 100));
    assertEquals(10000L, OrcaUtil.minAmountWithSlippageTolerance(10000L, 0));
    assertEquals(9900L, OrcaUtil.minAmountWithSlippageTolerance(10000L, 100));
    assertEquals(0L, OrcaUtil.minAmountWithSlippageTolerance(10000L, 10000));
    // Round-down: 9999 * 0.99 = 9899.01.
    assertEquals(9899L, OrcaUtil.minAmountWithSlippageTolerance(9999L, 100));
    assertThrows(IllegalArgumentException.class, () -> OrcaUtil.maxAmountWithSlippageTolerance(10000L, 10001));
    assertThrows(IllegalArgumentException.class, () -> OrcaUtil.minAmountWithSlippageTolerance(10000L, 10001));
    assertThrows(IllegalArgumentException.class, () -> OrcaUtil.maxAmountWithSlippageTolerance(10000L, -1));
    assertThrows(IllegalArgumentException.class, () -> OrcaUtil.minAmountWithSlippageTolerance(10000L, -1));
  }

  // --------------------------------------------------------------------------
  // Position status boundaries
  // (rust-sdk/core/src/math/position.rs `position_status`.)
  // --------------------------------------------------------------------------

  @Test
  void positionStatusBoundaries() {
    final var lower = SQRT_PRICE_TICK_NEG_100;
    final var upper = SQRT_PRICE_TICK_100;
    assertEquals(OrcaUtil.PositionStatus.INVALID, OrcaUtil.positionStatus(TWO_POW_64, lower, lower));
    assertEquals(OrcaUtil.PositionStatus.IN_RANGE, OrcaUtil.positionStatus(TWO_POW_64, lower, upper));
    // Equality with either bound counts as out-of-range.
    assertEquals(OrcaUtil.PositionStatus.BELOW_RANGE, OrcaUtil.positionStatus(lower, lower, upper));
    assertEquals(OrcaUtil.PositionStatus.ABOVE_RANGE, OrcaUtil.positionStatus(upper, lower, upper));
    assertEquals(OrcaUtil.PositionStatus.BELOW_RANGE,
        OrcaUtil.positionStatus(lower.subtract(BigInteger.ONE), lower, upper));
    assertEquals(OrcaUtil.PositionStatus.ABOVE_RANGE,
        OrcaUtil.positionStatus(upper.add(BigInteger.ONE), lower, upper));
    // Reversed bounds are re-ordered.
    assertEquals(OrcaUtil.PositionStatus.IN_RANGE, OrcaUtil.positionStatus(TWO_POW_64, upper, lower));
    assertTrue(OrcaUtil.isPositionInRange(TWO_POW_64, lower, upper));
    assertFalse(OrcaUtil.isPositionInRange(lower, lower, upper));
  }

  // --------------------------------------------------------------------------
  // price <-> sqrt-price / tick-index conversions
  // (rust-sdk/core/src/math/price.rs tests; expected doubles verified
  // independently under IEEE-754 semantics.)
  // --------------------------------------------------------------------------

  @Test
  void priceToSqrtPriceX64() {
    assertEquals(bi("184467348503352096"), OrcaUtil.priceToSqrtPriceX64(0.00999999, 8, 6));
    assertEquals(bi("184467440737095516160"), OrcaUtil.priceToSqrtPriceX64(100.0, 6, 6));
    assertEquals(bi("1844776783959692673024"), OrcaUtil.priceToSqrtPriceX64(100.0111, 6, 8));
    assertEquals(bi("18446744073709551616000"), OrcaUtil.priceToSqrtPriceX64(1_000_000.0, 6, 6));
    assertEquals(BigInteger.ZERO, OrcaUtil.priceToSqrtPriceX64(0.0, 6, 6));
    assertEquals(BigInteger.ZERO, OrcaUtil.priceToSqrtPriceX64(-1.0, 6, 6));
  }

  @Test
  void sqrtPriceX64ToPrice() {
    assertEquals(0.00999999, OrcaUtil.sqrtPriceX64ToPrice(bi("184467348503352096"), 8, 6));
    assertEquals(100.0, OrcaUtil.sqrtPriceX64ToPrice(bi("184467440737095516160"), 6, 6));
    assertEquals(100.01109999999998, OrcaUtil.sqrtPriceX64ToPrice(bi("1844776783959692673024"), 6, 8));
    // SOL/USDC (Rust `test_sol_usdc`) and BONK/USDC (`test_bonk_usdc`).
    assertEquals(140.66116595692344, OrcaUtil.sqrtPriceX64ToPrice(bi("6918418495991757039"), 9, 6));
    assertEquals(2.0791623715496336e-5, OrcaUtil.sqrtPriceX64ToPrice(bi("265989152599097743"), 5, 6));
  }

  @Test
  void tickIndexToPrice() {
    assertEquals(0.009997009410018782, OrcaUtil.tickIndexToPrice(-92111, 8, 6));
    assertEquals(1.0, OrcaUtil.tickIndexToPrice(0, 6, 6));
    assertEquals(99.99991187245698, OrcaUtil.tickIndexToPrice(92108, 6, 8));
  }

  @Test
  void priceToTickIndex() {
    assertEquals(-92111, OrcaUtil.priceToTickIndex(0.009998, 8, 6));
    assertEquals(0, OrcaUtil.priceToTickIndex(1.0, 6, 6));
    assertEquals(92108, OrcaUtil.priceToTickIndex(99.999912, 6, 8));
  }

  @Test
  void invertPrice() {
    assertEquals(1000099.1186364421, OrcaUtil.invertPrice(0.00999999, 8, 6));
    assertEquals(0.010000004406380065, OrcaUtil.invertPrice(100.0, 6, 6));
    assertEquals(9.999008911870883e-7, OrcaUtil.invertPrice(100.0111, 6, 8));
  }

  @Test
  void invertSqrtPriceX64() {
    // Tick 0 inverts onto itself.
    assertEquals(TWO_POW_64, OrcaUtil.invertSqrtPriceX64(TWO_POW_64));
    // sqrt-price(tick 100) <-> sqrt-price(tick -100).
    assertEquals(SQRT_PRICE_TICK_NEG_100, OrcaUtil.invertSqrtPriceX64(SQRT_PRICE_TICK_100));
    assertEquals(SQRT_PRICE_TICK_100, OrcaUtil.invertSqrtPriceX64(SQRT_PRICE_TICK_NEG_100));
    // Bounds invert onto each other.
    assertEquals(OrcaUtil.MIN_SQRT_PRICE_X64, OrcaUtil.invertSqrtPriceX64(OrcaUtil.MAX_SQRT_PRICE_X64));
    assertEquals(OrcaUtil.MAX_SQRT_PRICE_X64, OrcaUtil.invertSqrtPriceX64(OrcaUtil.MIN_SQRT_PRICE_X64));
    // Clamped to the nearest tick: a price between ticks maps to tick 0.
    assertEquals(TWO_POW_64, OrcaUtil.invertSqrtPriceX64(TWO_POW_64.add(BigInteger.ONE)));
  }

  // --------------------------------------------------------------------------
  // Tick / tick-array helpers
  // (rust-sdk/core/src/math/tick.rs and programs/whirlpool/src/state/tick.rs.)
  // --------------------------------------------------------------------------

  @Test
  void isTickIndexInBoundsAndBound() {
    assertTrue(OrcaUtil.isTickIndexInBounds(OrcaUtil.MIN_TICK_INDEX));
    assertTrue(OrcaUtil.isTickIndexInBounds(OrcaUtil.MAX_TICK_INDEX));
    assertFalse(OrcaUtil.isTickIndexInBounds(OrcaUtil.MIN_TICK_INDEX - 1));
    assertFalse(OrcaUtil.isTickIndexInBounds(OrcaUtil.MAX_TICK_INDEX + 1));
    assertEquals(OrcaUtil.MIN_TICK_INDEX, OrcaUtil.boundTickIndex(OrcaUtil.MIN_TICK_INDEX - 1));
    assertEquals(OrcaUtil.MAX_TICK_INDEX, OrcaUtil.boundTickIndex(OrcaUtil.MAX_TICK_INDEX + 1));
    assertEquals(42, OrcaUtil.boundTickIndex(42));
  }

  @Test
  void isUsableTick() {
    assertTrue(OrcaUtil.isUsableTick(0, 1));
    assertTrue(OrcaUtil.isUsableTick(OrcaUtil.MIN_TICK_INDEX, 1));
    assertTrue(OrcaUtil.isUsableTick(OrcaUtil.MAX_TICK_INDEX, 1));
    assertFalse(OrcaUtil.isUsableTick(OrcaUtil.MIN_TICK_INDEX - 1, 1));
    assertFalse(OrcaUtil.isUsableTick(OrcaUtil.MAX_TICK_INDEX + 1, 1));
    assertTrue(OrcaUtil.isUsableTick(128, 64));
    assertTrue(OrcaUtil.isUsableTick(-128, 64));
    assertFalse(OrcaUtil.isUsableTick(127, 64));
    assertFalse(OrcaUtil.isUsableTick(-127, 64));
    assertThrows(IllegalArgumentException.class, () -> OrcaUtil.isUsableTick(0, 0));
    assertThrows(IllegalArgumentException.class, () -> OrcaUtil.isUsableTick(0, -1));
  }

  /// Vectors from `test_get_full_range_tick_indexes` (spacings 1, 128,
  /// FULL_RANGE_ONLY_TICK_SPACING_THRESHOLD, u16::MAX).
  @Test
  void fullRangeTickIndexes() {
    assertArrayEqualsRange(-443_636, 443_636, OrcaUtil.fullRangeTickIndexes(1));
    assertArrayEqualsRange(-443_520, 443_520, OrcaUtil.fullRangeTickIndexes(128));
    assertArrayEqualsRange(-425_984, 425_984,
        OrcaUtil.fullRangeTickIndexes(OrcaUtil.FULL_RANGE_ONLY_TICK_SPACING_THRESHOLD));
    assertArrayEqualsRange(-393_210, 393_210, OrcaUtil.fullRangeTickIndexes(65_535));
    assertThrows(IllegalArgumentException.class, () -> OrcaUtil.fullRangeTickIndexes(0));
    assertThrows(IllegalArgumentException.class, () -> OrcaUtil.fullRangeTickIndexes(-1));
  }

  private static void assertArrayEqualsRange(final int lower, final int upper, final int[] range) {
    assertEquals(2, range.length);
    assertEquals(lower, range[0]);
    assertEquals(upper, range[1]);
  }

  /// Vectors from `test_get_tick_array_start_tick_index` (rust-sdk tick.rs).
  @Test
  void startTickIndex() {
    assertEquals(704, OrcaUtil.ticksPerArray(8));
    assertThrows(IllegalArgumentException.class, () -> OrcaUtil.ticksPerArray(0));
    assertEquals(0, OrcaUtil.startTickIndex(0, 8));
    assertEquals(704, OrcaUtil.startTickIndex(740, 8));
    assertEquals(337_920, OrcaUtil.startTickIndex(338_433, 128));
    assertEquals(-704, OrcaUtil.startTickIndex(-624, 8));
    assertEquals(-337_920, OrcaUtil.startTickIndex(-337_409, 128));
    // Exact negative multiples must not be pushed one array further left.
    assertEquals(-704, OrcaUtil.startTickIndex(-704, 8));
    assertEquals(704, OrcaUtil.startTickIndex(704, 8));
    // MIN_TICK_INDEX falls in the left-edge array below the supported range.
    assertEquals(-444_224, OrcaUtil.startTickIndex(OrcaUtil.MIN_TICK_INDEX, 8));
    assertEquals(-450_560, OrcaUtil.startTickIndex(OrcaUtil.MIN_TICK_INDEX, 128));
    // Offset variant.
    assertEquals(-704, OrcaUtil.startTickIndex(0, 8, -1));
    assertEquals(1408, OrcaUtil.startTickIndex(740, 8, 1));
    assertEquals(704, OrcaUtil.startTickIndex(740, 8, 0));
  }

  /// Agreement subset of `check_is_valid_start_tick_tests`
  /// (programs/whirlpool/src/state/tick.rs). The out-of-bounds left-edge
  /// values are deliberately not pinned here: the Java implementation
  /// computes the left-edge start with `Math.floorMod` while the on-chain
  /// program uses Rust's truncated `%`, so they disagree there (see the
  /// suite report).
  @Test
  void isValidStartTickIndex() {
    assertTrue(OrcaUtil.isValidStartTickIndex(0, 8));
    assertTrue(OrcaUtil.isValidStartTickIndex(704, 8));
    assertTrue(OrcaUtil.isValidStartTickIndex(-704, 8));
    assertTrue(OrcaUtil.isValidStartTickIndex(337_920, 128));
    assertTrue(OrcaUtil.isValidStartTickIndex(-337_920, 128));
    assertFalse(OrcaUtil.isValidStartTickIndex(703, 8));
    assertFalse(OrcaUtil.isValidStartTickIndex(-352, 8));
    // Out of bounds above MIN_TICK_INDEX is always invalid.
    assertFalse(OrcaUtil.isValidStartTickIndex(450_560, 8));
    assertFalse(OrcaUtil.isValidStartTickIndex(2_353_573, 8));
    // Far below any left-edge candidate.
    assertFalse(OrcaUtil.isValidStartTickIndex(-2_353_573, 128));
    // Left-edge arrays extending below MIN_TICK_INDEX, per the on-chain
    // check_is_valid_start_tick tests (test_min_tick_array_start_tick_is_valid_ts8/128):
    // MIN_TICK_INDEX - (MIN_TICK_INDEX % ticksInArray + ticksInArray) with Rust's
    // truncated remainder.
    assertTrue(OrcaUtil.isValidStartTickIndex(-444_224, 8));
    assertFalse(OrcaUtil.isValidStartTickIndex(-444_928, 8));
    assertTrue(OrcaUtil.isValidStartTickIndex(-450_560, 128));
    assertFalse(OrcaUtil.isValidStartTickIndex(-461_824, 128));
    // the start tick derived for MIN_TICK_INDEX must validate against itself
    assertTrue(OrcaUtil.isValidStartTickIndex(OrcaUtil.startTickIndex(OrcaUtil.MIN_TICK_INDEX, 8), 8));
    assertTrue(OrcaUtil.isValidStartTickIndex(OrcaUtil.startTickIndex(OrcaUtil.MIN_TICK_INDEX, 128), 128));
  }

  @Test
  void u64RangeResults() {
    // rust-sdk/core/src/math/token.rs vectors at u64::MAX: results land in
    // [2^63, 2^64) and come back as unsigned long bits.
    assertEquals(Long.parseUnsignedLong("18428297329635842063"), OrcaUtil.applySwapFee(-1L, 1_000));
    assertEquals(Long.parseUnsignedLong("18262276632972456098"), OrcaUtil.applySwapFee(-1L, 10_000));
    assertEquals(Long.parseUnsignedLong("16602069666338596453"),
        OrcaUtil.minAmountWithSlippageTolerance(-1L, 1_000));
    assertEquals(0L, OrcaUtil.minAmountWithSlippageTolerance(-1L, 10_000));
  }

  // --------------------------------------------------------------------------
  // Position-bundle helpers
  // (rust-sdk/core/src/math/bundle.rs tests.)
  // --------------------------------------------------------------------------

  private static byte[] bundleBitmap(final int fill) {
    final var bitmap = new byte[OrcaUtil.POSITION_BUNDLE_BYTES];
    Arrays.fill(bitmap, (byte) fill);
    return bitmap;
  }

  @Test
  void firstUnoccupiedPositionInBundle() {
    assertEquals(OptionalInt.of(0), OrcaUtil.firstUnoccupiedPositionInBundle(bundleBitmap(0)));

    final var lowBundle = bundleBitmap(0);
    lowBundle[0] = (byte) 0b1110_1111;
    assertEquals(OptionalInt.of(4), OrcaUtil.firstUnoccupiedPositionInBundle(lowBundle));

    final var highBundle = bundleBitmap(0xFF);
    highBundle[10] = (byte) 0b1011_1111;
    assertEquals(OptionalInt.of(86), OrcaUtil.firstUnoccupiedPositionInBundle(highBundle));

    // Last slot boundary.
    final var lastSlot = bundleBitmap(0xFF);
    lastSlot[OrcaUtil.POSITION_BUNDLE_BYTES - 1] = (byte) 0b0111_1111;
    assertEquals(OptionalInt.of(255), OrcaUtil.firstUnoccupiedPositionInBundle(lastSlot));

    assertEquals(OptionalInt.empty(), OrcaUtil.firstUnoccupiedPositionInBundle(bundleBitmap(0xFF)));
  }

  @Test
  void isPositionBundleFull() {
    assertFalse(OrcaUtil.isPositionBundleFull(bundleBitmap(0)));
    assertTrue(OrcaUtil.isPositionBundleFull(bundleBitmap(0xFF)));
    final var partial = bundleBitmap(0);
    partial[0] = (byte) 0xFF;
    assertFalse(OrcaUtil.isPositionBundleFull(partial));
    final var nearlyFull = bundleBitmap(0xFF);
    nearlyFull[OrcaUtil.POSITION_BUNDLE_BYTES - 1] = (byte) 0xFE;
    assertFalse(OrcaUtil.isPositionBundleFull(nearlyFull));
  }

  @Test
  void isPositionBundleEmpty() {
    assertTrue(OrcaUtil.isPositionBundleEmpty(bundleBitmap(0)));
    assertFalse(OrcaUtil.isPositionBundleEmpty(bundleBitmap(0xFF)));
    final var partial = bundleBitmap(0);
    partial[0] = (byte) 0b11_1111;
    assertFalse(OrcaUtil.isPositionBundleEmpty(partial));
    final var lastBit = bundleBitmap(0);
    lastBit[OrcaUtil.POSITION_BUNDLE_BYTES - 1] = (byte) 0x80;
    assertFalse(OrcaUtil.isPositionBundleEmpty(lastBit));
  }

  @Test
  void bundleBitmapLengthValidation() {
    final var tooShort = new byte[OrcaUtil.POSITION_BUNDLE_BYTES - 1];
    final var tooLong = new byte[OrcaUtil.POSITION_BUNDLE_BYTES + 1];
    assertThrows(IllegalArgumentException.class, () -> OrcaUtil.firstUnoccupiedPositionInBundle(tooShort));
    assertThrows(IllegalArgumentException.class, () -> OrcaUtil.firstUnoccupiedPositionInBundle(tooLong));
    assertThrows(IllegalArgumentException.class, () -> OrcaUtil.isPositionBundleFull(tooShort));
    assertThrows(IllegalArgumentException.class, () -> OrcaUtil.isPositionBundleEmpty(tooLong));
  }

  @Test
  void isValidBundleIndexBoundaries() {
    assertTrue(OrcaUtil.isValidBundleIndex(0));
    assertTrue(OrcaUtil.isValidBundleIndex(OrcaUtil.POSITION_BUNDLE_SIZE - 1));
    assertFalse(OrcaUtil.isValidBundleIndex(-1));
    assertFalse(OrcaUtil.isValidBundleIndex(OrcaUtil.POSITION_BUNDLE_SIZE));
  }
}

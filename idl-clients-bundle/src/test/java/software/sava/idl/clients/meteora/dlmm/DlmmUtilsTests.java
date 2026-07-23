package software.sava.idl.clients.meteora.dlmm;

import org.junit.jupiter.api.Test;
import software.sava.idl.clients.meteora.dlmm.gen.types.LbPair;
import software.sava.idl.clients.meteora.dlmm.gen.types.StaticParameters;
import software.sava.idl.clients.meteora.dlmm.gen.types.VariableParameters;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

import static org.junit.jupiter.api.Assertions.*;

/// Expected values are derived independently of {@link DlmmUtils}: fixed-point results were
/// computed with arbitrary-precision integer arithmetic emulating the Rust reference
/// (`commons/src/math/u64x64_math.rs`, `price_math.rs` and `commons/src/extensions/{lb_pair,bin_array}.rs`
/// in the MeteoraAg/dlmm-sdk repository), and fee expectations are written out as the documented
/// formulas over BigInteger/long arithmetic.
final class DlmmUtilsTests {

  private static final BigInteger ONE_Q64 = BigInteger.ONE.shiftLeft(64);
  private static final BigInteger U128_MAX = BigInteger.ONE.shiftLeft(128).subtract(BigInteger.ONE);

  private static StaticParameters staticParameters(final int baseFactor,
                                                   final long variableFeeControl,
                                                   final int protocolShare,
                                                   final int baseFeePowerFactor) {
    return new StaticParameters(
        baseFactor,
        30,
        600,
        5_000,
        variableFeeControl,
        350_000,
        DlmmUtils.MIN_BIN_ID,
        DlmmUtils.MAX_BIN_ID,
        protocolShare,
        baseFeePowerFactor,
        0,
        0,
        new byte[3]
    );
  }

  private static LbPair lbPair(final StaticParameters parameters,
                               final long volatilityAccumulator,
                               final int binStep,
                               final long[] binArrayBitmap) {
    final var vParameters = new VariableParameters(
        volatilityAccumulator, 0, 0, new byte[4], 0, new byte[8]
    );
    return new LbPair(
        null,
        null,
        parameters,
        vParameters,
        new byte[1],
        new byte[2],
        0,
        0,
        binStep,
        0,
        0,
        new byte[2],
        0,
        0,
        null,
        null,
        null,
        null,
        null,
        new byte[32],
        null,
        null,
        binArrayBitmap,
        0,
        new byte[32],
        null,
        null,
        0,
        0,
        new byte[8],
        0,
        null,
        0,
        0,
        0,
        new byte[21]
    );
  }

  /// Bitmap bit for bin-array index `i` lives at offset `i + 512` across 16 little-endian u64 limbs.
  private static long[] bitmapWithArrayIndexes(final int... binArrayIndexes) {
    final long[] limbs = new long[16];
    for (final int index : binArrayIndexes) {
      final int offset = index + 512;
      limbs[offset >> 6] |= 1L << (offset & 63);
    }
    return limbs;
  }

  // ---------------------------------------------------------------------------
  // Q64.64 pow
  // ---------------------------------------------------------------------------

  @Test
  void powZeroExponentIsOne() {
    assertEquals(DlmmUtils.Q64X64_ONE, DlmmUtils.pow(BigInteger.ONE.shiftLeft(63), 0));
    assertEquals(DlmmUtils.Q64X64_ONE, DlmmUtils.pow(ONE_Q64, 0));
    assertEquals(DlmmUtils.Q64X64_ONE, DlmmUtils.pow(BigInteger.ZERO, 0));
    assertEquals(ONE_Q64, DlmmUtils.Q64X64_ONE);
  }

  @Test
  void powExactValues() {
    final var half = BigInteger.ONE.shiftLeft(63);
    final var two = BigInteger.ONE.shiftLeft(65);

    // 0.5^1 = 0.5, exact: no truncation occurs.
    assertEquals(BigInteger.ONE.shiftLeft(63), DlmmUtils.pow(half, 1));
    // 0.5^2 = 0.25 and 0.5^3 = 0.125 are exact powers of two.
    assertEquals(BigInteger.ONE.shiftLeft(62), DlmmUtils.pow(half, 2));
    assertEquals(BigInteger.ONE.shiftLeft(61), DlmmUtils.pow(half, 3));
    // 0.5^-1 = 2 via u128::MAX / 2^63 = 2^65 - 1 (one unit below exact 2.0).
    assertEquals(BigInteger.ONE.shiftLeft(65).subtract(BigInteger.ONE), DlmmUtils.pow(half, -1));

    // base == 1.0 triggers the 1/base inversion trick:
    // 1^1 = u128::MAX / (u128::MAX / 2^64) = 2^64 + 1 in the reference algorithm.
    assertEquals(ONE_Q64.add(BigInteger.ONE), DlmmUtils.pow(ONE_Q64, 1));

    // 2^1 = 2 via double inversion: u128::MAX / ((2^64 * (u128::MAX / 2^65)) >> 64).
    assertEquals(new BigInteger("36893488147419103236"), DlmmUtils.pow(two, 1));
    // 2^-1 = 0.5, inversion cancels the negative exponent: 2^63 - 1.
    assertEquals(BigInteger.ONE.shiftLeft(63).subtract(BigInteger.ONE), DlmmUtils.pow(two, -1));

    // The Rust reference takes a u128, so a Java caller's base is read modulo
    // 2^128 — the initial mask is the type boundary, not decoration. Unmasked,
    // a bit-128 base rides the inversion path into a zero quotient and null.
    assertEquals(DlmmUtils.pow(half, 2), DlmmUtils.pow(half.add(BigInteger.ONE.shiftLeft(128)), 2));
  }

  @Test
  void powOverflowAndInvalidInputs() {
    final var half = BigInteger.ONE.shiftLeft(63);
    // |exp| >= MAX_EXPONENTIAL (0x80000) is unsupported.
    assertNull(DlmmUtils.pow(half, 0x80000));
    assertNull(DlmmUtils.pow(half, -0x80000));
    assertNull(DlmmUtils.pow(half, Integer.MIN_VALUE));
    // Largest supported exponent underflows 0.5^524287 to zero.
    assertNull(DlmmUtils.pow(half, 0x7FFFF));
    // Zero base with a non-zero exponent underflows to zero.
    assertNull(DlmmUtils.pow(BigInteger.ZERO, 1));
    assertThrows(IllegalArgumentException.class, () -> DlmmUtils.pow(null, 1));
  }

  // ---------------------------------------------------------------------------
  // getPriceFromId
  // ---------------------------------------------------------------------------

  @Test
  void getPriceFromIdExactValues() {
    // Expected u128 values computed by emulating u64x64_math::pow / price_math::get_price_from_id
    // with arbitrary-precision integers.
    assertEquals(ONE_Q64, DlmmUtils.getPriceFromId(0, 10));
    assertEquals(new BigInteger("18465190817783261167"), DlmmUtils.getPriceFromId(1, 10));
    assertEquals(new BigInteger("18428315757951600016"), DlmmUtils.getPriceFromId(-1, 10));
    assertEquals(new BigInteger("80716369362312265840737"), DlmmUtils.getPriceFromId(8388, 10));
    assertEquals(new BigInteger("4215778900974969"), DlmmUtils.getPriceFromId(-8388, 10));
    assertEquals(new BigInteger("18492860933893825495"), DlmmUtils.getPriceFromId(1, 25));
    assertEquals(new BigInteger("23678699809202413098"), DlmmUtils.getPriceFromId(100, 25));
    assertEquals(new BigInteger("18068232580748957375736"), DlmmUtils.getPriceFromId(17221, 4));
    // binStep == BASIS_POINT_MAX doubles the price per bin.
    assertEquals(new BigInteger("36893488147419103236"), DlmmUtils.getPriceFromId(1, 10_000));
    // Extremes for the smallest bin step saturate the u128 range.
    assertEquals(U128_MAX, DlmmUtils.getPriceFromId(DlmmUtils.MAX_BIN_ID, 1));
    assertEquals(BigInteger.ONE, DlmmUtils.getPriceFromId(DlmmUtils.MIN_BIN_ID, 1));
  }

  @Test
  void getPriceFromIdTreatsBinStepAsU16() {
    // 0x1000A & 0xFFFF == 10.
    assertEquals(DlmmUtils.getPriceFromId(1, 10), DlmmUtils.getPriceFromId(1, 0x1000A));
    // 0x10000 & 0xFFFF == 0 -> base is exactly 1.0.
    assertEquals(ONE_Q64.add(BigInteger.ONE), DlmmUtils.getPriceFromId(1, 0x10000));
  }

  @Test
  void getPriceFromIdLbPairOverload() {
    final var lbPair = lbPair(staticParameters(10_000, 40_000, 500, 0), 0, 10, new long[16]);
    assertEquals(new BigInteger("18465190817783261167"), DlmmUtils.getPriceFromId(lbPair, 1));
    assertEquals(new BigInteger("4215778900974969"), DlmmUtils.getPriceFromId(lbPair, -8388));
  }

  @Test
  void getPriceFromIdMatchesBinPriceOracle() {
    // The already-tested BigDecimal binPrice path serves as an independent oracle. The fixed-point
    // path floors the Q64.64 base and truncates each multiply; each of the <= ~25 truncations loses
    // under one unit of the smallest intermediate (>= ~2^49 units for these cases), bounding the
    // relative error well below 1e-13.
    final var tolerance = new BigDecimal("1e-13");
    final var q64 = new BigDecimal(ONE_Q64);
    final int[][] cases = {{10, 8388}, {10, -8388}, {25, 100}, {4, 17221}, {80, -500}, {1, 100_000}};
    for (final int[] binStepAndId : cases) {
      final int binStep = binStepAndId[0];
      final int binId = binStepAndId[1];
      final var exact = DlmmUtils.binStepBase(binStep)
          .pow(binId, new MathContext(50))
          .multiply(q64);
      final var fixedPoint = new BigDecimal(DlmmUtils.getPriceFromId(binId, binStep));
      final var relativeDiff = fixedPoint.subtract(exact).abs().divide(exact, MathContext.DECIMAL64);
      assertTrue(
          relativeDiff.compareTo(tolerance) < 0,
          "binStep=" + binStep + " binId=" + binId + " relativeDiff=" + relativeDiff.toPlainString()
      );
    }
  }

  // ---------------------------------------------------------------------------
  // Bin-array helpers (MAX_BIN_PER_ARRAY = 70)
  // ---------------------------------------------------------------------------

  @Test
  void binIdToArrayIndex() {
    assertEquals(0, DlmmUtils.binIdToArrayIndex(0));
    assertEquals(0, DlmmUtils.binIdToArrayIndex(1));
    assertEquals(0, DlmmUtils.binIdToArrayIndex(69));
    assertEquals(1, DlmmUtils.binIdToArrayIndex(70));
    assertEquals(1, DlmmUtils.binIdToArrayIndex(139));
    assertEquals(2, DlmmUtils.binIdToArrayIndex(140));
    // Negative ids round toward negative infinity, matching Rust div_rem adjustment.
    assertEquals(-1, DlmmUtils.binIdToArrayIndex(-1));
    assertEquals(-1, DlmmUtils.binIdToArrayIndex(-69));
    assertEquals(-1, DlmmUtils.binIdToArrayIndex(-70));
    assertEquals(-2, DlmmUtils.binIdToArrayIndex(-71));
    assertEquals(-2, DlmmUtils.binIdToArrayIndex(-140));
    assertEquals(-3, DlmmUtils.binIdToArrayIndex(-141));
    assertEquals(6337, DlmmUtils.binIdToArrayIndex(DlmmUtils.MAX_BIN_ID));
    assertEquals(-6338, DlmmUtils.binIdToArrayIndex(DlmmUtils.MIN_BIN_ID));
  }

  @Test
  void binIdToArrayUpperIndex() {
    assertEquals(1, DlmmUtils.binIdToArrayUpperIndex(0));
    assertEquals(1, DlmmUtils.binIdToArrayUpperIndex(69));
    assertEquals(2, DlmmUtils.binIdToArrayUpperIndex(70));
    assertEquals(0, DlmmUtils.binIdToArrayUpperIndex(-1));
    assertEquals(0, DlmmUtils.binIdToArrayUpperIndex(-70));
    assertEquals(-1, DlmmUtils.binIdToArrayUpperIndex(-71));
  }

  @Test
  void binArrayLowerUpperBinId() {
    assertArrayEquals(new int[]{0, 69}, DlmmUtils.getBinArrayLowerUpperBinId(0));
    assertArrayEquals(new int[]{70, 139}, DlmmUtils.getBinArrayLowerUpperBinId(1));
    assertArrayEquals(new int[]{-70, -1}, DlmmUtils.getBinArrayLowerUpperBinId(-1));
    assertArrayEquals(new int[]{-140, -71}, DlmmUtils.getBinArrayLowerUpperBinId(-2));
    assertArrayEquals(new int[]{443590, 443659}, DlmmUtils.getBinArrayLowerUpperBinId(6337));
    assertArrayEquals(new int[]{-443660, -443591}, DlmmUtils.getBinArrayLowerUpperBinId(-6338));

    assertEquals(0, DlmmUtils.binArrayLowerBinId(0));
    assertEquals(69, DlmmUtils.binArrayUpperBinId(0));
    assertEquals(-140, DlmmUtils.binArrayLowerBinId(-2));
    assertEquals(-71, DlmmUtils.binArrayUpperBinId(-2));
    assertEquals(443590, DlmmUtils.binArrayLowerBinId(6337));
    assertEquals(443659, DlmmUtils.binArrayUpperBinId(6337));

    // Mirrors the Rust checked arithmetic: overflow must throw rather than wrap.
    assertThrows(ArithmeticException.class, () -> DlmmUtils.binArrayLowerBinId(Integer.MAX_VALUE));
    assertThrows(ArithmeticException.class, () -> DlmmUtils.getBinArrayLowerUpperBinId(Integer.MAX_VALUE / 70));
  }

  @Test
  void bitmapRangeHelpers() {
    // BIN_ARRAY_BITMAP_SIZE = 512: primary bitmap covers [-512, 511].
    assertEquals(-512, DlmmUtils.LOWER_BITMAP_BIN_ARRAY_INDEX);
    assertEquals(511, DlmmUtils.UPPER_BITMAP_BIN_ARRAY_INDEX);

    assertFalse(DlmmUtils.isOverflowDefaultBinArrayBitmap(0));
    assertFalse(DlmmUtils.isOverflowDefaultBinArrayBitmap(511));
    assertTrue(DlmmUtils.isOverflowDefaultBinArrayBitmap(512));
    assertFalse(DlmmUtils.isOverflowDefaultBinArrayBitmap(-512));
    assertTrue(DlmmUtils.isOverflowDefaultBinArrayBitmap(-513));

    assertFalse(DlmmUtils.isOverflowDefaultPairBitmap(511));
    assertTrue(DlmmUtils.isOverflowDefaultPairBitmap(512));
    assertFalse(DlmmUtils.isOverflowDefaultPairBitmap(-512));
    assertTrue(DlmmUtils.isOverflowDefaultPairBitmap(-513));

    assertFalse(DlmmUtils.useExtension(-512, 511));
    assertTrue(DlmmUtils.useExtension(-513, 511));
    assertTrue(DlmmUtils.useExtension(-512, 512));

    assertEquals(0, DlmmUtils.getBinArrayOffsetInPairBitmap(-512));
    assertEquals(512, DlmmUtils.getBinArrayOffsetInPairBitmap(0));
    assertEquals(1023, DlmmUtils.getBinArrayOffsetInPairBitmap(511));
  }

  @Test
  void toBitmap() {
    assertEquals(BigInteger.ZERO, DlmmUtils.toBitmap(new long[16]));

    final long[] limbs = new long[16];
    limbs[0] = 1L;
    assertEquals(BigInteger.ONE, DlmmUtils.toBitmap(limbs));

    // Negative longs are unsigned u64 limbs.
    limbs[0] = -1L;
    assertEquals(BigInteger.ONE.shiftLeft(64).subtract(BigInteger.ONE), DlmmUtils.toBitmap(limbs));

    limbs[0] = 0L;
    limbs[15] = Long.MIN_VALUE;
    assertEquals(BigInteger.ONE.shiftLeft(1023), DlmmUtils.toBitmap(limbs));

    limbs[15] = 0L;
    limbs[1] = 5L;
    assertEquals(BigInteger.valueOf(5).shiftLeft(64), DlmmUtils.toBitmap(limbs));

    limbs[1] = 0L;
    limbs[2] = -2L;
    assertEquals(BigInteger.ONE.shiftLeft(64).subtract(BigInteger.TWO).shiftLeft(128), DlmmUtils.toBitmap(limbs));
  }

  @Test
  void nextBinArrayIndexWithLiquidity() {
    final var parameters = staticParameters(10_000, 40_000, 500, 0);
    final var lbPair = lbPair(parameters, 0, 10, bitmapWithArrayIndexes(-512, -10, 3, 511));

    // Forward (swapForY = false) scans toward higher indexes, inclusive of the start.
    assertArrayEquals(new int[]{3, 1}, DlmmUtils.nextBinArrayIndexWithLiquidity(lbPair, false, 0));
    assertArrayEquals(new int[]{3, 1}, DlmmUtils.nextBinArrayIndexWithLiquidity(lbPair, false, 3));
    assertArrayEquals(new int[]{511, 1}, DlmmUtils.nextBinArrayIndexWithLiquidity(lbPair, false, 4));
    assertArrayEquals(new int[]{511, 1}, DlmmUtils.nextBinArrayIndexWithLiquidity(lbPair, false, 511));
    // The start index is inclusive: -512 itself has liquidity.
    assertArrayEquals(new int[]{-512, 1}, DlmmUtils.nextBinArrayIndexWithLiquidity(lbPair, false, -512));
    assertArrayEquals(new int[]{-10, 1}, DlmmUtils.nextBinArrayIndexWithLiquidity(lbPair, false, -511));

    // Backward (swapForY = true) scans toward lower indexes, inclusive of the start.
    assertArrayEquals(new int[]{-10, 1}, DlmmUtils.nextBinArrayIndexWithLiquidity(lbPair, true, 0));
    assertArrayEquals(new int[]{-10, 1}, DlmmUtils.nextBinArrayIndexWithLiquidity(lbPair, true, -10));
    assertArrayEquals(new int[]{-512, 1}, DlmmUtils.nextBinArrayIndexWithLiquidity(lbPair, true, -11));
    assertArrayEquals(new int[]{-512, 1}, DlmmUtils.nextBinArrayIndexWithLiquidity(lbPair, true, -512));
    assertArrayEquals(new int[]{511, 1}, DlmmUtils.nextBinArrayIndexWithLiquidity(lbPair, true, 511));

    // Falling off either end reports [min - 1 | max + 1, not found].
    final var sparse = lbPair(parameters, 0, 10, bitmapWithArrayIndexes(3));
    assertArrayEquals(new int[]{512, 0}, DlmmUtils.nextBinArrayIndexWithLiquidity(sparse, false, 4));
    assertArrayEquals(new int[]{-513, 0}, DlmmUtils.nextBinArrayIndexWithLiquidity(sparse, true, 2));

    final var empty = lbPair(parameters, 0, 10, new long[16]);
    assertArrayEquals(new int[]{512, 0}, DlmmUtils.nextBinArrayIndexWithLiquidity(empty, false, 0));
    assertArrayEquals(new int[]{-513, 0}, DlmmUtils.nextBinArrayIndexWithLiquidity(empty, true, 0));

    assertThrows(IllegalArgumentException.class, () -> DlmmUtils.nextBinArrayIndexWithLiquidity(lbPair, false, 512));
    assertThrows(IllegalArgumentException.class, () -> DlmmUtils.nextBinArrayIndexWithLiquidity(lbPair, true, -513));
  }

  // ---------------------------------------------------------------------------
  // Fee math (FEE_DENOMINATOR = 1e9, MAX_FEE_RATE = 1e8)
  // ---------------------------------------------------------------------------

  @Test
  void getBaseFee() {
    // base_factor * bin_step * 10 * 10^base_fee_power_factor.
    final var lbPair = lbPair(staticParameters(10_000, 40_000, 500, 0), 10_000, 10, new long[16]);
    assertEquals(BigInteger.valueOf(1_000_000L), DlmmUtils.getBaseFee(lbPair));

    final var powered = lbPair(staticParameters(10_000, 40_000, 500, 2), 10_000, 10, new long[16]);
    assertEquals(BigInteger.valueOf(100_000_000L), DlmmUtils.getBaseFee(powered));

    // u16 maxima: 65535 * 65535 * 10.
    final var maxed = lbPair(staticParameters(65_535, 0, 500, 0), 0, 65_535, new long[16]);
    assertEquals(BigInteger.valueOf(42_948_362_250L), DlmmUtils.getBaseFee(maxed));
  }

  @Test
  void computeVariableFee() {
    final var lbPair = lbPair(staticParameters(10_000, 40_000, 500, 0), 10_000, 10, new long[16]);

    // ceil(40_000 * (10_000 * 10)^2 / 1e11) = ceil(4e14 / 1e11) = 4000 (exact division).
    assertEquals(BigInteger.valueOf(4_000L), DlmmUtils.computeVariableFee(lbPair, 10_000));
    // ceil(40_000 * (3 * 10)^2 / 1e11) = ceil(0.00036) = 1: exercises the ceil rounding.
    assertEquals(BigInteger.ONE, DlmmUtils.computeVariableFee(lbPair, 3));
    assertEquals(BigInteger.ZERO, DlmmUtils.computeVariableFee(lbPair, 0));

    // getVariableFee reads the pair's current volatilityAccumulator (10_000).
    assertEquals(BigInteger.valueOf(4_000L), DlmmUtils.getVariableFee(lbPair));

    final var noVariableFee = lbPair(staticParameters(10_000, 0, 500, 0), 10_000, 10, new long[16]);
    assertEquals(BigInteger.ZERO, DlmmUtils.computeVariableFee(noVariableFee, 10_000));
    assertEquals(BigInteger.ZERO, DlmmUtils.getVariableFee(noVariableFee));
  }

  @Test
  void getTotalFee() {
    // 1_000_000 base + 4_000 variable.
    final var lbPair = lbPair(staticParameters(10_000, 40_000, 500, 0), 10_000, 10, new long[16]);
    assertEquals(BigInteger.valueOf(1_004_000L), DlmmUtils.getTotalFee(lbPair));

    // Base fee alone exceeds MAX_FEE_RATE -> capped at 1e8.
    final var maxed = lbPair(staticParameters(65_535, 0, 500, 0), 0, 65_535, new long[16]);
    assertEquals(BigInteger.valueOf(100_000_000L), DlmmUtils.getTotalFee(maxed));
  }

  @Test
  void computeFee() {
    // total fee rate = 1_004_000; fee = ceil(amount * 1_004_000 / (1e9 - 1_004_000)).
    final var lbPair = lbPair(staticParameters(10_000, 40_000, 500, 0), 10_000, 10, new long[16]);
    assertEquals(0L, DlmmUtils.computeFee(lbPair, 0L));
    // ceil(12_345 * 1_004_000 / 998_996_000) = ceil(12.406...) = 13.
    assertEquals(13L, DlmmUtils.computeFee(lbPair, 12_345L));
    // ceil(1e9 * 1_004_000 / 998_996_000) = 1_005_010.
    assertEquals(1_005_010L, DlmmUtils.computeFee(lbPair, 1_000_000_000L));
    // -1L is u64::MAX: ceil((2^64 - 1) * 1_004_000 / 998_996_000).
    assertEquals(18_539_144_350_932_727L, DlmmUtils.computeFee(lbPair, -1L));
  }

  @Test
  void computeFeeFromAmount() {
    // fee = ceil(amountWithFees * 1_004_000 / 1e9).
    final var lbPair = lbPair(staticParameters(10_000, 40_000, 500, 0), 10_000, 10, new long[16]);
    assertEquals(0L, DlmmUtils.computeFeeFromAmount(lbPair, 0L));
    // ceil(12_345 * 1_004_000 / 1e9) = ceil(12.394...) = 13.
    assertEquals(13L, DlmmUtils.computeFeeFromAmount(lbPair, 12_345L));
    // 1e9 * 1_004_000 / 1e9 = 1_004_000 exactly.
    assertEquals(1_004_000L, DlmmUtils.computeFeeFromAmount(lbPair, 1_000_000_000L));
  }

  /// `amountWithFees` is a u64 carried in a signed `long`, so it is masked with
  /// `2^64 - 1` before the arithmetic. For any value below 2^63 the mask is a
  /// no-op — it only earns its keep when the high bit is set, i.e. when the
  /// `long` is negative and denotes a u64 above 2^63. Without such a case the
  /// mask is indistinguishable from not masking at all.
  @Test
  void computeFeeFromAmountMasksTheHighU64Range() {
    final var lbPair = lbPair(staticParameters(10_000, 40_000, 500, 0), 10_000, 10, new long[16]);

    // -1L is u64 2^64 - 1: fee = ceil((2^64 - 1) * 1_004_000 / 1e9)
    final var maxU64 = new java.math.BigInteger("18446744073709551615");
    final var expected = maxU64
        .multiply(java.math.BigInteger.valueOf(1_004_000L))
        .add(java.math.BigInteger.valueOf(999_999_999L))
        .divide(java.math.BigInteger.valueOf(1_000_000_000L));
    assertEquals(expected.longValueExact(), DlmmUtils.computeFeeFromAmount(lbPair, -1L));

    // a u64 above 2^63 must yield a strictly larger fee than the largest
    // positive long — not a negative or wrapped one. (Adjacent u64 values are
    // no good here: the rate is ~0.001, so a difference of one rounds away.)
    final long atMaxLong = DlmmUtils.computeFeeFromAmount(lbPair, Long.MAX_VALUE);
    final long atMaxU64 = DlmmUtils.computeFeeFromAmount(lbPair, -1L);
    assertTrue(atMaxLong > 0L);
    assertTrue(atMaxU64 > atMaxLong,
        "u64 2^64-1 must charge more than u64 2^63-1");
    // ~2x, since 2^64-1 is about twice 2^63-1
    assertEquals(2L, Math.round((double) atMaxU64 / atMaxLong));
  }

  @Test
  void computeProtocolFee() {
    // protocol fee = floor(feeAmount * protocol_share / 10_000).
    final var lbPair = lbPair(staticParameters(10_000, 40_000, 500, 0), 10_000, 10, new long[16]);
    assertEquals(0L, DlmmUtils.computeProtocolFee(lbPair, 0L));
    // floor(12_345 * 500 / 10_000) = floor(617.25) = 617.
    assertEquals(617L, DlmmUtils.computeProtocolFee(lbPair, 12_345L));

    final var noShare = lbPair(staticParameters(10_000, 40_000, 0, 0), 10_000, 10, new long[16]);
    assertEquals(0L, DlmmUtils.computeProtocolFee(noShare, Long.MAX_VALUE));

    // (2^64 - 1) * 5_000 / 10_000 = 2^63 - 1 = Long.MAX_VALUE, the largest representable result.
    final var halfShare = lbPair(staticParameters(10_000, 40_000, 5_000, 0), 10_000, 10, new long[16]);
    assertEquals(Long.MAX_VALUE, DlmmUtils.computeProtocolFee(halfShare, -1L));

    // (2^64 - 1) * 10_000 / 10_000 = 2^64 - 1 overflows a signed long.
    //
    // The message matters: without the explicit bit-length guard the value still
    // fails, but inside `longValueExact()` and with BigInteger's own wording. A
    // bare `assertThrows(ArithmeticException.class, ..)` passes either way, so it
    // cannot tell the guard from its absence.
    final var fullShare = lbPair(staticParameters(10_000, 40_000, 10_000, 0), 10_000, 10, new long[16]);
    final var overflow = assertThrows(ArithmeticException.class,
        () -> DlmmUtils.computeProtocolFee(fullShare, -1L));
    assertEquals("Protocol fee overflows u64", overflow.getMessage());
  }

  /// The `scaleFactor` overloads scale the price *up* before converting. A
  /// divide here would still return a plausible bin id, so the test pins the
  /// relationship to the unscaled overload and checks the direction.
  @Test
  void binIdScaleFactorOverloadsMultiply() {
    final double price = 1.25d;
    final double scaleFactor = 1_000d;
    final double binStepBase = 1.0001d;
    final double logBinStepBase = Math.log(binStepBase);

    final double scaled = DlmmUtils.binId(price, scaleFactor, binStepBase);
    assertEquals(DlmmUtils.binId(price * scaleFactor, binStepBase), scaled);
    assertNotEquals(DlmmUtils.binId(price / scaleFactor, binStepBase), scaled,
        "the price is scaled up, not down");
    assertNotEquals(0d, scaled);
    assertTrue(Double.isFinite(scaled));

    final double scaledLog = DlmmUtils.binIdFromLogBinStepBase(price, scaleFactor, logBinStepBase);
    assertEquals(DlmmUtils.binIdFromLogBinStepBase(price * scaleFactor, logBinStepBase), scaledLog);
    assertNotEquals(DlmmUtils.binIdFromLogBinStepBase(price / scaleFactor, logBinStepBase), scaledLog);
    assertNotEquals(0d, scaledLog);

    // the two spellings agree, which is the point of keeping both
    assertEquals(scaled, scaledLog, 1e-6d);

    // a unit scale factor is the identity
    assertEquals(DlmmUtils.binId(price, binStepBase), DlmmUtils.binId(price, 1d, binStepBase));
  }
}

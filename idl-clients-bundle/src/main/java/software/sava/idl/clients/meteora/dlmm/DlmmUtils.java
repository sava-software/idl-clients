package software.sava.idl.clients.meteora.dlmm;


import software.sava.idl.clients.meteora.dlmm.gen.types.LbPair;
import software.sava.idl.clients.meteora.dlmm.gen.types.RemainingAccountsInfo;
import software.sava.idl.clients.meteora.dlmm.gen.types.RemainingAccountsSlice;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

import static software.sava.idl.clients.meteora.dlmm.gen.LbClmmConstants.*;


public final class DlmmUtils {

  /// Minimum bin id supported by the program (computed for 1 bps bin step).
  /// Mirrors `commons::constants::MIN_BIN_ID`.
  public static final int MIN_BIN_ID = -443636;

  /// Maximum bin id supported by the program (computed for 1 bps bin step).
  /// Mirrors `commons::constants::MAX_BIN_ID`.
  public static final int MAX_BIN_ID = 443636;

  /// Q64.64 fixed-point: number of fractional bits.
  public static final int Q64X64_SCALE_OFFSET = 64;

  /// Q64.64 representation of `1.0`.
  public static final BigInteger Q64X64_ONE = BigInteger.ONE.shiftLeft(Q64X64_SCALE_OFFSET);

  /// `1 << 128 - 1`, mask for the 128-bit truncation applied by u128 arithmetic in Rust.
  public static final BigInteger U128_MASK = BigInteger.ONE.shiftLeft(128).subtract(BigInteger.ONE);

  /// Maximum exponential supported by [#pow(BigInteger, int)] (matches Rust `MAX_EXPONENTIAL`).
  private static final int Q64X64_MAX_EXPONENTIAL = 0x80000;

  /// `u128::MAX`.
  private static final BigInteger U128_MAX = U128_MASK;

  /// `1 << 1024 - 1`, mask for the 1024-bit `LbPair.binArrayBitmap`.
  private static final BigInteger U1024_MASK = BigInteger.ONE.shiftLeft(1024).subtract(BigInteger.ONE);

  public static final RemainingAccountsInfo NO_REMAINING_ACCOUNTS = new RemainingAccountsInfo(new RemainingAccountsSlice[0]);

  private static final int BASIS_POINT_MAX_DECIMALS = 4;
  public static final int BASIS_POINT_MAX = 10_000;
  public static final int LOWER_BITMAP_BIN_ARRAY_INDEX = -BIN_ARRAY_BITMAP_SIZE;
  public static final int UPPER_BITMAP_BIN_ARRAY_INDEX = BIN_ARRAY_BITMAP_SIZE - 1;

  public static boolean isOverflowDefaultBinArrayBitmap(final int binArrayIndex) {
    return binArrayIndex > UPPER_BITMAP_BIN_ARRAY_INDEX || binArrayIndex < LOWER_BITMAP_BIN_ARRAY_INDEX;
  }

  public static boolean useExtension(final int minBinArrayIndex, final int maxBinArrayIndex) {
    return isOverflowDefaultBinArrayBitmap(minBinArrayIndex) || isOverflowDefaultBinArrayBitmap(maxBinArrayIndex);
  }

  public static int binIdToArrayIndex(final int binId) {
    final int idx = binId / (int) MAX_BIN_PER_ARRAY;
    return binId < 0 && (binId % MAX_BIN_PER_ARRAY) != 0 ? idx - 1 : idx;
  }

  public static int binIdToArrayUpperIndex(final int binId) {
    return binIdToArrayIndex(binId + (int) MAX_BIN_PER_ARRAY);
  }

  public static int scaleDifference(final int xTokenDecimals, final int yTokenDecimals) {
    return yTokenDecimals - xTokenDecimals;
  }

  public static BigDecimal binStepBase(final int binStep) {
    final var binStepNum = BigDecimal.valueOf(binStep).movePointLeft(BASIS_POINT_MAX_DECIMALS);
    return BigDecimal.ONE.add(binStepNum);
  }

  public static double inverseLogBinStepBase(final int binStep) {
    return 1 / StrictMath.log1p(binStep * 0.0001d);
  }

  public static double priceScaleFactor(final int scaleDifference) {
    return StrictMath.pow(10, scaleDifference);
  }

  public static double powTen(final double val, final int scaleDifference) {
    return val * priceScaleFactor(scaleDifference);
  }

  public static double binStepBaseDouble(final int binStep) {
    return Math.fma(binStep, 0.0001d, 1);
  }

  public static BigDecimal binStepBase(final LbPair lbPair) {
    return binStepBase(lbPair.binStep());
  }

  /// binStepBase = 1 + (stepSize * 0.0001)
  /// price = binStepBase ^ binId
  /// binId = ln(price) / ln(binStepBase)
  public static BigDecimal binPrice(final BigDecimal binStepBase,
                                    final int binId,
                                    final int scaleDifference,
                                    final MathContext mathContext) {
    return binStepBase.pow(binId, mathContext).movePointLeft(scaleDifference);
  }

  public static BigDecimal binPrice(final int binStep,
                                    final int binId,
                                    final int scaleDifference,
                                    final MathContext mathContext) {
    final var binStepBase = DlmmUtils.binStepBase(binStep);
    return binPrice(binStepBase, binId, scaleDifference, mathContext);
  }

  public static double binId(final BigDecimal price, final int scaleDifference, final BigDecimal binStepBase) {
    final var adjustedPrice = price.movePointRight(scaleDifference);
    return binId(adjustedPrice.doubleValue(), binStepBase.doubleValue());
  }

  public static double unscaledBinPrice(final double binStepBase, final int binId) {
    return StrictMath.pow(binStepBase, binId);
  }

  public static double binPrice(final double binStepBase, final int binId, final int scaleDifference) {
    return powTen(unscaledBinPrice(binStepBase, binId), -scaleDifference);
  }

  public static double binPrice(final double binStepBase, final int binId, final double scaleFactor) {
    return unscaledBinPrice(binStepBase, binId) * scaleFactor;
  }

  public static double binIdFromLogBinStepBase(final double unscaledPrice, final double logBinStepBase) {
    return StrictMath.log(unscaledPrice) / logBinStepBase;
  }

  public static double binId(final double unscaledPrice, final double binStepBase) {
    return binIdFromLogBinStepBase(unscaledPrice, StrictMath.log(binStepBase));
  }

  public static double binIdFromLogBinStepBase(final double price,
                                               final int scaleDifference,
                                               final double logBinStepBase) {
    return binIdFromLogBinStepBase(powTen(price, scaleDifference), logBinStepBase);
  }

  public static double binId(final double price, final int scaleDifference, final double binStepBase) {
    return binId(powTen(price, scaleDifference), binStepBase);
  }

  public static double binIdFromInverseLogBinStepBase(final double unscaledPrice,
                                                      final double inverseLogBinStepBase) {
    return StrictMath.log(unscaledPrice) * inverseLogBinStepBase;
  }

  public static double binIdFromInverseLogBinStepBase(final double price,
                                                      final int scaleDifference,
                                                      final double inverseLogBinStepBase) {
    return binIdFromInverseLogBinStepBase(powTen(price, scaleDifference), inverseLogBinStepBase);
  }

  public static double binIdFromLogBinStepBase(final double price,
                                               final double scaleFactor,
                                               final double logBinStepBase) {
    return binIdFromLogBinStepBase(price * scaleFactor, logBinStepBase);
  }

  public static double binId(final double price, final double scaleFactor, final double binStepBase) {
    return binId(price * scaleFactor, binStepBase);
  }

  public static double binIdFromInverseLogBinStepBase(final double price,
                                                      final double scaleFactor,
                                                      final double inverseLogBinStepBase) {
    return binIdFromInverseLogBinStepBase(price * scaleFactor, inverseLogBinStepBase);
  }

  // ---------------------------------------------------------------------------
  // Q64.64 fixed-point power (mirrors commons::math::u64x64_math::pow)
  // ---------------------------------------------------------------------------

  /// Q64.64 power: returns `base^exp` in Q64.64 fixed-point, or `null` on overflow.
  /// Mirrors `u64x64_math::pow` from the dlmm SDK byte-exactly (same 19-bit binary expansion,
  /// same `1 / base` invert trick when `base >= 1` to avoid 256-bit overflow).
  ///
  /// @param base Q64.64 value, treated as a u128.
  /// @param exp  signed exponent (negative is computed as `1 / base^|exp|`).
  /// @return Q64.64 result, or `null` on overflow / when `|exp| >= 0x80000`.
  public static BigInteger pow(final BigInteger base, final int exp) {
    if (base == null) {
      throw new IllegalArgumentException("base must not be null");
    }
    if (exp == 0) {
      return Q64X64_ONE;
    }
    boolean invert = exp < 0;
    final long absExp = exp == Integer.MIN_VALUE ? (1L << 31) : Math.abs((long) exp);
    if (absExp >= Q64X64_MAX_EXPONENTIAL) {
      return null;
    }
    int e = (int) absExp;
    BigInteger squaredBase = base.and(U128_MASK);
    BigInteger result = Q64X64_ONE;

    // Inverse the base when base >= 1 to keep upper bits zero (avoids 256-bit overflow).
    if (squaredBase.compareTo(result) >= 0) {
      if (squaredBase.signum() == 0) {
        return null;
      }
      squaredBase = U128_MAX.divide(squaredBase);
      invert = !invert;
    }

    // 19 binary-expansion steps (matches Rust loop unrolled in u64x64_math::pow).
    for (int bit = 0; bit < 19; bit++) {
      if (bit > 0) {
        squaredBase = squaredBase.multiply(squaredBase).shiftRight(Q64X64_SCALE_OFFSET).and(U128_MASK);
      }
      if ((e & (1 << bit)) != 0) {
        result = result.multiply(squaredBase).shiftRight(Q64X64_SCALE_OFFSET).and(U128_MASK);
        if (result.bitLength() > 128) {
          return null;
        }
      }
    }

    if (result.signum() == 0) {
      return null;
    }
    if (invert) {
      result = U128_MAX.divide(result);
    }
    return result;
  }

  /// Q64.64 price for a bin id at the given bin step (mirrors `commons::math::price_math::get_price_from_id`).
  /// Returns `null` on overflow.
  ///
  /// @param activeId bin id (signed).
  /// @param binStep  bin step in basis points (u16).
  public static BigInteger getPriceFromId(final int activeId, final int binStep) {
    final var bps = BigInteger.valueOf(binStep & 0xFFFFL).shiftLeft(Q64X64_SCALE_OFFSET)
        .divide(BigInteger.valueOf(BASIS_POINT_MAX));
    final var base = Q64X64_ONE.add(bps);
    return pow(base, activeId);
  }

  /// Q64.64 price for a bin id on the given pair.
  public static BigInteger getPriceFromId(final LbPair lbPair, final int activeId) {
    return getPriceFromId(activeId, lbPair.binStep());
  }

  // ---------------------------------------------------------------------------
  // Bin-array range helpers
  // ---------------------------------------------------------------------------

  /// Inclusive `(lowerBinId, upperBinId)` covered by a bin-array index.
  /// Mirrors `BinArrayExtension::get_bin_array_lower_upper_bin_id`.
  public static int[] getBinArrayLowerUpperBinId(final int binArrayIndex) {
    final int lower = Math.multiplyExact(binArrayIndex, (int) MAX_BIN_PER_ARRAY);
    final int upper = Math.addExact(lower, (int) MAX_BIN_PER_ARRAY) - 1;
    return new int[]{lower, upper};
  }

  /// Inclusive lower bin id covered by a bin-array index.
  public static int binArrayLowerBinId(final int binArrayIndex) {
    return Math.multiplyExact(binArrayIndex, (int) MAX_BIN_PER_ARRAY);
  }

  /// Inclusive upper bin id covered by a bin-array index.
  public static int binArrayUpperBinId(final int binArrayIndex) {
    return Math.addExact(binArrayLowerBinId(binArrayIndex), (int) MAX_BIN_PER_ARRAY) - 1;
  }

  // ---------------------------------------------------------------------------
  // Primary bin-array bitmap (LbPair.binArrayBitmap, 1024 bits)
  // ---------------------------------------------------------------------------

  /// Whether the bin array index falls outside the pair's primary 1024-bit bitmap and requires
  /// the `BinArrayBitmapExtension` PDA. Mirrors `LbPairExtension::is_overflow_default_bin_array_bitmap`.
  public static boolean isOverflowDefaultPairBitmap(final int binArrayIndex) {
    return binArrayIndex > BIN_ARRAY_BITMAP_SIZE - 1 || binArrayIndex < -BIN_ARRAY_BITMAP_SIZE;
  }

  /// Offset within the primary 1024-bit bitmap for a bin-array index (0..1023).
  /// Mirrors `LbPairExtension::get_bin_array_offset`.
  public static int getBinArrayOffsetInPairBitmap(final int binArrayIndex) {
    return binArrayIndex + BIN_ARRAY_BITMAP_SIZE;
  }

  /// Decode `LbPair.binArrayBitmap` (16 little-endian u64 limbs) into a single 1024-bit BigInteger
  /// where bit `i` corresponds to bin-array offset `i - BIN_ARRAY_BITMAP_SIZE`.
  public static BigInteger toBitmap(final long[] limbs) {
    BigInteger acc = BigInteger.ZERO;
    for (int i = 0; i < limbs.length; i++) {
      // Treat each limb as unsigned u64.
      final var unsigned = limbs[i] >= 0 ? BigInteger.valueOf(limbs[i]) : BigInteger.valueOf(limbs[i]).add(BigInteger.ONE.shiftLeft(64));
      acc = acc.or(unsigned.shiftLeft(i * 64));
    }
    return acc;
  }

  /// Walk the pair's primary bitmap forward (`swapForY = false`, increasing bin ids) or backward
  /// (`swapForY = true`, decreasing) starting from `startArrayIndex` and return the next
  /// bin-array index with liquidity. Mirrors `LbPairExtension::next_bin_array_index_with_liquidity_internal`.
  ///
  /// The returned array is `[binArrayIndex, found]` where `found == 1` indicates a liquidity bit
  /// was found within the primary bitmap and `found == 0` indicates the walk fell off the bitmap
  /// (the returned `binArrayIndex` is then `min - 1` or `max + 1`, matching the Rust impl — the
  /// caller should consult the `BinArrayBitmapExtension` PDA from there).
  ///
  /// @param lbPair          source of the 1024-bit bitmap.
  /// @param swapForY        direction; true scans toward lower bin ids.
  /// @param startArrayIndex bin-array index to start the search from (inclusive).
  /// @return `[binArrayIndex, found ? 1 : 0]`.
  /// @throws IllegalArgumentException if `startArrayIndex` falls outside the primary bitmap range.
  public static int[] nextBinArrayIndexWithLiquidity(final LbPair lbPair,
                                                     final boolean swapForY,
                                                     final int startArrayIndex) {
    if (isOverflowDefaultPairBitmap(startArrayIndex)) {
      throw new IllegalArgumentException("startArrayIndex " + startArrayIndex + " is outside the primary bitmap range");
    }
    final var bitmap = toBitmap(lbPair.binArrayBitmap());
    final int arrayOffset = getBinArrayOffsetInPairBitmap(startArrayIndex);
    final int minBitmapId = -BIN_ARRAY_BITMAP_SIZE;
    final int maxBitmapId = BIN_ARRAY_BITMAP_SIZE - 1;
    final int bitmapRange = maxBitmapId - minBitmapId; // 1023

    if (swapForY) {
      // Shift left by (1023 - arrayOffset); leading zeros count = bits to walk back.
      final int shift = bitmapRange - arrayOffset;
      final var shifted = bitmap.shiftLeft(shift).and(U1024_MASK);
      if (shifted.signum() == 0) {
        return new int[]{minBitmapId - 1, 0};
      }
      // Leading zeros in a 1024-bit window.
      final int leadingZeros = 1024 - shifted.bitLength();
      return new int[]{startArrayIndex - leadingZeros, 1};
    } else {
      final var shifted = bitmap.shiftRight(arrayOffset);
      if (shifted.signum() == 0) {
        return new int[]{maxBitmapId + 1, 0};
      }
      final int trailingZeros = shifted.getLowestSetBit();
      return new int[]{startArrayIndex + trailingZeros, 1};
    }
  }

  // ---------------------------------------------------------------------------
  // Fee math (mirrors LbPairExtension::{get_base_fee,get_variable_fee,get_total_fee,compute_fee,compute_protocol_fee,compute_fee_from_amount})
  // ---------------------------------------------------------------------------

  /// Static (base) fee rate in u128 units (scaled by `FEE_DENOMINATOR = 10^9`).
  /// `base_factor * bin_step * 10 * 10^base_fee_power_factor`.
  public static BigInteger getBaseFee(final LbPair lbPair) {
    final var p = lbPair.parameters();
    final var base = BigInteger.valueOf(p.baseFactor() & 0xFFFFL)
        .multiply(BigInteger.valueOf(lbPair.binStep() & 0xFFFFL))
        .multiply(BigInteger.TEN);
    final var pow = BigInteger.TEN.pow(p.baseFeePowerFactor());
    return base.multiply(pow);
  }

  /// Variable fee component for the given volatility accumulator.
  public static BigInteger computeVariableFee(final LbPair lbPair, final long volatilityAccumulator) {
    final var p = lbPair.parameters();
    if (p.variableFeeControl() <= 0) {
      return BigInteger.ZERO;
    }
    final var va = BigInteger.valueOf(volatilityAccumulator & 0xFFFFFFFFL);
    final var binStep = BigInteger.valueOf(lbPair.binStep() & 0xFFFFL);
    final var control = BigInteger.valueOf(p.variableFeeControl() & 0xFFFFFFFFL);

    final var sqVfaBin = va.multiply(binStep).pow(2);
    final var vFee = control.multiply(sqVfaBin);
    // Ceil-div by 100_000_000_000.
    final var denom = BigInteger.valueOf(100_000_000_000L);
    return vFee.add(denom.subtract(BigInteger.ONE)).divide(denom);
  }

  /// Variable fee component using the pair's current `volatilityAccumulator` (does not advance it).
  public static BigInteger getVariableFee(final LbPair lbPair) {
    return computeVariableFee(lbPair, lbPair.vParameters().volatilityAccumulator());
  }

  /// Total fee rate (base + variable), capped at `MAX_FEE_RATE`. Scaled by `FEE_DENOMINATOR`.
  public static BigInteger getTotalFee(final LbPair lbPair) {
    final var total = getBaseFee(lbPair).add(getVariableFee(lbPair));
    final var cap = BigInteger.valueOf(MAX_FEE_RATE);
    return total.min(cap);
  }

  /// Compute the fee charged on `amount` (token units, u64). Uses ceil-div, matching
  /// `LbPairExtension::compute_fee` so the on-chain and off-chain quotes agree.
  ///
  /// @return fee in token units; throws on u64 overflow.
  public static long computeFee(final LbPair lbPair, final long amount) {
    final var totalFeeRate = getTotalFee(lbPair);
    final var denominator = BigInteger.valueOf(FEE_DENOMINATOR).subtract(totalFeeRate);
    if (denominator.signum() <= 0) {
      throw new ArithmeticException("Fee denominator underflow");
    }
    final var amt = BigInteger.valueOf(amount).and(BigInteger.ONE.shiftLeft(64).subtract(BigInteger.ONE));
    final var num = amt.multiply(totalFeeRate).add(denominator).subtract(BigInteger.ONE);
    final var fee = num.divide(denominator);
    if (fee.bitLength() > 63) {
      throw new ArithmeticException("Fee overflows u64");
    }
    return fee.longValueExact();
  }

  /// Compute the fee component already baked into `amountWithFees` (mirrors
  /// `LbPairExtension::compute_fee_from_amount`).
  public static long computeFeeFromAmount(final LbPair lbPair, final long amountWithFees) {
    final var totalFeeRate = getTotalFee(lbPair);
    final var amt = BigInteger.valueOf(amountWithFees).and(BigInteger.ONE.shiftLeft(64).subtract(BigInteger.ONE));
    final var num = amt.multiply(totalFeeRate).add(BigInteger.valueOf(FEE_DENOMINATOR - 1));
    final var fee = num.divide(BigInteger.valueOf(FEE_DENOMINATOR));
    if (fee.bitLength() > 63) {
      throw new ArithmeticException("Fee overflows u64");
    }
    return fee.longValueExact();
  }

  /// Protocol-share split of the fee (`protocol_share * fee / BASIS_POINT_MAX`).
  public static long computeProtocolFee(final LbPair lbPair, final long feeAmount) {
    final var amt = BigInteger.valueOf(feeAmount).and(BigInteger.ONE.shiftLeft(64).subtract(BigInteger.ONE));
    final var share = BigInteger.valueOf(lbPair.parameters().protocolShare() & 0xFFFFL);
    final var pf = amt.multiply(share).divide(BigInteger.valueOf(BASIS_POINT_MAX));
    if (pf.bitLength() > 63) {
      throw new ArithmeticException("Protocol fee overflows u64");
    }
    return pf.longValueExact();
  }

  private DlmmUtils() {
  }
}

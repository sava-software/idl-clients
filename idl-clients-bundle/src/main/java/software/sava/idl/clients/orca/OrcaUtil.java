package software.sava.idl.clients.orca;

import software.sava.core.accounts.ProgramDerivedAddress;
import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.core.gen.SerDeUtil;
import software.sava.idl.clients.orca.whirlpools.gen.WhirlpoolPDAs;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.OptionalInt;

/// Whirlpool domain-specific calculations mirrored from the upstream Rust
/// program (`programs/whirlpool/src/state/{tick,tick_array,position_bundle}.rs`).
///
/// All helpers are pure functions on the canonical on-chain constants and are
/// intended for callers wiring `OrcaWhirlpoolsClient` instructions that take a
/// `tickLowerIndex` / `tickUpperIndex` / `startTickIndex` / `bundleIndex`, or
/// that need the `tick_array` / `bundled_position` PDAs derived by
/// [#tickArrayPDA] / [#bundledPositionPDA].
public final class OrcaUtil {

  /// Maximum supported tick index (inclusive). Source:
  /// `state::tick::MAX_TICK_INDEX`.
  public static final int MAX_TICK_INDEX = 443_636;

  /// Minimum supported tick index (inclusive). Source:
  /// `state::tick::MIN_TICK_INDEX`.
  public static final int MIN_TICK_INDEX = -443_636;

  /// Number of ticks in a single tick array. Source:
  /// `state::tick_array::TICK_ARRAY_SIZE`.
  public static final int TICK_ARRAY_SIZE = 88;

  /// Maximum sqrt-price (Q64.64). Source: `math::tick_math::MAX_SQRT_PRICE_X64`.
  public static final java.math.BigInteger MAX_SQRT_PRICE_X64 =
      new java.math.BigInteger("79226673515401279992447579055");

  /// Minimum sqrt-price (Q64.64). Source: `math::tick_math::MIN_SQRT_PRICE_X64`.
  public static final java.math.BigInteger MIN_SQRT_PRICE_X64 =
      java.math.BigInteger.valueOf(4_295_048_016L);

  /// Tick-spacing threshold at/above which only full-range positions are
  /// allowed. Source: `math::tick_math::FULL_RANGE_ONLY_TICK_SPACING_THRESHOLD`.
  public static final int FULL_RANGE_ONLY_TICK_SPACING_THRESHOLD = 32_768;

  /// Number of positions a single `PositionBundle` can hold. Source:
  /// `state::position_bundle::POSITION_BUNDLE_SIZE`.
  public static final int POSITION_BUNDLE_SIZE = 256;

  private OrcaUtil() {
  }

  /// Returns true when `tickIndex` is within the protocol's supported range
  /// `[MIN_TICK_INDEX, MAX_TICK_INDEX]`.
  public static boolean isTickIndexInBounds(final int tickIndex) {
    return tickIndex >= MIN_TICK_INDEX && tickIndex <= MAX_TICK_INDEX;
  }

  /// Clamps `tickIndex` to `[MIN_TICK_INDEX, MAX_TICK_INDEX]`.
  public static int boundTickIndex(final int tickIndex) {
    if (tickIndex < MIN_TICK_INDEX) {
      return MIN_TICK_INDEX;
    }
    return Math.min(tickIndex, MAX_TICK_INDEX);
  }

  /// Returns true when `tickIndex` is in-bounds and a multiple of
  /// `tickSpacing` (i.e. a tick the pool can actually use).
  public static boolean isUsableTick(final int tickIndex, final int tickSpacing) {
    if (tickSpacing <= 0) {
      throw new IllegalArgumentException("tickSpacing must be positive");
    }
    if (!isTickIndexInBounds(tickIndex)) {
      return false;
    }
    return tickIndex % tickSpacing == 0;
  }

  /// Full-range `(lower, upper)` tick indexes for a pool of the given
  /// `tickSpacing`, snapped inwards to the nearest usable ticks. Mirrors
  /// `Tick::full_range_indexes`.
  ///
  /// Returns a 2-element array `[lower, upper]`.
  public static int[] fullRangeTickIndexes(final int tickSpacing) {
    if (tickSpacing <= 0) {
      throw new IllegalArgumentException("tickSpacing must be positive");
    }
    final int lower = (MIN_TICK_INDEX / tickSpacing) * tickSpacing;
    final int upper = (MAX_TICK_INDEX / tickSpacing) * tickSpacing;
    return new int[]{lower, upper};
  }

  /// Number of ticks covered by a single tick array at the given
  /// `tickSpacing` (`TICK_ARRAY_SIZE * tickSpacing`).
  public static int ticksPerArray(final int tickSpacing) {
    if (tickSpacing <= 0) {
      throw new IllegalArgumentException("tickSpacing must be positive");
    }
    return TICK_ARRAY_SIZE * tickSpacing;
  }

  /// Returns the `start_tick_index` of the tick array that contains
  /// `tickIndex` for a pool of the given `tickSpacing`. The result is always
  /// a multiple of `TICK_ARRAY_SIZE * tickSpacing` and is the value passed to
  /// `initializeTickArray` / `initializeDynamicTickArray` and used as the
  /// `start_tick_index` seed in [#tickArrayPDA(PublicKey, PublicKey, int)].
  ///
  /// Uses floor-division semantics so negative tick indexes map to the array
  /// whose start tick is `<= tickIndex`.
  public static int startTickIndex(final int tickIndex, final int tickSpacing) {
    final int ticksInArray = ticksPerArray(tickSpacing);
    // Floor-division towards negative infinity.
    int q = tickIndex / ticksInArray;
    if ((tickIndex % ticksInArray != 0) && ((tickIndex ^ ticksInArray) < 0)) {
      q -= 1;
    }
    return q * ticksInArray;
  }

  /// Variant of [#startTickIndex(int, int)] that offsets the result by
  /// `offset` whole tick-arrays (e.g. `offset = -1` for the previous array,
  /// `+1` for the next). Useful for pre-initializing the neighbouring tick
  /// arrays required by swaps / liquidity changes.
  public static int startTickIndex(final int tickIndex, final int tickSpacing, final int offset) {
    return startTickIndex(tickIndex, tickSpacing) + offset * ticksPerArray(tickSpacing);
  }

  /// Returns true when `startTickIndex` is a valid `start_tick_index` for a
  /// tick array — either a multiple of `TICK_ARRAY_SIZE * tickSpacing` within
  /// the supported range, or the special left-edge array that extends below
  /// `MIN_TICK_INDEX`. Mirrors `Tick::check_is_valid_start_tick`.
  public static boolean isValidStartTickIndex(final int startTickIndex, final int tickSpacing) {
    final int ticksInArray = ticksPerArray(tickSpacing);
    if (!isTickIndexInBounds(startTickIndex)) {
      if (startTickIndex > MIN_TICK_INDEX) {
        return false;
      }
      // Rust's `%` truncates toward zero, so the remainder of the negative
      // MIN_TICK_INDEX is negative — floorMod lands one array too far left
      final int minArrayStart =
          MIN_TICK_INDEX - (MIN_TICK_INDEX % ticksInArray + ticksInArray);
      return startTickIndex == minArrayStart;
    }
    return Math.floorMod(startTickIndex, ticksInArray) == 0;
  }

  /// Returns true when `bundleIndex` is a valid index into a
  /// `PositionBundle` (`0 <= bundleIndex < POSITION_BUNDLE_SIZE`). Mirrors
  /// `PositionBundle::is_valid_bundle_index`.
  public static boolean isValidBundleIndex(final int bundleIndex) {
    return bundleIndex >= 0 && bundleIndex < POSITION_BUNDLE_SIZE;
  }

  /// Encodes a bundle index as the seed bytes used by the on-chain program's
  /// `bundled_position` PDA derivation:
  /// `seeds = [b"bundled_position", position_bundle_mint, bundle_index.to_string().as_bytes()]`.
  ///
  /// Used by [#bundledPositionPDA(PublicKey, PublicKey, int)].
  public static byte[] bundleIndexSeedBytes(final int bundleIndex) {
    if (!isValidBundleIndex(bundleIndex)) {
      throw new IllegalArgumentException("bundleIndex out of range: " + bundleIndex);
    }
    return Integer.toString(bundleIndex).getBytes(StandardCharsets.US_ASCII);
  }

  // --------------------------------------------------------------------------
  // PDA derivations with program-specific seed encodings
  //
  // The on-chain program derives the `tick_array` and `bundled_position` PDAs
  // from the *decimal string* of the integer seed
  // (`start_tick_index.to_string().as_bytes()` in
  // `instructions/initialize_tick_array.rs` / `open_bundled_position.rs`) —
  // an encoding the anchor IDL cannot express, so the generated
  // `WhirlpoolPDAs.tickArrayPDA` / `bundledPositionPDA` helpers take raw seed
  // bytes. The typed helpers below delegate to them with the source-verified
  // string encoding (`SerDeUtil.asciiSeed`).
  // --------------------------------------------------------------------------

  /// Derives the `tick_array` PDA for the array starting at `startTickIndex`:
  /// `seeds = [b"tick_array", whirlpool, start_tick_index.to_string().as_bytes()]`.
  ///
  /// Note the decimal-string seed: the index's little-endian bytes derive an
  /// address that does not exist on-chain.
  public static ProgramDerivedAddress tickArrayPDA(final PublicKey whirlpoolProgram,
                                                   final PublicKey whirlpool,
                                                   final int startTickIndex) {
    return WhirlpoolPDAs.tickArrayPDA(whirlpoolProgram, whirlpool, SerDeUtil.asciiSeed(startTickIndex));
  }

  /// Derives the `bundled_position` PDA for `bundleIndex`:
  /// `seeds = [b"bundled_position", position_bundle_mint, bundle_index.to_string().as_bytes()]`.
  ///
  /// Note the decimal-string seed: the index's little-endian bytes derive an
  /// address that does not exist on-chain.
  public static ProgramDerivedAddress bundledPositionPDA(final PublicKey whirlpoolProgram,
                                                         final PublicKey positionBundleMint,
                                                         final int bundleIndex) {
    return WhirlpoolPDAs.bundledPositionPDA(whirlpoolProgram, positionBundleMint, bundleIndexSeedBytes(bundleIndex));
  }

  // --------------------------------------------------------------------------
  // Position-bundle bitmap helpers
  // (Mirrors `rust-sdk/core/src/math/bundle.rs`.)
  //
  // The bitmap is the 32-byte `position_bitmap` stored on `PositionBundle`,
  // little-endian: bit `i` is set when slot `i` is occupied.
  // --------------------------------------------------------------------------

  /// Number of bytes in the `position_bitmap` field of a `PositionBundle`
  /// (`POSITION_BUNDLE_SIZE / 8`).
  public static final int POSITION_BUNDLE_BYTES = POSITION_BUNDLE_SIZE / 8;

  private static void requireBundleBitmap(final byte[] bitmap) {
    if (bitmap.length != POSITION_BUNDLE_BYTES) {
      throw new IllegalArgumentException(
          "position bundle bitmap must be " + POSITION_BUNDLE_BYTES + " bytes, got " + bitmap.length);
    }
  }

  /// Returns the index of the first unoccupied slot in a position bundle, or
  /// `OptionalInt.empty()` when the bundle is full. Mirrors
  /// `first_unoccupied_position_in_bundle`.
  public static OptionalInt firstUnoccupiedPositionInBundle(final byte[] bitmap) {
    requireBundleBitmap(bitmap);
    for (int byteIndex = 0; byteIndex < POSITION_BUNDLE_BYTES; byteIndex++) {
      final int b = bitmap[byteIndex] & 0xFF;
      if (b != 0xFF) {
        for (int bit = 0; bit < 8; bit++) {
          if ((b & (1 << bit)) == 0) {
            return OptionalInt.of(byteIndex * 8 + bit);
          }
        }
      }
    }
    return OptionalInt.empty();
  }

  /// Returns true when every slot in the bundle is occupied. Mirrors
  /// `is_position_bundle_full`.
  public static boolean isPositionBundleFull(final byte[] bitmap) {
    requireBundleBitmap(bitmap);
    for (final byte b : bitmap) {
      if ((b & 0xFF) != 0xFF) {
        return false;
      }
    }
    return true;
  }

  /// Returns true when no slot in the bundle is occupied. Mirrors
  /// `is_position_bundle_empty`.
  public static boolean isPositionBundleEmpty(final byte[] bitmap) {
    requireBundleBitmap(bitmap);
    for (final byte b : bitmap) {
      if (b != 0) {
        return false;
      }
    }
    return true;
  }

  // --------------------------------------------------------------------------
  // Fee / slippage helpers
  // (Mirrors `rust-sdk/core/src/math/token.rs`.)
  //
  // Amount inputs/outputs are u64; we accept/return them as Java `long` and
  // treat them as unsigned. Values must satisfy `0 <= amount <= 2^64 - 1`
  // (i.e. callers should pass them via `Long.parseUnsignedLong` or as
  // already-unsigned longs). Overflow of the u64 range throws
  // `ArithmeticException`. Invalid arguments throw `IllegalArgumentException`.
  // --------------------------------------------------------------------------

  /// Slippage / transfer-fee bps denominator (`10_000`).
  public static final int BPS_DENOMINATOR = 10_000;

  /// Whirlpool swap-fee rate denominator (`1_000_000`, i.e. fee rate is
  /// expressed in 1e-6 units, matching `Whirlpool.fee_rate` and
  /// `Whirlpool.protocol_fee_rate` semantics).
  public static final int FEE_RATE_DENOMINATOR = 1_000_000;

  private static final BigInteger U64_MAX = BigInteger.ONE.shiftLeft(64).subtract(BigInteger.ONE);
  private static final BigInteger BPS_DENOMINATOR_BI = BigInteger.valueOf(BPS_DENOMINATOR);
  private static final BigInteger FEE_RATE_DENOMINATOR_BI = BigInteger.valueOf(FEE_RATE_DENOMINATOR);

  private static BigInteger u64(final long unsignedAmount) {
    final var bi = new BigInteger(Long.toUnsignedString(unsignedAmount));
    if (bi.compareTo(U64_MAX) > 0) {
      throw new ArithmeticException("amount exceeds u64: " + bi);
    }
    return bi;
  }

  private static long toU64(final BigInteger value) {
    if (value.signum() < 0 || value.compareTo(U64_MAX) > 0) {
      throw new ArithmeticException("amount exceeds u64: " + value);
    }
    // longValueExact would reject the valid u64 range above Long.MAX_VALUE
    return value.longValue(); // returns as unsigned long bits
  }

  private static long mulDivU64(final long amount,
                                final BigInteger numeratorFactor,
                                final BigInteger denominator,
                                final boolean roundUp) {
    if (amount == 0L || numeratorFactor.signum() == 0) {
      return 0L;
    }
    final var numerator = u64(amount).multiply(numeratorFactor);
    var quotient = numerator.divide(denominator);
    if (roundUp && numerator.mod(denominator).signum() != 0) {
      quotient = quotient.add(BigInteger.ONE);
    }
    return toU64(quotient);
  }

  /// Apply a swap fee to `amount`. `feeRate` is in 1e-6 units (i.e.
  /// `Whirlpool.fee_rate`). Returns the amount the user keeps after the fee.
  /// Mirrors `try_apply_swap_fee`.
  public static long applySwapFee(final long amount, final int feeRate) {
    if (feeRate < 0 || feeRate > FEE_RATE_DENOMINATOR) {
      throw new IllegalArgumentException("feeRate out of range: " + feeRate);
    }
    final var product = FEE_RATE_DENOMINATOR_BI.subtract(BigInteger.valueOf(feeRate));
    return mulDivU64(amount, product, FEE_RATE_DENOMINATOR_BI, false);
  }

  /// Inverse of [#applySwapFee(long, int)] — given the post-fee amount,
  /// returns the pre-fee amount. Mirrors `try_reverse_apply_swap_fee`.
  public static long reverseApplySwapFee(final long amount, final int feeRate) {
    if (feeRate < 0 || feeRate > FEE_RATE_DENOMINATOR) {
      throw new IllegalArgumentException("feeRate out of range: " + feeRate);
    }
    final var denominator = FEE_RATE_DENOMINATOR_BI.subtract(BigInteger.valueOf(feeRate));
    return mulDivU64(amount, FEE_RATE_DENOMINATOR_BI, denominator, true);
  }

  /// Apply a Token-2022 transfer fee to `amount`. `feeBps` is in basis
  /// points (`0..=10_000`); `maxFee` caps the absolute fee deducted (as a
  /// u64). Returns `amount - fee`. Mirrors `try_apply_transfer_fee`.
  public static long applyTransferFee(final long amount, final int feeBps, final long maxFee) {
    if (feeBps < 0 || feeBps > BPS_DENOMINATOR) {
      throw new IllegalArgumentException("feeBps out of range: " + feeBps);
    }
    if (feeBps == 0 || amount == 0L) {
      return amount;
    }
    final var amountBi = u64(amount);
    final var numerator = amountBi.multiply(BigInteger.valueOf(feeBps));
    // div_ceil
    final var rawFee = numerator.add(BPS_DENOMINATOR_BI.subtract(BigInteger.ONE))
        .divide(BPS_DENOMINATOR_BI);
    final var maxFeeBi = u64(maxFee);
    final var feeAmount = rawFee.min(maxFeeBi);
    return toU64(amountBi.subtract(feeAmount));
  }

  /// Inverse of [#applyTransferFee(long, int, long)] — given the post-fee
  /// amount the recipient received, returns the pre-fee amount the sender
  /// must transfer. Mirrors `try_reverse_apply_transfer_fee`.
  public static long reverseApplyTransferFee(final long amount, final int feeBps, final long maxFee) {
    if (feeBps < 0 || feeBps > BPS_DENOMINATOR) {
      throw new IllegalArgumentException("feeBps out of range: " + feeBps);
    }
    if (feeBps == 0) {
      return amount;
    }
    if (amount == 0L) {
      return 0L;
    }
    final var amountBi = u64(amount);
    final var maxFeeBi = u64(maxFee);
    if (feeBps == BPS_DENOMINATOR) {
      return toU64(amountBi.add(maxFeeBi));
    }
    final var numerator = amountBi.multiply(BPS_DENOMINATOR_BI);
    final var denominator = BPS_DENOMINATOR_BI.subtract(BigInteger.valueOf(feeBps));
    // div_ceil
    final var rawPreFee = numerator.add(denominator.subtract(BigInteger.ONE)).divide(denominator);
    final var feeAmount = rawPreFee.subtract(amountBi);
    if (feeAmount.compareTo(maxFeeBi) >= 0) {
      return toU64(amountBi.add(maxFeeBi));
    }
    return toU64(rawPreFee);
  }

  /// Maximum amount the caller might end up sending given an estimate and a
  /// slippage tolerance in bps (`amount * (10000 + bps) / 10000`, rounded
  /// up). Suitable for computing `otherAmountThreshold` when *paying* with
  /// the threshold token. Mirrors `try_get_max_amount_with_slippage_tolerance`.
  public static long maxAmountWithSlippageTolerance(final long amount, final int slippageBps) {
    if (slippageBps < 0 || slippageBps > BPS_DENOMINATOR) {
      throw new IllegalArgumentException("slippageBps out of range: " + slippageBps);
    }
    final var product = BPS_DENOMINATOR_BI.add(BigInteger.valueOf(slippageBps));
    return mulDivU64(amount, product, BPS_DENOMINATOR_BI, true);
  }

  /// Minimum amount the caller is willing to receive given an estimate and
  /// a slippage tolerance in bps (`amount * (10000 - bps) / 10000`, rounded
  /// down). Suitable for computing `otherAmountThreshold` when *receiving*
  /// the threshold token. Mirrors `try_get_min_amount_with_slippage_tolerance`.
  public static long minAmountWithSlippageTolerance(final long amount, final int slippageBps) {
    if (slippageBps < 0 || slippageBps > BPS_DENOMINATOR) {
      throw new IllegalArgumentException("slippageBps out of range: " + slippageBps);
    }
    final var product = BPS_DENOMINATOR_BI.subtract(BigInteger.valueOf(slippageBps));
    return mulDivU64(amount, product, BPS_DENOMINATOR_BI, false);
  }

  // --------------------------------------------------------------------------
  // Position status (sqrt-price based)
  // (Mirrors `rust-sdk/core/src/math/position.rs`, but takes the position's
  // sqrt-price bounds directly instead of deriving them from tick indexes —
  // tick → sqrt-price conversion is the heavy Q64.64 routine and lives in a
  // separate stage.)
  // --------------------------------------------------------------------------

  public enum PositionStatus {
    /// `tickLower == tickUpper` (invalid position bounds).
    INVALID,
    /// `currentSqrtPrice < sqrtPriceLower` — position is entirely in token A.
    BELOW_RANGE,
    /// `sqrtPriceLower < currentSqrtPrice < sqrtPriceUpper`. Note: matching
    /// the Rust SDK, equality with either bound counts as out-of-range.
    IN_RANGE,
    /// `currentSqrtPrice > sqrtPriceUpper` — position is entirely in token B.
    ABOVE_RANGE
  }

  /// Convenience predicate: returns true when `currentSqrtPrice` is strictly
  /// inside `(sqrtPriceLower, sqrtPriceUpper)`. Mirrors `is_position_in_range`.
  public static boolean isPositionInRange(final BigInteger currentSqrtPrice,
                                          final BigInteger sqrtPriceLower,
                                          final BigInteger sqrtPriceUpper) {
    return positionStatus(currentSqrtPrice, sqrtPriceLower, sqrtPriceUpper) == PositionStatus.IN_RANGE;
  }

  /// Returns the status of a position with bounds `[sqrtPriceLower,
  /// sqrtPriceUpper]` relative to the pool's `currentSqrtPrice`. All inputs
  /// are Q64.64 sqrt-prices (the same units as `Whirlpool.sqrt_price` and
  /// `Position.tick_lower_index / tick_upper_index` after conversion).
  /// Mirrors `position_status`.
  public static PositionStatus positionStatus(final BigInteger currentSqrtPrice,
                                              final BigInteger sqrtPriceLower,
                                              final BigInteger sqrtPriceUpper) {
    final int cmp = sqrtPriceLower.compareTo(sqrtPriceUpper);
    if (cmp == 0) {
      return PositionStatus.INVALID;
    }
    final BigInteger lower;
    final BigInteger upper;
    if (cmp < 0) {
      lower = sqrtPriceLower;
      upper = sqrtPriceUpper;
    } else {
      lower = sqrtPriceUpper;
      upper = sqrtPriceLower;
    }
    if (currentSqrtPrice.compareTo(lower) <= 0) {
      return PositionStatus.BELOW_RANGE;
    }
    if (currentSqrtPrice.compareTo(upper) >= 0) {
      return PositionStatus.ABOVE_RANGE;
    }
    return PositionStatus.IN_RANGE;
  }

  // --------------------------------------------------------------------------
  // Sqrt-price slippage bounds
  // (Mirrors `rust-sdk/core/src/math/price.rs::get_sqrt_price_slippage_bounds`.)
  // --------------------------------------------------------------------------

  /// Min / max sqrt-priceX64 bounds for slippage-protected swaps. Pass the
  /// matching bound (lower for `aToB`, upper for `bToA`) as the
  /// `sqrtPriceLimit` arg on `swap` / `swapV2`.
  public record SqrtPriceSlippageBounds(BigInteger minSqrtPrice, BigInteger maxSqrtPrice) {
  }

  private static final BigInteger SLIPPAGE_PRECISION = BigInteger.valueOf(1_000_000L);
  private static final BigInteger SQRT_SLIPPAGE_DENOMINATOR = BigInteger.valueOf(100_000L);

  /// Integer (floor) square root using Newton's method. Mirrors
  /// `sqrt_u128` in the Rust SDK; works on arbitrary-precision `BigInteger`
  /// inputs (the Rust impl is `u128`).
  static BigInteger sqrtFloor(final BigInteger value) {
    if (value.signum() < 0) {
      throw new IllegalArgumentException("sqrt of negative value: " + value);
    }
    if (value.compareTo(BigInteger.valueOf(2L)) < 0) {
      return value;
    }
    var prev = value.shiftRight(1);
    var next = prev.add(value.divide(prev)).shiftRight(1);
    while (next.compareTo(prev) < 0) {
      prev = next;
      next = prev.add(value.divide(prev)).shiftRight(1);
    }
    return prev;
  }

  private static BigInteger sqrtCeil(final BigInteger value) {
    final var floor = sqrtFloor(value);
    return floor.multiply(floor).compareTo(value) < 0 ? floor.add(BigInteger.ONE) : floor;
  }

  /// Compute the min/max sqrt-price bounds for a slippage-protected swap.
  ///
  /// - `sqrtPriceX64` is the pool's current sqrt-price (Q64.64).
  /// - `slippageBps` is the tolerance in basis points (`0..=10_000`); larger
  ///   values are clamped to `10_000`.
  ///
  /// Result is clamped to `[MIN_SQRT_PRICE_X64, MAX_SQRT_PRICE_X64]`. Mirrors
  /// `get_sqrt_price_slippage_bounds`.
  public static SqrtPriceSlippageBounds getSqrtPriceSlippageBounds(final BigInteger sqrtPriceX64,
                                                                   final int slippageBps) {
    if (slippageBps < 0) {
      throw new IllegalArgumentException("slippageBps must be non-negative: " + slippageBps);
    }
    final int cappedBps = Math.min(slippageBps, BPS_DENOMINATOR);
    final var bps = BigInteger.valueOf(cappedBps);
    final var lowerRadicand = BPS_DENOMINATOR_BI.subtract(bps).multiply(SLIPPAGE_PRECISION);
    final var upperRadicand = BPS_DENOMINATOR_BI.add(bps).multiply(SLIPPAGE_PRECISION);
    final var lowerFactor = sqrtFloor(lowerRadicand);
    final var upperFactor = sqrtCeil(upperRadicand);
    final var minScaled = sqrtPriceX64.multiply(lowerFactor).divide(SQRT_SLIPPAGE_DENOMINATOR);
    final var maxScaled = sqrtPriceX64.multiply(upperFactor).divide(SQRT_SLIPPAGE_DENOMINATOR);
    final var min = minScaled.max(MIN_SQRT_PRICE_X64);
    final var max = maxScaled.min(MAX_SQRT_PRICE_X64);
    return new SqrtPriceSlippageBounds(min, max);
  }

  // --------------------------------------------------------------------------
  // tick_index <-> sqrt_priceX64 conversions
  // (Mirrors `rust-sdk/core/src/math/tick.rs::{tick_index_to_sqrt_price,
  // sqrt_price_to_tick_index}`. These are the canonical Uniswap V3-style
  // multiplicative ladder using U256/u128 fixed-point math; ported here with
  // `BigInteger` to keep the magic constants and shifts byte-exact with the
  // Rust implementation.)
  // --------------------------------------------------------------------------

  /// u128 bit mask (`2^128 - 1`). Exposed for the `quote/` helpers that need
  /// to model Rust `wrapping_sub` on u128.
  public static final BigInteger U128_MASK = BigInteger.ONE.shiftLeft(128).subtract(BigInteger.ONE);

  // Positive-tick magic constants (U256 multiplicands; rounded with >> 96 by mul_shift_96).
  private static final BigInteger POS_BASE_EVEN = new BigInteger("79228162514264337593543950336");
  private static final BigInteger POS_BASE_ODD = new BigInteger("79232123823359799118286999567");
  private static final BigInteger[] POS_FACTORS = {
      new BigInteger("79236085330515764027303304731"),    // bit 2
      new BigInteger("79244008939048815603706035061"),    // bit 4
      new BigInteger("79259858533276714757314932305"),    // bit 8
      new BigInteger("79291567232598584799939703904"),    // bit 16
      new BigInteger("79355022692464371645785046466"),    // bit 32
      new BigInteger("79482085999252804386437311141"),    // bit 64
      new BigInteger("79736823300114093921829183326"),    // bit 128
      new BigInteger("80248749790819932309965073892"),    // bit 256
      new BigInteger("81282483887344747381513967011"),    // bit 512
      new BigInteger("83390072131320151908154831281"),    // bit 1024
      new BigInteger("87770609709833776024991924138"),    // bit 2048
      new BigInteger("97234110755111693312479820773"),    // bit 4096
      new BigInteger("119332217159966728226237229890"),   // bit 8192
      new BigInteger("179736315981702064433883588727"),   // bit 16384
      new BigInteger("407748233172238350107850275304"),   // bit 32768
      new BigInteger("2098478828474011932436660412517"),  // bit 65536
      new BigInteger("55581415166113811149459800483533"), // bit 131072
      new BigInteger("38992368544603139932233054999993551") // bit 262144
  };

  // Negative-tick magic constants (u128 multiplicands; rounded with >> 64).
  private static final BigInteger NEG_BASE_EVEN = new BigInteger("18446744073709551616");
  private static final BigInteger NEG_BASE_ODD = new BigInteger("18445821805675392311");
  private static final BigInteger[] NEG_FACTORS = {
      new BigInteger("18444899583751176498"), // bit 2
      new BigInteger("18443055278223354162"), // bit 4
      new BigInteger("18439367220385604838"), // bit 8
      new BigInteger("18431993317065449817"), // bit 16
      new BigInteger("18417254355718160513"), // bit 32
      new BigInteger("18387811781193591352"), // bit 64
      new BigInteger("18329067761203520168"), // bit 128
      new BigInteger("18212142134806087854"), // bit 256
      new BigInteger("17980523815641551639"), // bit 512
      new BigInteger("17526086738831147013"), // bit 1024
      new BigInteger("16651378430235024244"), // bit 2048
      new BigInteger("15030750278693429944"), // bit 4096
      new BigInteger("12247334978882834399"), // bit 8192
      new BigInteger("8131365268884726200"),  // bit 16384
      new BigInteger("3584323654723342297"),  // bit 32768
      new BigInteger("696457651847595233"),   // bit 65536
      new BigInteger("26294789957452057"),    // bit 131072
      new BigInteger("37481735321082")        // bit 262144
  };

  private static BigInteger sqrtPriceFromPositiveTick(final int tick) {
    BigInteger ratio = (tick & 1) != 0 ? POS_BASE_ODD : POS_BASE_EVEN;
    for (int i = 0; i < POS_FACTORS.length; i++) {
      final int mask = 2 << i; // 2, 4, 8, ..., 262144
      if ((tick & mask) != 0) {
        // mul_shift_96: (ratio * factor) >> 96, truncated to u128.
        ratio = ratio.multiply(POS_FACTORS[i]).shiftRight(96).and(U128_MASK);
      }
    }
    return ratio.shiftRight(32);
  }

  private static BigInteger sqrtPriceFromNegativeTick(final int tick) {
    final int absTick = Math.abs(tick);
    BigInteger ratio = (absTick & 1) != 0 ? NEG_BASE_ODD : NEG_BASE_EVEN;
    for (int i = 0; i < NEG_FACTORS.length; i++) {
      final int mask = 2 << i;
      if ((absTick & mask) != 0) {
        // (ratio * factor) >> 64, truncated to u128.
        ratio = ratio.multiply(NEG_FACTORS[i]).shiftRight(64).and(U128_MASK);
      }
    }
    return ratio;
  }

  /// Convert a tick index to its sqrt-priceX64 (Q64.64). Mirrors
  /// `tick_index_to_sqrt_price` — precision is only guaranteed for
  /// `tickIndex` within `[MIN_TICK_INDEX, MAX_TICK_INDEX]`.
  public static BigInteger tickIndexToSqrtPriceX64(final int tickIndex) {
    return tickIndex >= 0
        ? sqrtPriceFromPositiveTick(tickIndex)
        : sqrtPriceFromNegativeTick(tickIndex);
  }

  public static BigInteger tickIndexToSqrtPriceX64Checked(final int tickIndex) {
    if (tickIndex < OrcaUtil.MIN_TICK_INDEX || tickIndex > OrcaUtil.MAX_TICK_INDEX) {
      throw new IllegalArgumentException("tick index out of range: " + tickIndex);
    }
    return tickIndexToSqrtPriceX64(tickIndex);
  }

  // log_b(2) in Q32.32 form (one bit per 2^32). Source: `LOG_B_2_X32`.
  private static final BigInteger LOG_B_2_X32 = BigInteger.valueOf(59543866431248L);
  private static final BigInteger LOG_B_P_ERR_MARGIN_LOWER_X64 =
      BigInteger.valueOf(184467440737095516L);
  // 15793534762490258745 > Long.MAX_VALUE, declare as unsigned via String.
  private static final BigInteger LOG_B_P_ERR_MARGIN_UPPER_X64 =
      new BigInteger("15793534762490258745");
  private static final int BIT_PRECISION = 14;

  /// Convert a sqrt-priceX64 (Q64.64, u128) to its tick index. Mirrors
  /// `sqrt_price_to_tick_index`. Panics in the Rust impl on `sqrt_price == 0`;
  /// here we throw `IllegalArgumentException`.
  public static int sqrtPriceX64ToTickIndex(final BigInteger sqrtPriceX64) {
    if (sqrtPriceX64.signum() <= 0) {
      throw new IllegalArgumentException("sqrtPriceX64 must be positive: " + sqrtPriceX64);
    }
    // msb = 128 - leading_zeros - 1 = bitLength - 1 for u128 (BigInteger is unsigned positive here).
    final int msb = sqrtPriceX64.bitLength() - 1;
    final BigInteger log2pIntegerX32 = BigInteger.valueOf((long) msb - 64L).shiftLeft(32);

    // Bring sqrt_price to Q1.63 form (normalize to bit 63).
    BigInteger r = msb >= 64
        ? sqrtPriceX64.shiftRight(msb - 63)
        : sqrtPriceX64.shiftLeft(63 - msb);

    BigInteger bit = BigInteger.ONE.shiftLeft(63); // 0x8000_0000_0000_0000
    BigInteger log2pFractionX64 = BigInteger.ZERO;
    int precision = 0;

    while (bit.signum() > 0 && precision < BIT_PRECISION) {
      r = r.multiply(r);
      // is_r_more_than_two = r >> 127 (0 or 1)
      final int isRMoreThanTwo = r.testBit(127) ? 1 : 0;
      r = r.shiftRight(63 + isRMoreThanTwo);
      if (isRMoreThanTwo == 1) {
        log2pFractionX64 = log2pFractionX64.add(bit);
      }
      bit = bit.shiftRight(1);
      precision++;
    }

    final BigInteger log2pFractionX32 = log2pFractionX64.shiftRight(32);
    final BigInteger log2pX32 = log2pIntegerX32.add(log2pFractionX32);

    // Transform from base 2 to base 1.0001.
    final BigInteger logbpX64 = log2pX32.multiply(LOG_B_2_X32);

    final int tickLow = arithmeticShiftRightToInt(logbpX64.subtract(LOG_B_P_ERR_MARGIN_LOWER_X64), 64);
    final int tickHigh = arithmeticShiftRightToInt(logbpX64.add(LOG_B_P_ERR_MARGIN_UPPER_X64), 64);

    if (tickLow == tickHigh) {
      return tickLow;
    }
    final BigInteger actualTickHighSqrtPrice = tickIndexToSqrtPriceX64(tickHigh);
    return actualTickHighSqrtPrice.compareTo(sqrtPriceX64) <= 0 ? tickHigh : tickLow;
  }

  /// Arithmetic (sign-preserving) shift-right of a signed BigInteger by `n`
  /// bits, cast to i32 (low 32 bits). Mirrors the `(x >> 64) as i32` step in
  /// the Rust `sqrt_price_to_tick_index`.
  private static int arithmeticShiftRightToInt(final BigInteger value, final int n) {
    return value.shiftRight(n).intValue();
  }

  // --------------------------------------------------------------------------
  // price <-> sqrt_priceX64 / tick_index (floating-point convenience wrappers)
  // (Mirrors `rust-sdk/core/src/math/price.rs::{price_to_sqrt_price,
  // sqrt_price_to_price, tick_index_to_price, price_to_tick_index,
  // invert_price}`. As in the Rust impl, these use `double` arithmetic and
  // are intended for display / one-shot conversions — do not chain them.)
  // --------------------------------------------------------------------------

  private static final double Q64_RESOLUTION = 18446744073709551616.0; // 2^64

  /// Convert a decimal price `(amountB / amountA)` to a sqrt-priceX64,
  /// adjusting for token decimals. Mirrors `price_to_sqrt_price`.
  public static BigInteger priceToSqrtPriceX64(final double price, final int decimalsA, final int decimalsB) {
    final double power = Math.pow(10.0, decimalsA - decimalsB);
    final double scaled = Math.floor(Math.sqrt(price / power) * Q64_RESOLUTION);
    // u128 cast from f64: clamp to non-negative and round to BigInteger.
    if (!Double.isFinite(scaled) || scaled <= 0.0) {
      return BigInteger.ZERO;
    }
    return new BigInteger(new java.math.BigDecimal(scaled).toBigInteger().toString());
  }

  /// Convert a sqrt-priceX64 to a decimal price, adjusting for token decimals.
  /// Mirrors `sqrt_price_to_price`.
  public static double sqrtPriceX64ToPrice(final BigInteger sqrtPriceX64,
                                           final int decimalsA,
                                           final int decimalsB) {
    final double power = Math.pow(10.0, decimalsA - decimalsB);
    final double sp = sqrtPriceX64.doubleValue();
    final double ratio = sp / Q64_RESOLUTION;
    return ratio * ratio * power;
  }

  /// Convert a tick index to a decimal price. Mirrors `tick_index_to_price`.
  public static double tickIndexToPrice(final int tickIndex, final int decimalsA, final int decimalsB) {
    return sqrtPriceX64ToPrice(tickIndexToSqrtPriceX64(tickIndex), decimalsA, decimalsB);
  }

  /// Convert a decimal price to a tick index. Mirrors `price_to_tick_index`.
  public static int priceToTickIndex(final double price, final int decimalsA, final int decimalsB) {
    return sqrtPriceX64ToTickIndex(priceToSqrtPriceX64(price, decimalsA, decimalsB));
  }

  /// Invert a decimal price (Pa/Pb -> Pb/Pa) via the tick index. Mirrors
  /// `invert_price`. Lossy due to the tick-index round-trip; matches the Rust
  /// SDK's behavior exactly.
  public static double invertPrice(final double price, final int decimalsA, final int decimalsB) {
    final int tickIndex = priceToTickIndex(price, decimalsA, decimalsB);
    return tickIndexToPrice(-tickIndex, decimalsA, decimalsB);
  }

  /// Invert a sqrt-priceX64 via the tick-index round-trip. Mirrors
  /// `invert_sqrt_price`; clamps to the nearest tick index.
  public static BigInteger invertSqrtPriceX64(final BigInteger sqrtPriceX64) {
    final int tickIndex = sqrtPriceX64ToTickIndex(sqrtPriceX64);
    return tickIndexToSqrtPriceX64(-tickIndex);
  }

  // --------------------------------------------------------------------------
  // Token delta math
  // (Mirrors `rust-sdk/core/src/math/token.rs::{try_get_amount_delta_a,
  // try_get_amount_delta_b, try_get_next_sqrt_price_from_a,
  // try_get_next_sqrt_price_from_b}`.)
  //
  // All sqrt-prices are Q64.64 (u128) and `liquidity` is u128. Inputs are
  // accepted as `BigInteger` to allow the full unsigned range; outputs that
  // map to u64 are returned as Java `long` (treated as unsigned). Overflow
  // beyond u64 throws `ArithmeticException`; sqrt-price results outside
  // `[MIN_SQRT_PRICE_X64, MAX_SQRT_PRICE_X64]` throw
  // `ArithmeticException` (matches the Rust `SQRT_PRICE_OUT_OF_BOUNDS`).
  // --------------------------------------------------------------------------

  private static BigInteger requireU128(final BigInteger value, final String name) {
    if (value.signum() < 0 || value.compareTo(U128_MASK) > 0) {
      throw new IllegalArgumentException(name + " out of u128 range: " + value);
    }
    return value;
  }

  private static BigInteger[] orderPrices(final BigInteger a, final BigInteger b) {
    return a.compareTo(b) <= 0 ? new BigInteger[]{a, b} : new BigInteger[]{b, a};
  }

  /// Compute the token-A delta between two sqrt-prices for a given
  /// `liquidity`. Mirrors `try_get_amount_delta_a`, except the result is
  /// returned uncapped: the Rust version errors when it exceeds u64.
  public static BigInteger tryGetAmountDeltaA(final BigInteger sqrtPrice1,
                                              final BigInteger sqrtPrice2,
                                              final BigInteger liquidity,
                                              final boolean roundUp) {
    requireU128(sqrtPrice1, "sqrtPrice1");
    requireU128(sqrtPrice2, "sqrtPrice2");
    requireU128(liquidity, "liquidity");
    final BigInteger[] ordered = orderPrices(sqrtPrice1, sqrtPrice2);
    final BigInteger sqrtPriceLower = ordered[0];
    final BigInteger sqrtPriceUpper = ordered[1];
    final BigInteger sqrtPriceDiff = sqrtPriceUpper.subtract(sqrtPriceLower);
    final BigInteger numerator = liquidity.multiply(sqrtPriceDiff).shiftLeft(64);
    final BigInteger denominator = sqrtPriceLower.multiply(sqrtPriceUpper);
    if (denominator.signum() == 0) {
      throw new ArithmeticException("denominator is zero");
    }
    final BigInteger[] qr = numerator.divideAndRemainder(denominator);
    BigInteger quotient = qr[0];
    if (roundUp && qr[1].signum() != 0) {
      quotient = quotient.add(BigInteger.ONE);
    }
    return quotient;
  }

  /// Compute the token-B delta between two sqrt-prices for a given
  /// `liquidity`. Mirrors `try_get_amount_delta_b`, except the result is
  /// returned uncapped: the Rust version errors when it exceeds u64.
  public static BigInteger tryGetAmountDeltaB(final BigInteger sqrtPrice1,
                                              final BigInteger sqrtPrice2,
                                              final BigInteger liquidity,
                                              final boolean roundUp) {
    requireU128(sqrtPrice1, "sqrtPrice1");
    requireU128(sqrtPrice2, "sqrtPrice2");
    requireU128(liquidity, "liquidity");
    final BigInteger[] ordered = orderPrices(sqrtPrice1, sqrtPrice2);
    final BigInteger sqrtPriceDiff = ordered[1].subtract(ordered[0]);
    final BigInteger product = liquidity.multiply(sqrtPriceDiff);
    BigInteger quotient = product.shiftRight(64);
    // round when low-64 bits are non-zero (product & u64::MAX > 0).
    final boolean shouldRound = roundUp && product.and(U64_MAX).signum() > 0;
    if (shouldRound) {
      quotient = quotient.add(BigInteger.ONE);
    }
    return quotient;
  }

  private static BigInteger requireSqrtPriceBounds(final BigInteger result) {
    if (result.compareTo(MIN_SQRT_PRICE_X64) < 0 || result.compareTo(MAX_SQRT_PRICE_X64) > 0) {
      throw new ArithmeticException("sqrt-price out of bounds: " + result);
    }
    return result;
  }

  /// Compute the next sqrt-price after a swap consuming/producing a given
  /// amount of token A. Mirrors `try_get_next_sqrt_price_from_a`. The result
  /// is always rounded up (matches Rust). Throws if the result is outside
  /// `[MIN_SQRT_PRICE_X64, MAX_SQRT_PRICE_X64]`.
  public static BigInteger tryGetNextSqrtPriceFromA(final BigInteger currentSqrtPrice,
                                                    final BigInteger currentLiquidity,
                                                    final long amount,
                                                    final boolean specifiedInput) {
    requireU128(currentSqrtPrice, "currentSqrtPrice");
    requireU128(currentLiquidity, "currentLiquidity");
    final BigInteger amountBi = u64(amount);
    if (amountBi.signum() == 0) {
      return currentSqrtPrice;
    }
    final BigInteger p = currentSqrtPrice.multiply(amountBi);
    final BigInteger numerator = currentLiquidity.multiply(currentSqrtPrice).shiftLeft(64);
    final BigInteger currentLiquidityShifted = currentLiquidity.shiftLeft(64);
    final BigInteger denominator = specifiedInput
        ? currentLiquidityShifted.add(p)
        : currentLiquidityShifted.subtract(p);
    if (denominator.signum() <= 0) {
      throw new ArithmeticException("denominator is non-positive");
    }
    final BigInteger[] qr = numerator.divideAndRemainder(denominator);
    BigInteger result = qr[0];
    if (qr[1].signum() != 0) {
      result = result.add(BigInteger.ONE);
    }
    return requireSqrtPriceBounds(result);
  }

  /// Compute the next sqrt-price after a swap consuming/producing a given
  /// amount of token B. Mirrors `try_get_next_sqrt_price_from_b`. Throws if
  /// the result is outside `[MIN_SQRT_PRICE_X64, MAX_SQRT_PRICE_X64]`.
  public static BigInteger tryGetNextSqrtPriceFromB(final BigInteger currentSqrtPrice,
                                                    final BigInteger currentLiquidity,
                                                    final long amount,
                                                    final boolean specifiedInput) {
    requireU128(currentSqrtPrice, "currentSqrtPrice");
    requireU128(currentLiquidity, "currentLiquidity");
    final BigInteger amountBi = u64(amount);
    if (amountBi.signum() == 0) {
      return currentSqrtPrice;
    }
    if (currentLiquidity.signum() == 0) {
      throw new ArithmeticException("currentLiquidity is zero");
    }
    final BigInteger amountShifted = amountBi.shiftLeft(64);
    final BigInteger[] qr = amountShifted.divideAndRemainder(currentLiquidity);
    BigInteger delta = qr[0];
    if (!specifiedInput && qr[1].signum() != 0) {
      delta = delta.add(BigInteger.ONE);
    }
    final BigInteger result = specifiedInput
        ? currentSqrtPrice.add(delta)
        : currentSqrtPrice.subtract(delta);
    return requireSqrtPriceBounds(result);
  }
}

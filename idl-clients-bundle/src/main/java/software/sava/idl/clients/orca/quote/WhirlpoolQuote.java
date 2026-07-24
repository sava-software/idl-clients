package software.sava.idl.clients.orca.quote;

import software.sava.idl.clients.orca.OrcaUtil;
import software.sava.idl.clients.orca.OrcaUtil.PositionStatus;
import software.sava.idl.clients.orca.whirlpools.gen.types.Position;
import software.sava.idl.clients.orca.whirlpools.gen.types.PositionRewardInfo;
import software.sava.idl.clients.orca.whirlpools.gen.types.Tick;
import software.sava.idl.clients.orca.whirlpools.gen.types.Whirlpool;
import software.sava.idl.clients.orca.whirlpools.gen.types.WhirlpoolRewardInfo;

import java.math.BigInteger;

/// Whirlpool quote helpers — mirrors `rust-sdk/core/src/quote/{fees,rewards,
/// liquidity}.rs` byte-exactly. Consumes the generated `Whirlpool`, `Position`,
/// `Tick`, `WhirlpoolRewardInfo`, `PositionRewardInfo` state records and
/// returns pre-computed amounts callers can feed to the matching
/// `OrcaWhirlpoolsClient` instruction builders (`collectFees(V2)`,
/// `collectReward(V2)`, `increaseLiquidity(V2)`, `decreaseLiquidity(V2)`).
///
/// Token-2022 transfer fees are passed as optional [TransferFee] arguments
/// (`null` = no transfer fee, matching Rust's `Option::None`).
///
/// The heavier swap quoter (`swap_quote_by_input_token` / `_output_token`) is
/// intentionally omitted; it requires iterating a tick-array sequence which is
/// a non-trivial follow-up port.
public final class WhirlpoolQuote {

  private WhirlpoolQuote() {
  }

  // u128 wrapping subtraction (`a.wrapping_sub(b)` in Rust).
  private static BigInteger wrappingSubU128(final BigInteger a, final BigInteger b) {
    return a.subtract(b).and(OrcaUtil.U128_MASK);
  }

  private static long transferFee(final long amount, final TransferFee fee) {
    return fee == null ? amount : OrcaUtil.applyTransferFee(amount, fee.feeBps(), fee.maxFee());
  }

  private static long reverseTransferFee(final long amount, final TransferFee fee) {
    return fee == null ? amount : OrcaUtil.reverseApplyTransferFee(amount, fee.feeBps(), fee.maxFee());
  }

  // ===========================================================================
  // collect_fees_quote
  // ===========================================================================

  /// Calculate fees owed for a position. Mirrors Rust `collect_fees_quote`.
  ///
  /// Inputs must come from the same Whirlpool — `tickLower` and `tickUpper`
  /// should be the `Tick` records at `position.tickLowerIndex()` /
  /// `position.tickUpperIndex()`. Pass `null` for either transfer fee when the
  /// corresponding mint has no Token-2022 transfer-hook fee.
  public static CollectFeesQuote collectFeesQuote(final Whirlpool whirlpool,
                                                  final Position position,
                                                  final Tick tickLower,
                                                  final Tick tickUpper,
                                                  final TransferFee transferFeeA,
                                                  final TransferFee transferFeeB) {
    BigInteger feeGrowthBelowA = tickLower.feeGrowthOutsideA();
    BigInteger feeGrowthAboveA = tickUpper.feeGrowthOutsideA();
    BigInteger feeGrowthBelowB = tickLower.feeGrowthOutsideB();
    BigInteger feeGrowthAboveB = tickUpper.feeGrowthOutsideB();

    final int tickCurrent = whirlpool.tickCurrentIndex();
    final BigInteger feeGrowthGlobalA = whirlpool.feeGrowthGlobalA();
    final BigInteger feeGrowthGlobalB = whirlpool.feeGrowthGlobalB();

    if (tickCurrent < position.tickLowerIndex()) {
      feeGrowthBelowA = wrappingSubU128(feeGrowthGlobalA, feeGrowthBelowA);
      feeGrowthBelowB = wrappingSubU128(feeGrowthGlobalB, feeGrowthBelowB);
    }
    if (tickCurrent >= position.tickUpperIndex()) {
      feeGrowthAboveA = wrappingSubU128(feeGrowthGlobalA, feeGrowthAboveA);
      feeGrowthAboveB = wrappingSubU128(feeGrowthGlobalB, feeGrowthAboveB);
    }

    final BigInteger feeGrowthInsideA = wrappingSubU128(
        wrappingSubU128(feeGrowthGlobalA, feeGrowthBelowA), feeGrowthAboveA);
    final BigInteger feeGrowthInsideB = wrappingSubU128(
        wrappingSubU128(feeGrowthGlobalB, feeGrowthBelowB), feeGrowthAboveB);

    final BigInteger feeGrowthDeltaA = wrappingSubU128(feeGrowthInsideA, position.feeGrowthCheckpointA());
    final BigInteger feeGrowthDeltaB = wrappingSubU128(feeGrowthInsideB, position.feeGrowthCheckpointB());

    final BigInteger feeOwedDeltaA = feeGrowthDeltaA.multiply(position.liquidity()).shiftRight(64);
    final BigInteger feeOwedDeltaB = feeGrowthDeltaB.multiply(position.liquidity()).shiftRight(64);

    final long withdrawableFeeA = addU64(position.feeOwedA(), toU64(feeOwedDeltaA));
    final long withdrawableFeeB = addU64(position.feeOwedB(), toU64(feeOwedDeltaB));

    return new CollectFeesQuote(
        transferFee(withdrawableFeeA, transferFeeA),
        transferFee(withdrawableFeeB, transferFeeB));
  }

  private static final BigInteger U64_MAX = BigInteger.ONE.shiftLeft(64).subtract(BigInteger.ONE);

  private static long toU64(final BigInteger value) {
    if (value.signum() < 0 || value.compareTo(U64_MAX) > 0) {
      throw new ArithmeticException("amount exceeds u64: " + value);
    }
    // longValueExact would reject the valid u64 range above Long.MAX_VALUE
    return value.longValue(); // returns as unsigned long bits
  }

  private static long addU64(final long a, final long b) {
    final BigInteger sum = new BigInteger(Long.toUnsignedString(a))
        .add(new BigInteger(Long.toUnsignedString(b)));
    return toU64(sum);
  }

  // ===========================================================================
  // collect_rewards_quote
  // ===========================================================================

  /// Calculate rewards owed for a position. Mirrors Rust
  /// `collect_rewards_quote`. The returned `rewards` array always has length
  /// 3, matching Whirlpool's `NUM_REWARDS`. Pass `null` for any transfer fee
  /// whose reward mint has no Token-2022 transfer-hook fee.
  public static CollectRewardsQuote collectRewardsQuote(final Whirlpool whirlpool,
                                                        final Position position,
                                                        final Tick tickLower,
                                                        final Tick tickUpper,
                                                        final long currentTimestamp,
                                                        final TransferFee transferFee1,
                                                        final TransferFee transferFee2,
                                                        final TransferFee transferFee3) {
    final long timestampDelta = currentTimestamp - whirlpool.rewardLastUpdatedTimestamp();
    final BigInteger timestampDeltaBi = BigInteger.valueOf(timestampDelta);
    final TransferFee[] fees = {transferFee1, transferFee2, transferFee3};
    final CollectRewardQuote[] out = new CollectRewardQuote[CollectRewardsQuote.NUM_REWARDS];

    final BigInteger poolLiquidity = whirlpool.liquidity();
    final WhirlpoolRewardInfo[] wri = whirlpool.rewardInfos();
    final PositionRewardInfo[] pri = position.rewardInfos();
    final BigInteger[] tickLowerRgo = tickLower.rewardGrowthsOutside();
    final BigInteger[] tickUpperRgo = tickUpper.rewardGrowthsOutside();

    for (int i = 0; i < CollectRewardsQuote.NUM_REWARDS; i++) {
      BigInteger rewardGrowth = wri[i].growthGlobalX64();
      if (poolLiquidity.signum() != 0) {
        final BigInteger rewardGrowthDelta = wri[i].emissionsPerSecondX64()
            .multiply(timestampDeltaBi)
            .divide(poolLiquidity);
        rewardGrowth = rewardGrowth.add(rewardGrowthDelta);
      }

      BigInteger rewardGrowthBelow = tickLowerRgo[i];
      BigInteger rewardGrowthAbove = tickUpperRgo[i];
      if (whirlpool.tickCurrentIndex() < position.tickLowerIndex()) {
        rewardGrowthBelow = wrappingSubU128(rewardGrowth, rewardGrowthBelow);
      }
      if (whirlpool.tickCurrentIndex() >= position.tickUpperIndex()) {
        rewardGrowthAbove = wrappingSubU128(rewardGrowth, rewardGrowthAbove);
      }

      final BigInteger rewardGrowthInside = wrappingSubU128(
          wrappingSubU128(rewardGrowth, rewardGrowthBelow), rewardGrowthAbove);
      final BigInteger rewardGrowthDelta =
          wrappingSubU128(rewardGrowthInside, pri[i].growthInsideCheckpoint());

      final long rewardOwedDelta;
      if (rewardGrowthDelta.signum() == 0 || position.liquidity().signum() == 0) {
        rewardOwedDelta = 0L;
      } else {
        // checked_mul fallback to 0 on overflow; in BigInteger we don't overflow,
        // but Rust uses `unwrap_or(0)` so on overflow we mirror by checking u128 range.
        final BigInteger product = position.liquidity().multiply(rewardGrowthDelta);
        if (product.compareTo(OrcaUtil.U128_MASK) > 0) {
          rewardOwedDelta = 0L;
        } else {
          // `(product >> 64) as u64` — truncate to u64 low bits.
          rewardOwedDelta = product.shiftRight(64).and(U64_MAX).longValue();
        }
      }
      final long withdrawableReward = addU64(pri[i].amountOwed(), rewardOwedDelta);
      out[i] = new CollectRewardQuote(transferFee(withdrawableReward, fees[i]));
    }
    return new CollectRewardsQuote(out);
  }

  // ===========================================================================
  // liquidity quotes
  // ===========================================================================

  private static int[] orderTicks(final int t1, final int t2) {
    return t1 <= t2 ? new int[]{t1, t2} : new int[]{t2, t1};
  }

  /// Estimate token A/B amounts for a given liquidity delta and price range.
  /// Mirrors Rust `try_get_token_estimates_from_liquidity`.
  /// Returns `[tokenA, tokenB]` as unsigned `long`s.
  public static long[] tryGetTokenEstimatesFromLiquidity(final BigInteger liquidityDelta,
                                                         final BigInteger currentSqrtPrice,
                                                         final int tickLowerIndex,
                                                         final int tickUpperIndex,
                                                         final boolean roundUp) {
    if (liquidityDelta.signum() == 0) {
      return new long[]{0L, 0L};
    }
    final BigInteger sqrtPriceLower = OrcaUtil.tickIndexToSqrtPriceX64(tickLowerIndex);
    final BigInteger sqrtPriceUpper = OrcaUtil.tickIndexToSqrtPriceX64(tickUpperIndex);
    final PositionStatus status =
        OrcaUtil.positionStatus(currentSqrtPrice, sqrtPriceLower, sqrtPriceUpper);
    switch (status) {
      case BELOW_RANGE: {
        final long tokenA = tokenAFromLiquidity(liquidityDelta, sqrtPriceLower, sqrtPriceUpper, roundUp);
        return new long[]{tokenA, 0L};
      }
      case IN_RANGE: {
        final long tokenA = tokenAFromLiquidity(liquidityDelta, currentSqrtPrice, sqrtPriceUpper, roundUp);
        final long tokenB = tokenBFromLiquidity(liquidityDelta, sqrtPriceLower, currentSqrtPrice, roundUp);
        return new long[]{tokenA, tokenB};
      }
      case ABOVE_RANGE: {
        final long tokenB = tokenBFromLiquidity(liquidityDelta, sqrtPriceLower, sqrtPriceUpper, roundUp);
        return new long[]{0L, tokenB};
      }
      case INVALID:
      default:
        return new long[]{0L, 0L};
    }
  }

  // try_get_token_a_from_liquidity
  private static long tokenAFromLiquidity(final BigInteger liquidityDelta,
                                          final BigInteger sqrtPriceLower,
                                          final BigInteger sqrtPriceUpper,
                                          final boolean roundUp) {
    final BigInteger diff = sqrtPriceUpper.subtract(sqrtPriceLower);
    final BigInteger numerator = liquidityDelta.multiply(diff).shiftLeft(64);
    final BigInteger denominator = sqrtPriceUpper.multiply(sqrtPriceLower);
    final BigInteger[] qr = numerator.divideAndRemainder(denominator);
    BigInteger q = qr[0];
    if (roundUp && qr[1].signum() != 0) {
      q = q.add(BigInteger.ONE);
    }
    return toU64(q);
  }

  // try_get_token_b_from_liquidity
  private static long tokenBFromLiquidity(final BigInteger liquidityDelta,
                                          final BigInteger sqrtPriceLower,
                                          final BigInteger sqrtPriceUpper,
                                          final boolean roundUp) {
    final BigInteger diff = sqrtPriceUpper.subtract(sqrtPriceLower);
    final BigInteger mul = liquidityDelta.multiply(diff);
    BigInteger result = mul.shiftRight(64);
    if (roundUp && mul.and(U64_MAX).signum() > 0) {
      result = result.add(BigInteger.ONE);
    }
    return toU64(result);
  }

  // try_get_liquidity_from_a
  private static BigInteger liquidityFromA(final long tokenDeltaA,
                                           final BigInteger sqrtPriceLower,
                                           final BigInteger sqrtPriceUpper) {
    final BigInteger diff = sqrtPriceUpper.subtract(sqrtPriceLower);
    final BigInteger mul = new BigInteger(Long.toUnsignedString(tokenDeltaA))
        .multiply(sqrtPriceLower)
        .multiply(sqrtPriceUpper);
    return mul.divide(diff).shiftRight(64);
  }

  // try_get_liquidity_from_b
  private static BigInteger liquidityFromB(final long tokenDeltaB,
                                           final BigInteger sqrtPriceLower,
                                           final BigInteger sqrtPriceUpper) {
    final BigInteger numerator = new BigInteger(Long.toUnsignedString(tokenDeltaB)).shiftLeft(64);
    final BigInteger diff = sqrtPriceUpper.subtract(sqrtPriceLower);
    return numerator.divide(diff);
  }

  /// `decrease_liquidity_quote` — quote a liquidity decrease by `liquidityDelta`.
  public static DecreaseLiquidityQuote decreaseLiquidityQuote(final BigInteger liquidityDelta,
                                                              final int slippageToleranceBps,
                                                              final BigInteger currentSqrtPrice,
                                                              final int tickIndex1,
                                                              final int tickIndex2,
                                                              final TransferFee transferFeeA,
                                                              final TransferFee transferFeeB) {
    if (liquidityDelta.signum() == 0) {
      return DecreaseLiquidityQuote.ZERO;
    }
    final int[] range = orderTicks(tickIndex1, tickIndex2);
    final long[] est = tryGetTokenEstimatesFromLiquidity(
        liquidityDelta, currentSqrtPrice, range[0], range[1], false);
    final long tokenEstA = transferFee(est[0], transferFeeA);
    final long tokenEstB = transferFee(est[1], transferFeeB);
    final long tokenMinA = OrcaUtil.minAmountWithSlippageTolerance(tokenEstA, slippageToleranceBps);
    final long tokenMinB = OrcaUtil.minAmountWithSlippageTolerance(tokenEstB, slippageToleranceBps);
    return new DecreaseLiquidityQuote(liquidityDelta, tokenEstA, tokenEstB, tokenMinA, tokenMinB);
  }

  /// `decrease_liquidity_quote_a` — quote a liquidity decrease by a token-A amount.
  public static DecreaseLiquidityQuote decreaseLiquidityQuoteA(final long tokenAmountA,
                                                               final int slippageToleranceBps,
                                                               final BigInteger currentSqrtPrice,
                                                               final int tickIndex1,
                                                               final int tickIndex2,
                                                               final TransferFee transferFeeA,
                                                               final TransferFee transferFeeB) {
    final int[] range = orderTicks(tickIndex1, tickIndex2);
    final long tokenDeltaA = reverseTransferFee(tokenAmountA, transferFeeA);
    if (tokenDeltaA == 0L) {
      return DecreaseLiquidityQuote.ZERO;
    }
    final BigInteger sqrtPriceLower = OrcaUtil.tickIndexToSqrtPriceX64(range[0]);
    final BigInteger sqrtPriceUpper = OrcaUtil.tickIndexToSqrtPriceX64(range[1]);
    final PositionStatus status =
        OrcaUtil.positionStatus(currentSqrtPrice, sqrtPriceLower, sqrtPriceUpper);
    final BigInteger liquidity = switch (status) {
      case BELOW_RANGE -> liquidityFromA(tokenDeltaA, sqrtPriceLower, sqrtPriceUpper);
      case IN_RANGE -> liquidityFromA(tokenDeltaA, currentSqrtPrice, sqrtPriceUpper);
      case INVALID, ABOVE_RANGE -> BigInteger.ZERO;
    };
    return decreaseLiquidityQuote(liquidity, slippageToleranceBps, currentSqrtPrice,
        tickIndex1, tickIndex2, transferFeeA, transferFeeB);
  }

  /// `decrease_liquidity_quote_b` — quote a liquidity decrease by a token-B amount.
  public static DecreaseLiquidityQuote decreaseLiquidityQuoteB(final long tokenAmountB,
                                                               final int slippageToleranceBps,
                                                               final BigInteger currentSqrtPrice,
                                                               final int tickIndex1,
                                                               final int tickIndex2,
                                                               final TransferFee transferFeeA,
                                                               final TransferFee transferFeeB) {
    final int[] range = orderTicks(tickIndex1, tickIndex2);
    final long tokenDeltaB = reverseTransferFee(tokenAmountB, transferFeeB);
    if (tokenDeltaB == 0L) {
      return DecreaseLiquidityQuote.ZERO;
    }
    final BigInteger sqrtPriceLower = OrcaUtil.tickIndexToSqrtPriceX64(range[0]);
    final BigInteger sqrtPriceUpper = OrcaUtil.tickIndexToSqrtPriceX64(range[1]);
    final PositionStatus status =
        OrcaUtil.positionStatus(currentSqrtPrice, sqrtPriceLower, sqrtPriceUpper);
    final BigInteger liquidity = switch (status) {
      case INVALID, BELOW_RANGE -> BigInteger.ZERO;
      case ABOVE_RANGE -> liquidityFromB(tokenDeltaB, sqrtPriceLower, sqrtPriceUpper);
      case IN_RANGE -> liquidityFromB(tokenDeltaB, sqrtPriceLower, currentSqrtPrice);
    };
    return decreaseLiquidityQuote(liquidity, slippageToleranceBps, currentSqrtPrice,
        tickIndex1, tickIndex2, transferFeeA, transferFeeB);
  }

  /// `increase_liquidity_quote` — quote a liquidity increase by `liquidityDelta`.
  public static IncreaseLiquidityQuote increaseLiquidityQuote(final BigInteger liquidityDelta,
                                                              final int slippageToleranceBps,
                                                              final BigInteger currentSqrtPrice,
                                                              final int tickIndex1,
                                                              final int tickIndex2,
                                                              final TransferFee transferFeeA,
                                                              final TransferFee transferFeeB) {
    if (liquidityDelta.signum() == 0) {
      return IncreaseLiquidityQuote.ZERO;
    }
    final int[] range = orderTicks(tickIndex1, tickIndex2);
    final long[] est = tryGetTokenEstimatesFromLiquidity(
        liquidityDelta, currentSqrtPrice, range[0], range[1], true);
    final long tokenEstA = reverseTransferFee(est[0], transferFeeA);
    final long tokenEstB = reverseTransferFee(est[1], transferFeeB);
    final long tokenMaxA = OrcaUtil.maxAmountWithSlippageTolerance(tokenEstA, slippageToleranceBps);
    final long tokenMaxB = OrcaUtil.maxAmountWithSlippageTolerance(tokenEstB, slippageToleranceBps);
    return new IncreaseLiquidityQuote(liquidityDelta, tokenEstA, tokenEstB, tokenMaxA, tokenMaxB);
  }

  /// `increase_liquidity_quote_a` — quote a liquidity increase by a token-A amount.
  public static IncreaseLiquidityQuote increaseLiquidityQuoteA(final long tokenAmountA,
                                                               final int slippageToleranceBps,
                                                               final BigInteger currentSqrtPrice,
                                                               final int tickIndex1,
                                                               final int tickIndex2,
                                                               final TransferFee transferFeeA,
                                                               final TransferFee transferFeeB) {
    final int[] range = orderTicks(tickIndex1, tickIndex2);
    final long tokenDeltaA = transferFee(tokenAmountA, transferFeeA);
    if (tokenDeltaA == 0L) {
      return IncreaseLiquidityQuote.ZERO;
    }
    final BigInteger sqrtPriceLower = OrcaUtil.tickIndexToSqrtPriceX64(range[0]);
    final BigInteger sqrtPriceUpper = OrcaUtil.tickIndexToSqrtPriceX64(range[1]);
    final PositionStatus status =
        OrcaUtil.positionStatus(currentSqrtPrice, sqrtPriceLower, sqrtPriceUpper);
    final BigInteger liquidity = switch (status) {
      case BELOW_RANGE -> liquidityFromA(tokenDeltaA, sqrtPriceLower, sqrtPriceUpper);
      case IN_RANGE -> liquidityFromA(tokenDeltaA, currentSqrtPrice, sqrtPriceUpper);
      case INVALID, ABOVE_RANGE -> BigInteger.ZERO;
    };
    return increaseLiquidityQuote(liquidity, slippageToleranceBps, currentSqrtPrice,
        tickIndex1, tickIndex2, transferFeeA, transferFeeB);
  }

  /// `increase_liquidity_quote_b` — quote a liquidity increase by a token-B amount.
  public static IncreaseLiquidityQuote increaseLiquidityQuoteB(final long tokenAmountB,
                                                               final int slippageToleranceBps,
                                                               final BigInteger currentSqrtPrice,
                                                               final int tickIndex1,
                                                               final int tickIndex2,
                                                               final TransferFee transferFeeA,
                                                               final TransferFee transferFeeB) {
    final int[] range = orderTicks(tickIndex1, tickIndex2);
    final long tokenDeltaB = transferFee(tokenAmountB, transferFeeB);
    if (tokenDeltaB == 0L) {
      return IncreaseLiquidityQuote.ZERO;
    }
    final BigInteger sqrtPriceLower = OrcaUtil.tickIndexToSqrtPriceX64(range[0]);
    final BigInteger sqrtPriceUpper = OrcaUtil.tickIndexToSqrtPriceX64(range[1]);
    final PositionStatus status =
        OrcaUtil.positionStatus(currentSqrtPrice, sqrtPriceLower, sqrtPriceUpper);
    final BigInteger liquidity = switch (status) {
      case INVALID, BELOW_RANGE -> BigInteger.ZERO;
      case ABOVE_RANGE -> liquidityFromB(tokenDeltaB, sqrtPriceLower, sqrtPriceUpper);
      case IN_RANGE -> liquidityFromB(tokenDeltaB, sqrtPriceLower, currentSqrtPrice);
    };
    return increaseLiquidityQuote(liquidity, slippageToleranceBps, currentSqrtPrice,
        tickIndex1, tickIndex2, transferFeeA, transferFeeB);
  }
}

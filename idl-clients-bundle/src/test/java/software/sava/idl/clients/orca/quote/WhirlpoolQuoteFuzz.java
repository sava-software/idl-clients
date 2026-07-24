package software.sava.idl.clients.orca.quote;

import software.sava.idl.clients.orca.OrcaUtil;

import java.math.BigInteger;
import java.util.Arrays;

/// Jazzer entry point for the Whirlpool liquidity-quote math. The bytes are
/// carved into `(liquidity u128, sqrt price in range, two ticks, slippage
/// bps, optional transfer fees)` and driven through the increase and decrease
/// quotes.
///
/// Crash-only fuzzing cannot see a wrong answer, so the harness asserts the
/// money properties on every successful quote:
///
/// - rounding direction: `tokenMin <= tokenEst` on a decrease and
///   `tokenMax >= tokenEst` on an increase (unsigned) — the slippage bound
///   must never be *tighter* than the estimate, or the on-chain guard
///   rejects trades the quote promised;
/// - tick-order invariance: the tick arguments are sorted internally, so the
///   `(t1, t2)` and `(t2, t1)` calls must agree — both succeed with equal
///   quotes, or both reject;
/// - the u64/u128 guards reject with `RuntimeException`; any other throwable
///   is a finding.
///
/// Deliberately has no Jazzer imports so it compiles with the regular test
/// sources; the raw `byte[]` signature is all the driver needs.
///
/// Run with `./gradlew :idl-clients-bundle:fuzzWhirlpoolQuote [-PmaxFuzzTime=<seconds>]`.
public final class WhirlpoolQuoteFuzz {

  private static final BigInteger SQRT_PRICE_RANGE = OrcaUtil.MAX_SQRT_PRICE_X64
      .subtract(OrcaUtil.MIN_SQRT_PRICE_X64)
      .add(BigInteger.ONE);
  private static final int TICK_RANGE = OrcaUtil.MAX_TICK_INDEX - OrcaUtil.MIN_TICK_INDEX + 1;

  public static void fuzzerTestOneInput(final byte[] data) {
    if (data.length < 8) {
      return;
    }
    final byte[] input = Arrays.copyOf(data, 64);

    final var liquidity = new BigInteger(1, Arrays.copyOfRange(input, 0, 16));
    final var sqrtPrice = OrcaUtil.MIN_SQRT_PRICE_X64
        .add(new BigInteger(1, Arrays.copyOfRange(input, 16, 32)).mod(SQRT_PRICE_RANGE));
    final int tick1 = OrcaUtil.MIN_TICK_INDEX + Math.floorMod(readInt(input, 32), TICK_RANGE);
    final int tick2 = OrcaUtil.MIN_TICK_INDEX + Math.floorMod(readInt(input, 36), TICK_RANGE);
    final int slippageBps = Math.floorMod(readShort(input, 40), 10_001);
    final var transferFeeA = (input[42] & 1) == 0 ? null
        : TransferFee.of(Math.floorMod(readShort(input, 43), 10_001), readLong(input, 45));
    final var transferFeeB = (input[42] & 2) == 0 ? null
        : TransferFee.of(Math.floorMod(readShort(input, 53), 10_001), readLong(input, 55));

    final var decrease = quoteDecrease(liquidity, slippageBps, sqrtPrice, tick1, tick2, transferFeeA, transferFeeB);
    final var decreaseSwapped = quoteDecrease(liquidity, slippageBps, sqrtPrice, tick2, tick1, transferFeeA, transferFeeB);
    if (decrease == null ? decreaseSwapped != null : !decrease.equals(decreaseSwapped)) {
      throw new IllegalStateException(String.format(
          "decrease quote is tick-order sensitive: (%d,%d) -> %s but (%d,%d) -> %s",
          tick1, tick2, decrease, tick2, tick1, decreaseSwapped));
    }
    if (decrease != null) {
      expectNotTighter(decrease.tokenMinA(), decrease.tokenEstA(), "decrease tokenMinA > tokenEstA");
      expectNotTighter(decrease.tokenMinB(), decrease.tokenEstB(), "decrease tokenMinB > tokenEstB");
    }

    final var increase = quoteIncrease(liquidity, slippageBps, sqrtPrice, tick1, tick2, transferFeeA, transferFeeB);
    final var increaseSwapped = quoteIncrease(liquidity, slippageBps, sqrtPrice, tick2, tick1, transferFeeA, transferFeeB);
    if (increase == null ? increaseSwapped != null : !increase.equals(increaseSwapped)) {
      throw new IllegalStateException(String.format(
          "increase quote is tick-order sensitive: (%d,%d) -> %s but (%d,%d) -> %s",
          tick1, tick2, increase, tick2, tick1, increaseSwapped));
    }
    if (increase != null) {
      expectNotTighter(increase.tokenEstA(), increase.tokenMaxA(), "increase tokenMaxA < tokenEstA");
      expectNotTighter(increase.tokenEstB(), increase.tokenMaxB(), "increase tokenMaxB < tokenEstB");
    }
  }

  private static DecreaseLiquidityQuote quoteDecrease(final BigInteger liquidity,
                                                      final int slippageBps,
                                                      final BigInteger sqrtPrice,
                                                      final int tick1,
                                                      final int tick2,
                                                      final TransferFee feeA,
                                                      final TransferFee feeB) {
    try {
      return WhirlpoolQuote.decreaseLiquidityQuote(liquidity, slippageBps, sqrtPrice, tick1, tick2, feeA, feeB);
    } catch (final RuntimeException rejected) {
      // u64/u128 overflow rejection is the documented contract
      return null;
    }
  }

  private static IncreaseLiquidityQuote quoteIncrease(final BigInteger liquidity,
                                                      final int slippageBps,
                                                      final BigInteger sqrtPrice,
                                                      final int tick1,
                                                      final int tick2,
                                                      final TransferFee feeA,
                                                      final TransferFee feeB) {
    try {
      return WhirlpoolQuote.increaseLiquidityQuote(liquidity, slippageBps, sqrtPrice, tick1, tick2, feeA, feeB);
    } catch (final RuntimeException rejected) {
      return null;
    }
  }

  private static void expectNotTighter(final long lower, final long upper, final String message) {
    if (Long.compareUnsigned(lower, upper) > 0) {
      throw new IllegalStateException(message + ": " + Long.toUnsignedString(lower)
          + " > " + Long.toUnsignedString(upper));
    }
  }

  private static int readInt(final byte[] data, final int offset) {
    return ((data[offset] & 0xFF) << 24)
        | ((data[offset + 1] & 0xFF) << 16)
        | ((data[offset + 2] & 0xFF) << 8)
        | (data[offset + 3] & 0xFF);
  }

  private static int readShort(final byte[] data, final int offset) {
    return ((data[offset] & 0xFF) << 8) | (data[offset + 1] & 0xFF);
  }

  private static long readLong(final byte[] data, final int offset) {
    long value = 0;
    for (int i = 0; i < 8; i++) {
      value = (value << 8) | (data[offset + i] & 0xFF);
    }
    return value;
  }

  private WhirlpoolQuoteFuzz() {
  }
}

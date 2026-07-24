package software.sava.idl.clients.orca;

import java.math.BigInteger;
import java.util.Arrays;

/// Jazzer entry point hunting the tick-math approximation's error sliver.
///
/// `sqrtPriceX64ToTickIndex` is a 14-bit base-2 log approximation with
/// hand-tuned error margins (`LOG_B_P_ERR_MARGIN_*`), refined by one exact
/// comparison. Its contract — the one swap routing relies on when it picks
/// tick arrays — is the bracketing invariant against the *exact* factor-table
/// forward function:
///
///   tickIndexToSqrtPriceX64(t) <= p < tickIndexToSqrtPriceX64(t + 1)
///
/// The hand-written tests sweep exact tick boundaries and a few fixed offsets
/// between them; this harness searches the full u128 sqrt-price domain for a
/// price the approximation mis-brackets — which is exactly the failure the
/// baseline's accepted-as-untriaged lower-margin mutant would cause, and the
/// input that would settle that acceptance.
///
/// The fuzzer's bytes are folded into a price in
/// `[MIN_SQRT_PRICE_X64, MAX_SQRT_PRICE_X64]`; a leading zero byte instead
/// drives the non-positive-input guard, which must reject with a
/// `RuntimeException` rather than compute a tick from garbage.
///
/// Deliberately has no Jazzer imports so it compiles with the regular test
/// sources; the raw `byte[]` signature is all the driver needs.
///
/// Run with `./gradlew :idl-clients-bundle:fuzzOrcaTickMath [-PmaxFuzzTime=<seconds>]`.
public final class OrcaTickMathFuzz {

  private static final BigInteger RANGE = OrcaUtil.MAX_SQRT_PRICE_X64
      .subtract(OrcaUtil.MIN_SQRT_PRICE_X64)
      .add(BigInteger.ONE);

  public static void fuzzerTestOneInput(final byte[] data) {
    if (data.length == 0) {
      return;
    }
    if (data[0] == 0) {
      // the non-positive guard: zero and negative inputs must be rejected, not
      // silently logged
      final var nonPositive = new BigInteger(data.length > 1 ? Arrays.copyOfRange(data, 1, data.length) : data);
      try {
        OrcaUtil.sqrtPriceX64ToTickIndex(nonPositive.signum() > 0 ? nonPositive.negate() : nonPositive);
        throw new IllegalStateException("a non-positive sqrt price must be rejected");
      } catch (final IllegalArgumentException expected) {
        return;
      }
    }

    final var sqrtPrice = OrcaUtil.MIN_SQRT_PRICE_X64.add(new BigInteger(1, data).mod(RANGE));

    final int tick = OrcaUtil.sqrtPriceX64ToTickIndex(sqrtPrice);
    if (tick < OrcaUtil.MIN_TICK_INDEX || tick > OrcaUtil.MAX_TICK_INDEX) {
      throw new IllegalStateException(String.format(
          "tick %d out of range for in-range sqrt price %s", tick, sqrtPrice));
    }

    final var tickSqrtPrice = OrcaUtil.tickIndexToSqrtPriceX64(tick);
    if (tickSqrtPrice.compareTo(sqrtPrice) > 0) {
      throw new IllegalStateException(String.format(
          "mis-bracketed low: tickIndexToSqrtPriceX64(%d) = %s > %s", tick, tickSqrtPrice, sqrtPrice));
    }
    if (tick < OrcaUtil.MAX_TICK_INDEX) {
      final var nextSqrtPrice = OrcaUtil.tickIndexToSqrtPriceX64(tick + 1);
      if (nextSqrtPrice.compareTo(sqrtPrice) <= 0) {
        throw new IllegalStateException(String.format(
            "mis-bracketed high: tickIndexToSqrtPriceX64(%d) = %s <= %s", tick + 1, nextSqrtPrice, sqrtPrice));
      }
    }
  }

  private OrcaTickMathFuzz() {
  }
}

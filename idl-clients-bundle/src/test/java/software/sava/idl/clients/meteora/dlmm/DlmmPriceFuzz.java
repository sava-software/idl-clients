package software.sava.idl.clients.meteora.dlmm;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

/// Jazzer entry point for the DLMM fixed-point price path — the canonical
/// "fast path beside a reference path" differential. `getPriceFromId` computes
/// `(1 + binStep/10000)^binId` in Q64.64 via 19 binary-expansion steps with
/// per-step truncation; the oracle is the same power computed in `BigDecimal`
/// at 60 digits through `binStepBase`.
///
/// The domain is constrained to `|binId * ln(base)| <= 22`, which keeps the
/// price inside `[~2^32, ~2^96]` so the fixed-point path cannot
/// under/overflow to `null`. The tolerance is *budget-dependent*: `pow`
/// inverts a base >= 1 into a fraction whose intermediates shrink toward
/// `2^64 * e^-budget`, so each truncation costs up to `~e^budget * 2^-64`
/// relative — a fixed tolerance either flaps at high budgets or is vacuous at
/// low ones (found the hard way: `(binStep=511, binId=353)` and
/// `(binStep=8129, binId=36)` sit at 2e-12 and 1.7e-11, matching the Rust
/// reference bit-for-bit — see `truncationEnvelopeAtHighExponentBudgets` in
/// `DlmmUtilsTests`). The bound `max(1e-12, 1000 * e^budget * 2^-64)` tracks
/// the analysis with ~1000x headroom while still catching any wrong-branch
/// error, which is at least a full `1.0001` factor. Two exact checks ride
/// along: determinism, and strict monotonicity between adjacent bins (the
/// ratio is >= 1.0001, which at these magnitudes dwarfs any truncation).
///
/// Deliberately has no Jazzer imports so it compiles with the regular test
/// sources; the raw `byte[]` signature is all the driver needs.
///
/// Run with `./gradlew :idl-clients-bundle:fuzzDlmmPrice [-PmaxFuzzTime=<seconds>]`.
public final class DlmmPriceFuzz {

  private static final MathContext ORACLE_PRECISION = new MathContext(60);
  private static final BigDecimal TWO_POW_64 = new BigDecimal(BigInteger.ONE.shiftLeft(64));
  private static final BigDecimal ABSOLUTE_FLOOR = BigDecimal.valueOf(64L);

  public static void fuzzerTestOneInput(final byte[] data) {
    if (data.length < 7) {
      return;
    }
    final int binStep = 1 + (((data[0] & 0xFF) << 8 | (data[1] & 0xFF)) % 10_000);
    final int rawId = (data[2] & 0xFF) << 24 | (data[3] & 0xFF) << 16 | (data[4] & 0xFF) << 8 | (data[5] & 0xFF);
    // keep |binId * ln(base)| <= 22 so neither path leaves its computable regime
    final double lnBase = Math.log1p(binStep * 1e-4);
    final int maxAbsId = (int) Math.floor(22.0 / lnBase);
    if (maxAbsId == 0) {
      return;
    }
    final int binId = Math.floorMod(rawId, 2 * maxAbsId + 1) - maxAbsId;
    final double budget = Math.abs(binId) * lnBase;
    final var relativeTolerance = BigDecimal.valueOf(Math.max(1e-12, 1e3 * Math.exp(budget) * 0x1p-64));

    final var fixedPoint = DlmmUtils.getPriceFromId(binId, binStep);
    if (fixedPoint == null) {
      throw new IllegalStateException(String.format(
          "in-domain price under/overflowed: binStep=%d binId=%d", binStep, binId));
    }
    if (!fixedPoint.equals(DlmmUtils.getPriceFromId(binId, binStep))) {
      throw new IllegalStateException("getPriceFromId is not deterministic");
    }

    final var oracle = DlmmUtils.binStepBase(binStep)
        .pow(binId, ORACLE_PRECISION)
        .multiply(TWO_POW_64);
    final var diff = new BigDecimal(fixedPoint).subtract(oracle).abs();
    final var bound = oracle.multiply(relativeTolerance).max(ABSOLUTE_FLOOR);
    if (diff.compareTo(bound) > 0) {
      throw new IllegalStateException(String.format(
          "fixed-point price diverged from the BigDecimal oracle: binStep=%d binId=%d fixed=%s oracle=%s diff=%s bound=%s",
          binStep, binId, fixedPoint, oracle.toPlainString(), diff.toPlainString(), bound.toPlainString()));
    }

    // adjacent bins are a >= 1.0001 ratio apart — strictly monotonic at these
    // magnitudes regardless of truncation
    if (binId < maxAbsId) {
      final var next = DlmmUtils.getPriceFromId(binId + 1, binStep);
      if (next != null && next.compareTo(fixedPoint) <= 0) {
        throw new IllegalStateException(String.format(
            "price is not monotonic: binStep=%d price(%d)=%s >= price(%d)=%s",
            binStep, binId, fixedPoint, binId + 1, next));
      }
    }
  }

  private DlmmPriceFuzz() {
  }
}

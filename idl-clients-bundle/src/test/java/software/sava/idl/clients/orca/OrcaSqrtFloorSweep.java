package software.sava.idl.clients.orca;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/// Exhaustive-enough equivalence evidence for the `sqrtFloor-guess family` rows in
/// `config/pitest/orca-accepted.csv`.
///
/// `OrcaUtil.sqrtFloor` seeds Newton's integer square root with `value.shiftRight(1)`. The
/// `EXPERIMENTAL_BIG_INTEGER` mutant seeds with `shiftLeft(1)` and the `NAKED_RECEIVER` mutant
/// drops the shift, seeding with `value` itself. PIT reports both SURVIVED and the baseline
/// accepts them: the iteration `next = (prev + value / prev) / 2` descends monotonically to
/// `floor(sqrt(value))` from any seed at or above the true root, and `v/2`, `2v` and `v` all
/// qualify for `v >= 2` (`v < 2` returns early). Only the iteration count changes.
///
/// Until 2026-08-15 this was reasoned in `config/pitest/README.md` and backed by numbers from a
/// sweep that was run once in a session and never committed — 200,490 inputs, then 122,765 more,
/// with the reference being Python's `math.isqrt`. The input sets are reconstructed here from that
/// description and the reference is `BigInteger.sqrt()`, so the claim now has something that
/// re-derives it rather than a sentence asserting someone once did.
///
/// Named outside `*Test*` for the same reason as [OrcaTickMarginSweep]: `check` runs it, the
/// `orca` mutation suite does not re-run it per mutant.
final class OrcaSqrtFloorSweep {

  private static final BigInteger TWO = BigInteger.valueOf(2L);

  /// The production algorithm with the seed left open, so all three variants are the same code
  /// path differing only where PIT differs them. Deliberately not a call into `OrcaUtil` — that
  /// method is one of the three, and comparing it against itself proves nothing.
  private static BigInteger newtonSqrt(final BigInteger value, final Seed seed) {
    if (value.signum() < 0) {
      throw new IllegalArgumentException("sqrt of negative value: " + value);
    }
    if (value.compareTo(TWO) < 0) {
      return value;
    }
    var prev = seed.of(value);
    var next = prev.add(value.divide(prev)).shiftRight(1);
    while (next.compareTo(prev) < 0) {
      prev = next;
      next = prev.add(value.divide(prev)).shiftRight(1);
    }
    return prev;
  }

  @FunctionalInterface
  private interface Seed {

    BigInteger of(BigInteger value);
  }

  private static final Seed HALVED = v -> v.shiftRight(1);        // production
  private static final Seed DOUBLED = v -> v.shiftLeft(1);        // EXPERIMENTAL_BIG_INTEGER
  private static final Seed UNSHIFTED = v -> v;                   // NAKED_RECEIVER

  /// Every value below 200,000, plus `2^e ± 3` for e in 60..129 — 200,490 inputs, the set the
  /// original sweep recorded.
  private static List<BigInteger> denseAndBoundary() {
    final var out = new ArrayList<BigInteger>(200_490);
    for (int v = 0; v < 200_000; ++v) {
      out.add(BigInteger.valueOf(v));
    }
    for (int e = 60; e <= 129; ++e) {
      final var pow = BigInteger.ONE.shiftLeft(e);
      for (int d = -3; d <= 3; ++d) {
        out.add(pow.add(BigInteger.valueOf(d)));
      }
    }
    return out;
  }

  /// 0..1999, `2^k ± 1` for k in 2..256, and 120k pseudo-random values up to 256 bits — 122,765
  /// cases. Seeded, because a sweep whose inputs change per run cannot be the evidence for a
  /// committed baseline row.
  private static List<BigInteger> wideAndRandom() {
    final var out = new ArrayList<BigInteger>(122_765);
    for (int v = 0; v < 2_000; ++v) {
      out.add(BigInteger.valueOf(v));
    }
    for (int k = 2; k <= 256; ++k) {
      final var pow = BigInteger.ONE.shiftLeft(k);
      out.add(pow.subtract(BigInteger.ONE));
      out.add(pow);
      out.add(pow.add(BigInteger.ONE));
    }
    final var random = new Random(0x5EEDL);
    for (int i = 0; i < 120_000; ++i) {
      out.add(new BigInteger(1 + random.nextInt(256), random));
    }
    return out;
  }

  /// The input sets are the claim's provenance, so their sizes are pinned: a silently smaller
  /// sweep would still pass and would still read as evidence.
  @Test
  void theRecordedInputSetsAreTheOnesBeingSwept() {
    assertEquals(200_490, denseAndBoundary().size(), "every value below 200,000 plus 2^e ± 3, e in 60..129");
    assertEquals(122_765, wideAndRandom().size(), "0..1999, 2^k ± 1 for k in 2..256, and 120k random");
    // Distinctness matters for the boundary sets; the random tail may legitimately collide.
    assertEquals(200_490, new LinkedHashSet<>(denseAndBoundary()).size(), "no duplicate boundary inputs");
  }

  /// All three seeds resolve to the same root, which is what the accepted rows assert.
  ///
  /// A failure here retires the `sqrtFloor-guess family` rows from `orca-accepted.csv`.
  @Test
  void everySeedReachesTheSameRoot() {
    final var differences = new ArrayList<String>();
    for (final var v : denseAndBoundary()) {
      final var halved = newtonSqrt(v, HALVED);
      final var doubled = newtonSqrt(v, DOUBLED);
      final var unshifted = newtonSqrt(v, UNSHIFTED);
      if (!halved.equals(doubled) || !halved.equals(unshifted)) {
        differences.add("v=" + v + " halved=" + halved + " doubled=" + doubled + " unshifted=" + unshifted);
      }
    }
    for (final var v : wideAndRandom()) {
      final var halved = newtonSqrt(v, HALVED);
      if (!halved.equals(newtonSqrt(v, UNSHIFTED))) {
        differences.add("v=" + v + " halved=" + halved + " unshifted=" + newtonSqrt(v, UNSHIFTED));
      }
    }
    assertTrue(differences.isEmpty(),
        () -> differences.size() + " inputs separate the seeds; the accepted sqrtFloor-guess "
            + "mutants are no longer equivalent:\n"
            + String.join("\n", differences.subList(0, Math.min(10, differences.size()))));
  }

  /// Equivalent to each other is not the same as correct, and an accepted mutant is only harmless
  /// while the thing it agrees with is right.
  @Test
  void everySeedReachesTheTrueIntegerRoot() {
    for (final var v : denseAndBoundary()) {
      assertEquals(v.sqrt(), newtonSqrt(v, HALVED), () -> "floor(sqrt(" + v + "))");
    }
    for (final var v : wideAndRandom()) {
      assertEquals(v.sqrt(), newtonSqrt(v, UNSHIFTED), () -> "floor(sqrt(" + v + "))");
    }
  }

  /// The reimplementation above has to stay the shipped algorithm, or it is evidence about code
  /// that no longer exists. This is the only assertion that calls `OrcaUtil`.
  @Test
  void theReimplementationMatchesTheProduction() {
    for (final var v : denseAndBoundary()) {
      if (!OrcaUtil.sqrtFloor(v).equals(newtonSqrt(v, HALVED))) {
        throw new AssertionError("the sweep's Newton iteration diverges from OrcaUtil.sqrtFloor at " + v);
      }
    }
  }
}

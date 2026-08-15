package software.sava.idl.clients.orca;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/// Exhaustive equivalence evidence for the two accepted-baseline mutant families on
/// `OrcaUtil.sqrtPriceX64ToTickIndex` — the `log-margin family` rows in
/// `config/pitest/orca-accepted.csv`.
///
/// **The subject is three variants of the production method, none of which exists in the
/// codebase.** `sqrtPriceX64ToTickIndex` computes `tickLow` as `x - LOG_B_P_ERR_MARGIN_LOWER_X64`;
/// the `EXPERIMENTAL_BIG_INTEGER` mutant adds instead, and the `NAKED_RECEIVER` mutant drops the
/// subtraction. PIT reports both as SURVIVED, the baseline accepts them, and this is the evidence
/// for that acceptance. Rewriting the comparison to call `OrcaUtil.sqrtPriceX64ToTickIndex` would
/// make it a tautology that justifies nothing — the ladder and the approximation below are a
/// deliberate second implementation, and [#theMirrorStillMatchesTheProduction] is what keeps them
/// honest without collapsing them together.
///
/// The analysis: all three variants change only `tickLow`, and with `frac(x) < 0.01` the fast-path
/// collapse `tickLow == tickHigh == floor(x)` is the only reachable divergence — it returns
/// `floor(x)` where the refinement would return `floor(x) - 1`. That needs the 14-bit log
/// approximation to *overshoot*: some price p below the tick-k boundary with `x(p) >= k`. Because
/// `x(p)` is weakly monotone in p, overshoot at boundary k is equivalent to
/// `x(sqrtPrice(k) - 1) >= k`, so one evaluation per boundary is an exhaustive search.
///
/// Named outside `*Test*` on purpose. JUnit discovers it by annotation so `check` runs it, but the
/// `orca` mutation suite selects tests by `software.sava.idl.clients.orca.*Test*`, so PIT does not
/// re-run this sweep against every mutant — which would both cost minutes and inflate the timeout
/// budget that `orca-timeouts.csv` already records a liveness kill against.
final class OrcaTickMarginSweep {

  private static final BigInteger U128 = BigInteger.ONE.shiftLeft(128).subtract(BigInteger.ONE);

  private static final BigInteger POS_BASE_EVEN = new BigInteger("79228162514264337593543950336");
  private static final BigInteger POS_BASE_ODD = new BigInteger("79232123823359799118286999567");
  private static final BigInteger[] POS_FACTORS = factors(
      "79236085330515764027303304731", "79244008939048815603706035061",
      "79259858533276714757314932305", "79291567232598584799939703904",
      "79355022692464371645785046466", "79482085999252804386437311141",
      "79736823300114093921829183326", "80248749790819932309965073892",
      "81282483887344747381513967011", "83390072131320151908154831281",
      "87770609709833776024991924138", "97234110755111693312479820773",
      "119332217159966728226237229890", "179736315981702064433883588727",
      "407748233172238350107850275304", "2098478828474011932436660412517",
      "55581415166113811149459800483533", "38992368544603139932233054999993551");

  private static final BigInteger NEG_BASE_EVEN = new BigInteger("18446744073709551616");
  private static final BigInteger NEG_BASE_ODD = new BigInteger("18445821805675392311");
  private static final BigInteger[] NEG_FACTORS = factors(
      "18444899583751176498", "18443055278223354162", "18439367220385604838",
      "18431993317065449817", "18417254355718160513", "18387811781193591352",
      "18329067761203520168", "18212142134806087854", "17980523815641551639",
      "17526086738831147013", "16651378430235024244", "15030750278693429944",
      "12247334978882834399", "8131365268884726200", "3584323654723342297",
      "696457651847595233", "26294789957452057", "37481735321082");

  private static final BigInteger LOG_B_2_X32 = BigInteger.valueOf(59_543_866_431_248L);
  private static final BigInteger LOWER = BigInteger.valueOf(184_467_440_737_095_516L);
  private static final BigInteger UPPER = new BigInteger("15793534762490258745");
  private static final int BIT_PRECISION = 14;

  private static BigInteger[] factors(final String... values) {
    final var out = new BigInteger[values.length];
    for (int i = 0; i < values.length; ++i) {
      out[i] = new BigInteger(values[i]);
    }
    return out;
  }

  /// The exact factor-table forward function, mirrored rather than called.
  private static BigInteger sqrtPrice(final long tick) {
    if (tick >= 0) {
      var ratio = (tick & 1) != 0 ? POS_BASE_ODD : POS_BASE_EVEN;
      for (int i = 0; i < POS_FACTORS.length; ++i) {
        if ((tick & (2L << i)) != 0) {
          ratio = ratio.multiply(POS_FACTORS[i]).shiftRight(96).and(U128);
        }
      }
      return ratio.shiftRight(32);
    }
    final long a = -tick;
    var ratio = (a & 1) != 0 ? NEG_BASE_ODD : NEG_BASE_EVEN;
    for (int i = 0; i < NEG_FACTORS.length; ++i) {
      if ((a & (2L << i)) != 0) {
        ratio = ratio.multiply(NEG_FACTORS[i]).shiftRight(64).and(U128);
      }
    }
    return ratio;
  }

  /// The 14-bit base-2 log approximation, scaled to base b and Q64.64.
  private static BigInteger logbpX64(final BigInteger p) {
    final int msb = p.bitLength() - 1;
    final var intX32 = BigInteger.valueOf(msb - 64L).shiftLeft(32);
    var r = msb >= 64 ? p.shiftRight(msb - 63) : p.shiftLeft(63 - msb);
    var bit = BigInteger.ONE.shiftLeft(63);
    var frac = BigInteger.ZERO;
    for (int precision = 0; bit.signum() > 0 && precision < BIT_PRECISION; ++precision) {
      r = r.multiply(r);
      final int is2 = r.testBit(127) ? 1 : 0;
      r = r.shiftRight(63 + is2);
      if (is2 == 1) {
        frac = frac.add(bit);
      }
      bit = bit.shiftRight(1);
    }
    return intX32.add(frac.shiftRight(32)).multiply(LOG_B_2_X32);
  }

  /// The resolved tick under each of the three `tickLow` seeds: original, the NAKED_RECEIVER
  /// mutant (no margin), and the BIG_INTEGER mutant (margin added rather than subtracted).
  private static BigInteger[] variants(final BigInteger p) {
    final var x = logbpX64(p);
    final var th = x.add(UPPER).shiftRight(64);
    final var seeds = new BigInteger[]{
        x.subtract(LOWER).shiftRight(64),
        x.shiftRight(64),
        x.add(LOWER).shiftRight(64),
    };
    final var out = new BigInteger[seeds.length];
    for (int i = 0; i < seeds.length; ++i) {
      out[i] = seeds[i].equals(th) ? seeds[i]
          : (sqrtPrice(th.longValueExact()).compareTo(p) <= 0 ? th : seeds[i]);
    }
    return out;
  }

  /// Every tick boundary in the valid domain, which is the whole search: an overshoot anywhere is
  /// an overshoot at some `sqrtPrice(k) - 1`, and there is nowhere else for one to hide.
  ///
  /// Zero overshoots as of 2026-07-23, unchanged when this moved out of `tools/` on 2026-08-15.
  /// A failure here retires the `log-margin family` rows from `orca-accepted.csv` — the mutants
  /// would no longer be equivalent, and PIT reporting them SURVIVED would be a real gap.
  ///
  /// **How close this is.** "Zero overshoots" is a bit; the margin behind it is the number worth
  /// watching, and it is thin. The tightest boundary clears by **34,045,085,876,224** in Q64.64 —
  /// 0.0000018 ticks — at k=283,388, measured 2026-08-15. That is one thirty-fourth of the log
  /// approximation's own quantum (`LOG_B_2_X32`, 59,543,866,431,248): biasing `logbpX64` up by a
  /// single unit of that quantum puts two boundaries over the line. The headroom is reported on
  /// every run so a shrinking margin is visible before it reaches zero rather than after.
  @Test
  void theApproximationNeverOvershootsATickBoundary() {
    final var overshoots = new ArrayList<String>();
    final var nonMonotonic = new ArrayList<Integer>();
    BigInteger previous = null;
    BigInteger tightest = null;
    int tightestAt = 0;
    for (int k = OrcaUtil.MIN_TICK_INDEX + 1; k <= OrcaUtil.MAX_TICK_INDEX; ++k) {
      final var p = sqrtPrice(k).subtract(BigInteger.ONE);
      final var x = logbpX64(p);
      final var headroom = BigInteger.valueOf(k).shiftLeft(64).subtract(x);
      if (tightest == null || headroom.compareTo(tightest) < 0) {
        tightest = headroom;
        tightestAt = k;
      }
      if (headroom.signum() <= 0) {
        final var v = variants(p);
        overshoots.add("k=" + k + " p=" + p
            + " original=" + v[0] + " naked=" + v[1] + " add=" + v[2]);
      }
      if (previous != null && x.compareTo(previous) < 0) {
        nonMonotonic.add(k);
      }
      previous = x;
    }
    System.out.println("tightest headroom " + tightest + " (Q64.64) at k=" + tightestAt);
    assertTrue(overshoots.isEmpty(),
        () -> overshoots.size() + " boundaries overshoot; the accepted log-margin mutants are no "
            + "longer equivalent:\n" + String.join("\n", overshoots.subList(0, Math.min(10, overshoots.size()))));
    // Monotonicity is what makes one sample per boundary exhaustive rather than a spot check.
    assertTrue(nonMonotonic.isEmpty(),
        () -> "x(p) is not monotone at " + nonMonotonic.size() + " boundaries, so one evaluation "
            + "per boundary is no longer an exhaustive search: " + nonMonotonic.subList(0, Math.min(5, nonMonotonic.size())));
  }

  /// The mirror above is only evidence while it is still a mirror.
  ///
  /// This is the one assertion that reaches into `OrcaUtil`, and it reaches for the *forward*
  /// function, not the one under analysis. If the factor tables or the Q64.64 scaling move, the
  /// sweep must move with them or it is answering a question about code that no longer ships.
  @Test
  void theMirrorStillMatchesTheProduction() {
    assertEquals(OrcaUtil.MIN_SQRT_PRICE_X64, sqrtPrice(OrcaUtil.MIN_TICK_INDEX));
    assertEquals(OrcaUtil.MAX_SQRT_PRICE_X64, sqrtPrice(OrcaUtil.MAX_TICK_INDEX));
    for (int k = OrcaUtil.MIN_TICK_INDEX; k <= OrcaUtil.MAX_TICK_INDEX; ++k) {
      if (!sqrtPrice(k).equals(OrcaUtil.tickIndexToSqrtPriceX64(k))) {
        throw new AssertionError("the sweep's ladder diverges from OrcaUtil at tick " + k);
      }
    }
  }
}

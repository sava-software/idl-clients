package software.sava.idl.clients.orca;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/// Equivalence evidence for the accepted `log-margin family` row on
/// `OrcaUtil.sqrtPriceX64ToTickIndex` — and the counter-evidence that removed its sibling.
///
/// Two mutants seed `tickLow` differently. `NAKED_RECEIVER` drops the margin
/// (`tickLow = floor(x)`); `EXPERIMENTAL_BIG_INTEGER` adds it rather than subtracting
/// (`floor(x + 0.01)`). They are **not** the same case, and treating them as one is what kept a
/// behaviour-changing mutant accepted until 2026-08-15:
///
///   - naked needs the approximation to *overshoot* a boundary — `x(p) >= k` for some p below the
///     tick-k boundary. That never happens, so it is equivalent and stays accepted.
///   - add needs only that `x` lands within the margin below a boundary. That happens at 10,452 of
///     the 887,272 boundaries, so it is not equivalent. It is out of the baseline and killed by
///     `OrcaUtilTests.theLowerMarginMustBeSubtractedNotAdded`.
///
/// **What is shared with production and what is not.** The reverse log and both margins come from
/// `OrcaUtil` — the mutants are mutations of *that* code, so seeding from a copy would prove
/// something about the copy. It is not a tautology: the refinement absorbs a shifted log, so
/// `sqrtPriceX64ToTickIndex` keeps returning the right tick while the naked mutant quietly becomes
/// behavioural, and a sweep reading its own copy reports zero divergences throughout. The *forward*
/// ladder below stays an independent mirror, because it is the oracle the refinement consults, and
/// [#theMirrorStillMatchesTheProduction] holds it to `tickIndexToSqrtPriceX64`.
///
/// Named outside `*Test*` on purpose. JUnit discovers it by annotation so `check` runs it, but the
/// `orca` mutation suite selects tests by `software.sava.idl.clients.orca.*Test*`, so PIT does not
/// re-run this sweep against every mutant — which would both cost minutes and inflate the timeout
/// budget that `orca-timeouts.csv` already records a liveness kill against. It can describe a
/// divergence; it can never kill anything.
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

  private static BigInteger[] factors(final String... values) {
    final var out = new BigInteger[values.length];
    for (int i = 0; i < values.length; ++i) {
      out[i] = new BigInteger(values[i]);
    }
    return out;
  }

  /// The exact factor-table forward function, and the only thing here that is a mirror rather
  /// than a call: it is the oracle the refinement consults, so it has to be independent.
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

  /// The resolved tick under each of the three `tickLow` seeds: original, the NAKED_RECEIVER
  /// mutant (no margin), and the BIG_INTEGER mutant (margin added rather than subtracted).
  private static BigInteger[] variants(final BigInteger p) {
    final var x = OrcaUtil.logbpX64(p);
    final var th = x.add(OrcaUtil.LOG_B_P_ERR_MARGIN_UPPER_X64).shiftRight(64);
    final var seeds = new BigInteger[]{
        x.subtract(OrcaUtil.LOG_B_P_ERR_MARGIN_LOWER_X64).shiftRight(64),
        x.shiftRight(64),
        x.add(OrcaUtil.LOG_B_P_ERR_MARGIN_LOWER_X64).shiftRight(64),
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
  ///
  /// **Overshoot is the naked-receiver mutant's condition, and only its.** This test says nothing
  /// about the subtract-to-add sibling, which diverges on a far weaker condition and is checked by
  /// [#onlyTheNakedReceiverVariantAgreesAtEveryBoundary]. Reading "zero overshoots" as clearing both is the mistake
  /// that kept a behaviour-changing mutant in the accepted baseline until 2026-08-15.
  @Test
  void theApproximationNeverOvershootsATickBoundary() {
    final var overshoots = new ArrayList<String>();
    final var nonMonotonic = new ArrayList<Integer>();
    BigInteger previous = null;
    BigInteger tightest = null;
    int tightestAt = 0;
    for (int k = OrcaUtil.MIN_TICK_INDEX + 1; k <= OrcaUtil.MAX_TICK_INDEX; ++k) {
      final var p = sqrtPrice(k).subtract(BigInteger.ONE);
      final var x = OrcaUtil.logbpX64(p);
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

  /// What the equivalence claim actually needs: run all three variants at every boundary and
  /// compare the resolved ticks, rather than inferring agreement from a condition only one of them
  /// depends on. The name is the finding — one of the two survives the comparison, not both.
  ///
  /// The naked-receiver mutant agrees everywhere. The subtract-to-add mutant does not, and this is
  /// the assertion that says so: it disagrees at **10,452** of the 887,272 boundaries, first at
  /// p=5,042,765,844 (correct tick -440,427, mutant -440,426) and last at
  /// p=79,214,790,999,700,809,360,952,498,414 (443,632 against 443,633). Adding the margin lifts
  /// `tickLow` onto `tickHigh` wherever the approximation lands inside it, and the resulting
  /// equal-estimates fast return skips the refinement that would have stepped back down.
  ///
  /// So this expects exactly one surviving equivalence, not three. The mutant that is not
  /// equivalent is killed by `OrcaUtilTests.theLowerMarginMustBeSubtractedNotAdded`, which is
  /// inside the suite's `targetTests` where PIT can see it — this sweep is not, by design, so it
  /// can describe the divergence but can never kill anything on its own.
  @Test
  void onlyTheNakedReceiverVariantAgreesAtEveryBoundary() {
    int nakedDiverges = 0;
    int addDiverges = 0;
    for (int k = OrcaUtil.MIN_TICK_INDEX + 1; k <= OrcaUtil.MAX_TICK_INDEX; ++k) {
      final var resolved = variants(sqrtPrice(k).subtract(BigInteger.ONE));
      if (!resolved[0].equals(resolved[1])) {
        ++nakedDiverges;
      }
      if (!resolved[0].equals(resolved[2])) {
        ++addDiverges;
      }
    }
    assertEquals(0, nakedDiverges,
        "dropping the lower margin altogether resolves a different tick, so the accepted "
            + "NakedReceiverMutator row in orca-accepted.csv is no longer equivalent");
    assertEquals(10_452, addDiverges,
        "the count of boundaries where adding the lower margin instead of subtracting it resolves "
            + "the tick above the price's own — 0 would mean the mutant became equivalent and "
            + "theLowerMarginMustBeSubtractedNotAdded can no longer kill it");
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

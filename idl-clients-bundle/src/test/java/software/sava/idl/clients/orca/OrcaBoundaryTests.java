package software.sava.idl.clients.orca;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.idl.clients.orca.whirlpools.gen.types.AccountsType;
import software.sava.idl.clients.orca.quote.TransferFee;
import software.sava.idl.clients.orca.quote.WhirlpoolQuote;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/// Boundary cases for the money-math guards: the happy paths prove the right
/// number comes back, these prove the guards actually fire — and that the
/// largest legal value is still accepted on each side of every bound.
final class OrcaBoundaryTests {

  private static final BigInteger U64_MAX = BigInteger.ONE.shiftLeft(64).subtract(BigInteger.ONE);

  private static PublicKey key(final int fill) {
    final byte[] publicKey = new byte[PublicKey.PUBLIC_KEY_LENGTH];
    Arrays.fill(publicKey, (byte) fill);
    return PublicKey.createPubKey(publicKey);
  }

  // ---------------------------------------------------------------------------
  // u64 range: the maximum is legal, one past it is not
  // ---------------------------------------------------------------------------

  /// `-1L` is u64::MAX. With a zero max fee nothing is deducted, so the full
  /// u64 range must round-trip rather than being rejected as negative.
  @Test
  void u64MaxSurvivesTransferFeeMath() {
    assertEquals(-1L, OrcaUtil.applyTransferFee(-1L, 1, 0L));

    // the reverse direction overflows u64 when the cap is added at the top
    assertThrows(ArithmeticException.class,
        () -> OrcaUtil.reverseApplyTransferFee(-1L, OrcaUtil.BPS_DENOMINATOR, 1L));
  }

  @Test
  void feeBpsRangeIsGuardedOnBothSides() {
    assertThrows(IllegalArgumentException.class, () -> OrcaUtil.applyTransferFee(100L, -1, 0L));
    assertThrows(IllegalArgumentException.class, () -> OrcaUtil.applyTransferFee(100L, OrcaUtil.BPS_DENOMINATOR + 1, 0L));
    assertThrows(IllegalArgumentException.class, () -> OrcaUtil.reverseApplyTransferFee(100L, -1, 0L));
    assertThrows(IllegalArgumentException.class, () -> OrcaUtil.reverseApplyTransferFee(100L, OrcaUtil.BPS_DENOMINATOR + 1, 0L));
    // both extremes are legal
    assertEquals(100L, OrcaUtil.applyTransferFee(100L, 0, 0L));
    assertEquals(0L, OrcaUtil.applyTransferFee(100L, OrcaUtil.BPS_DENOMINATOR, -1L));
  }

  // ---------------------------------------------------------------------------
  // position status: exact-boundary prices
  // ---------------------------------------------------------------------------

  /// The range check is inclusive-below / exclusive-above on the lower bound:
  /// a price exactly on the lower sqrt-price is BELOW_RANGE, exactly on the
  /// upper is ABOVE_RANGE. An off-by-one here misprices a position as
  /// in-range at its own boundary.
  @Test
  void positionStatusAtTheExactBounds() {
    final var lower = BigInteger.valueOf(1_000L);
    final var upper = BigInteger.valueOf(2_000L);

    assertEquals(OrcaUtil.PositionStatus.BELOW_RANGE, OrcaUtil.positionStatus(lower, lower, upper));
    assertEquals(OrcaUtil.PositionStatus.ABOVE_RANGE, OrcaUtil.positionStatus(upper, lower, upper));
    assertEquals(OrcaUtil.PositionStatus.IN_RANGE, OrcaUtil.positionStatus(lower.add(BigInteger.ONE), lower, upper));
    assertEquals(OrcaUtil.PositionStatus.BELOW_RANGE, OrcaUtil.positionStatus(lower.subtract(BigInteger.ONE), lower, upper));
    assertEquals(OrcaUtil.PositionStatus.INVALID, OrcaUtil.positionStatus(lower, lower, lower));
    // reversed bounds are normalized, not misclassified
    assertEquals(OrcaUtil.PositionStatus.IN_RANGE, OrcaUtil.positionStatus(lower.add(BigInteger.ONE), upper, lower));
  }

  // ---------------------------------------------------------------------------
  // sqrt price <-> tick index
  // ---------------------------------------------------------------------------

  @Test
  void sqrtPriceToTickRejectsNonPositive() {
    assertThrows(IllegalArgumentException.class, () -> OrcaUtil.sqrtPriceX64ToTickIndex(BigInteger.ZERO));
    assertThrows(IllegalArgumentException.class, () -> OrcaUtil.sqrtPriceX64ToTickIndex(BigInteger.valueOf(-1L)));
  }

  /// Round trip at and around the extremes, plus tick zero where the positive
  /// and negative conversion paths meet.
  @Test
  void tickSqrtPriceRoundTrips() {
    assertEquals(BigInteger.ONE.shiftLeft(64), OrcaUtil.tickIndexToSqrtPriceX64(0), "tick 0 is price 1.0 in Q64.64");
    for (final int tick : new int[]{
        OrcaUtil.MIN_TICK_INDEX, OrcaUtil.MIN_TICK_INDEX + 1, -443_635, -65_536, -1,
        0, 1, 65_536, OrcaUtil.MAX_TICK_INDEX - 1, OrcaUtil.MAX_TICK_INDEX
    }) {
      final var sqrtPrice = OrcaUtil.tickIndexToSqrtPriceX64(tick);
      assertEquals(tick, OrcaUtil.sqrtPriceX64ToTickIndex(sqrtPrice), "tick " + tick);
    }

    // the checked variant accepts the extremes and rejects one past them
    assertEquals(OrcaUtil.tickIndexToSqrtPriceX64(OrcaUtil.MAX_TICK_INDEX),
        OrcaUtil.tickIndexToSqrtPriceX64Checked(OrcaUtil.MAX_TICK_INDEX));
    assertThrows(IllegalArgumentException.class,
        () -> OrcaUtil.tickIndexToSqrtPriceX64Checked(OrcaUtil.MAX_TICK_INDEX + 1));
    assertThrows(IllegalArgumentException.class,
        () -> OrcaUtil.tickIndexToSqrtPriceX64Checked(OrcaUtil.MIN_TICK_INDEX - 1));
  }

  @Test
  void boundTickIndexClampsBothEnds() {
    assertEquals(OrcaUtil.MIN_TICK_INDEX, OrcaUtil.boundTickIndex(OrcaUtil.MIN_TICK_INDEX - 1));
    assertEquals(OrcaUtil.MIN_TICK_INDEX, OrcaUtil.boundTickIndex(OrcaUtil.MIN_TICK_INDEX));
    assertEquals(0, OrcaUtil.boundTickIndex(0));
    assertEquals(OrcaUtil.MAX_TICK_INDEX, OrcaUtil.boundTickIndex(OrcaUtil.MAX_TICK_INDEX));
    assertEquals(OrcaUtil.MAX_TICK_INDEX, OrcaUtil.boundTickIndex(OrcaUtil.MAX_TICK_INDEX + 1));
  }

  /// A negative price square-roots to NaN, which must clamp to ZERO rather
  /// than reaching the BigDecimal conversion.
  @Test
  void priceToSqrtPriceClampsNonPositive() {
    assertEquals(BigInteger.ZERO, OrcaUtil.priceToSqrtPriceX64(-1.0, 0, 0));
    assertEquals(BigInteger.ZERO, OrcaUtil.priceToSqrtPriceX64(0.0, 0, 0));
    assertTrue(OrcaUtil.priceToSqrtPriceX64(1.0, 0, 0).signum() > 0);
  }

  // ---------------------------------------------------------------------------
  // swap-step price moves
  // ---------------------------------------------------------------------------

  /// A zero amount is a fast path that returns the current price *unvalidated*
  /// — mirroring the Rust, which only bounds-checks computed prices.
  @Test
  void zeroAmountReturnsTheCurrentPriceUnvalidated() {
    // 1 is far below MIN_SQRT_PRICE_X64: only the fast path can return it
    final var belowBounds = BigInteger.ONE;
    final var liquidity = BigInteger.valueOf(1_000_000L);
    assertEquals(belowBounds, OrcaUtil.tryGetNextSqrtPriceFromA(belowBounds, liquidity, 0L, true));
    assertEquals(belowBounds, OrcaUtil.tryGetNextSqrtPriceFromB(belowBounds, liquidity, 0L, true));
  }

  @Test
  void nextSqrtPriceFromBRejectsZeroLiquidity() {
    assertThrows(ArithmeticException.class,
        () -> OrcaUtil.tryGetNextSqrtPriceFromB(BigInteger.ONE.shiftLeft(64), BigInteger.ZERO, 5L, true));
  }

  // ---------------------------------------------------------------------------
  // liquidity estimates
  // ---------------------------------------------------------------------------

  /// An estimate exceeding u64 must be rejected, not silently truncated.
  @Test
  void tokenEstimatesRejectAmountsBeyondU64() {
    final var hugeLiquidity = BigInteger.ONE.shiftLeft(100);
    final var current = OrcaUtil.tickIndexToSqrtPriceX64(0);
    assertThrows(ArithmeticException.class,
        () -> WhirlpoolQuote.tryGetTokenEstimatesFromLiquidity(hugeLiquidity, current, -100, 100, false));
  }

  @Test
  void zeroLiquidityEstimatesToZero() {
    final var current = OrcaUtil.tickIndexToSqrtPriceX64(0);
    assertArrayEquals(new long[]{0L, 0L},
        WhirlpoolQuote.tryGetTokenEstimatesFromLiquidity(BigInteger.ZERO, current, -100, 100, false));
  }

  /// The three range cases produce token amounts on the correct side.
  @Test
  void tokenEstimatesFollowThePositionStatus() {
    final var liquidity = BigInteger.valueOf(1_000_000_000L);
    final var below = OrcaUtil.tickIndexToSqrtPriceX64(-200);
    final var inRange = OrcaUtil.tickIndexToSqrtPriceX64(0);
    final var above = OrcaUtil.tickIndexToSqrtPriceX64(200);

    final long[] belowAmounts = WhirlpoolQuote.tryGetTokenEstimatesFromLiquidity(liquidity, below, -100, 100, false);
    assertTrue(belowAmounts[0] > 0L, "below range is all token A");
    assertEquals(0L, belowAmounts[1]);

    final long[] inAmounts = WhirlpoolQuote.tryGetTokenEstimatesFromLiquidity(liquidity, inRange, -100, 100, false);
    assertTrue(inAmounts[0] > 0L, "in range holds both");
    assertTrue(inAmounts[1] > 0L, "in range holds both");

    final long[] aboveAmounts = WhirlpoolQuote.tryGetTokenEstimatesFromLiquidity(liquidity, above, -100, 100, false);
    assertEquals(0L, aboveAmounts[0]);
    assertTrue(aboveAmounts[1] > 0L, "above range is all token B");

    // rounding up never yields less
    final long[] roundedUp = WhirlpoolQuote.tryGetTokenEstimatesFromLiquidity(liquidity, inRange, -100, 100, true);
    assertTrue(Long.compareUnsigned(roundedUp[0], inAmounts[0]) >= 0);
    assertTrue(Long.compareUnsigned(roundedUp[1], inAmounts[1]) >= 0);
  }

  // ---------------------------------------------------------------------------
  // remaining accounts builder
  // ---------------------------------------------------------------------------

  @Test
  void remainingAccountsBuilderAssemblesSlicesInOrder() {
    final var hookProgram = key(1);
    final var metasPda = key(2);
    final var extra = key(3);
    final var tickArray1 = key(4);
    final var tickArray2 = key(5);

    final var remaining = WhirlpoolRemainingAccounts.builder()
        .addTransferHook(AccountsType.TransferHookA, hookProgram, metasPda, List.of(extra))
        .addSupplementalTickArrays(tickArray1, tickArray2)
        .build();

    // slice metadata: one 3-account hook slice, one 2-account tick-array slice
    final var slices = remaining.info().slices();
    assertEquals(2, slices.length);
    assertEquals(AccountsType.TransferHookA, slices[0].accountsType());
    assertEquals(3, slices[0].length());
    assertEquals(AccountsType.SupplementalTickArrays, slices[1].accountsType());
    assertEquals(2, slices[1].length());

    // the flat account list preserves slice order, all read-only
    assertEquals(
        List.of(
            AccountMeta.createRead(hookProgram),
            AccountMeta.createRead(metasPda),
            AccountMeta.createRead(extra),
            AccountMeta.createRead(tickArray1),
            AccountMeta.createRead(tickArray2)
        ),
        remaining.accounts());
  }

  @Test
  void remainingAccountsEmptySlicesAreDropped() {
    final var empty = WhirlpoolRemainingAccounts.builder()
        .addSlice(AccountsType.TransferHookA, List.of())
        .addSupplementalTickArrays()
        .build();
    assertNull(empty.info(), "no slices -> no info");
    assertEquals(List.of(), empty.accounts());

    // appending nothing leaves the instruction untouched
    final var ix = software.sava.core.tx.Instruction.createInstruction(
        AccountMeta.createInvoked(key(9)), List.of(), new byte[]{1});
    assertSame(ix, WhirlpoolRemainingAccounts.append(ix, empty));
    assertSame(ix, WhirlpoolRemainingAccounts.append(ix, null));

    // appending a real set attaches exactly those accounts
    final var one = WhirlpoolRemainingAccounts.builder()
        .addSlice(AccountsType.TransferHookA, AccountMeta.createRead(key(3)))
        .build();
    final var extended = WhirlpoolRemainingAccounts.append(ix, one);
    assertEquals(1, extended.accounts().size());
    assertEquals(key(3), extended.accounts().getFirst().publicKey());
  }

  // ---------------------------------------------------------------------------
  // factories
  // ---------------------------------------------------------------------------

  @Test
  void factoriesProduceWiredInstances() {
    assertEquals(0, TransferFee.of(0).feeBps());
    assertEquals(-1L, TransferFee.of(250).maxFee(), "no-cap sentinel is u64::MAX");
    assertEquals(7L, TransferFee.of(250, 7L).maxFee());

    final var accounts = OrcaAccounts.createAccounts(
        "whirLbMiicVdio4qvUfM5KAg6Ct8VwpYzGff3uctyCc",
        "3axbTs2z5GBy6usVbNVoqEgZMng3vZvMnAoX29BFfwhr",
        "metaqbxxUerdq28cj1RbAWkYQm3ybzjb6a8bt518x1s");
    assertNotNull(accounts);
    assertEquals(OrcaAccounts.MAIN_NET.invokedWhirlpoolProgram(), accounts.invokedWhirlpoolProgram());
    assertEquals(
        "whirLbMiicVdio4qvUfM5KAg6Ct8VwpYzGff3uctyCc",
        accounts.invokedWhirlpoolProgram().publicKey().toBase58());
    assertEquals(
        "3axbTs2z5GBy6usVbNVoqEgZMng3vZvMnAoX29BFfwhr",
        accounts.whirlpoolNftUpdateAuthority().toBase58());

    final var client = OrcaWhirlpoolsClient.createClient(accounts);
    assertNotNull(client);
    assertEquals(accounts.invokedWhirlpoolProgram().publicKey(), client.whirlpoolProgramKey());
    assertNotNull(OrcaWhirlpoolsClient.createClient(software.sava.core.accounts.SolanaAccounts.MAIN_NET, accounts));
  }

  /// The hand-picked round trip above covers ten ticks; these mutants perturb
  /// the conversion elsewhere in an 887k-wide domain, so this sweeps it.
  ///
  /// A prime stride is used rather than a round one so the samples do not align
  /// with the algorithm's own power-of-two structure — the bit-precision loop,
  /// the `msb >= 64` normalisation branch and the tick-refinement step all key
  /// off binary boundaries, and a stride of 1024 would systematically miss them.
  /// The sweep is kept to a few thousand points because PIT re-runs it once per
  /// mutant in this class.
  @Test
  void tickSqrtPriceRoundTripsAcrossTheWholeDomain() {
    final int stride = 439; // prime, ~2020 samples over the full range
    for (int tick = OrcaUtil.MIN_TICK_INDEX; tick <= OrcaUtil.MAX_TICK_INDEX; tick += stride) {
      final var sqrtPrice = OrcaUtil.tickIndexToSqrtPriceX64(tick);
      assertEquals(tick, OrcaUtil.sqrtPriceX64ToTickIndex(sqrtPrice), "round trip at tick " + tick);
    }

    // dense around zero, where the positive and negative paths meet
    for (int tick = -600; tick <= 600; tick++) {
      final var sqrtPrice = OrcaUtil.tickIndexToSqrtPriceX64(tick);
      assertEquals(tick, OrcaUtil.sqrtPriceX64ToTickIndex(sqrtPrice), "round trip at tick " + tick);
    }

    // dense at both extremes
    for (int tick = OrcaUtil.MAX_TICK_INDEX - 300; tick <= OrcaUtil.MAX_TICK_INDEX; tick++) {
      assertEquals(tick, OrcaUtil.sqrtPriceX64ToTickIndex(OrcaUtil.tickIndexToSqrtPriceX64(tick)),
          "round trip at tick " + tick);
    }
    for (int tick = OrcaUtil.MIN_TICK_INDEX; tick <= OrcaUtil.MIN_TICK_INDEX + 300; tick++) {
      assertEquals(tick, OrcaUtil.sqrtPriceX64ToTickIndex(OrcaUtil.tickIndexToSqrtPriceX64(tick)),
          "round trip at tick " + tick);
    }
  }

  /// `tickIndexToSqrtPriceX64` must be strictly increasing: the tick index is a
  /// log-scale price, so a single inverted pair would let a position's lower
  /// bound sit above its upper bound. This catches perturbations that preserve
  /// the round trip but distort the curve between samples.
  @Test
  void sqrtPriceIsStrictlyIncreasingInTheTickIndex() {
    final int stride = 439;
    var previous = OrcaUtil.tickIndexToSqrtPriceX64(OrcaUtil.MIN_TICK_INDEX);
    for (int tick = OrcaUtil.MIN_TICK_INDEX + stride; tick <= OrcaUtil.MAX_TICK_INDEX; tick += stride) {
      final var current = OrcaUtil.tickIndexToSqrtPriceX64(tick);
      assertTrue(current.compareTo(previous) > 0,
          "sqrt price must increase with the tick index, at tick " + tick);
      previous = current;
    }

    // strictly increasing step by step across the sign change too
    previous = OrcaUtil.tickIndexToSqrtPriceX64(-600);
    for (int tick = -599; tick <= 600; tick++) {
      final var current = OrcaUtil.tickIndexToSqrtPriceX64(tick);
      assertTrue(current.compareTo(previous) > 0, "adjacent ticks must differ, at tick " + tick);
      previous = current;
    }

    // and the whole curve stays inside the declared bounds
    assertEquals(OrcaUtil.MIN_SQRT_PRICE_X64, OrcaUtil.tickIndexToSqrtPriceX64(OrcaUtil.MIN_TICK_INDEX));
    assertEquals(OrcaUtil.MAX_SQRT_PRICE_X64, OrcaUtil.tickIndexToSqrtPriceX64(OrcaUtil.MAX_TICK_INDEX));
  }

  // ---------------------------------------------------------------------------
  // BigInteger arithmetic (EXPERIMENTAL_BIG_INTEGER)
  // ---------------------------------------------------------------------------

  /// `reverseApplyTransferFee` recovers the pre-fee amount with a ceiling
  /// division written as `(n + d - 1) / d`. The `- 1` is what makes it a *ceil*
  /// rather than a round-up-always: on an exact multiple it must contribute
  /// nothing, so `d - 1` and `d + 1` differ by exactly one unit there and
  /// nowhere else. Only an exactly-divisible case can tell them apart.
  @Test
  void reverseTransferFeeCeilingIsExactOnWholeMultiples() {
    // feeBps = 1000 (10%) => denominator = 9000, and numerator = amount * 10000.
    // amount = 9 gives numerator 90000 = 9000 * 10, an exact multiple.
    final long exact = OrcaUtil.reverseApplyTransferFee(9L, 1_000, Long.MAX_VALUE);
    assertEquals(10L, exact, "an exact multiple must not be rounded up again");

    // one unit up is no longer a whole multiple, so the ceiling rounds:
    // ceil(10 * 10000 / 9000) = ceil(11.11..) = 12
    assertEquals(12L, OrcaUtil.reverseApplyTransferFee(10L, 1_000, Long.MAX_VALUE));

    // a zero fee is the identity, whatever the rounding
    assertEquals(1_234L, OrcaUtil.reverseApplyTransferFee(1_234L, 0, Long.MAX_VALUE));

    // and the round trip holds: applying the fee to the recovered amount
    // returns the original
    assertEquals(9L, OrcaUtil.applyTransferFee(exact, 1_000, Long.MAX_VALUE));

    // The recovered fee (rawPreFee - amount) is used for one thing only: the
    // max-fee cap. With an unbounded cap the comparison never fires, so the
    // subtraction is unobservable — it takes a cap the *correct* fee stays under
    // to pin it. Here the fee is 1, so a cap of 5 must leave the result
    // uncapped, while a cap of 0 must engage it.
    assertEquals(10L, OrcaUtil.reverseApplyTransferFee(9L, 1_000, 5L),
        "a fee of 1 is below the cap of 5, so the pre-fee amount stands");
    assertEquals(9L, OrcaUtil.reverseApplyTransferFee(9L, 1_000, 0L),
        "a zero cap forces amount + maxFee");
    assertEquals(10L, OrcaUtil.reverseApplyTransferFee(9L, 1_000, 1L),
        "the cap engages at exactly the fee, giving amount + maxFee");
  }

  /// `sqrtPriceX64ToTickIndex` brackets the answer with an error margin and then
  /// picks between `tickLow` and `tickHigh`. The round-trip sweep only ever feeds
  /// it *exact* tick boundaries, where the answer is always `tickHigh` — so the
  /// `tickLow` subtraction is never the deciding term. Prices strictly between
  /// two ticks exercise the other branch.
  @Test
  void tickIndexResolvesPricesBetweenTicks() {
    for (final int tick : new int[]{-10_000, -1, 0, 1, 10_000, 100_000}) {
      final var lower = OrcaUtil.tickIndexToSqrtPriceX64(tick);
      final var upper = OrcaUtil.tickIndexToSqrtPriceX64(tick + 1);
      assertTrue(upper.compareTo(lower) > 0);

      // strictly inside [tick, tick+1) must still resolve to `tick`, at a spread
      // of offsets across the interval — the error-margin bracket only stops
      // agreeing with itself near the ends, so both ends and the middle matter
      final var width = upper.subtract(lower);
      for (final int denom : new int[]{2, 3, 4, 8, 16, 64, 256}) {
        final var offset = width.divide(BigInteger.valueOf(denom));
        assertEquals(tick, OrcaUtil.sqrtPriceX64ToTickIndex(lower.add(offset)),
            "tick " + tick + " + width/" + denom);
        assertEquals(tick, OrcaUtil.sqrtPriceX64ToTickIndex(upper.subtract(offset)),
            "tick " + (tick + 1) + " - width/" + denom);
      }
      assertEquals(tick, OrcaUtil.sqrtPriceX64ToTickIndex(lower.add(BigInteger.ONE)),
          "one above tick " + tick);
      assertEquals(tick, OrcaUtil.sqrtPriceX64ToTickIndex(upper.subtract(BigInteger.ONE)),
          "one below tick " + (tick + 1));
    }
  }

  /// `tokenBFromLiquidity` rounds up only when the 64-bit shift actually
  /// discarded something, detected by masking off the low 64 bits. Masking with
  /// `and` and with `or` agree on almost every input — they diverge exactly when
  /// the product is a whole multiple of 2^64 and there is nothing to round.
  @Test
  void tokenBRoundsUpOnlyWhenTheShiftDiscardsBits() {
    // above the range, token B is liquidity * (upper - lower) >> 64
    final int lowerTick = 0;
    final int upperTick = 128;
    final var lower = OrcaUtil.tickIndexToSqrtPriceX64(lowerTick);
    final var upper = OrcaUtil.tickIndexToSqrtPriceX64(upperTick);
    final var diff = upper.subtract(lower);
    final var above = OrcaUtil.tickIndexToSqrtPriceX64(upperTick + 1);

    // choose liquidity so that liquidity * diff is an exact multiple of 2^64
    final var twoPow64 = BigInteger.ONE.shiftLeft(64);
    final var gcd = diff.gcd(twoPow64);
    final var liquidity = twoPow64.divide(gcd);
    assertEquals(0, liquidity.multiply(diff).mod(twoPow64).signum(),
        "the product must land exactly on a 2^64 boundary");

    final long roundedUp = WhirlpoolQuote.tryGetTokenEstimatesFromLiquidity(
        liquidity, above, lowerTick, upperTick, true)[1];
    final long roundedDown = WhirlpoolQuote.tryGetTokenEstimatesFromLiquidity(
        liquidity, above, lowerTick, upperTick, false)[1];

    assertEquals(roundedDown, roundedUp,
        "nothing was discarded by the shift, so rounding up must be a no-op");
    assertTrue(roundedUp > 0L);

    // and where the product is *not* a whole multiple, rounding up does add one
    final var inexact = liquidity.add(BigInteger.ONE);
    assertNotEquals(0, inexact.multiply(diff).mod(twoPow64).signum());
    assertEquals(
        WhirlpoolQuote.tryGetTokenEstimatesFromLiquidity(inexact, above, lowerTick, upperTick, false)[1] + 1L,
        WhirlpoolQuote.tryGetTokenEstimatesFromLiquidity(inexact, above, lowerTick, upperTick, true)[1],
        "a discarded remainder must round up by exactly one");
  }
}

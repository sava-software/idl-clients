package software.sava.idl.clients.core.math;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

/// The property under test throughout is the two's-complement boundary: every
/// helper must treat the sign bit as magnitude rather than sign, so the whole
/// unsigned range round trips — never negate, never mask off low bits, and
/// never reject `[2^63, 2^64)` as out of range.
final class SafeMathTests {

  private static final BigInteger TWO_POW_63 = BigInteger.ONE.shiftLeft(63);
  private static final BigInteger TWO_POW_64 = BigInteger.ONE.shiftLeft(64);
  private static final BigInteger TWO_POW_128 = BigInteger.ONE.shiftLeft(128);

  @Test
  void u64ReinterpretsTheSignBitNotTheMagnitude() {
    assertEquals(BigInteger.ZERO, SafeMath.toUnsignedBigInteger(0L));
    assertEquals(BigInteger.ONE, SafeMath.toUnsignedBigInteger(1L));
    assertEquals(TWO_POW_63.subtract(BigInteger.ONE), SafeMath.toUnsignedBigInteger(Long.MAX_VALUE));
    assertEquals(TWO_POW_63, SafeMath.toUnsignedBigInteger(Long.MIN_VALUE));
    assertEquals(TWO_POW_64.subtract(BigInteger.ONE), SafeMath.toUnsignedBigInteger(-1L));
    assertEquals(TWO_POW_64.subtract(BigInteger.TWO), SafeMath.toUnsignedBigInteger(-2L));
  }

  /// Scale matters: a decimal built from the integer must be scale 0, or a
  /// downstream `movePointLeft` lands in the wrong place.
  @Test
  void unsignedDecimalCarriesTheIntegerValueAtScaleZero() {
    assertEquals(new BigDecimal("0"), SafeMath.toUnsignedBigDecimal(0L));
    assertEquals(new BigDecimal("9223372036854775807"), SafeMath.toUnsignedBigDecimal(Long.MAX_VALUE));
    assertEquals(new BigDecimal("9223372036854775808"), SafeMath.toUnsignedBigDecimal(Long.MIN_VALUE));
    assertEquals(new BigDecimal("18446744073709551615"), SafeMath.toUnsignedBigDecimal(-1L));
    assertEquals(0, SafeMath.toUnsignedBigDecimal(-1L).scale());
  }

  /// The bug this method exists to prevent: `longValueExact` rejects the top
  /// half of the u64 range, so every one of these must come back as the raw
  /// bit pattern rather than throwing.
  @Test
  void toU64ReturnsRawBitsAcrossTheWholeUnsignedRange() {
    assertEquals(0L, SafeMath.toU64(BigInteger.ZERO));
    assertEquals(1L, SafeMath.toU64(BigInteger.ONE));
    assertEquals(Long.MAX_VALUE, SafeMath.toU64(TWO_POW_63.subtract(BigInteger.ONE)));
    assertEquals(Long.MIN_VALUE, SafeMath.toU64(TWO_POW_63));
    assertEquals(-1L, SafeMath.toU64(TWO_POW_64.subtract(BigInteger.ONE)));
  }

  @Test
  void toU64RejectsBothSidesOfTheRange() {
    assertThrows(ArithmeticException.class, () -> SafeMath.toU64(BigInteger.valueOf(-1L)));
    assertThrows(ArithmeticException.class, () -> SafeMath.toU64(TWO_POW_64));
    assertThrows(ArithmeticException.class, () -> SafeMath.toU64(TWO_POW_128));
  }

  /// The pairing that matters at the call sites: whatever the widening
  /// produced, `toU64` must narrow back to the identical bit pattern.
  @Test
  void toU64RoundTripsEveryUnsignedWidening() {
    for (final long value : new long[]{0L, 1L, Long.MAX_VALUE, Long.MIN_VALUE, -1L, -2L}) {
      assertEquals(value, SafeMath.toU64(SafeMath.toUnsignedBigInteger(value)));
    }
  }

  @Test
  void checkedAddTreatsBothOperandsAsUnsigned() {
    assertEquals(3L, SafeMath.checkedAddU64(1L, 2L));
    // a zero addend leaves the carry test exactly at its boundary (sum == a),
    // and it is the common case on chain: WhirlpoolQuote adds a fee delta of
    // zero to an owed balance whenever nothing has accrued
    assertEquals(5L, SafeMath.checkedAddU64(5L, 0L));
    assertEquals(5L, SafeMath.checkedAddU64(0L, 5L));
    assertEquals(0L, SafeMath.checkedAddU64(0L, 0L));
    assertEquals(-1L, SafeMath.checkedAddU64(-1L, 0L), "u64::MAX + 0 is still u64::MAX");
    // Long.MAX_VALUE + 1 is a u64 overflow only in signed arithmetic
    assertEquals(Long.MIN_VALUE, SafeMath.checkedAddU64(Long.MAX_VALUE, 1L));
    // -2 is u64 2^64-2, so adding 1 stays in range and adding 2 does not
    assertEquals(-1L, SafeMath.checkedAddU64(-2L, 1L));
    assertThrows(ArithmeticException.class, () -> SafeMath.checkedAddU64(-2L, 2L));
    assertThrows(ArithmeticException.class, () -> SafeMath.checkedAddU64(-1L, -1L));
  }

  @Test
  void wrappingSubWrapsModTwoPow128() {
    assertEquals(BigInteger.ZERO, SafeMath.wrappingSubU128(BigInteger.ONE, BigInteger.ONE));
    assertEquals(BigInteger.valueOf(3), SafeMath.wrappingSubU128(BigInteger.valueOf(5), BigInteger.TWO));
    assertEquals(
        TWO_POW_128.subtract(BigInteger.ONE),
        SafeMath.wrappingSubU128(BigInteger.ONE, BigInteger.TWO)
    );
    final var max = TWO_POW_128.subtract(BigInteger.ONE);
    assertEquals(BigInteger.ONE, SafeMath.wrappingSubU128(BigInteger.ZERO, max));
    assertEquals(max, SafeMath.wrappingSubU128(max, BigInteger.ZERO));
  }

  /// The mask form the Orca quote layer used before consolidation, pinned as
  /// equivalent over the u128 domain so the branch form cannot silently drift
  /// from it.
  @Test
  void wrappingSubAgreesWithTheMaskForm() {
    final var max = TWO_POW_128.subtract(BigInteger.ONE);
    for (final var a : new BigInteger[]{BigInteger.ZERO, BigInteger.ONE, TWO_POW_63, max}) {
      for (final var b : new BigInteger[]{BigInteger.ZERO, BigInteger.ONE, TWO_POW_63, max}) {
        assertEquals(a.subtract(b).and(SafeMath.U128_MASK), SafeMath.wrappingSubU128(a, b), a + " - " + b);
      }
    }
  }

  @Test
  void boundsAreTheUnsignedMaxima() {
    assertEquals(TWO_POW_64.subtract(BigInteger.ONE), SafeMath.U64_MAX);
    assertEquals(TWO_POW_128.subtract(BigInteger.ONE), SafeMath.U128_MASK);
  }


  @Test
  void checkedSubTreatsBothOperandsAsUnsigned() {
    assertEquals(3L, SafeMath.checkedSubU64(5L, 2L));
    assertEquals(0L, SafeMath.checkedSubU64(7L, 7L));
    // -1 is u64::MAX, so the difference is a valid u64 the signed view calls negative
    assertEquals(-2L, SafeMath.checkedSubU64(-1L, 1L));
    assertEquals("18446744073709551614", Long.toUnsignedString(SafeMath.checkedSubU64(-1L, 1L)));
    // an unsigned underflow is the case that must not wrap to a huge balance
    assertThrows(ArithmeticException.class, () -> SafeMath.checkedSubU64(1L, 2L));
    assertThrows(ArithmeticException.class, () -> SafeMath.checkedSubU64(1L, -1L));
  }

  @Test
  void saturatingSubClampsInsteadOfThrowing() {
    assertEquals(3L, SafeMath.saturatingSubU64(5L, 2L));
    assertEquals(0L, SafeMath.saturatingSubU64(1L, 2L));
    assertEquals(0L, SafeMath.saturatingSubU64(1L, -1L), "b is u64::MAX, so the difference clamps");
    assertEquals(-2L, SafeMath.saturatingSubU64(-1L, 1L));
  }

  @Test
  void mulDivComputesTheProductAtFullWidth() {
    assertEquals(50L, SafeMath.mulDivU64(10L, 20L, 4L));
    // the intermediate exceeds u64 while the quotient does not: 2^63 * 4 / 8 = 2^62
    assertEquals(1L << 62, SafeMath.mulDivU64(1L << 63, 4L, 8L));
    // truncates toward zero, like integer division on chain
    assertEquals(3L, SafeMath.mulDivU64(10L, 1L, 3L));
    // operands are unsigned: u64::MAX * 1 / 1 round-trips
    assertEquals(-1L, SafeMath.mulDivU64(-1L, 1L, 1L));
    // BigInteger.divide throws ArithmeticException on a zero denominator all by
    // itself, so the type alone cannot tell the guard from its absence — the
    // guard is here for the diagnosis, and only the message pins it
    final var zeroDenominator = assertThrows(ArithmeticException.class,
        () -> SafeMath.mulDivU64(10L, 20L, 0L));
    assertTrue(zeroDenominator.getMessage().contains("denominator is zero"), zeroDenominator.getMessage());
    // a quotient past u64 is an error, not a truncation
    assertThrows(ArithmeticException.class, () -> SafeMath.mulDivU64(-1L, 4L, 1L));
  }

  /// The overload OrcaUtil's fee math uses: a BigInteger rate against its bps
  /// or 1e-6 denominator, with the rounding direction the program charges in.
  @Test
  void mulDivRoundsAwayFromZeroOnlyWhenAsked() {
    final var bps = BigInteger.valueOf(10_000L);
    final var factor = BigInteger.valueOf(9_999L);
    // 100 * 9999 / 10000 = 99.99 — the direction decides who absorbs the ulp
    assertEquals(99L, SafeMath.mulDivU64(100L, factor, bps, false));
    assertEquals(100L, SafeMath.mulDivU64(100L, factor, bps, true));
    // an exact division has no remainder, so rounding up must not add one
    assertEquals(50L, SafeMath.mulDivU64(100L, BigInteger.valueOf(5_000L), bps, true));
    assertEquals(50L, SafeMath.mulDivU64(100L, BigInteger.valueOf(5_000L), bps, false));
    // the zero short-circuit and the long way agree, in both directions
    assertEquals(0L, SafeMath.mulDivU64(0L, factor, bps, true));
    assertEquals(0L, SafeMath.mulDivU64(100L, BigInteger.ZERO, bps, true));
    // unsigned operand, and a quotient past u64 is still an error
    assertEquals(-1L, SafeMath.mulDivU64(-1L, BigInteger.ONE, BigInteger.ONE, false));
    assertThrows(ArithmeticException.class,
        () -> SafeMath.mulDivU64(-1L, BigInteger.TWO, BigInteger.ONE, false));
    // the denominator is checked before the short-circuit, so a zero
    // denominator is an error even when the amount would have returned early
    final var zeroDenominator = assertThrows(ArithmeticException.class,
        () -> SafeMath.mulDivU64(0L, factor, BigInteger.ZERO, false));
    assertTrue(zeroDenominator.getMessage().contains("denominator is zero"), zeroDenominator.getMessage());
  }

  /// BigInteger reads shiftRight(-n) as shiftLeft(n), so an unguarded negative
  /// shift moves the value the wrong way and inverts the rounding test instead
  /// of failing. Both shift helpers reject it as a caller error.
  @Test
  void shiftHelpersRejectANegativeShift() {
    final var two = BigInteger.TWO;
    assertThrows(IllegalArgumentException.class, () -> SafeMath.mulShiftRight(two, two, -1, false));
    assertThrows(IllegalArgumentException.class, () -> SafeMath.mulShiftRight(two, two, -1, true));
    assertThrows(IllegalArgumentException.class, () -> SafeMath.mulShiftTruncateU128(two, two, -1));
    // zero is a legal shift: nothing is discarded, so roundUp cannot fire
    assertEquals(BigInteger.valueOf(4), SafeMath.mulShiftRight(two, two, 0, true));
    assertEquals(BigInteger.valueOf(4), SafeMath.mulShiftTruncateU128(two, two, 0));
  }

  @Test
  void wrappingAddTruncatesAtTwoPow128() {
    final var max = SafeMath.U128_MASK;
    assertEquals(BigInteger.valueOf(5), SafeMath.wrappingAddU128(BigInteger.TWO, BigInteger.valueOf(3)));
    assertEquals(BigInteger.ZERO, SafeMath.wrappingAddU128(max, BigInteger.ONE), "u128::MAX + 1 rolls to zero");
    assertEquals(BigInteger.ONE, SafeMath.wrappingAddU128(max, BigInteger.TWO));
    // the inverse of wrappingSubU128 across the boundary
    assertEquals(max, SafeMath.wrappingSubU128(SafeMath.wrappingAddU128(max, BigInteger.ONE), BigInteger.ONE));
  }

  @Test
  void mulShiftRightRoundsOnlyWhenBitsAreDiscarded() {
    final var q64 = BigInteger.ONE.shiftLeft(64);
    // exact: nothing is shifted out, so roundUp cannot change the result
    assertEquals(BigInteger.valueOf(3), SafeMath.mulShiftRight(BigInteger.valueOf(3), q64, 64, false));
    assertEquals(BigInteger.valueOf(3), SafeMath.mulShiftRight(BigInteger.valueOf(3), q64, 64, true));
    // inexact: exactly one ulp of difference between the two directions
    final var inexact = q64.add(BigInteger.ONE);
    assertEquals(BigInteger.ONE, SafeMath.mulShiftRight(inexact, BigInteger.ONE, 64, false));
    assertEquals(BigInteger.TWO, SafeMath.mulShiftRight(inexact, BigInteger.ONE, 64, true));
    // the product is NOT truncated — callers narrow deliberately
    assertEquals(q64, SafeMath.mulShiftRight(q64.multiply(q64), BigInteger.ONE, 64, false));
  }

  @Test
  void mulShiftTruncateDiscardsTheHighBits() {
    final var max = SafeMath.U128_MASK;
    assertEquals(BigInteger.valueOf(6), SafeMath.mulShiftTruncateU128(BigInteger.valueOf(3), BigInteger.valueOf(4), 1));
    // a product wider than u128 keeps only the low 128 bits after the shift
    final var wide = SafeMath.mulShiftTruncateU128(max, max, 0);
    assertEquals(BigInteger.ONE, wide, "(2^128-1)^2 mod 2^128 == 1");
    assertTrue(wide.compareTo(max) <= 0, "result must always fit u128");
  }

  @Test
  void toU128NarrowsAndRejectsBothSides() {
    final var max = SafeMath.U128_MASK;
    assertEquals(BigInteger.ZERO, SafeMath.toU128(BigInteger.ZERO));
    assertSame(max, SafeMath.toU128(max));
    assertThrows(ArithmeticException.class, () -> SafeMath.toU128(BigInteger.valueOf(-1)));
    assertThrows(ArithmeticException.class, () -> SafeMath.toU128(max.add(BigInteger.ONE)));
  }

  @Test
  void rangeFailuresNameWhichSideWasViolated() {
    // a negative does not "exceed" the field; sending the reader after an
    // overflow that never happened is the whole reason these differ
    final var negative = assertThrows(ArithmeticException.class,
        () -> SafeMath.toU64(BigInteger.valueOf(-1)));
    assertTrue(negative.getMessage().contains("negative"), negative.getMessage());

    final var tooBig = assertThrows(ArithmeticException.class,
        () -> SafeMath.toU64(SafeMath.U64_MAX.add(BigInteger.ONE)));
    assertTrue(tooBig.getMessage().contains("exceeds"), tooBig.getMessage());
  }
}

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
}

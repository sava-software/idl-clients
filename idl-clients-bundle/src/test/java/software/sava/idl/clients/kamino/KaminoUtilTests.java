package software.sava.idl.clients.kamino;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class KaminoUtilTests {

  // 2^60
  private static final BigInteger ONE_SF = new BigInteger("1152921504606846976");
  // 1.5 * 2^60 == 3 * 2^59
  private static final BigInteger ONE_POINT_FIVE_SF = new BigInteger("1729382256910270464");
  // 2.5 * 2^60 == 5 * 2^59
  private static final BigInteger TWO_POINT_FIVE_SF = new BigInteger("2882303761517117440");
  // 2^-60 == 5^60 / 10^60, exact with 42 significant digits.
  private static final String TWO_POW_MINUS_60 = "8.67361737988403547205962240695953369140625E-19";

  @Test
  void fromSfExactDyadicValues() {
    assertEquals(BigDecimal.ONE, KaminoUtil.fromSf(ONE_SF));
    assertEquals(new BigDecimal("1.5"), KaminoUtil.fromSf(ONE_POINT_FIVE_SF));
    assertEquals(new BigDecimal("2.5"), KaminoUtil.fromSf(TWO_POINT_FIVE_SF));
    // 123 * 2^60
    assertEquals(new BigDecimal("123"), KaminoUtil.fromSf(new BigInteger("141809345066642178048")));
    assertEquals(new BigDecimal("-1"), KaminoUtil.fromSf(ONE_SF.negate()));
    assertEquals(BigDecimal.ZERO, KaminoUtil.fromSf(BigInteger.ZERO));
  }

  @Test
  void fromSfStripsTrailingZeros() {
    // 100 * 2^60 == 115292150460684697600; 100 stripped is 1E+2.
    final var oneHundred = KaminoUtil.fromSf(new BigInteger("115292150460684697600"));
    assertEquals(new BigDecimal("1E+2"), oneHundred);
    assertEquals(0, oneHundred.compareTo(new BigDecimal(100)));
  }

  @Test
  void fromSfDefaultMathContextRoundsTo40DigitsHalfEven() {
    // 2^-60 has 42 significant digits ending ...691406|25; dropping "25" rounds down under HALF_EVEN.
    assertEquals(
        new BigDecimal("8.673617379884035472059622406959533691406E-19"),
        KaminoUtil.fromSf(BigInteger.ONE));
    // The full exact expansion differs in the trailing digits, proving rounding occurred.
    assertTrue(KaminoUtil.fromSf(BigInteger.ONE).compareTo(new BigDecimal(TWO_POW_MINUS_60)) < 0);
  }

  @Test
  void fromSfExplicitMathContext() {
    // 2^-60 to 10 significant digits HALF_EVEN is 8.673617380E-19; the trailing zero is stripped.
    assertEquals(
        new BigDecimal("8.67361738E-19"),
        KaminoUtil.fromSf(BigInteger.ONE, new MathContext(10, RoundingMode.HALF_EVEN)));
    assertEquals(
        new BigDecimal("8.6736E-19"),
        KaminoUtil.fromSf(BigInteger.ONE, new MathContext(5, RoundingMode.DOWN)));
    assertEquals(
        new BigDecimal("8.6737E-19"),
        KaminoUtil.fromSf(BigInteger.ONE, new MathContext(5, RoundingMode.UP)));
    // Exact result is unaffected by the context rounding mode.
    assertEquals(
        new BigDecimal("1.5"),
        KaminoUtil.fromSf(ONE_POINT_FIVE_SF, new MathContext(5, RoundingMode.DOWN)));
  }

  @Test
  void fromSfScaleAndRoundingMode() {
    // 1.5 at scale 0: HALF_EVEN ties away to the even neighbor 2.
    assertEquals(new BigDecimal("2"), KaminoUtil.fromSf(ONE_POINT_FIVE_SF, 0, RoundingMode.HALF_EVEN));
    assertEquals(BigDecimal.ONE, KaminoUtil.fromSf(ONE_POINT_FIVE_SF, 0, RoundingMode.HALF_DOWN));
    assertEquals(BigDecimal.ONE, KaminoUtil.fromSf(ONE_POINT_FIVE_SF, 0, RoundingMode.DOWN));
    assertEquals(new BigDecimal("2"), KaminoUtil.fromSf(ONE_POINT_FIVE_SF, 0, RoundingMode.UP));
    // 2.5 at scale 0: HALF_EVEN ties to the even neighbor 2, HALF_UP goes to 3.
    assertEquals(new BigDecimal("2"), KaminoUtil.fromSf(TWO_POINT_FIVE_SF, 0, RoundingMode.HALF_EVEN));
    assertEquals(new BigDecimal("3"), KaminoUtil.fromSf(TWO_POINT_FIVE_SF, 0, RoundingMode.HALF_UP));
    // 2^-60 at scale 20 is 0.0000000000000000008673... => 8.6E-19 DOWN / 8.7E-19 UP.
    assertEquals(new BigDecimal("8.6E-19"), KaminoUtil.fromSf(BigInteger.ONE, 20, RoundingMode.DOWN));
    assertEquals(new BigDecimal("8.7E-19"), KaminoUtil.fromSf(BigInteger.ONE, 20, RoundingMode.UP));
  }

  @Test
  void fromSfBigDecimalOverloads() {
    assertEquals(new BigDecimal("1.5"), KaminoUtil.fromSf(new BigDecimal(ONE_POINT_FIVE_SF)));
    assertEquals(
        new BigDecimal("8.6736E-19"),
        KaminoUtil.fromSf(BigDecimal.ONE, new MathContext(5, RoundingMode.DOWN)));
    assertEquals(new BigDecimal("2"), KaminoUtil.fromSf(new BigDecimal(ONE_POINT_FIVE_SF), 0, RoundingMode.HALF_EVEN));
    // Fractional inputs are supported: 0.5 * 2^60 scaled back down.
    assertEquals(
        new BigDecimal("0.5"),
        KaminoUtil.fromSf(new BigDecimal("576460752303423488")));
  }

  @Test
  void toSfExactValues() {
    assertEquals(ONE_SF, KaminoUtil.toSf(BigDecimal.ONE));
    assertEquals(ONE_POINT_FIVE_SF, KaminoUtil.toSf(new BigDecimal("1.5")));
    assertEquals(ONE_SF.negate(), KaminoUtil.toSf(new BigDecimal("-1")));
    assertEquals(BigInteger.ZERO, KaminoUtil.toSf(BigDecimal.ZERO));
    assertEquals(new BigInteger("141809345066642178048"), KaminoUtil.toSf(new BigDecimal("123")));
  }

  @Test
  void toSfTruncatesFractionalSf() {
    // 0.1 * 2^60 == 115292150460684697.6, truncated toward zero by toBigInteger().
    assertEquals(new BigInteger("115292150460684697"), KaminoUtil.toSf(new BigDecimal("0.1")));
    assertEquals(new BigInteger("-115292150460684697"), KaminoUtil.toSf(new BigDecimal("-0.1")));
  }

  @Test
  void sfRoundTrips() {
    assertEquals(ONE_POINT_FIVE_SF, KaminoUtil.toSf(KaminoUtil.fromSf(ONE_POINT_FIVE_SF)));
    assertEquals(new BigDecimal("1.5"), KaminoUtil.fromSf(KaminoUtil.toSf(new BigDecimal("1.5"))));
    assertEquals(new BigDecimal("0.5"), KaminoUtil.fromSf(KaminoUtil.toSf(new BigDecimal("0.5"))));
    // 0.1 is not dyadic, so toSf truncates 0.6 of an Sf unit (~5.2E-19) and the
    // round trip comes back strictly below 0.1.
    final var truncated = KaminoUtil.fromSf(KaminoUtil.toSf(new BigDecimal("0.1")));
    assertTrue(truncated.compareTo(new BigDecimal("0.1")) < 0);
    assertEquals(new BigDecimal("0.09999999999999999947958295720695787167642"), truncated);
  }
}

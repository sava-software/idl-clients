package software.sava.idl.clients.kamino;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;

public final class KaminoUtil {

  private static final MathContext PRECISION = new MathContext(40, RoundingMode.HALF_EVEN);
  private static final int FRACTIONS = 60;
  private static final BigDecimal MULTIPLIER = BigDecimal.TWO.pow(FRACTIONS);

  public static BigDecimal fromSf(final BigInteger valueSf) {
    return fromSf(new BigDecimal(valueSf));
  }

  public static BigDecimal fromSf(final BigInteger valueSf, final MathContext mathContext) {
    return fromSf(new BigDecimal(valueSf), mathContext);
  }

  public static BigDecimal fromSf(final BigInteger valueSf, final int scale, final RoundingMode roundingMode) {
    return fromSf(new BigDecimal(valueSf), scale, roundingMode);
  }

  public static BigDecimal fromSf(final BigDecimal valueSf) {
    return valueSf.divide(MULTIPLIER, PRECISION).stripTrailingZeros();
  }

  public static BigDecimal fromSf(final BigDecimal valueSf, final MathContext mathContext) {
    return valueSf.divide(MULTIPLIER, mathContext).stripTrailingZeros();
  }

  public static BigDecimal fromSf(final BigDecimal valueSf, final int scale, final RoundingMode roundingMode) {
    return valueSf.divide(MULTIPLIER, scale, roundingMode).stripTrailingZeros();
  }

  public static BigInteger toSf(final BigDecimal valueDecimal) {
    return valueDecimal.multiply(MULTIPLIER).toBigInteger();
  }

  private KaminoUtil() {
  }
}

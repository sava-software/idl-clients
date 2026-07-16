package software.sava.idl.clients.oracles;

import org.junit.jupiter.api.Test;
import software.sava.idl.clients.oracles.pyth.receiver.gen.types.PriceFeedMessage;
import software.sava.idl.clients.oracles.pyth.receiver.gen.types.PriceUpdateV2;
import software.sava.idl.clients.oracles.pyth.receiver.gen.types.VerificationLevel;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class OracleUtilTests {

  @Test
  void scaleLongPriceNegativeExponent() {
    assertEquals(new BigDecimal("1.23456789"), OracleUtil.scalePrice(123456789L, -8));
    assertEquals(new BigDecimal("0.000000001"), OracleUtil.scalePrice(1L, -9));
  }

  @Test
  void scaleLongPricePositiveExponent() {
    final var scaled = OracleUtil.scalePrice(5L, 3);
    assertEquals(new BigDecimal("5000"), scaled);
    // movePointRight caps the preferred scale at zero rather than going negative.
    assertEquals(0, scaled.scale());
  }

  @Test
  void scaleLongPriceZeroExponent() {
    assertEquals(new BigDecimal("7"), OracleUtil.scalePrice(7L, 0));
    assertEquals(BigDecimal.ZERO, OracleUtil.scalePrice(0L, 0));
  }

  @Test
  void negativeLongsAreTreatedAsUnsigned() {
    // -1L reinterpreted as u64 is 2^64 - 1.
    assertEquals(new BigDecimal("18446744073709551615"), OracleUtil.scalePrice(-1L, 0));
    assertEquals(new BigDecimal("18446744073709.551615"), OracleUtil.scalePrice(-1L, -6));
    // Long.MIN_VALUE reinterpreted as u64 is 2^63.
    assertEquals(new BigDecimal("9223372036854775808"), OracleUtil.scalePrice(Long.MIN_VALUE, 0));
  }

  @Test
  void scaleBigIntegerPrice() {
    assertEquals(new BigDecimal("1.23456789"), OracleUtil.scalePrice(BigInteger.valueOf(123456789L), -8));
    final var scaled = OracleUtil.scalePrice(BigInteger.valueOf(42), 2);
    assertEquals(new BigDecimal("4200"), scaled);
    assertEquals(0, scaled.scale());
    assertEquals(
        new BigDecimal("18.446744073709551615"),
        OracleUtil.scalePrice(new BigInteger("18446744073709551615"), -18));
  }

  @Test
  void scaleBigIntegerPriceKeepsSign() {
    // Unlike the long overload, negative BigIntegers are signed values, not u64 reinterpretations.
    assertEquals(new BigDecimal("-1"), OracleUtil.scalePrice(BigInteger.valueOf(-1), 0));
    assertEquals(new BigDecimal("-0.00000001"), OracleUtil.scalePrice(BigInteger.valueOf(-1), -8));
  }

  @Test
  void scalePythPullPrice() {
    // Trailing zeros are preserved: scale equals -exponent.
    assertEquals(
        new BigDecimal("64230.11500000"),
        OracleUtil.scalePythPullPrice(priceUpdate(6423011500000L, -8)));
    assertEquals(new BigDecimal("500"), OracleUtil.scalePythPullPrice(priceUpdate(5L, 2)));
    // Negative price longs follow the unsigned branch.
    assertEquals(
        new BigDecimal("18446744073709551615"),
        OracleUtil.scalePythPullPrice(priceUpdate(-1L, 0)));
  }

  private static PriceUpdateV2 priceUpdate(final long price, final int exponent) {
    final var priceMessage = new PriceFeedMessage(new byte[32], price, 0L, exponent, 0L, 0L, 0L, 0L);
    return new PriceUpdateV2(null, null, null, VerificationLevel.Full.INSTANCE, priceMessage, 0L);
  }
}

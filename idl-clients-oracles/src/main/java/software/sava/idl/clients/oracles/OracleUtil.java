package software.sava.idl.clients.oracles;

import software.sava.idl.clients.oracles.pyth.receiver.gen.types.PriceUpdateV2;

import java.math.BigDecimal;
import java.math.BigInteger;

public final class OracleUtil {

  public static BigDecimal scalePythPullPrice(final PriceUpdateV2 priceUpdate) {
    final var priceMessage = priceUpdate.priceMessage();
    return scalePrice(priceMessage.price(), priceMessage.exponent());
  }

  public static BigDecimal scalePrice(final long scaledPrice, final int exponent) {
    final var price = scaledPrice < 0
        ? new BigDecimal(Long.toUnsignedString(scaledPrice))
        : new BigDecimal(scaledPrice);
    return price.movePointRight(exponent);
  }

  public static BigDecimal scalePrice(final BigInteger scaledPrice, final int exponent) {
    return new BigDecimal(scaledPrice).movePointRight(exponent);
  }

  private OracleUtil() {
  }
}

package software.sava.idl.clients.oracles;

import software.sava.idl.clients.oracles.pyth.receiver.gen.types.PriceUpdateV2;

import java.math.BigDecimal;
import java.math.BigInteger;

public final class OracleUtil {

  /// Pyth's PriceFeedMessage.price is an i64; a negative value is a genuinely
  /// negative price, not a u64 to reinterpret.
  public static BigDecimal scalePythPullPrice(final PriceUpdateV2 priceUpdate) {
    final var priceMessage = priceUpdate.priceMessage();
    return BigDecimal.valueOf(priceMessage.price()).movePointRight(priceMessage.exponent());
  }

  /// Treats {@code scaledPrice} as an unsigned u64; use for oracle fields declared
  /// unsigned. Signed i64 prices must not go through this overload — a negative
  /// value would come back as a number near 2^64.
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

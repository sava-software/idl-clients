package software.sava.idl.clients.kamino.scope.entries;

import software.sava.idl.clients.kamino.scope.gen.types.OracleType;

import java.math.BigDecimal;

public record FixedPrice(int index, long value, int exp, BigDecimal decimal) implements ScopeEntry {

  public static FixedPrice createEntry(final int index, final long value, final int exp) {
    final var decimal = value < 0
        ? new BigDecimal(Long.toUnsignedString(value))
        : BigDecimal.valueOf(value);
    // scaleByPowerOfTen, not movePointLeft: movePointLeft normalizes a negative
    // resulting scale through setScale(0), materializing value*10^|exp| — a hostile
    // exp from account bytes inflates that into a billion-digit BigInteger
    return new FixedPrice(index, value, exp, decimal.scaleByPowerOfTen(-exp).stripTrailingZeros());
  }

  @Override
  public OracleType oracleType() {
    return OracleType.FixedPrice;
  }
}

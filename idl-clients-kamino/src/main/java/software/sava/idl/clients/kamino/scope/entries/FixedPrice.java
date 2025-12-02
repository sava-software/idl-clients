package software.sava.idl.clients.kamino.scope.entries;

import software.sava.idl.clients.kamino.scope.gen.types.OracleType;

import java.math.BigDecimal;

public record FixedPrice(long value, int exp, BigDecimal decimal) implements ScopeEntry {

  public static FixedPrice createEntry(final long value, final int exp) {
    final var decimal = value < 0
        ? new BigDecimal(Long.toUnsignedString(value))
        : BigDecimal.valueOf(value);
    return new FixedPrice(value, exp, decimal.movePointLeft(exp).stripTrailingZeros());
  }

  @Override
  public OracleType oracleType() {
    return OracleType.FixedPrice;
  }
}

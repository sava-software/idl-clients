package software.sava.idl.clients.kamino.scope.entries;

import software.sava.idl.clients.kamino.scope.gen.types.OracleType;

public record DiscountToMaturity(int discountPerYearBps, long maturityTimestamp) implements ScopeEntry {

  @Override
  public OracleType oracleType() {
    return OracleType.DiscountToMaturity;
  }
}

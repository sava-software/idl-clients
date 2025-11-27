package software.sava.idl.clients.kamino.scope.entries;

import software.sava.idl.clients.kamino.scope.gen.types.OracleType;

public record FixedPrice(long value, int exp) implements ScopeEntry {

  @Override
  public OracleType oracleType() {
    return OracleType.FixedPrice;
  }
}

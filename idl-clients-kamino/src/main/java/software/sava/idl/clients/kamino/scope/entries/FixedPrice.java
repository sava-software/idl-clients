package software.sava.idl.clients.kamino.scope.entries;

import software.sava.idl.clients.kamino.scope.gen.types.OracleType;
import software.sava.idl.clients.kamino.scope.gen.types.Price;

public record FixedPrice(long value, long exp) implements ScopeEntry {

  @Override
  public OracleType oracleType() {
    return OracleType.FixedPrice;
  }
}

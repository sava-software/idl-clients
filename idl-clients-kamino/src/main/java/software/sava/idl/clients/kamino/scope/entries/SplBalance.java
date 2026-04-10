package software.sava.idl.clients.kamino.scope.entries;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.kamino.scope.gen.types.OracleType;

public record SplBalance(int index, PublicKey priceAccount) implements FunctionalEntry {

  @Override
  public OracleType oracleType() {
    return OracleType.SplBalance;
  }
}

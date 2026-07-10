package software.sava.idl.clients.kamino.scope.entries;

import software.sava.idl.clients.kamino.scope.gen.types.EmaType;
import software.sava.idl.clients.kamino.scope.gen.types.OracleType;

import java.util.Set;

public record PythLazerEMA(int index,
                           ScopeEntry sourceEntry,
                           Set<EmaType> emaTypes) implements ScopeEntry {

  @Override
  public OracleType oracleType() {
    return OracleType.PythLazerEMA;
  }
}

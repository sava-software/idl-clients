package software.sava.idl.clients.kamino.scope.entries;

import software.sava.idl.clients.kamino.scope.gen.types.OracleType;

public record ScopeTwap(OracleType oracleType, ScopeEntry sourceEntry) implements ScopeEntry {
  
}

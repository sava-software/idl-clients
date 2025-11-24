package software.sava.idl.clients.kamino.scope.entries;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.kamino.scope.gen.types.OracleType;

public record OracleEntry(OracleType oracleType, PublicKey oracle) implements ScopeEntry {

}

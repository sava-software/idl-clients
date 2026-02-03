package software.sava.idl.clients.kamino.scope.entries;

import software.sava.idl.clients.kamino.scope.gen.types.OracleType;

public sealed interface ScopeEntry permits
    CappedFloored,
    Deprecated,
    DiscountToMaturity,
    FixedPrice,
    MostRecentOf,
    NotYetSupported,
    OracleEntry,
    ScopeTwap,
    Unused {

  int index();

  OracleType oracleType();
}

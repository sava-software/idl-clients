package software.sava.idl.clients.kamino.scope.entries;

import software.sava.idl.clients.kamino.scope.gen.types.OracleType;

public sealed interface ScopeEntry permits
    CappedFloored,
    OracleEntry,
    MostRecentOf,
    CappedMostRecentOf,
    ScopeTwap,
    DiscountToMaturity,
    PythLazerEntry,
    ChainlinkRWA,
    ChainlinkX,
    Chainlink,
    FixedPrice {

  OracleType oracleType();
}

package software.sava.idl.clients.kamino.scope.entries;

import software.sava.idl.clients.kamino.scope.gen.types.OracleType;

public sealed interface ScopeEntry permits AdrenaLp,
    CappedFloored,
    CappedMostRecentOf,
    Chainlink,
    ChainlinkExchangeRate,
    ChainlinkNAV,
    ChainlinkRWA,
    ChainlinkX,
    Deprecated,
    DiscountToMaturity,
    FixedPrice,
    FlashtradeLp,
    JitoRestaking,
    JupiterLpFetch,
    KToken,
    MeteoraDlmmAtoB,
    MeteoraDlmmBtoA,
    MostRecentOf,
    MsolStake,
    NotYetSupported,
    OracleEntry,
    OrcaWhirlpoolAtoB,
    OrcaWhirlpoolBtoA,
    PythLazer,
    PythPull,
    PythPullEMA,
    RaydiumAmmV3AtoB,
    RaydiumAmmV3BtoA,
    RedStone,
    ScopeTwap,
    Securitize,
    SplStake,
    SwitchboardOnDemand,
    Unused {

  OracleType oracleType();
}

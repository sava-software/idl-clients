package software.sava.idl.clients.kamino.scope.entries;

import software.sava.core.accounts.PublicKey;

public sealed interface OracleEntry extends ScopeEntry permits AdrenaLp,
    Chainlink,
    ChainlinkExchangeRate,
    ChainlinkNAV,
    ChainlinkRWA,
    ChainlinkStatusEntry,
    ChainlinkX,
    FlashtradeLp,
    JitoRestaking,
    JupiterLpFetch,
    KToken,
    MeteoraDlmmAtoB,
    MeteoraDlmmBtoA,
    MsolStake,
    OrcaWhirlpoolAtoB,
    OrcaWhirlpoolBtoA,
    PythLazer,
    PythPull,
    PythPullEMA,
    RaydiumAmmV3AtoB,
    RaydiumAmmV3BtoA,
    RedStone,
    ReferencesEntry,
    Securitize,
    SplStake,
    SwitchboardOnDemand {

  PublicKey oracle();

  boolean twapEnabled();
}

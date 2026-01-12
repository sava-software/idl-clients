package software.sava.idl.clients.kamino.scope.entries;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.kamino.scope.gen.types.EmaType;

import java.util.Set;

public sealed interface OracleEntry extends ScopeEntry permits
    AdrenaLp,
    ChainlinkExchangeRate,
    ChainlinkNAV,
    ChainlinkStatusEntry,
    FlashtradeLp,
    JitoRestaking,
    JupiterLpFetch,
    KToken,
    MeteoraDlmmAtoB,
    MeteoraDlmmBtoA,
    MsolStake,
    OrcaWhirlpoolAtoB,
    OrcaWhirlpoolBtoA,
    PythPullEMA,
    RaydiumAmmV3AtoB,
    RaydiumAmmV3BtoA,
    RedStone,
    ReferencesEntry,
    SplStake,
    SwitchboardOnDemand {

  PublicKey oracle();

  Set<EmaType> emaTypes();

  default boolean twapEnabled() {
    return !emaTypes().isEmpty();
  }
}

package software.sava.idl.clients.kamino.scope.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

public enum OracleType implements RustEnum {

  Unused,
  DeprecatedPlaceholder1,
  DeprecatedPlaceholder2,
  DeprecatedPlaceholder3,
  DeprecatedPlaceholder4,
  SplStake,
  KToken,
  DeprecatedPlaceholder5,
  MsolStake,
  KTokenToTokenA,
  KTokenToTokenB,
  JupiterLpFetch,
  ScopeTwap,
  OrcaWhirlpoolAtoB,
  OrcaWhirlpoolBtoA,
  RaydiumAmmV3AtoB,
  RaydiumAmmV3BtoA,
  JupiterLpCompute,
  MeteoraDlmmAtoB,
  MeteoraDlmmBtoA,
  JupiterLpScope,
  PythPull,
  PythPullEMA,
  FixedPrice,
  SwitchboardOnDemand,
  JitoRestaking,
  Chainlink,
  DiscountToMaturity,
  MostRecentOf,
  PythLazer,
  RedStone,
  AdrenaLp,
  Securitize,
  CappedFloored,
  ChainlinkRWA,
  ChainlinkNAV,
  FlashtradeLp,
  ChainlinkX,
  ChainlinkExchangeRate,
  CappedMostRecentOf;

  public static OracleType read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, OracleType.values(), _data, _offset);
  }
}
package software.sava.idl.clients.drift.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

public enum DriftAction implements RustEnum {

  UpdateFunding,
  SettlePnl,
  TriggerOrder,
  FillOrderMatch,
  FillOrderAmmLowRisk,
  FillOrderAmmImmediate,
  Liquidate,
  MarginCalc,
  UpdateTwap,
  UpdateAMMCurve,
  OracleOrderPrice,
  UseMMOraclePrice,
  UpdateAmmCache,
  UpdateLpPoolAum,
  LpPoolSwap;

  public static DriftAction read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, DriftAction.values(), _data, _offset);
  }
}
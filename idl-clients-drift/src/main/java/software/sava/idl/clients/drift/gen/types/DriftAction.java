package software.sava.idl.clients.drift.gen.types;

import software.sava.core.borsh.Borsh;

public enum DriftAction implements Borsh.Enum {

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
    return Borsh.read(DriftAction.values(), _data, _offset);
  }
}
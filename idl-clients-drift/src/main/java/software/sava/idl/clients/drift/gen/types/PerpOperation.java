package software.sava.idl.clients.drift.gen.types;

import software.sava.core.borsh.Borsh;

public enum PerpOperation implements Borsh.Enum {

  UpdateFunding,
  AmmFill,
  Fill,
  SettlePnl,
  SettlePnlWithPosition,
  Liquidation,
  AmmImmediateFill,
  SettleRevPool;

  public static PerpOperation read(final byte[] _data, final int _offset) {
    return Borsh.read(PerpOperation.values(), _data, _offset);
  }
}
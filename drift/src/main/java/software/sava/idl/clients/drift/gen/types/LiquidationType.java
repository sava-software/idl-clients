package software.sava.idl.clients.drift.gen.types;

import software.sava.core.borsh.Borsh;

public enum LiquidationType implements Borsh.Enum {

  LiquidatePerp,
  LiquidateSpot,
  LiquidateBorrowForPerpPnl,
  LiquidatePerpPnlForDeposit,
  PerpBankruptcy,
  SpotBankruptcy;

  public static LiquidationType read(final byte[] _data, final int _offset) {
    return Borsh.read(LiquidationType.values(), _data, _offset);
  }
}
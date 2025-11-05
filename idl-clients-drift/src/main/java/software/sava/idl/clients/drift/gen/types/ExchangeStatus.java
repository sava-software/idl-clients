package software.sava.idl.clients.drift.gen.types;

import software.sava.core.borsh.Borsh;

public enum ExchangeStatus implements Borsh.Enum {

  DepositPaused,
  WithdrawPaused,
  AmmPaused,
  FillPaused,
  LiqPaused,
  FundingPaused,
  SettlePnlPaused,
  AmmImmediateFillPaused;

  public static ExchangeStatus read(final byte[] _data, final int _offset) {
    return Borsh.read(ExchangeStatus.values(), _data, _offset);
  }
}
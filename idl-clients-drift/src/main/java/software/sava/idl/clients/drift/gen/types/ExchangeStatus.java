package software.sava.idl.clients.drift.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

public enum ExchangeStatus implements RustEnum {

  DepositPaused,
  WithdrawPaused,
  AmmPaused,
  FillPaused,
  LiqPaused,
  FundingPaused,
  SettlePnlPaused,
  AmmImmediateFillPaused;

  public static ExchangeStatus read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, ExchangeStatus.values(), _data, _offset);
  }
}
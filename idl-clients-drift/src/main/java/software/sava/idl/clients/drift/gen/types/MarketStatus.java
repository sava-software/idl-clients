package software.sava.idl.clients.drift.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

public enum MarketStatus implements RustEnum {

  Initialized,
  Active,
  FundingPaused,
  AmmPaused,
  FillPaused,
  WithdrawPaused,
  ReduceOnly,
  Settlement,
  Delisted;

  public static MarketStatus read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, MarketStatus.values(), _data, _offset);
  }
}
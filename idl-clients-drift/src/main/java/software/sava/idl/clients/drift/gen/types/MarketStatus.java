package software.sava.idl.clients.drift.gen.types;

import software.sava.core.borsh.Borsh;

public enum MarketStatus implements Borsh.Enum {

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
    return Borsh.read(MarketStatus.values(), _data, _offset);
  }
}
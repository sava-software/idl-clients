package software.sava.idl.clients.drift.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

public enum SpotOperation implements RustEnum {

  UpdateCumulativeInterest,
  Fill,
  Deposit,
  Withdraw,
  Liquidation;

  public static SpotOperation read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, SpotOperation.values(), _data, _offset);
  }
}
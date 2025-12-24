package software.sava.idl.clients.drift.vaults.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

public enum WithdrawUnit implements RustEnum {

  Shares,
  Token,
  SharesPercent;

  public static WithdrawUnit read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, WithdrawUnit.values(), _data, _offset);
  }
}
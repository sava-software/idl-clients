package software.sava.idl.clients.drift.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

public enum SpotBalanceType implements RustEnum {

  Deposit,
  Borrow;

  public static SpotBalanceType read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, SpotBalanceType.values(), _data, _offset);
  }
}
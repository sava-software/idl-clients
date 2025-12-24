package software.sava.idl.clients.drift.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

public enum ConstituentLpOperation implements RustEnum {

  Swap,
  Deposit,
  Withdraw;

  public static ConstituentLpOperation read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, ConstituentLpOperation.values(), _data, _offset);
  }
}
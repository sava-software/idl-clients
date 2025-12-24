package software.sava.idl.clients.drift.vaults.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

public enum FeeUpdateAction implements RustEnum {

  Pending,
  Applied,
  Cancelled;

  public static FeeUpdateAction read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, FeeUpdateAction.values(), _data, _offset);
  }
}
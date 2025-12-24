package software.sava.idl.clients.kamino.farms.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

public enum LockingMode implements RustEnum {

  None,
  Continuous,
  WithExpiry;

  public static LockingMode read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, LockingMode.values(), _data, _offset);
  }
}
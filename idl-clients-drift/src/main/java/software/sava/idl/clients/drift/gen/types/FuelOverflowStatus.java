package software.sava.idl.clients.drift.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

public enum FuelOverflowStatus implements RustEnum {

  Exists;

  public static FuelOverflowStatus read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, FuelOverflowStatus.values(), _data, _offset);
  }
}
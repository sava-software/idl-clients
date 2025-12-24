package software.sava.idl.clients.drift.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

public enum LpStatus implements RustEnum {

  Uncollateralized,
  Active,
  Decommissioning;

  public static LpStatus read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, LpStatus.values(), _data, _offset);
  }
}
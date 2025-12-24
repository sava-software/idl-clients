package software.sava.idl.clients.drift.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

public enum OrderParamsBitFlag implements RustEnum {

  ImmediateOrCancel,
  UpdateHighLeverageMode;

  public static OrderParamsBitFlag read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, OrderParamsBitFlag.values(), _data, _offset);
  }
}
package software.sava.idl.clients.drift.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

public enum PositionDirection implements RustEnum {

  Long,
  Short;

  public static PositionDirection read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, PositionDirection.values(), _data, _offset);
  }
}
package software.sava.idl.clients.drift.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

public enum PositionUpdateType implements RustEnum {

  Open,
  Increase,
  Reduce,
  Close,
  Flip;

  public static PositionUpdateType read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, PositionUpdateType.values(), _data, _offset);
  }
}
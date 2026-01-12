package software.sava.idl.clients.kamino.scope.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

public enum EmaType implements RustEnum {

  Ema1h,
  Ema8h,
  Ema24h;

  public static EmaType read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, EmaType.values(), _data, _offset);
  }
}
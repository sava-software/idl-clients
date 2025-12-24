package software.sava.idl.clients.meteora.dlmm.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

public enum Rounding implements RustEnum {

  Up,
  Down;

  public static Rounding read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, Rounding.values(), _data, _offset);
  }
}
package software.sava.idl.clients.drift.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

public enum SwapDirection implements RustEnum {

  Add,
  Remove;

  public static SwapDirection read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, SwapDirection.values(), _data, _offset);
  }
}
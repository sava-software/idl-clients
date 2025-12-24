package software.sava.idl.clients.drift.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

public enum SwapReduceOnly implements RustEnum {

  In,
  Out;

  public static SwapReduceOnly read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, SwapReduceOnly.values(), _data, _offset);
  }
}
package software.sava.idl.clients.drift.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

public enum PostOnlyParam implements RustEnum {

  None,
  MustPostOnly,
  TryPostOnly,
  Slide;

  public static PostOnlyParam read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, PostOnlyParam.values(), _data, _offset);
  }
}
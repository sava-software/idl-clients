package software.sava.idl.clients.drift.gen.types;

import software.sava.core.borsh.Borsh;

public enum PostOnlyParam implements Borsh.Enum {

  None,
  MustPostOnly,
  TryPostOnly,
  Slide;

  public static PostOnlyParam read(final byte[] _data, final int _offset) {
    return Borsh.read(PostOnlyParam.values(), _data, _offset);
  }
}
package software.sava.idl.clients.drift.gen.types;

import software.sava.core.borsh.Borsh;

public enum SwapDirection implements Borsh.Enum {

  Add,
  Remove;

  public static SwapDirection read(final byte[] _data, final int _offset) {
    return Borsh.read(SwapDirection.values(), _data, _offset);
  }
}
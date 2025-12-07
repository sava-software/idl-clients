package software.sava.idl.clients.meteora.dlmm.gen.types;

import software.sava.core.borsh.Borsh;

public enum Rounding implements Borsh.Enum {

  Up,
  Down;

  public static Rounding read(final byte[] _data, final int _offset) {
    return Borsh.read(Rounding.values(), _data, _offset);
  }
}
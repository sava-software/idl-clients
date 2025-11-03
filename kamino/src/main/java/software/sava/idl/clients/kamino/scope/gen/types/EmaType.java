package software.sava.idl.clients.kamino.scope.gen.types;

import software.sava.core.borsh.Borsh;

public enum EmaType implements Borsh.Enum {

  Ema1h;

  public static EmaType read(final byte[] _data, final int _offset) {
    return Borsh.read(EmaType.values(), _data, _offset);
  }
}
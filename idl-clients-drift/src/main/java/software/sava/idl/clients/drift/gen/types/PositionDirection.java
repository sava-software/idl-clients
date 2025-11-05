package software.sava.idl.clients.drift.gen.types;

import software.sava.core.borsh.Borsh;

public enum PositionDirection implements Borsh.Enum {

  Long,
  Short;

  public static PositionDirection read(final byte[] _data, final int _offset) {
    return Borsh.read(PositionDirection.values(), _data, _offset);
  }
}
package software.sava.idl.clients.drift.gen.types;

import software.sava.core.borsh.Borsh;

public enum PositionUpdateType implements Borsh.Enum {

  Open,
  Increase,
  Reduce,
  Close,
  Flip;

  public static PositionUpdateType read(final byte[] _data, final int _offset) {
    return Borsh.read(PositionUpdateType.values(), _data, _offset);
  }
}
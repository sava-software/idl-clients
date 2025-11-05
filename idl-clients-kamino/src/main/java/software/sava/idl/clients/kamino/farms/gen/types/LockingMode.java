package software.sava.idl.clients.kamino.farms.gen.types;

import software.sava.core.borsh.Borsh;

public enum LockingMode implements Borsh.Enum {

  None,
  Continuous,
  WithExpiry;

  public static LockingMode read(final byte[] _data, final int _offset) {
    return Borsh.read(LockingMode.values(), _data, _offset);
  }
}
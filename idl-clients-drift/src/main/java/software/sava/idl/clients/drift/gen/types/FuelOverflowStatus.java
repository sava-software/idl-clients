package software.sava.idl.clients.drift.gen.types;

import software.sava.core.borsh.Borsh;

public enum FuelOverflowStatus implements Borsh.Enum {

  Exists;

  public static FuelOverflowStatus read(final byte[] _data, final int _offset) {
    return Borsh.read(FuelOverflowStatus.values(), _data, _offset);
  }
}
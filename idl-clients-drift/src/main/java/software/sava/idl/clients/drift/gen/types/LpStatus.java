package software.sava.idl.clients.drift.gen.types;

import software.sava.core.borsh.Borsh;

public enum LpStatus implements Borsh.Enum {

  Uncollateralized,
  Active,
  Decommissioning;

  public static LpStatus read(final byte[] _data, final int _offset) {
    return Borsh.read(LpStatus.values(), _data, _offset);
  }
}
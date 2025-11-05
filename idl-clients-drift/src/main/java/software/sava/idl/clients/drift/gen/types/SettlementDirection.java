package software.sava.idl.clients.drift.gen.types;

import software.sava.core.borsh.Borsh;

public enum SettlementDirection implements Borsh.Enum {

  ToLpPool,
  FromLpPool,
  None;

  public static SettlementDirection read(final byte[] _data, final int _offset) {
    return Borsh.read(SettlementDirection.values(), _data, _offset);
  }
}
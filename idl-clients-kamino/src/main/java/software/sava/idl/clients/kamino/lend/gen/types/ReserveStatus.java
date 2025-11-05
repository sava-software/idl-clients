package software.sava.idl.clients.kamino.lend.gen.types;

import software.sava.core.borsh.Borsh;

public enum ReserveStatus implements Borsh.Enum {

  Active,
  Obsolete,
  Hidden;

  public static ReserveStatus read(final byte[] _data, final int _offset) {
    return Borsh.read(ReserveStatus.values(), _data, _offset);
  }
}
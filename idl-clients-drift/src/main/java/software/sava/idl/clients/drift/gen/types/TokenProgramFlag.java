package software.sava.idl.clients.drift.gen.types;

import software.sava.core.borsh.Borsh;

public enum TokenProgramFlag implements Borsh.Enum {

  Token2022,
  TransferHook;

  public static TokenProgramFlag read(final byte[] _data, final int _offset) {
    return Borsh.read(TokenProgramFlag.values(), _data, _offset);
  }
}
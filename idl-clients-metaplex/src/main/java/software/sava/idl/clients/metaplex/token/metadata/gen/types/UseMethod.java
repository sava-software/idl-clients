package software.sava.idl.clients.metaplex.token.metadata.gen.types;

import software.sava.core.borsh.Borsh;

public enum UseMethod implements Borsh.Enum {

  Burn,
  Multiple,
  Single;

  public static UseMethod read(final byte[] _data, final int _offset) {
    return Borsh.read(UseMethod.values(), _data, _offset);
  }
}
package software.sava.idl.clients.metaplex.token.metadata.gen.types;

import software.sava.core.borsh.Borsh;

public enum TokenState implements Borsh.Enum {

  Unlocked,
  Locked,
  Listed;

  public static TokenState read(final byte[] _data, final int _offset) {
    return Borsh.read(TokenState.values(), _data, _offset);
  }
}
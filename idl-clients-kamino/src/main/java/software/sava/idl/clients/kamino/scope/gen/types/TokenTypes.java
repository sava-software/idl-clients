package software.sava.idl.clients.kamino.scope.gen.types;

import software.sava.core.borsh.Borsh;

public enum TokenTypes implements Borsh.Enum {

  TokenA,
  TokenB;

  public static TokenTypes read(final byte[] _data, final int _offset) {
    return Borsh.read(TokenTypes.values(), _data, _offset);
  }
}
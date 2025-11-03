package software.sava.idl.clients.token.gen.types;

import software.sava.core.borsh.Borsh;

public enum AccountState implements Borsh.Enum {

  uninitialized,
  initialized,
  frozen;

  public static AccountState read(final byte[] _data, final int _offset) {
    return Borsh.read(AccountState.values(), _data, _offset);
  }
}
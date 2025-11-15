package software.sava.idl.clients.spl.system.gen.types;

import software.sava.core.borsh.Borsh;

public enum NonceState implements Borsh.Enum {

  uninitialized,
  initialized;

  public static NonceState read(final byte[] _data, final int _offset) {
    return Borsh.read(NonceState.values(), _data, _offset);
  }
}
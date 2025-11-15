package software.sava.idl.clients.spl.system.gen.types;

import software.sava.core.borsh.Borsh;

public enum NonceVersion implements Borsh.Enum {

  legacy,
  current;

  public static NonceVersion read(final byte[] _data, final int _offset) {
    return Borsh.read(NonceVersion.values(), _data, _offset);
  }
}
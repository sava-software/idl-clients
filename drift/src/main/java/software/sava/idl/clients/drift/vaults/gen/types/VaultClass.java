package software.sava.idl.clients.drift.vaults.gen.types;

import software.sava.core.borsh.Borsh;

public enum VaultClass implements Borsh.Enum {

  Normal,
  Trusted;

  public static VaultClass read(final byte[] _data, final int _offset) {
    return Borsh.read(VaultClass.values(), _data, _offset);
  }
}
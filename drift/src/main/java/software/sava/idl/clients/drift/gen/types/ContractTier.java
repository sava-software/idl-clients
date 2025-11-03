package software.sava.idl.clients.drift.gen.types;

import software.sava.core.borsh.Borsh;

public enum ContractTier implements Borsh.Enum {

  A,
  B,
  C,
  Speculative,
  HighlySpeculative,
  Isolated;

  public static ContractTier read(final byte[] _data, final int _offset) {
    return Borsh.read(ContractTier.values(), _data, _offset);
  }
}
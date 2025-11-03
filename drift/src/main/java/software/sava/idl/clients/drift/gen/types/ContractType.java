package software.sava.idl.clients.drift.gen.types;

import software.sava.core.borsh.Borsh;

public enum ContractType implements Borsh.Enum {

  Perpetual,
  Future,
  Prediction;

  public static ContractType read(final byte[] _data, final int _offset) {
    return Borsh.read(ContractType.values(), _data, _offset);
  }
}
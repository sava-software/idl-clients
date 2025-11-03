package software.sava.idl.clients.drift.gen.types;

import software.sava.core.borsh.Borsh;

public enum ConstituentStatus implements Borsh.Enum {

  ReduceOnly,
  Decommissioned;

  public static ConstituentStatus read(final byte[] _data, final int _offset) {
    return Borsh.read(ConstituentStatus.values(), _data, _offset);
  }
}
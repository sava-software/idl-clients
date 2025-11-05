package software.sava.idl.clients.drift.vaults.gen.types;

import software.sava.core.borsh.Borsh;

public enum FeeUpdateAction implements Borsh.Enum {

  Pending,
  Applied,
  Cancelled;

  public static FeeUpdateAction read(final byte[] _data, final int _offset) {
    return Borsh.read(FeeUpdateAction.values(), _data, _offset);
  }
}
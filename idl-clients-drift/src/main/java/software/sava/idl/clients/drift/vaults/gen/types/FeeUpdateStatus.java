package software.sava.idl.clients.drift.vaults.gen.types;

import software.sava.core.borsh.Borsh;

public enum FeeUpdateStatus implements Borsh.Enum {

  None,
  PendingFeeUpdate;

  public static FeeUpdateStatus read(final byte[] _data, final int _offset) {
    return Borsh.read(FeeUpdateStatus.values(), _data, _offset);
  }
}
package software.sava.idl.clients.drift.gen.types;

import software.sava.core.borsh.Borsh;

public enum DepositDirection implements Borsh.Enum {

  Deposit,
  Withdraw;

  public static DepositDirection read(final byte[] _data, final int _offset) {
    return Borsh.read(DepositDirection.values(), _data, _offset);
  }
}
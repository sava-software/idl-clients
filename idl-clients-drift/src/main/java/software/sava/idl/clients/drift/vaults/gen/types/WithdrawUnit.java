package software.sava.idl.clients.drift.vaults.gen.types;

import software.sava.core.borsh.Borsh;

public enum WithdrawUnit implements Borsh.Enum {

  Shares,
  Token,
  SharesPercent;

  public static WithdrawUnit read(final byte[] _data, final int _offset) {
    return Borsh.read(WithdrawUnit.values(), _data, _offset);
  }
}
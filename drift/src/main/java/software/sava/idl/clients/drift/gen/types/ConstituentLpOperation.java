package software.sava.idl.clients.drift.gen.types;

import software.sava.core.borsh.Borsh;

public enum ConstituentLpOperation implements Borsh.Enum {

  Swap,
  Deposit,
  Withdraw;

  public static ConstituentLpOperation read(final byte[] _data, final int _offset) {
    return Borsh.read(ConstituentLpOperation.values(), _data, _offset);
  }
}
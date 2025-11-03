package software.sava.idl.clients.drift.gen.types;

import software.sava.core.borsh.Borsh;

public enum SpotBalanceType implements Borsh.Enum {

  Deposit,
  Borrow;

  public static SpotBalanceType read(final byte[] _data, final int _offset) {
    return Borsh.read(SpotBalanceType.values(), _data, _offset);
  }
}
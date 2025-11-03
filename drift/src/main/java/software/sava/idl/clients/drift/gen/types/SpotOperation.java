package software.sava.idl.clients.drift.gen.types;

import software.sava.core.borsh.Borsh;

public enum SpotOperation implements Borsh.Enum {

  UpdateCumulativeInterest,
  Fill,
  Deposit,
  Withdraw,
  Liquidation;

  public static SpotOperation read(final byte[] _data, final int _offset) {
    return Borsh.read(SpotOperation.values(), _data, _offset);
  }
}
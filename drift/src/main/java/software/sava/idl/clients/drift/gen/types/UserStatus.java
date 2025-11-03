package software.sava.idl.clients.drift.gen.types;

import software.sava.core.borsh.Borsh;

public enum UserStatus implements Borsh.Enum {

  BeingLiquidated,
  Bankrupt,
  ReduceOnly,
  AdvancedLp,
  ProtectedMakerOrders;

  public static UserStatus read(final byte[] _data, final int _offset) {
    return Borsh.read(UserStatus.values(), _data, _offset);
  }
}
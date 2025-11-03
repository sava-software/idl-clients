package software.sava.idl.clients.drift.gen.types;

import software.sava.core.borsh.Borsh;

public enum LiquidationMultiplierType implements Borsh.Enum {

  Discount,
  Premium;

  public static LiquidationMultiplierType read(final byte[] _data, final int _offset) {
    return Borsh.read(LiquidationMultiplierType.values(), _data, _offset);
  }
}
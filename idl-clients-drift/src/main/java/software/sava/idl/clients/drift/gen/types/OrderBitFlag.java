package software.sava.idl.clients.drift.gen.types;

import software.sava.core.borsh.Borsh;

public enum OrderBitFlag implements Borsh.Enum {

  SignedMessage,
  OracleTriggerMarket,
  SafeTriggerOrder,
  NewTriggerReduceOnly,
  HasBuilder;

  public static OrderBitFlag read(final byte[] _data, final int _offset) {
    return Borsh.read(OrderBitFlag.values(), _data, _offset);
  }
}
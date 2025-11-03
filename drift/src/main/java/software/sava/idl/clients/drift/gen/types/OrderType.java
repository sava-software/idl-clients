package software.sava.idl.clients.drift.gen.types;

import software.sava.core.borsh.Borsh;

public enum OrderType implements Borsh.Enum {

  Market,
  Limit,
  TriggerMarket,
  TriggerLimit,
  Oracle;

  public static OrderType read(final byte[] _data, final int _offset) {
    return Borsh.read(OrderType.values(), _data, _offset);
  }
}
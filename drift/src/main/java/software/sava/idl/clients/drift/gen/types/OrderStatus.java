package software.sava.idl.clients.drift.gen.types;

import software.sava.core.borsh.Borsh;

public enum OrderStatus implements Borsh.Enum {

  Init,
  Open,
  Filled,
  Canceled;

  public static OrderStatus read(final byte[] _data, final int _offset) {
    return Borsh.read(OrderStatus.values(), _data, _offset);
  }
}
package software.sava.idl.clients.drift.gen.types;

import software.sava.core.borsh.Borsh;

public enum OrderAction implements Borsh.Enum {

  Place,
  Cancel,
  Fill,
  Trigger,
  Expire;

  public static OrderAction read(final byte[] _data, final int _offset) {
    return Borsh.read(OrderAction.values(), _data, _offset);
  }
}
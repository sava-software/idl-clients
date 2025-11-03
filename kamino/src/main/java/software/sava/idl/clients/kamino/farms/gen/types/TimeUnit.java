package software.sava.idl.clients.kamino.farms.gen.types;

import software.sava.core.borsh.Borsh;

public enum TimeUnit implements Borsh.Enum {

  Seconds,
  Slots;

  public static TimeUnit read(final byte[] _data, final int _offset) {
    return Borsh.read(TimeUnit.values(), _data, _offset);
  }
}
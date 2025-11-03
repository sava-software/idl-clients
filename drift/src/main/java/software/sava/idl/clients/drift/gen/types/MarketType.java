package software.sava.idl.clients.drift.gen.types;

import software.sava.core.borsh.Borsh;

public enum MarketType implements Borsh.Enum {

  Spot,
  Perp;

  public static MarketType read(final byte[] _data, final int _offset) {
    return Borsh.read(MarketType.values(), _data, _offset);
  }
}
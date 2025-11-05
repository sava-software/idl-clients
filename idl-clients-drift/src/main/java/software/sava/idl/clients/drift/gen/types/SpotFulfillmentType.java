package software.sava.idl.clients.drift.gen.types;

import software.sava.core.borsh.Borsh;

public enum SpotFulfillmentType implements Borsh.Enum {

  SerumV3,
  Match,
  PhoenixV1,
  OpenbookV2;

  public static SpotFulfillmentType read(final byte[] _data, final int _offset) {
    return Borsh.read(SpotFulfillmentType.values(), _data, _offset);
  }
}
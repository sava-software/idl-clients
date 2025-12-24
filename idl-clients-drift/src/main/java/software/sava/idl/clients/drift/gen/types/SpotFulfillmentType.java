package software.sava.idl.clients.drift.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

public enum SpotFulfillmentType implements RustEnum {

  SerumV3,
  Match,
  PhoenixV1,
  OpenbookV2;

  public static SpotFulfillmentType read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, SpotFulfillmentType.values(), _data, _offset);
  }
}
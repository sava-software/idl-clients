package software.sava.idl.clients.drift.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

public enum OrderType implements RustEnum {

  Market,
  Limit,
  TriggerMarket,
  TriggerLimit,
  Oracle;

  public static OrderType read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, OrderType.values(), _data, _offset);
  }
}
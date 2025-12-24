package software.sava.idl.clients.drift.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

public enum MarketType implements RustEnum {

  Spot,
  Perp;

  public static MarketType read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, MarketType.values(), _data, _offset);
  }
}
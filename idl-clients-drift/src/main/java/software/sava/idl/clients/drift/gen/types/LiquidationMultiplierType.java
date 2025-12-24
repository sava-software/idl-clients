package software.sava.idl.clients.drift.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

public enum LiquidationMultiplierType implements RustEnum {

  Discount,
  Premium;

  public static LiquidationMultiplierType read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, LiquidationMultiplierType.values(), _data, _offset);
  }
}
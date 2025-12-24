package software.sava.idl.clients.drift.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

public enum OrderBitFlag implements RustEnum {

  SignedMessage,
  OracleTriggerMarket,
  SafeTriggerOrder,
  NewTriggerReduceOnly,
  HasBuilder;

  public static OrderBitFlag read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, OrderBitFlag.values(), _data, _offset);
  }
}
package software.sava.idl.clients.drift.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

public enum OrderStatus implements RustEnum {

  Init,
  Open,
  Filled,
  Canceled;

  public static OrderStatus read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, OrderStatus.values(), _data, _offset);
  }
}
package software.sava.idl.clients.drift.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

public enum OrderAction implements RustEnum {

  Place,
  Cancel,
  Fill,
  Trigger,
  Expire;

  public static OrderAction read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, OrderAction.values(), _data, _offset);
  }
}
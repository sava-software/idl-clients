package software.sava.idl.clients.drift.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

public enum SettlementDirection implements RustEnum {

  ToLpPool,
  FromLpPool,
  None;

  public static SettlementDirection read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, SettlementDirection.values(), _data, _offset);
  }
}
package software.sava.idl.clients.drift.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

public enum SettlePnlMode implements RustEnum {

  MustSettle,
  TrySettle;

  public static SettlePnlMode read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, SettlePnlMode.values(), _data, _offset);
  }
}
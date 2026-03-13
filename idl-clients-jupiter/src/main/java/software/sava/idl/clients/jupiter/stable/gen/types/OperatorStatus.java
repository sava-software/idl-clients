package software.sava.idl.clients.jupiter.stable.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

public enum OperatorStatus implements RustEnum {

  Enabled,
  Disabled;

  public static OperatorStatus read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, OperatorStatus.values(), _data, _offset);
  }
}
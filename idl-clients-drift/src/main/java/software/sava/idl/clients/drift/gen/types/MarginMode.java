package software.sava.idl.clients.drift.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

public enum MarginMode implements RustEnum {

  Default,
  HighLeverage,
  HighLeverageMaintenance;

  public static MarginMode read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, MarginMode.values(), _data, _offset);
  }
}
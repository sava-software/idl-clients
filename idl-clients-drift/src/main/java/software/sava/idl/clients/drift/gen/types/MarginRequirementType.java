package software.sava.idl.clients.drift.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

public enum MarginRequirementType implements RustEnum {

  Initial,
  Fill,
  Maintenance;

  public static MarginRequirementType read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, MarginRequirementType.values(), _data, _offset);
  }
}
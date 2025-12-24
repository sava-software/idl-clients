package software.sava.idl.clients.drift.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

public enum ContractType implements RustEnum {

  Perpetual,
  Future,
  Prediction;

  public static ContractType read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, ContractType.values(), _data, _offset);
  }
}
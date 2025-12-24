package software.sava.idl.clients.drift.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

public enum ContractTier implements RustEnum {

  A,
  B,
  C,
  Speculative,
  HighlySpeculative,
  Isolated;

  public static ContractTier read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, ContractTier.values(), _data, _offset);
  }
}
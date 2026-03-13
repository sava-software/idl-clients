package software.sava.idl.clients.jupiter.stable.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

public enum BenefactorStatus implements RustEnum {

  Active,
  Disabled;

  public static BenefactorStatus read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, BenefactorStatus.values(), _data, _offset);
  }
}
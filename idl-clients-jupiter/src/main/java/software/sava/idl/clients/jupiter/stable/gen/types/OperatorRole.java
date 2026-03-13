package software.sava.idl.clients.jupiter.stable.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

public enum OperatorRole implements RustEnum {

  Admin,
  PeriodManager,
  GlobalDisabler,
  VaultManager,
  VaultDisabler,
  BenefactorManager,
  BenefactorDisabler,
  PegManager,
  CollateralManager;

  public static OperatorRole read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, OperatorRole.values(), _data, _offset);
  }
}
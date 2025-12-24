package software.sava.idl.clients.drift.vaults.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

public enum FuelDistributionMode implements RustEnum {

  UsersOnly,
  UsersAndManager;

  public static FuelDistributionMode read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, FuelDistributionMode.values(), _data, _offset);
  }
}
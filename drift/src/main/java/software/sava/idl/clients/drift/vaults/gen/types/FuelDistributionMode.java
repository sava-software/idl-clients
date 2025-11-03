package software.sava.idl.clients.drift.vaults.gen.types;

import software.sava.core.borsh.Borsh;

public enum FuelDistributionMode implements Borsh.Enum {

  UsersOnly,
  UsersAndManager;

  public static FuelDistributionMode read(final byte[] _data, final int _offset) {
    return Borsh.read(FuelDistributionMode.values(), _data, _offset);
  }
}
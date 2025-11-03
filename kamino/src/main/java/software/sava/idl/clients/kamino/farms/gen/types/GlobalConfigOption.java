package software.sava.idl.clients.kamino.farms.gen.types;

import software.sava.core.borsh.Borsh;

public enum GlobalConfigOption implements Borsh.Enum {

  SetPendingGlobalAdmin,
  SetTreasuryFeeBps;

  public static GlobalConfigOption read(final byte[] _data, final int _offset) {
    return Borsh.read(GlobalConfigOption.values(), _data, _offset);
  }
}
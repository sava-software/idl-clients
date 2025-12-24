package software.sava.idl.clients.kamino.farms.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

public enum GlobalConfigOption implements RustEnum {

  SetPendingGlobalAdmin,
  SetTreasuryFeeBps;

  public static GlobalConfigOption read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, GlobalConfigOption.values(), _data, _offset);
  }
}
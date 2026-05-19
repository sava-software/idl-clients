package software.sava.idl.clients.phoenix.perpetuals.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

public enum CommodityMarketState implements RustEnum {

  Active,
  AfterHours,
  Reopen;

  public static CommodityMarketState read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, CommodityMarketState.values(), _data, _offset);
  }
}
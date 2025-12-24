package software.sava.idl.clients.drift.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

public enum TwapPeriod implements RustEnum {

  FundingPeriod,
  FiveMin;

  public static TwapPeriod read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, TwapPeriod.values(), _data, _offset);
  }
}
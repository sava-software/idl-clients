package software.sava.idl.clients.drift.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

public enum RevenueShareOrderBitFlag implements RustEnum {

  Init,
  Open,
  Completed,
  Referral;

  public static RevenueShareOrderBitFlag read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, RevenueShareOrderBitFlag.values(), _data, _offset);
  }
}
package software.sava.idl.clients.drift.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

public enum LpPoolFeatureBitFlags implements RustEnum {

  SettleLpPool,
  SwapLpPool,
  MintRedeemLpPool;

  public static LpPoolFeatureBitFlags read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, LpPoolFeatureBitFlags.values(), _data, _offset);
  }
}
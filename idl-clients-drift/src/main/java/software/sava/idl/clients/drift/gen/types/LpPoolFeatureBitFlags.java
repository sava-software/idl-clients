package software.sava.idl.clients.drift.gen.types;

import software.sava.core.borsh.Borsh;

public enum LpPoolFeatureBitFlags implements Borsh.Enum {

  SettleLpPool,
  SwapLpPool,
  MintRedeemLpPool;

  public static LpPoolFeatureBitFlags read(final byte[] _data, final int _offset) {
    return Borsh.read(LpPoolFeatureBitFlags.values(), _data, _offset);
  }
}
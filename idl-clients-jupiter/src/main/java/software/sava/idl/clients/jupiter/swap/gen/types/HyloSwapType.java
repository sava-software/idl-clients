package software.sava.idl.clients.jupiter.swap.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

public enum HyloSwapType implements RustEnum {

  MintStable,
  RedeemStable,
  MintLever,
  RedeemLever,
  SwapStableToLever,
  SwapLeverToStable,
  StabilityPoolDeposit,
  StabilityPoolWithdraw;

  public static HyloSwapType read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, HyloSwapType.values(), _data, _offset);
  }
}
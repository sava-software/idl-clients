package software.sava.idl.clients.drift.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

public enum OrderActionExplanation implements RustEnum {

  None,
  InsufficientFreeCollateral,
  OraclePriceBreachedLimitPrice,
  MarketOrderFilledToLimitPrice,
  OrderExpired,
  Liquidation,
  OrderFilledWithAMM,
  OrderFilledWithAMMJit,
  OrderFilledWithMatch,
  OrderFilledWithMatchJit,
  MarketExpired,
  RiskingIncreasingOrder,
  ReduceOnlyOrderIncreasedPosition,
  OrderFillWithSerum,
  NoBorrowLiquidity,
  OrderFillWithPhoenix,
  OrderFilledWithAMMJitLPSplit,
  OrderFilledWithLPJit,
  DeriskLp,
  OrderFilledWithOpenbookV2,
  TransferPerpPosition;

  public static OrderActionExplanation read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, OrderActionExplanation.values(), _data, _offset);
  }
}
package software.sava.idl.clients.kamino.farms.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

public enum FarmConfigOption implements RustEnum {

  UpdateRewardRps,
  UpdateRewardMinClaimDuration,
  WithdrawAuthority,
  DepositWarmupPeriod,
  WithdrawCooldownPeriod,
  RewardType,
  RpsDecimals,
  LockingMode,
  LockingStartTimestamp,
  LockingDuration,
  LockingEarlyWithdrawalPenaltyBps,
  DepositCapAmount,
  SlashedAmountSpillAddress,
  ScopePricesAccount,
  ScopeOraclePriceId,
  ScopeOracleMaxAge,
  UpdateRewardScheduleCurvePoints,
  UpdatePendingFarmAdmin,
  UpdateStrategyId,
  UpdateDelegatedRpsAdmin,
  UpdateVaultId,
  UpdateExtraDelegatedAuthority,
  UpdateIsRewardUserOnceEnabled,
  UpdateDelegatedAuthority,
  UpdateIsHarvestingPermissionless;

  public static FarmConfigOption read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, FarmConfigOption.values(), _data, _offset);
  }
}
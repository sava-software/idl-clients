package software.sava.idl.clients.kamino.farms.gen.types;

import software.sava.core.borsh.Borsh;

public enum FarmConfigOption implements Borsh.Enum {

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
  UpdateExtraDelegatedAuthority;

  public static FarmConfigOption read(final byte[] _data, final int _offset) {
    return Borsh.read(FarmConfigOption.values(), _data, _offset);
  }
}
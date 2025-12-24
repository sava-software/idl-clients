package software.sava.idl.clients.kamino.vaults.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

public enum VaultConfigField implements RustEnum {

  PerformanceFeeBps,
  ManagementFeeBps,
  MinDepositAmount,
  MinWithdrawAmount,
  MinInvestAmount,
  MinInvestDelaySlots,
  CrankFundFeePerReserve,
  PendingVaultAdmin,
  Name,
  LookupTable,
  Farm,
  AllocationAdmin,
  UnallocatedWeight,
  UnallocatedTokensCap,
  WithdrawalPenaltyLamports,
  WithdrawalPenaltyBps,
  FirstLossCapitalFarm,
  AllowAllocationsInWhitelistedReservesOnly,
  AllowInvestInWhitelistedReservesOnly;

  public static VaultConfigField read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, VaultConfigField.values(), _data, _offset);
  }
}
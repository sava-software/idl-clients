package software.sava.idl.clients.nt.bundle.gen.events;

import software.sava.idl.clients.core.gen.SerDe;

public sealed interface NtbundleEvent extends SerDe permits
    Allocated,
    AllocationsUpdated,
    AssignedProfitShare,
    AuthorizedReceiverAdded,
    AuthorizedReceiverRemoved,
    BundleCreatorAccountChanged,
    BundleDepositorInitialized,
    BundleMasterAccountInitialized,
    BundleMasterAdminChanged,
    ChangedCoreParams,
    ChargedFeesToUser,
    DelaysSet,
    DepositRequested,
    DepositedToDriftVault,
    DistributedToReceivers,
    DistributionEnded,
    DistributionStarted,
    DustSharesBurned,
    InitializedNewVaultDepositor,
    InitializedReceivers,
    InitializedVault,
    ManagerWithdrawal,
    MaxDepositAmountSet,
    MinDepositAmountSet,
    NettingCompleted,
    NewKeeperSet,
    NewManagerSet,
    OracleBufferSet,
    OracleMaxAgeSet,
    OracleUpdateTimeLimitSet,
    OracleUpdated,
    PausedDepositsWithdrawals,
    Redeemed,
    Refilled,
    RefundedDeposit,
    RequestedWithdrawalToDriftVault,
    StrategyAdded,
    StrategyEnabled,
    StrategyRemoved,
    UserBundleAccountClosed,
    VaultNeutralFeeIncrementerSet,
    WithdrawalRedemptionScheduleSet,
    WithdrawalRequested,
    WithdrawnFromDriftVault {

  static NtbundleEvent read(final byte[] _data, final int _offset) {
    if (Allocated.DISCRIMINATOR.equals(_data, _offset)) {
      return Allocated.read(_data, _offset);
    } else if (AllocationsUpdated.DISCRIMINATOR.equals(_data, _offset)) {
      return AllocationsUpdated.read(_data, _offset);
    } else if (AssignedProfitShare.DISCRIMINATOR.equals(_data, _offset)) {
      return AssignedProfitShare.read(_data, _offset);
    } else if (AuthorizedReceiverAdded.DISCRIMINATOR.equals(_data, _offset)) {
      return AuthorizedReceiverAdded.read(_data, _offset);
    } else if (AuthorizedReceiverRemoved.DISCRIMINATOR.equals(_data, _offset)) {
      return AuthorizedReceiverRemoved.read(_data, _offset);
    } else if (BundleCreatorAccountChanged.DISCRIMINATOR.equals(_data, _offset)) {
      return BundleCreatorAccountChanged.read(_data, _offset);
    } else if (BundleDepositorInitialized.DISCRIMINATOR.equals(_data, _offset)) {
      return BundleDepositorInitialized.read(_data, _offset);
    } else if (BundleMasterAccountInitialized.DISCRIMINATOR.equals(_data, _offset)) {
      return BundleMasterAccountInitialized.read(_data, _offset);
    } else if (BundleMasterAdminChanged.DISCRIMINATOR.equals(_data, _offset)) {
      return BundleMasterAdminChanged.read(_data, _offset);
    } else if (ChangedCoreParams.DISCRIMINATOR.equals(_data, _offset)) {
      return ChangedCoreParams.read(_data, _offset);
    } else if (ChargedFeesToUser.DISCRIMINATOR.equals(_data, _offset)) {
      return ChargedFeesToUser.read(_data, _offset);
    } else if (DelaysSet.DISCRIMINATOR.equals(_data, _offset)) {
      return DelaysSet.read(_data, _offset);
    } else if (DepositRequested.DISCRIMINATOR.equals(_data, _offset)) {
      return DepositRequested.read(_data, _offset);
    } else if (DepositedToDriftVault.DISCRIMINATOR.equals(_data, _offset)) {
      return DepositedToDriftVault.read(_data, _offset);
    } else if (DistributedToReceivers.DISCRIMINATOR.equals(_data, _offset)) {
      return DistributedToReceivers.read(_data, _offset);
    } else if (DistributionEnded.DISCRIMINATOR.equals(_data, _offset)) {
      return DistributionEnded.read(_data, _offset);
    } else if (DistributionStarted.DISCRIMINATOR.equals(_data, _offset)) {
      return DistributionStarted.read(_data, _offset);
    } else if (DustSharesBurned.DISCRIMINATOR.equals(_data, _offset)) {
      return DustSharesBurned.read(_data, _offset);
    } else if (InitializedNewVaultDepositor.DISCRIMINATOR.equals(_data, _offset)) {
      return InitializedNewVaultDepositor.read(_data, _offset);
    } else if (InitializedReceivers.DISCRIMINATOR.equals(_data, _offset)) {
      return InitializedReceivers.read(_data, _offset);
    } else if (InitializedVault.DISCRIMINATOR.equals(_data, _offset)) {
      return InitializedVault.read(_data, _offset);
    } else if (ManagerWithdrawal.DISCRIMINATOR.equals(_data, _offset)) {
      return ManagerWithdrawal.read(_data, _offset);
    } else if (MaxDepositAmountSet.DISCRIMINATOR.equals(_data, _offset)) {
      return MaxDepositAmountSet.read(_data, _offset);
    } else if (MinDepositAmountSet.DISCRIMINATOR.equals(_data, _offset)) {
      return MinDepositAmountSet.read(_data, _offset);
    } else if (NettingCompleted.DISCRIMINATOR.equals(_data, _offset)) {
      return NettingCompleted.read(_data, _offset);
    } else if (NewKeeperSet.DISCRIMINATOR.equals(_data, _offset)) {
      return NewKeeperSet.read(_data, _offset);
    } else if (NewManagerSet.DISCRIMINATOR.equals(_data, _offset)) {
      return NewManagerSet.read(_data, _offset);
    } else if (OracleBufferSet.DISCRIMINATOR.equals(_data, _offset)) {
      return OracleBufferSet.read(_data, _offset);
    } else if (OracleMaxAgeSet.DISCRIMINATOR.equals(_data, _offset)) {
      return OracleMaxAgeSet.read(_data, _offset);
    } else if (OracleUpdateTimeLimitSet.DISCRIMINATOR.equals(_data, _offset)) {
      return OracleUpdateTimeLimitSet.read(_data, _offset);
    } else if (OracleUpdated.DISCRIMINATOR.equals(_data, _offset)) {
      return OracleUpdated.read(_data, _offset);
    } else if (PausedDepositsWithdrawals.DISCRIMINATOR.equals(_data, _offset)) {
      return PausedDepositsWithdrawals.read(_data, _offset);
    } else if (Redeemed.DISCRIMINATOR.equals(_data, _offset)) {
      return Redeemed.read(_data, _offset);
    } else if (Refilled.DISCRIMINATOR.equals(_data, _offset)) {
      return Refilled.read(_data, _offset);
    } else if (RefundedDeposit.DISCRIMINATOR.equals(_data, _offset)) {
      return RefundedDeposit.read(_data, _offset);
    } else if (RequestedWithdrawalToDriftVault.DISCRIMINATOR.equals(_data, _offset)) {
      return RequestedWithdrawalToDriftVault.read(_data, _offset);
    } else if (StrategyAdded.DISCRIMINATOR.equals(_data, _offset)) {
      return StrategyAdded.read(_data, _offset);
    } else if (StrategyEnabled.DISCRIMINATOR.equals(_data, _offset)) {
      return StrategyEnabled.read(_data, _offset);
    } else if (StrategyRemoved.DISCRIMINATOR.equals(_data, _offset)) {
      return StrategyRemoved.read(_data, _offset);
    } else if (UserBundleAccountClosed.DISCRIMINATOR.equals(_data, _offset)) {
      return UserBundleAccountClosed.read(_data, _offset);
    } else if (VaultNeutralFeeIncrementerSet.DISCRIMINATOR.equals(_data, _offset)) {
      return VaultNeutralFeeIncrementerSet.read(_data, _offset);
    } else if (WithdrawalRedemptionScheduleSet.DISCRIMINATOR.equals(_data, _offset)) {
      return WithdrawalRedemptionScheduleSet.read(_data, _offset);
    } else if (WithdrawalRequested.DISCRIMINATOR.equals(_data, _offset)) {
      return WithdrawalRequested.read(_data, _offset);
    } else if (WithdrawnFromDriftVault.DISCRIMINATOR.equals(_data, _offset)) {
      return WithdrawnFromDriftVault.read(_data, _offset);
    } else {
      return null;
    }
  }

  static NtbundleEvent read(final byte[] _data) {
    return read(_data, 0);
  }

  static NtbundleEvent readCPI(final byte[] _data, final int _offset) {
    return read(_data, _offset + 8);
  }

  static NtbundleEvent readCPI(final byte[] _data) {
    return read(_data, 8);
  }
}
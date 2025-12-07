package software.sava.idl.clients.meteora.dlmm.gen.events;

import software.sava.core.borsh.Borsh;

public sealed interface LbClmmEvent extends Borsh permits
    AddLiquidity,
    ClaimFee,
    ClaimFee2,
    ClaimReward,
    ClaimReward2,
    CompositionFee,
    DecreasePositionLength,
    DynamicFeeParameterUpdate,
    FeeParameterUpdate,
    FundReward,
    GoToABin,
    IncreaseObservation,
    IncreasePositionLength,
    InitializeReward,
    LbPairCreate,
    PositionClose,
    PositionCreate,
    Rebalancing,
    RemoveLiquidity,
    Swap,
    UpdatePositionLockReleasePoint,
    UpdatePositionOperator,
    UpdateRewardDuration,
    UpdateRewardFunder,
    WithdrawIneligibleReward {

  static LbClmmEvent read(final byte[] _data, final int _offset) {
    if (AddLiquidity.DISCRIMINATOR.equals(_data, _offset)) {
      return AddLiquidity.read(_data, _offset);
    } else if (ClaimFee.DISCRIMINATOR.equals(_data, _offset)) {
      return ClaimFee.read(_data, _offset);
    } else if (ClaimFee2.DISCRIMINATOR.equals(_data, _offset)) {
      return ClaimFee2.read(_data, _offset);
    } else if (ClaimReward.DISCRIMINATOR.equals(_data, _offset)) {
      return ClaimReward.read(_data, _offset);
    } else if (ClaimReward2.DISCRIMINATOR.equals(_data, _offset)) {
      return ClaimReward2.read(_data, _offset);
    } else if (CompositionFee.DISCRIMINATOR.equals(_data, _offset)) {
      return CompositionFee.read(_data, _offset);
    } else if (DecreasePositionLength.DISCRIMINATOR.equals(_data, _offset)) {
      return DecreasePositionLength.read(_data, _offset);
    } else if (DynamicFeeParameterUpdate.DISCRIMINATOR.equals(_data, _offset)) {
      return DynamicFeeParameterUpdate.read(_data, _offset);
    } else if (FeeParameterUpdate.DISCRIMINATOR.equals(_data, _offset)) {
      return FeeParameterUpdate.read(_data, _offset);
    } else if (FundReward.DISCRIMINATOR.equals(_data, _offset)) {
      return FundReward.read(_data, _offset);
    } else if (GoToABin.DISCRIMINATOR.equals(_data, _offset)) {
      return GoToABin.read(_data, _offset);
    } else if (IncreaseObservation.DISCRIMINATOR.equals(_data, _offset)) {
      return IncreaseObservation.read(_data, _offset);
    } else if (IncreasePositionLength.DISCRIMINATOR.equals(_data, _offset)) {
      return IncreasePositionLength.read(_data, _offset);
    } else if (InitializeReward.DISCRIMINATOR.equals(_data, _offset)) {
      return InitializeReward.read(_data, _offset);
    } else if (LbPairCreate.DISCRIMINATOR.equals(_data, _offset)) {
      return LbPairCreate.read(_data, _offset);
    } else if (PositionClose.DISCRIMINATOR.equals(_data, _offset)) {
      return PositionClose.read(_data, _offset);
    } else if (PositionCreate.DISCRIMINATOR.equals(_data, _offset)) {
      return PositionCreate.read(_data, _offset);
    } else if (Rebalancing.DISCRIMINATOR.equals(_data, _offset)) {
      return Rebalancing.read(_data, _offset);
    } else if (RemoveLiquidity.DISCRIMINATOR.equals(_data, _offset)) {
      return RemoveLiquidity.read(_data, _offset);
    } else if (Swap.DISCRIMINATOR.equals(_data, _offset)) {
      return Swap.read(_data, _offset);
    } else if (UpdatePositionLockReleasePoint.DISCRIMINATOR.equals(_data, _offset)) {
      return UpdatePositionLockReleasePoint.read(_data, _offset);
    } else if (UpdatePositionOperator.DISCRIMINATOR.equals(_data, _offset)) {
      return UpdatePositionOperator.read(_data, _offset);
    } else if (UpdateRewardDuration.DISCRIMINATOR.equals(_data, _offset)) {
      return UpdateRewardDuration.read(_data, _offset);
    } else if (UpdateRewardFunder.DISCRIMINATOR.equals(_data, _offset)) {
      return UpdateRewardFunder.read(_data, _offset);
    } else if (WithdrawIneligibleReward.DISCRIMINATOR.equals(_data, _offset)) {
      return WithdrawIneligibleReward.read(_data, _offset);
    } else {
      return null;
    }
  }

  static LbClmmEvent read(final byte[] _data) {
    return read(_data, 0);
  }

  static LbClmmEvent readCPI(final byte[] _data, final int _offset) {
    return read(_data, _offset + 8);
  }

  static LbClmmEvent readCPI(final byte[] _data) {
    return read(_data, 8);
  }
}
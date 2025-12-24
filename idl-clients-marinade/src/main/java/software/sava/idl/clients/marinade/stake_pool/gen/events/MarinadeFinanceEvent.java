package software.sava.idl.clients.marinade.stake_pool.gen.events;

import software.sava.idl.clients.core.gen.SerDe;

public sealed interface MarinadeFinanceEvent extends SerDe permits
    ChangeAuthorityEvent,
    ConfigLpEvent,
    ConfigMarinadeEvent,
    InitializeEvent,
    EmergencyPauseEvent,
    ResumeEvent,
    ReallocValidatorListEvent,
    ReallocStakeListEvent,
    DeactivateStakeEvent,
    MergeStakesEvent,
    RedelegateEvent,
    StakeReserveEvent,
    UpdateActiveEvent,
    UpdateDeactivatedEvent,
    ClaimEvent,
    OrderUnstakeEvent,
    AddLiquidityEvent,
    LiquidUnstakeEvent,
    RemoveLiquidityEvent,
    AddValidatorEvent,
    RemoveValidatorEvent,
    SetValidatorScoreEvent,
    DepositStakeAccountEvent,
    DepositEvent,
    WithdrawStakeAccountEvent {

  static MarinadeFinanceEvent read(final byte[] _data, final int _offset) {
    if (ChangeAuthorityEvent.DISCRIMINATOR.equals(_data, _offset)) {
      return ChangeAuthorityEvent.read(_data, _offset);
    } else if (ConfigLpEvent.DISCRIMINATOR.equals(_data, _offset)) {
      return ConfigLpEvent.read(_data, _offset);
    } else if (ConfigMarinadeEvent.DISCRIMINATOR.equals(_data, _offset)) {
      return ConfigMarinadeEvent.read(_data, _offset);
    } else if (InitializeEvent.DISCRIMINATOR.equals(_data, _offset)) {
      return InitializeEvent.read(_data, _offset);
    } else if (EmergencyPauseEvent.DISCRIMINATOR.equals(_data, _offset)) {
      return EmergencyPauseEvent.read(_data, _offset);
    } else if (ResumeEvent.DISCRIMINATOR.equals(_data, _offset)) {
      return ResumeEvent.read(_data, _offset);
    } else if (ReallocValidatorListEvent.DISCRIMINATOR.equals(_data, _offset)) {
      return ReallocValidatorListEvent.read(_data, _offset);
    } else if (ReallocStakeListEvent.DISCRIMINATOR.equals(_data, _offset)) {
      return ReallocStakeListEvent.read(_data, _offset);
    } else if (DeactivateStakeEvent.DISCRIMINATOR.equals(_data, _offset)) {
      return DeactivateStakeEvent.read(_data, _offset);
    } else if (MergeStakesEvent.DISCRIMINATOR.equals(_data, _offset)) {
      return MergeStakesEvent.read(_data, _offset);
    } else if (RedelegateEvent.DISCRIMINATOR.equals(_data, _offset)) {
      return RedelegateEvent.read(_data, _offset);
    } else if (StakeReserveEvent.DISCRIMINATOR.equals(_data, _offset)) {
      return StakeReserveEvent.read(_data, _offset);
    } else if (UpdateActiveEvent.DISCRIMINATOR.equals(_data, _offset)) {
      return UpdateActiveEvent.read(_data, _offset);
    } else if (UpdateDeactivatedEvent.DISCRIMINATOR.equals(_data, _offset)) {
      return UpdateDeactivatedEvent.read(_data, _offset);
    } else if (ClaimEvent.DISCRIMINATOR.equals(_data, _offset)) {
      return ClaimEvent.read(_data, _offset);
    } else if (OrderUnstakeEvent.DISCRIMINATOR.equals(_data, _offset)) {
      return OrderUnstakeEvent.read(_data, _offset);
    } else if (AddLiquidityEvent.DISCRIMINATOR.equals(_data, _offset)) {
      return AddLiquidityEvent.read(_data, _offset);
    } else if (LiquidUnstakeEvent.DISCRIMINATOR.equals(_data, _offset)) {
      return LiquidUnstakeEvent.read(_data, _offset);
    } else if (RemoveLiquidityEvent.DISCRIMINATOR.equals(_data, _offset)) {
      return RemoveLiquidityEvent.read(_data, _offset);
    } else if (AddValidatorEvent.DISCRIMINATOR.equals(_data, _offset)) {
      return AddValidatorEvent.read(_data, _offset);
    } else if (RemoveValidatorEvent.DISCRIMINATOR.equals(_data, _offset)) {
      return RemoveValidatorEvent.read(_data, _offset);
    } else if (SetValidatorScoreEvent.DISCRIMINATOR.equals(_data, _offset)) {
      return SetValidatorScoreEvent.read(_data, _offset);
    } else if (DepositStakeAccountEvent.DISCRIMINATOR.equals(_data, _offset)) {
      return DepositStakeAccountEvent.read(_data, _offset);
    } else if (DepositEvent.DISCRIMINATOR.equals(_data, _offset)) {
      return DepositEvent.read(_data, _offset);
    } else if (WithdrawStakeAccountEvent.DISCRIMINATOR.equals(_data, _offset)) {
      return WithdrawStakeAccountEvent.read(_data, _offset);
    } else {
      return null;
    }
  }

  static MarinadeFinanceEvent read(final byte[] _data) {
    return read(_data, 0);
  }

  static MarinadeFinanceEvent readCPI(final byte[] _data, final int _offset) {
    return read(_data, _offset + 8);
  }

  static MarinadeFinanceEvent readCPI(final byte[] _data) {
    return read(_data, 8);
  }
}
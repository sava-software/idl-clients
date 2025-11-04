package software.sava.idl.clients.jupiter.voter.gen.events;

import software.sava.core.borsh.Borsh;

public sealed interface LockedVoterEvent extends Borsh permits
    ExtendLockDurationEvent,
    IncreaseLockedAmountEvent,
    MergePartialUnstakingEvent,
    NewEscrowEvent,
    NewLockerEvent,
    OpenPartialStakingEvent,
    LockerSetParamsEvent,
    SetVoteDelegateEvent,
    WithdrawPartialUnstakingEvent,
    ExitEscrowEvent {

  static LockedVoterEvent read(final byte[] _data, final int _offset) {
    if (ExtendLockDurationEvent.DISCRIMINATOR.equals(_data, _offset)) {
      return ExtendLockDurationEvent.read(_data, _offset);
    } else if (IncreaseLockedAmountEvent.DISCRIMINATOR.equals(_data, _offset)) {
      return IncreaseLockedAmountEvent.read(_data, _offset);
    } else if (MergePartialUnstakingEvent.DISCRIMINATOR.equals(_data, _offset)) {
      return MergePartialUnstakingEvent.read(_data, _offset);
    } else if (NewEscrowEvent.DISCRIMINATOR.equals(_data, _offset)) {
      return NewEscrowEvent.read(_data, _offset);
    } else if (NewLockerEvent.DISCRIMINATOR.equals(_data, _offset)) {
      return NewLockerEvent.read(_data, _offset);
    } else if (OpenPartialStakingEvent.DISCRIMINATOR.equals(_data, _offset)) {
      return OpenPartialStakingEvent.read(_data, _offset);
    } else if (LockerSetParamsEvent.DISCRIMINATOR.equals(_data, _offset)) {
      return LockerSetParamsEvent.read(_data, _offset);
    } else if (SetVoteDelegateEvent.DISCRIMINATOR.equals(_data, _offset)) {
      return SetVoteDelegateEvent.read(_data, _offset);
    } else if (WithdrawPartialUnstakingEvent.DISCRIMINATOR.equals(_data, _offset)) {
      return WithdrawPartialUnstakingEvent.read(_data, _offset);
    } else if (ExitEscrowEvent.DISCRIMINATOR.equals(_data, _offset)) {
      return ExitEscrowEvent.read(_data, _offset);
    } else {
      return null;
    }
  }

  static LockedVoterEvent read(final byte[] _data) {
    return read(_data, 0);
  }

  static LockedVoterEvent readCPI(final byte[] _data, final int _offset) {
    return read(_data, _offset + 8);
  }

  static LockedVoterEvent readCPI(final byte[] _data) {
    return read(_data, 8);
  }
}
package software.sava.idl.clients.loopscale.gen.events;

import software.sava.idl.clients.core.gen.SerDe;

public sealed interface LoopscaleEvent extends SerDe permits
    RewardsClaimedEvent,
    StakeEvent,
    TimelockCanceledEvent,
    TimelockCreatedEvent,
    TimelockExecutedEvent,
    UserRewardsInfoClosed {

  static LoopscaleEvent read(final byte[] _data, final int _offset) {
    if (RewardsClaimedEvent.DISCRIMINATOR.equals(_data, _offset)) {
      return RewardsClaimedEvent.read(_data, _offset);
    } else if (StakeEvent.DISCRIMINATOR.equals(_data, _offset)) {
      return StakeEvent.read(_data, _offset);
    } else if (TimelockCanceledEvent.DISCRIMINATOR.equals(_data, _offset)) {
      return TimelockCanceledEvent.read(_data, _offset);
    } else if (TimelockCreatedEvent.DISCRIMINATOR.equals(_data, _offset)) {
      return TimelockCreatedEvent.read(_data, _offset);
    } else if (TimelockExecutedEvent.DISCRIMINATOR.equals(_data, _offset)) {
      return TimelockExecutedEvent.read(_data, _offset);
    } else if (UserRewardsInfoClosed.DISCRIMINATOR.equals(_data, _offset)) {
      return UserRewardsInfoClosed.read(_data, _offset);
    } else {
      return null;
    }
  }

  static LoopscaleEvent read(final byte[] _data) {
    return read(_data, 0);
  }

  static LoopscaleEvent readCPI(final byte[] _data, final int _offset) {
    return read(_data, _offset + 8);
  }

  static LoopscaleEvent readCPI(final byte[] _data) {
    return read(_data, 8);
  }
}
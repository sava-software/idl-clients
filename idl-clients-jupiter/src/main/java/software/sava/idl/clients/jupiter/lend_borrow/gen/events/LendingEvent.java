package software.sava.idl.clients.jupiter.lend_borrow.gen.events;

import software.sava.idl.clients.core.gen.SerDe;

public sealed interface LendingEvent extends SerDe permits
    LogDeposit,
    LogRebalance,
    LogUpdateAuthority,
    LogUpdateAuths,
    LogUpdateRates,
    LogUpdateRebalancer,
    LogUpdateRewards,
    LogWithdraw {

  static LendingEvent read(final byte[] _data, final int _offset) {
    if (LogDeposit.DISCRIMINATOR.equals(_data, _offset)) {
      return LogDeposit.read(_data, _offset);
    } else if (LogRebalance.DISCRIMINATOR.equals(_data, _offset)) {
      return LogRebalance.read(_data, _offset);
    } else if (LogUpdateAuthority.DISCRIMINATOR.equals(_data, _offset)) {
      return LogUpdateAuthority.read(_data, _offset);
    } else if (LogUpdateAuths.DISCRIMINATOR.equals(_data, _offset)) {
      return LogUpdateAuths.read(_data, _offset);
    } else if (LogUpdateRates.DISCRIMINATOR.equals(_data, _offset)) {
      return LogUpdateRates.read(_data, _offset);
    } else if (LogUpdateRebalancer.DISCRIMINATOR.equals(_data, _offset)) {
      return LogUpdateRebalancer.read(_data, _offset);
    } else if (LogUpdateRewards.DISCRIMINATOR.equals(_data, _offset)) {
      return LogUpdateRewards.read(_data, _offset);
    } else if (LogWithdraw.DISCRIMINATOR.equals(_data, _offset)) {
      return LogWithdraw.read(_data, _offset);
    } else {
      return null;
    }
  }

  static LendingEvent read(final byte[] _data) {
    return read(_data, 0);
  }

  static LendingEvent readCPI(final byte[] _data, final int _offset) {
    return read(_data, _offset + 8);
  }

  static LendingEvent readCPI(final byte[] _data) {
    return read(_data, 8);
  }
}
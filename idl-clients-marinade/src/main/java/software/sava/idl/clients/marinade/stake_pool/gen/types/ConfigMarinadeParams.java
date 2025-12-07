package software.sava.idl.clients.marinade.stake_pool.gen.types;

import java.lang.Boolean;

import java.util.OptionalLong;

import software.sava.core.borsh.Borsh;

import static software.sava.core.encoding.ByteUtil.getInt64LE;

public record ConfigMarinadeParams(Fee rewardsFee,
                                   OptionalLong slotsForStakeDelta,
                                   OptionalLong minStake,
                                   OptionalLong minDeposit,
                                   OptionalLong minWithdraw,
                                   OptionalLong stakingSolCap,
                                   OptionalLong liquiditySolCap,
                                   Boolean withdrawStakeAccountEnabled,
                                   FeeCents delayedUnstakeFee,
                                   FeeCents withdrawStakeAccountFee,
                                   Fee maxStakeMovedPerEpoch) implements Borsh {

  public static ConfigMarinadeParams read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final Fee rewardsFee;
    if (_data[i] == 0) {
      rewardsFee = null;
      ++i;
    } else {
      ++i;
      rewardsFee = Fee.read(_data, i);
      i += rewardsFee.l();
    }
    final OptionalLong slotsForStakeDelta;
    if (_data[i] == 0) {
      slotsForStakeDelta = OptionalLong.empty();
      ++i;
    } else {
      ++i;
      slotsForStakeDelta = OptionalLong.of(getInt64LE(_data, i));
      i += 8;
    }
    final OptionalLong minStake;
    if (_data[i] == 0) {
      minStake = OptionalLong.empty();
      ++i;
    } else {
      ++i;
      minStake = OptionalLong.of(getInt64LE(_data, i));
      i += 8;
    }
    final OptionalLong minDeposit;
    if (_data[i] == 0) {
      minDeposit = OptionalLong.empty();
      ++i;
    } else {
      ++i;
      minDeposit = OptionalLong.of(getInt64LE(_data, i));
      i += 8;
    }
    final OptionalLong minWithdraw;
    if (_data[i] == 0) {
      minWithdraw = OptionalLong.empty();
      ++i;
    } else {
      ++i;
      minWithdraw = OptionalLong.of(getInt64LE(_data, i));
      i += 8;
    }
    final OptionalLong stakingSolCap;
    if (_data[i] == 0) {
      stakingSolCap = OptionalLong.empty();
      ++i;
    } else {
      ++i;
      stakingSolCap = OptionalLong.of(getInt64LE(_data, i));
      i += 8;
    }
    final OptionalLong liquiditySolCap;
    if (_data[i] == 0) {
      liquiditySolCap = OptionalLong.empty();
      ++i;
    } else {
      ++i;
      liquiditySolCap = OptionalLong.of(getInt64LE(_data, i));
      i += 8;
    }
    final Boolean withdrawStakeAccountEnabled;
    if (_data[i] == 0) {
      withdrawStakeAccountEnabled = null;
      ++i;
    } else {
      ++i;
      withdrawStakeAccountEnabled = _data[i] == 1;
      ++i;
    }
    final FeeCents delayedUnstakeFee;
    if (_data[i] == 0) {
      delayedUnstakeFee = null;
      ++i;
    } else {
      ++i;
      delayedUnstakeFee = FeeCents.read(_data, i);
      i += delayedUnstakeFee.l();
    }
    final FeeCents withdrawStakeAccountFee;
    if (_data[i] == 0) {
      withdrawStakeAccountFee = null;
      ++i;
    } else {
      ++i;
      withdrawStakeAccountFee = FeeCents.read(_data, i);
      i += withdrawStakeAccountFee.l();
    }
    final Fee maxStakeMovedPerEpoch;
    if (_data[i] == 0) {
      maxStakeMovedPerEpoch = null;
    } else {
      ++i;
      maxStakeMovedPerEpoch = Fee.read(_data, i);
    }
    return new ConfigMarinadeParams(rewardsFee,
                                    slotsForStakeDelta,
                                    minStake,
                                    minDeposit,
                                    minWithdraw,
                                    stakingSolCap,
                                    liquiditySolCap,
                                    withdrawStakeAccountEnabled,
                                    delayedUnstakeFee,
                                    withdrawStakeAccountFee,
                                    maxStakeMovedPerEpoch);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += Borsh.writeOptional(rewardsFee, _data, i);
    i += Borsh.writeOptional(slotsForStakeDelta, _data, i);
    i += Borsh.writeOptional(minStake, _data, i);
    i += Borsh.writeOptional(minDeposit, _data, i);
    i += Borsh.writeOptional(minWithdraw, _data, i);
    i += Borsh.writeOptional(stakingSolCap, _data, i);
    i += Borsh.writeOptional(liquiditySolCap, _data, i);
    i += Borsh.writeOptional(withdrawStakeAccountEnabled, _data, i);
    i += Borsh.writeOptional(delayedUnstakeFee, _data, i);
    i += Borsh.writeOptional(withdrawStakeAccountFee, _data, i);
    i += Borsh.writeOptional(maxStakeMovedPerEpoch, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return (rewardsFee == null ? 1 : (1 + rewardsFee.l()))
         + (slotsForStakeDelta == null || slotsForStakeDelta.isEmpty() ? 1 : (1 + 8))
         + (minStake == null || minStake.isEmpty() ? 1 : (1 + 8))
         + (minDeposit == null || minDeposit.isEmpty() ? 1 : (1 + 8))
         + (minWithdraw == null || minWithdraw.isEmpty() ? 1 : (1 + 8))
         + (stakingSolCap == null || stakingSolCap.isEmpty() ? 1 : (1 + 8))
         + (liquiditySolCap == null || liquiditySolCap.isEmpty() ? 1 : (1 + 8))
         + (withdrawStakeAccountEnabled == null ? 1 : (1 + 1))
         + (delayedUnstakeFee == null ? 1 : (1 + delayedUnstakeFee.l()))
         + (withdrawStakeAccountFee == null ? 1 : (1 + withdrawStakeAccountFee.l()))
         + (maxStakeMovedPerEpoch == null ? 1 : (1 + maxStakeMovedPerEpoch.l()));
  }
}

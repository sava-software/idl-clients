package software.sava.idl.clients.marinade.stake_pool.gen.types;

import java.lang.Boolean;

import java.util.OptionalLong;

import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

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
                                   Fee maxStakeMovedPerEpoch) implements SerDe {

  public static ConfigMarinadeParams read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final Fee rewardsFee;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      rewardsFee = null;
      ++i;
    } else {
      ++i;
      rewardsFee = Fee.read(_data, i);
      i += rewardsFee.l();
    }
    final OptionalLong slotsForStakeDelta;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      slotsForStakeDelta = OptionalLong.empty();
      ++i;
    } else {
      ++i;
      slotsForStakeDelta = OptionalLong.of(getInt64LE(_data, i));
      i += 8;
    }
    final OptionalLong minStake;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      minStake = OptionalLong.empty();
      ++i;
    } else {
      ++i;
      minStake = OptionalLong.of(getInt64LE(_data, i));
      i += 8;
    }
    final OptionalLong minDeposit;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      minDeposit = OptionalLong.empty();
      ++i;
    } else {
      ++i;
      minDeposit = OptionalLong.of(getInt64LE(_data, i));
      i += 8;
    }
    final OptionalLong minWithdraw;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      minWithdraw = OptionalLong.empty();
      ++i;
    } else {
      ++i;
      minWithdraw = OptionalLong.of(getInt64LE(_data, i));
      i += 8;
    }
    final OptionalLong stakingSolCap;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      stakingSolCap = OptionalLong.empty();
      ++i;
    } else {
      ++i;
      stakingSolCap = OptionalLong.of(getInt64LE(_data, i));
      i += 8;
    }
    final OptionalLong liquiditySolCap;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      liquiditySolCap = OptionalLong.empty();
      ++i;
    } else {
      ++i;
      liquiditySolCap = OptionalLong.of(getInt64LE(_data, i));
      i += 8;
    }
    final Boolean withdrawStakeAccountEnabled;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      withdrawStakeAccountEnabled = null;
      ++i;
    } else {
      ++i;
      withdrawStakeAccountEnabled = _data[i] == 1;
      ++i;
    }
    final FeeCents delayedUnstakeFee;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      delayedUnstakeFee = null;
      ++i;
    } else {
      ++i;
      delayedUnstakeFee = FeeCents.read(_data, i);
      i += delayedUnstakeFee.l();
    }
    final FeeCents withdrawStakeAccountFee;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      withdrawStakeAccountFee = null;
      ++i;
    } else {
      ++i;
      withdrawStakeAccountFee = FeeCents.read(_data, i);
      i += withdrawStakeAccountFee.l();
    }
    final Fee maxStakeMovedPerEpoch;
    if (SerDeUtil.isAbsent(1, _data, i)) {
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
    i += SerDeUtil.writeOptional(1, rewardsFee, _data, i);
    i += SerDeUtil.writeOptional(1, slotsForStakeDelta, _data, i);
    i += SerDeUtil.writeOptional(1, minStake, _data, i);
    i += SerDeUtil.writeOptional(1, minDeposit, _data, i);
    i += SerDeUtil.writeOptional(1, minWithdraw, _data, i);
    i += SerDeUtil.writeOptional(1, stakingSolCap, _data, i);
    i += SerDeUtil.writeOptional(1, liquiditySolCap, _data, i);
    i += SerDeUtil.writeOptional(1, withdrawStakeAccountEnabled, _data, i);
    i += SerDeUtil.writeOptional(1, delayedUnstakeFee, _data, i);
    i += SerDeUtil.writeOptional(1, withdrawStakeAccountFee, _data, i);
    i += SerDeUtil.writeOptional(1, maxStakeMovedPerEpoch, _data, i);
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

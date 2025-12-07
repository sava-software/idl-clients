package software.sava.idl.clients.marinade.stake_pool.gen.events;

import software.sava.core.accounts.PublicKey;
import software.sava.core.borsh.Borsh;
import software.sava.core.programs.Discriminator;
import software.sava.idl.clients.marinade.stake_pool.gen.types.BoolValueChange;
import software.sava.idl.clients.marinade.stake_pool.gen.types.FeeCentsValueChange;
import software.sava.idl.clients.marinade.stake_pool.gen.types.FeeValueChange;
import software.sava.idl.clients.marinade.stake_pool.gen.types.U64ValueChange;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.programs.Discriminator.createDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record ConfigMarinadeEvent(Discriminator discriminator,
                                  PublicKey state,
                                  FeeValueChange rewardsFeeChange,
                                  U64ValueChange slotsForStakeDeltaChange,
                                  U64ValueChange minStakeChange,
                                  U64ValueChange minDepositChange,
                                  U64ValueChange minWithdrawChange,
                                  U64ValueChange stakingSolCapChange,
                                  U64ValueChange liquiditySolCapChange,
                                  BoolValueChange withdrawStakeAccountEnabledChange,
                                  FeeCentsValueChange delayedUnstakeFeeChange,
                                  FeeCentsValueChange withdrawStakeAccountFeeChange,
                                  FeeValueChange maxStakeMovedPerEpochChange) implements MarinadeFinanceEvent {

  public static final Discriminator DISCRIMINATOR = toDiscriminator(9, 100, 48, 232, 83, 169, 174, 85);

  public static ConfigMarinadeEvent read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createDiscriminator(_data, _offset, 8);
    int i = _offset + discriminator.length();
    final var state = readPubKey(_data, i);
    i += 32;
    final FeeValueChange rewardsFeeChange;
    if (_data[i] == 0) {
      rewardsFeeChange = null;
      ++i;
    } else {
      ++i;
      rewardsFeeChange = FeeValueChange.read(_data, i);
      i += rewardsFeeChange.l();
    }
    final U64ValueChange slotsForStakeDeltaChange;
    if (_data[i] == 0) {
      slotsForStakeDeltaChange = null;
      ++i;
    } else {
      ++i;
      slotsForStakeDeltaChange = U64ValueChange.read(_data, i);
      i += slotsForStakeDeltaChange.l();
    }
    final U64ValueChange minStakeChange;
    if (_data[i] == 0) {
      minStakeChange = null;
      ++i;
    } else {
      ++i;
      minStakeChange = U64ValueChange.read(_data, i);
      i += minStakeChange.l();
    }
    final U64ValueChange minDepositChange;
    if (_data[i] == 0) {
      minDepositChange = null;
      ++i;
    } else {
      ++i;
      minDepositChange = U64ValueChange.read(_data, i);
      i += minDepositChange.l();
    }
    final U64ValueChange minWithdrawChange;
    if (_data[i] == 0) {
      minWithdrawChange = null;
      ++i;
    } else {
      ++i;
      minWithdrawChange = U64ValueChange.read(_data, i);
      i += minWithdrawChange.l();
    }
    final U64ValueChange stakingSolCapChange;
    if (_data[i] == 0) {
      stakingSolCapChange = null;
      ++i;
    } else {
      ++i;
      stakingSolCapChange = U64ValueChange.read(_data, i);
      i += stakingSolCapChange.l();
    }
    final U64ValueChange liquiditySolCapChange;
    if (_data[i] == 0) {
      liquiditySolCapChange = null;
      ++i;
    } else {
      ++i;
      liquiditySolCapChange = U64ValueChange.read(_data, i);
      i += liquiditySolCapChange.l();
    }
    final BoolValueChange withdrawStakeAccountEnabledChange;
    if (_data[i] == 0) {
      withdrawStakeAccountEnabledChange = null;
      ++i;
    } else {
      ++i;
      withdrawStakeAccountEnabledChange = BoolValueChange.read(_data, i);
      i += withdrawStakeAccountEnabledChange.l();
    }
    final FeeCentsValueChange delayedUnstakeFeeChange;
    if (_data[i] == 0) {
      delayedUnstakeFeeChange = null;
      ++i;
    } else {
      ++i;
      delayedUnstakeFeeChange = FeeCentsValueChange.read(_data, i);
      i += delayedUnstakeFeeChange.l();
    }
    final FeeCentsValueChange withdrawStakeAccountFeeChange;
    if (_data[i] == 0) {
      withdrawStakeAccountFeeChange = null;
      ++i;
    } else {
      ++i;
      withdrawStakeAccountFeeChange = FeeCentsValueChange.read(_data, i);
      i += withdrawStakeAccountFeeChange.l();
    }
    final FeeValueChange maxStakeMovedPerEpochChange;
    if (_data[i] == 0) {
      maxStakeMovedPerEpochChange = null;
    } else {
      ++i;
      maxStakeMovedPerEpochChange = FeeValueChange.read(_data, i);
    }
    return new ConfigMarinadeEvent(discriminator,
                                   state,
                                   rewardsFeeChange,
                                   slotsForStakeDeltaChange,
                                   minStakeChange,
                                   minDepositChange,
                                   minWithdrawChange,
                                   stakingSolCapChange,
                                   liquiditySolCapChange,
                                   withdrawStakeAccountEnabledChange,
                                   delayedUnstakeFeeChange,
                                   withdrawStakeAccountFeeChange,
                                   maxStakeMovedPerEpochChange);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    state.write(_data, i);
    i += 32;
    i += Borsh.writeOptional(rewardsFeeChange, _data, i);
    i += Borsh.writeOptional(slotsForStakeDeltaChange, _data, i);
    i += Borsh.writeOptional(minStakeChange, _data, i);
    i += Borsh.writeOptional(minDepositChange, _data, i);
    i += Borsh.writeOptional(minWithdrawChange, _data, i);
    i += Borsh.writeOptional(stakingSolCapChange, _data, i);
    i += Borsh.writeOptional(liquiditySolCapChange, _data, i);
    i += Borsh.writeOptional(withdrawStakeAccountEnabledChange, _data, i);
    i += Borsh.writeOptional(delayedUnstakeFeeChange, _data, i);
    i += Borsh.writeOptional(withdrawStakeAccountFeeChange, _data, i);
    i += Borsh.writeOptional(maxStakeMovedPerEpochChange, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return discriminator.length() + 32
         + (rewardsFeeChange == null ? 1 : (1 + rewardsFeeChange.l()))
         + (slotsForStakeDeltaChange == null ? 1 : (1 + slotsForStakeDeltaChange.l()))
         + (minStakeChange == null ? 1 : (1 + minStakeChange.l()))
         + (minDepositChange == null ? 1 : (1 + minDepositChange.l()))
         + (minWithdrawChange == null ? 1 : (1 + minWithdrawChange.l()))
         + (stakingSolCapChange == null ? 1 : (1 + stakingSolCapChange.l()))
         + (liquiditySolCapChange == null ? 1 : (1 + liquiditySolCapChange.l()))
         + (withdrawStakeAccountEnabledChange == null ? 1 : (1 + withdrawStakeAccountEnabledChange.l()))
         + (delayedUnstakeFeeChange == null ? 1 : (1 + delayedUnstakeFeeChange.l()))
         + (withdrawStakeAccountFeeChange == null ? 1 : (1 + withdrawStakeAccountFeeChange.l()))
         + (maxStakeMovedPerEpochChange == null ? 1 : (1 + maxStakeMovedPerEpochChange.l()));
  }
}

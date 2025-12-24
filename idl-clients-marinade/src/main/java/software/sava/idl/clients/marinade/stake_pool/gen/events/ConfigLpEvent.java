package software.sava.idl.clients.marinade.stake_pool.gen.events;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;
import software.sava.idl.clients.core.gen.SerDeUtil;
import software.sava.idl.clients.marinade.stake_pool.gen.types.FeeValueChange;
import software.sava.idl.clients.marinade.stake_pool.gen.types.U64ValueChange;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.programs.Discriminator.createDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record ConfigLpEvent(Discriminator discriminator,
                            PublicKey state,
                            FeeValueChange minFeeChange,
                            FeeValueChange maxFeeChange,
                            U64ValueChange liquidityTargetChange,
                            FeeValueChange treasuryCutChange) implements MarinadeFinanceEvent {

  public static final Discriminator DISCRIMINATOR = toDiscriminator(9, 100, 48, 232, 83, 169, 174, 85);

  public static ConfigLpEvent read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createDiscriminator(_data, _offset, 8);
    int i = _offset + discriminator.length();
    final var state = readPubKey(_data, i);
    i += 32;
    final FeeValueChange minFeeChange;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      minFeeChange = null;
      ++i;
    } else {
      ++i;
      minFeeChange = FeeValueChange.read(_data, i);
      i += minFeeChange.l();
    }
    final FeeValueChange maxFeeChange;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      maxFeeChange = null;
      ++i;
    } else {
      ++i;
      maxFeeChange = FeeValueChange.read(_data, i);
      i += maxFeeChange.l();
    }
    final U64ValueChange liquidityTargetChange;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      liquidityTargetChange = null;
      ++i;
    } else {
      ++i;
      liquidityTargetChange = U64ValueChange.read(_data, i);
      i += liquidityTargetChange.l();
    }
    final FeeValueChange treasuryCutChange;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      treasuryCutChange = null;
    } else {
      ++i;
      treasuryCutChange = FeeValueChange.read(_data, i);
    }
    return new ConfigLpEvent(discriminator,
                             state,
                             minFeeChange,
                             maxFeeChange,
                             liquidityTargetChange,
                             treasuryCutChange);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    state.write(_data, i);
    i += 32;
    i += SerDeUtil.writeOptional(1, minFeeChange, _data, i);
    i += SerDeUtil.writeOptional(1, maxFeeChange, _data, i);
    i += SerDeUtil.writeOptional(1, liquidityTargetChange, _data, i);
    i += SerDeUtil.writeOptional(1, treasuryCutChange, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return discriminator.length() + 32
         + (minFeeChange == null ? 1 : (1 + minFeeChange.l()))
         + (maxFeeChange == null ? 1 : (1 + maxFeeChange.l()))
         + (liquidityTargetChange == null ? 1 : (1 + liquidityTargetChange.l()))
         + (treasuryCutChange == null ? 1 : (1 + treasuryCutChange.l()));
  }
}

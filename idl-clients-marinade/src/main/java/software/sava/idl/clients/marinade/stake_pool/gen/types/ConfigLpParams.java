package software.sava.idl.clients.marinade.stake_pool.gen.types;

import java.util.OptionalLong;

import software.sava.core.borsh.Borsh;

import static software.sava.core.encoding.ByteUtil.getInt64LE;

public record ConfigLpParams(Fee minFee,
                             Fee maxFee,
                             OptionalLong liquidityTarget,
                             Fee treasuryCut) implements Borsh {

  public static ConfigLpParams read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final Fee minFee;
    if (_data[i] == 0) {
      minFee = null;
      ++i;
    } else {
      ++i;
      minFee = Fee.read(_data, i);
      i += minFee.l();
    }
    final Fee maxFee;
    if (_data[i] == 0) {
      maxFee = null;
      ++i;
    } else {
      ++i;
      maxFee = Fee.read(_data, i);
      i += maxFee.l();
    }
    final OptionalLong liquidityTarget;
    if (_data[i] == 0) {
      liquidityTarget = OptionalLong.empty();
      ++i;
    } else {
      ++i;
      liquidityTarget = OptionalLong.of(getInt64LE(_data, i));
      i += 8;
    }
    final Fee treasuryCut;
    if (_data[i] == 0) {
      treasuryCut = null;
    } else {
      ++i;
      treasuryCut = Fee.read(_data, i);
    }
    return new ConfigLpParams(minFee,
                              maxFee,
                              liquidityTarget,
                              treasuryCut);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += Borsh.writeOptional(minFee, _data, i);
    i += Borsh.writeOptional(maxFee, _data, i);
    i += Borsh.writeOptional(liquidityTarget, _data, i);
    i += Borsh.writeOptional(treasuryCut, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return (minFee == null ? 1 : (1 + minFee.l())) + (maxFee == null ? 1 : (1 + maxFee.l())) + (liquidityTarget == null || liquidityTarget.isEmpty() ? 1 : (1 + 8)) + (treasuryCut == null ? 1 : (1 + treasuryCut.l()));
  }
}

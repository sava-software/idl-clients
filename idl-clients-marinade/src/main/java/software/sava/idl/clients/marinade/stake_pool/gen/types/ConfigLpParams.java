package software.sava.idl.clients.marinade.stake_pool.gen.types;

import java.util.OptionalLong;

import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.encoding.ByteUtil.getInt64LE;

public record ConfigLpParams(Fee minFee,
                             Fee maxFee,
                             OptionalLong liquidityTarget,
                             Fee treasuryCut) implements SerDe {

  public static ConfigLpParams read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final Fee minFee;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      minFee = null;
      ++i;
    } else {
      ++i;
      minFee = Fee.read(_data, i);
      i += minFee.l();
    }
    final Fee maxFee;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      maxFee = null;
      ++i;
    } else {
      ++i;
      maxFee = Fee.read(_data, i);
      i += maxFee.l();
    }
    final OptionalLong liquidityTarget;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      liquidityTarget = OptionalLong.empty();
      ++i;
    } else {
      ++i;
      liquidityTarget = OptionalLong.of(getInt64LE(_data, i));
      i += 8;
    }
    final Fee treasuryCut;
    if (SerDeUtil.isAbsent(1, _data, i)) {
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
    i += SerDeUtil.writeOptional(1, minFee, _data, i);
    i += SerDeUtil.writeOptional(1, maxFee, _data, i);
    i += SerDeUtil.writeOptional(1, liquidityTarget, _data, i);
    i += SerDeUtil.writeOptional(1, treasuryCut, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return (minFee == null ? 1 : (1 + minFee.l())) + (maxFee == null ? 1 : (1 + maxFee.l())) + (liquidityTarget == null || liquidityTarget.isEmpty() ? 1 : (1 + 8)) + (treasuryCut == null ? 1 : (1 + treasuryCut.l()));
  }
}

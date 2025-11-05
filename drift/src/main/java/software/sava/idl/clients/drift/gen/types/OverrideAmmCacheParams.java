package software.sava.idl.clients.drift.gen.types;

import java.math.BigInteger;

import java.util.OptionalInt;
import java.util.OptionalLong;

import software.sava.core.borsh.Borsh;

import static software.sava.core.encoding.ByteUtil.getInt128LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;

public record OverrideAmmCacheParams(OptionalLong quoteOwedFromLpPool,
                                     OptionalLong lastSettleSlot,
                                     BigInteger lastFeePoolTokenAmount,
                                     BigInteger lastNetPnlPoolTokenAmount,
                                     OptionalInt ammPositionScalar,
                                     OptionalLong ammInventoryLimit) implements Borsh {

  public static OverrideAmmCacheParams read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final OptionalLong quoteOwedFromLpPool;
    if (_data[i] == 0) {
      quoteOwedFromLpPool = OptionalLong.empty();
      ++i;
    } else {
      ++i;
      quoteOwedFromLpPool = OptionalLong.of(getInt64LE(_data, i));
      i += 8;
    }
    final OptionalLong lastSettleSlot;
    if (_data[i] == 0) {
      lastSettleSlot = OptionalLong.empty();
      ++i;
    } else {
      ++i;
      lastSettleSlot = OptionalLong.of(getInt64LE(_data, i));
      i += 8;
    }
    final BigInteger lastFeePoolTokenAmount;
    if (_data[i] == 0) {
      lastFeePoolTokenAmount = null;
      ++i;
    } else {
      ++i;
      lastFeePoolTokenAmount = getInt128LE(_data, i);
      i += 16;
    }
    final BigInteger lastNetPnlPoolTokenAmount;
    if (_data[i] == 0) {
      lastNetPnlPoolTokenAmount = null;
      ++i;
    } else {
      ++i;
      lastNetPnlPoolTokenAmount = getInt128LE(_data, i);
      i += 16;
    }
    final OptionalInt ammPositionScalar;
    if (_data[i] == 0) {
      ammPositionScalar = OptionalInt.empty();
      ++i;
    } else {
      ++i;
      ammPositionScalar = OptionalInt.of(_data[i] & 0xFF);
      ++i;
    }
    final OptionalLong ammInventoryLimit;
    if (_data[i] == 0) {
      ammInventoryLimit = OptionalLong.empty();
    } else {
      ++i;
      ammInventoryLimit = OptionalLong.of(getInt64LE(_data, i));
    }
    return new OverrideAmmCacheParams(quoteOwedFromLpPool,
                                      lastSettleSlot,
                                      lastFeePoolTokenAmount,
                                      lastNetPnlPoolTokenAmount,
                                      ammPositionScalar,
                                      ammInventoryLimit);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += Borsh.writeOptional(quoteOwedFromLpPool, _data, i);
    i += Borsh.writeOptional(lastSettleSlot, _data, i);
    i += Borsh.write128Optional(lastFeePoolTokenAmount, _data, i);
    i += Borsh.write128Optional(lastNetPnlPoolTokenAmount, _data, i);
    i += Borsh.writeOptionalbyte(ammPositionScalar, _data, i);
    i += Borsh.writeOptional(ammInventoryLimit, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return (quoteOwedFromLpPool == null || quoteOwedFromLpPool.isEmpty() ? 1 : (1 + 8))
         + (lastSettleSlot == null || lastSettleSlot.isEmpty() ? 1 : (1 + 8))
         + (lastFeePoolTokenAmount == null ? 1 : (1 + 16))
         + (lastNetPnlPoolTokenAmount == null ? 1 : (1 + 16))
         + (ammPositionScalar == null || ammPositionScalar.isEmpty() ? 1 : (1 + 1))
         + (ammInventoryLimit == null || ammInventoryLimit.isEmpty() ? 1 : (1 + 8));
  }
}

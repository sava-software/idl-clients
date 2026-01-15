package software.sava.idl.clients.drift.gen.types;

import java.math.BigInteger;

import java.util.OptionalInt;
import java.util.OptionalLong;

import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.encoding.ByteUtil.getInt128LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;

public record OverrideAmmCacheParams(OptionalLong quoteOwedFromLpPool,
                                     OptionalLong lastSettleSlot,
                                     BigInteger lastFeePoolTokenAmount,
                                     BigInteger lastNetPnlPoolTokenAmount,
                                     OptionalInt ammPositionScalar,
                                     OptionalLong ammInventoryLimit) implements SerDe {

  public static final int QUOTE_OWED_FROM_LP_POOL_OFFSET = 1;

  public static OverrideAmmCacheParams read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final OptionalLong quoteOwedFromLpPool;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      quoteOwedFromLpPool = OptionalLong.empty();
      ++i;
    } else {
      ++i;
      quoteOwedFromLpPool = OptionalLong.of(getInt64LE(_data, i));
      i += 8;
    }
    final OptionalLong lastSettleSlot;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      lastSettleSlot = OptionalLong.empty();
      ++i;
    } else {
      ++i;
      lastSettleSlot = OptionalLong.of(getInt64LE(_data, i));
      i += 8;
    }
    final BigInteger lastFeePoolTokenAmount;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      lastFeePoolTokenAmount = null;
      ++i;
    } else {
      ++i;
      lastFeePoolTokenAmount = getInt128LE(_data, i);
      i += 16;
    }
    final BigInteger lastNetPnlPoolTokenAmount;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      lastNetPnlPoolTokenAmount = null;
      ++i;
    } else {
      ++i;
      lastNetPnlPoolTokenAmount = getInt128LE(_data, i);
      i += 16;
    }
    final OptionalInt ammPositionScalar;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      ammPositionScalar = OptionalInt.empty();
      ++i;
    } else {
      ++i;
      ammPositionScalar = OptionalInt.of(_data[i] & 0xFF);
      ++i;
    }
    final OptionalLong ammInventoryLimit;
    if (SerDeUtil.isAbsent(1, _data, i)) {
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
    i += SerDeUtil.writeOptional(1, quoteOwedFromLpPool, _data, i);
    i += SerDeUtil.writeOptional(1, lastSettleSlot, _data, i);
    i += SerDeUtil.write128Optional(1, lastFeePoolTokenAmount, _data, i);
    i += SerDeUtil.write128Optional(1, lastNetPnlPoolTokenAmount, _data, i);
    i += SerDeUtil.writeOptionalbyte(1, ammPositionScalar, _data, i);
    i += SerDeUtil.writeOptional(1, ammInventoryLimit, _data, i);
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

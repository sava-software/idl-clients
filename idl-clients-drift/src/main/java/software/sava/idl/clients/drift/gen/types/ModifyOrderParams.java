package software.sava.idl.clients.drift.gen.types;

import java.lang.Boolean;

import java.util.OptionalInt;
import java.util.OptionalLong;

import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;

public record ModifyOrderParams(PositionDirection direction,
                                OptionalLong baseAssetAmount,
                                OptionalLong price,
                                Boolean reduceOnly,
                                PostOnlyParam postOnly,
                                OptionalInt bitFlags,
                                OptionalLong maxTs,
                                OptionalLong triggerPrice,
                                OrderTriggerCondition triggerCondition,
                                OptionalInt oraclePriceOffset,
                                OptionalInt auctionDuration,
                                OptionalLong auctionStartPrice,
                                OptionalLong auctionEndPrice,
                                OptionalInt policy) implements SerDe {

  public static final int DIRECTION_OFFSET = 1;

  public static ModifyOrderParams read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final PositionDirection direction;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      direction = null;
      ++i;
    } else {
      ++i;
      direction = PositionDirection.read(_data, i);
      i += direction.l();
    }
    final OptionalLong baseAssetAmount;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      baseAssetAmount = OptionalLong.empty();
      ++i;
    } else {
      ++i;
      baseAssetAmount = OptionalLong.of(getInt64LE(_data, i));
      i += 8;
    }
    final OptionalLong price;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      price = OptionalLong.empty();
      ++i;
    } else {
      ++i;
      price = OptionalLong.of(getInt64LE(_data, i));
      i += 8;
    }
    final Boolean reduceOnly;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      reduceOnly = null;
      ++i;
    } else {
      ++i;
      reduceOnly = _data[i] == 1;
      ++i;
    }
    final PostOnlyParam postOnly;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      postOnly = null;
      ++i;
    } else {
      ++i;
      postOnly = PostOnlyParam.read(_data, i);
      i += postOnly.l();
    }
    final OptionalInt bitFlags;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      bitFlags = OptionalInt.empty();
      ++i;
    } else {
      ++i;
      bitFlags = OptionalInt.of(_data[i] & 0xFF);
      ++i;
    }
    final OptionalLong maxTs;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      maxTs = OptionalLong.empty();
      ++i;
    } else {
      ++i;
      maxTs = OptionalLong.of(getInt64LE(_data, i));
      i += 8;
    }
    final OptionalLong triggerPrice;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      triggerPrice = OptionalLong.empty();
      ++i;
    } else {
      ++i;
      triggerPrice = OptionalLong.of(getInt64LE(_data, i));
      i += 8;
    }
    final OrderTriggerCondition triggerCondition;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      triggerCondition = null;
      ++i;
    } else {
      ++i;
      triggerCondition = OrderTriggerCondition.read(_data, i);
      i += triggerCondition.l();
    }
    final OptionalInt oraclePriceOffset;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      oraclePriceOffset = OptionalInt.empty();
      ++i;
    } else {
      ++i;
      oraclePriceOffset = OptionalInt.of(getInt32LE(_data, i));
      i += 4;
    }
    final OptionalInt auctionDuration;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      auctionDuration = OptionalInt.empty();
      ++i;
    } else {
      ++i;
      auctionDuration = OptionalInt.of(_data[i] & 0xFF);
      ++i;
    }
    final OptionalLong auctionStartPrice;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      auctionStartPrice = OptionalLong.empty();
      ++i;
    } else {
      ++i;
      auctionStartPrice = OptionalLong.of(getInt64LE(_data, i));
      i += 8;
    }
    final OptionalLong auctionEndPrice;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      auctionEndPrice = OptionalLong.empty();
      ++i;
    } else {
      ++i;
      auctionEndPrice = OptionalLong.of(getInt64LE(_data, i));
      i += 8;
    }
    final OptionalInt policy;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      policy = OptionalInt.empty();
    } else {
      ++i;
      policy = OptionalInt.of(_data[i] & 0xFF);
    }
    return new ModifyOrderParams(direction,
                                 baseAssetAmount,
                                 price,
                                 reduceOnly,
                                 postOnly,
                                 bitFlags,
                                 maxTs,
                                 triggerPrice,
                                 triggerCondition,
                                 oraclePriceOffset,
                                 auctionDuration,
                                 auctionStartPrice,
                                 auctionEndPrice,
                                 policy);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += SerDeUtil.writeOptional(1, direction, _data, i);
    i += SerDeUtil.writeOptional(1, baseAssetAmount, _data, i);
    i += SerDeUtil.writeOptional(1, price, _data, i);
    i += SerDeUtil.writeOptional(1, reduceOnly, _data, i);
    i += SerDeUtil.writeOptional(1, postOnly, _data, i);
    i += SerDeUtil.writeOptionalbyte(1, bitFlags, _data, i);
    i += SerDeUtil.writeOptional(1, maxTs, _data, i);
    i += SerDeUtil.writeOptional(1, triggerPrice, _data, i);
    i += SerDeUtil.writeOptional(1, triggerCondition, _data, i);
    i += SerDeUtil.writeOptional(1, oraclePriceOffset, _data, i);
    i += SerDeUtil.writeOptionalbyte(1, auctionDuration, _data, i);
    i += SerDeUtil.writeOptional(1, auctionStartPrice, _data, i);
    i += SerDeUtil.writeOptional(1, auctionEndPrice, _data, i);
    i += SerDeUtil.writeOptionalbyte(1, policy, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return (direction == null ? 1 : (1 + direction.l()))
         + (baseAssetAmount == null || baseAssetAmount.isEmpty() ? 1 : (1 + 8))
         + (price == null || price.isEmpty() ? 1 : (1 + 8))
         + (reduceOnly == null ? 1 : (1 + 1))
         + (postOnly == null ? 1 : (1 + postOnly.l()))
         + (bitFlags == null || bitFlags.isEmpty() ? 1 : (1 + 1))
         + (maxTs == null || maxTs.isEmpty() ? 1 : (1 + 8))
         + (triggerPrice == null || triggerPrice.isEmpty() ? 1 : (1 + 8))
         + (triggerCondition == null ? 1 : (1 + triggerCondition.l()))
         + (oraclePriceOffset == null || oraclePriceOffset.isEmpty() ? 1 : (1 + 4))
         + (auctionDuration == null || auctionDuration.isEmpty() ? 1 : (1 + 1))
         + (auctionStartPrice == null || auctionStartPrice.isEmpty() ? 1 : (1 + 8))
         + (auctionEndPrice == null || auctionEndPrice.isEmpty() ? 1 : (1 + 8))
         + (policy == null || policy.isEmpty() ? 1 : (1 + 1));
  }
}

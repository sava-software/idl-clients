package software.sava.idl.clients.drift.gen.types;

import java.util.OptionalInt;
import java.util.OptionalLong;

import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.encoding.ByteUtil.getInt16LE;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt16LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

public record OrderParams(OrderType orderType,
                          MarketType marketType,
                          PositionDirection direction,
                          int userOrderId,
                          long baseAssetAmount,
                          long price,
                          int marketIndex,
                          boolean reduceOnly,
                          PostOnlyParam postOnly,
                          int bitFlags,
                          OptionalLong maxTs,
                          OptionalLong triggerPrice,
                          OrderTriggerCondition triggerCondition,
                          OptionalInt oraclePriceOffset,
                          OptionalInt auctionDuration,
                          OptionalLong auctionStartPrice,
                          OptionalLong auctionEndPrice) implements SerDe {

  public static OrderParams read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var orderType = OrderType.read(_data, i);
    i += orderType.l();
    final var marketType = MarketType.read(_data, i);
    i += marketType.l();
    final var direction = PositionDirection.read(_data, i);
    i += direction.l();
    final var userOrderId = _data[i] & 0xFF;
    ++i;
    final var baseAssetAmount = getInt64LE(_data, i);
    i += 8;
    final var price = getInt64LE(_data, i);
    i += 8;
    final var marketIndex = getInt16LE(_data, i);
    i += 2;
    final var reduceOnly = _data[i] == 1;
    ++i;
    final var postOnly = PostOnlyParam.read(_data, i);
    i += postOnly.l();
    final var bitFlags = _data[i] & 0xFF;
    ++i;
    final OptionalLong maxTs;
    if (_data[i] == 0) {
      maxTs = OptionalLong.empty();
      ++i;
    } else {
      ++i;
      maxTs = OptionalLong.of(getInt64LE(_data, i));
      i += 8;
    }
    final OptionalLong triggerPrice;
    if (_data[i] == 0) {
      triggerPrice = OptionalLong.empty();
      ++i;
    } else {
      ++i;
      triggerPrice = OptionalLong.of(getInt64LE(_data, i));
      i += 8;
    }
    final var triggerCondition = OrderTriggerCondition.read(_data, i);
    i += triggerCondition.l();
    final OptionalInt oraclePriceOffset;
    if (_data[i] == 0) {
      oraclePriceOffset = OptionalInt.empty();
      ++i;
    } else {
      ++i;
      oraclePriceOffset = OptionalInt.of(getInt32LE(_data, i));
      i += 4;
    }
    final OptionalInt auctionDuration;
    if (_data[i] == 0) {
      auctionDuration = OptionalInt.empty();
      ++i;
    } else {
      ++i;
      auctionDuration = OptionalInt.of(_data[i] & 0xFF);
      ++i;
    }
    final OptionalLong auctionStartPrice;
    if (_data[i] == 0) {
      auctionStartPrice = OptionalLong.empty();
      ++i;
    } else {
      ++i;
      auctionStartPrice = OptionalLong.of(getInt64LE(_data, i));
      i += 8;
    }
    final OptionalLong auctionEndPrice;
    if (_data[i] == 0) {
      auctionEndPrice = OptionalLong.empty();
    } else {
      ++i;
      auctionEndPrice = OptionalLong.of(getInt64LE(_data, i));
    }
    return new OrderParams(orderType,
                           marketType,
                           direction,
                           userOrderId,
                           baseAssetAmount,
                           price,
                           marketIndex,
                           reduceOnly,
                           postOnly,
                           bitFlags,
                           maxTs,
                           triggerPrice,
                           triggerCondition,
                           oraclePriceOffset,
                           auctionDuration,
                           auctionStartPrice,
                           auctionEndPrice);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += orderType.write(_data, i);
    i += marketType.write(_data, i);
    i += direction.write(_data, i);
    _data[i] = (byte) userOrderId;
    ++i;
    putInt64LE(_data, i, baseAssetAmount);
    i += 8;
    putInt64LE(_data, i, price);
    i += 8;
    putInt16LE(_data, i, marketIndex);
    i += 2;
    _data[i] = (byte) (reduceOnly ? 1 : 0);
    ++i;
    i += postOnly.write(_data, i);
    _data[i] = (byte) bitFlags;
    ++i;
    i += SerDeUtil.writeOptional(1, maxTs, _data, i);
    i += SerDeUtil.writeOptional(1, triggerPrice, _data, i);
    i += triggerCondition.write(_data, i);
    i += SerDeUtil.writeOptional(1, oraclePriceOffset, _data, i);
    i += SerDeUtil.writeOptionalbyte(1, auctionDuration, _data, i);
    i += SerDeUtil.writeOptional(1, auctionStartPrice, _data, i);
    i += SerDeUtil.writeOptional(1, auctionEndPrice, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return orderType.l()
         + marketType.l()
         + direction.l()
         + 1
         + 8
         + 8
         + 2
         + 1
         + postOnly.l()
         + 1
         + (maxTs == null || maxTs.isEmpty() ? 1 : (1 + 8))
         + (triggerPrice == null || triggerPrice.isEmpty() ? 1 : (1 + 8))
         + triggerCondition.l()
         + (oraclePriceOffset == null || oraclePriceOffset.isEmpty() ? 1 : (1 + 4))
         + (auctionDuration == null || auctionDuration.isEmpty() ? 1 : (1 + 1))
         + (auctionStartPrice == null || auctionStartPrice.isEmpty() ? 1 : (1 + 8))
         + (auctionEndPrice == null || auctionEndPrice.isEmpty() ? 1 : (1 + 8));
  }
}

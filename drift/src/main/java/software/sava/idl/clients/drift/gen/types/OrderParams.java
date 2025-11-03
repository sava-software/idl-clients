package software.sava.idl.clients.drift.gen.types;

import java.util.OptionalInt;
import java.util.OptionalLong;

import software.sava.core.borsh.Borsh;

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
                          OptionalLong auctionEndPrice) implements Borsh {

  public static OrderParams read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var orderType = OrderType.read(_data, i);
    i += Borsh.len(orderType);
    final var marketType = MarketType.read(_data, i);
    i += Borsh.len(marketType);
    final var direction = PositionDirection.read(_data, i);
    i += Borsh.len(direction);
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
    i += Borsh.len(postOnly);
    final var bitFlags = _data[i] & 0xFF;
    ++i;
    final OptionalLong maxTs;
    if (_data[i] == 0) {
      maxTs = OptionalLong.empty();
      ++i;
    ;
    } else {
      ++i;
    ;
      maxTs = OptionalLong.of(getInt64LE(_data, i));
      i += 8;
    }
    final OptionalLong triggerPrice;
    if (_data[i] == 0) {
      triggerPrice = OptionalLong.empty();
      ++i;
    ;
    } else {
      ++i;
    ;
      triggerPrice = OptionalLong.of(getInt64LE(_data, i));
      i += 8;
    }
    final var triggerCondition = OrderTriggerCondition.read(_data, i);
    i += Borsh.len(triggerCondition);
    final OptionalInt oraclePriceOffset;
    if (_data[i] == 0) {
      oraclePriceOffset = OptionalInt.empty();
      ++i;
    ;
    } else {
      ++i;
    ;
      oraclePriceOffset = OptionalInt.of(getInt32LE(_data, i));
      i += 4;
    }
    final OptionalInt auctionDuration;
    if (_data[i] == 0) {
      auctionDuration = OptionalInt.empty();
      ++i;
    ;
    } else {
      ++i;
    ;
      auctionDuration = OptionalInt.of(_data[i] & 0xFF);
      ++i;
    }
    final OptionalLong auctionStartPrice;
    if (_data[i] == 0) {
      auctionStartPrice = OptionalLong.empty();
      ++i;
    ;
    } else {
      ++i;
    ;
      auctionStartPrice = OptionalLong.of(getInt64LE(_data, i));
      i += 8;
    }
    final OptionalLong auctionEndPrice;
    if (_data[i] == 0) {
      auctionEndPrice = OptionalLong.empty();
    } else {
      ++i;
    ;
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
    i += Borsh.write(orderType, _data, i);
    i += Borsh.write(marketType, _data, i);
    i += Borsh.write(direction, _data, i);
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
    i += Borsh.write(postOnly, _data, i);
    _data[i] = (byte) bitFlags;
    ++i;
    i += Borsh.writeOptional(maxTs, _data, i);
    i += Borsh.writeOptional(triggerPrice, _data, i);
    i += Borsh.write(triggerCondition, _data, i);
    i += Borsh.writeOptional(oraclePriceOffset, _data, i);
    i += Borsh.writeOptionalbyte(auctionDuration, _data, i);
    i += Borsh.writeOptional(auctionStartPrice, _data, i);
    i += Borsh.writeOptional(auctionEndPrice, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return Borsh.len(orderType)
         + Borsh.len(marketType)
         + Borsh.len(direction)
         + 1
         + 8
         + 8
         + 2
         + 1
         + Borsh.len(postOnly)
         + 1
         + (maxTs == null || maxTs.isEmpty() ? 1 : (1 + 8))
         + (triggerPrice == null || triggerPrice.isEmpty() ? 1 : (1 + 8))
         + Borsh.len(triggerCondition)
         + (oraclePriceOffset == null || oraclePriceOffset.isEmpty() ? 1 : (1 + 4))
         + (auctionDuration == null || auctionDuration.isEmpty() ? 1 : (1 + 1))
         + (auctionStartPrice == null || auctionStartPrice.isEmpty() ? 1 : (1 + 8))
         + (auctionEndPrice == null || auctionEndPrice.isEmpty() ? 1 : (1 + 8));
  }
}

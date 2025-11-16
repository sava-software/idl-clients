package software.sava.idl.clients.drift.gen.types;

import software.sava.core.borsh.Borsh;

import static software.sava.core.encoding.ByteUtil.getInt16LE;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt16LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

/// @param slot The slot the order was placed
/// @param price The limit price for the order (can be 0 for market orders)
///              For orders with an auction, this price isn't used until the auction is complete
///              precision: PRICE_PRECISION
/// @param baseAssetAmount The size of the order
///                        precision for perps: BASE_PRECISION
///                        precision for spot: token mint precision
/// @param baseAssetAmountFilled The amount of the order filled
///                              precision for perps: BASE_PRECISION
///                              precision for spot: token mint precision
/// @param quoteAssetAmountFilled The amount of quote filled for the order
///                               precision: QUOTE_PRECISION
/// @param triggerPrice At what price the order will be triggered. Only relevant for trigger orders
///                     precision: PRICE_PRECISION
/// @param auctionStartPrice The start price for the auction. Only relevant for market/oracle orders
///                          precision: PRICE_PRECISION
/// @param auctionEndPrice The end price for the auction. Only relevant for market/oracle orders
///                        precision: PRICE_PRECISION
/// @param maxTs The time when the order will expire
/// @param oraclePriceOffset If set, the order limit price is the oracle price + this offset
///                          precision: PRICE_PRECISION
/// @param orderId The id for the order. Each users has their own order id space
/// @param marketIndex The perp/spot market index
/// @param status Whether the order is open or unused
/// @param orderType The type of order
/// @param marketType Whether market is spot or perp
/// @param userOrderId User generated order id. Can make it easier to place/cancel orders
/// @param existingPositionDirection What the users position was when the order was placed
/// @param direction Whether the user is going long or short. LONG = bid, SHORT = ask
/// @param reduceOnly Whether the order is allowed to only reduce position size
/// @param postOnly Whether the order must be a maker
/// @param immediateOrCancel Whether the order must be canceled the same slot it is placed
/// @param triggerCondition Whether the order is triggered above or below the trigger price. Only relevant for trigger orders
/// @param auctionDuration How many slots the auction lasts
/// @param postedSlotTail Last 8 bits of the slot the order was posted on-chain (not order slot for signed msg orders)
/// @param bitFlags Bitflags for further classification
///                 0: is_signed_message
public record Order(long slot,
                    long price,
                    long baseAssetAmount,
                    long baseAssetAmountFilled,
                    long quoteAssetAmountFilled,
                    long triggerPrice,
                    long auctionStartPrice,
                    long auctionEndPrice,
                    long maxTs,
                    int oraclePriceOffset,
                    int orderId,
                    int marketIndex,
                    OrderStatus status,
                    OrderType orderType,
                    MarketType marketType,
                    int userOrderId,
                    PositionDirection existingPositionDirection,
                    PositionDirection direction,
                    boolean reduceOnly,
                    boolean postOnly,
                    boolean immediateOrCancel,
                    OrderTriggerCondition triggerCondition,
                    int auctionDuration,
                    int postedSlotTail,
                    int bitFlags,
                    byte[] padding) implements Borsh {

  public static final int BYTES = 96;
  public static final int PADDING_LEN = 1;

  public static Order read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var slot = getInt64LE(_data, i);
    i += 8;
    final var price = getInt64LE(_data, i);
    i += 8;
    final var baseAssetAmount = getInt64LE(_data, i);
    i += 8;
    final var baseAssetAmountFilled = getInt64LE(_data, i);
    i += 8;
    final var quoteAssetAmountFilled = getInt64LE(_data, i);
    i += 8;
    final var triggerPrice = getInt64LE(_data, i);
    i += 8;
    final var auctionStartPrice = getInt64LE(_data, i);
    i += 8;
    final var auctionEndPrice = getInt64LE(_data, i);
    i += 8;
    final var maxTs = getInt64LE(_data, i);
    i += 8;
    final var oraclePriceOffset = getInt32LE(_data, i);
    i += 4;
    final var orderId = getInt32LE(_data, i);
    i += 4;
    final var marketIndex = getInt16LE(_data, i);
    i += 2;
    final var status = OrderStatus.read(_data, i);
    i += status.l();
    final var orderType = OrderType.read(_data, i);
    i += orderType.l();
    final var marketType = MarketType.read(_data, i);
    i += marketType.l();
    final var userOrderId = _data[i] & 0xFF;
    ++i;
    final var existingPositionDirection = PositionDirection.read(_data, i);
    i += existingPositionDirection.l();
    final var direction = PositionDirection.read(_data, i);
    i += direction.l();
    final var reduceOnly = _data[i] == 1;
    ++i;
    final var postOnly = _data[i] == 1;
    ++i;
    final var immediateOrCancel = _data[i] == 1;
    ++i;
    final var triggerCondition = OrderTriggerCondition.read(_data, i);
    i += triggerCondition.l();
    final var auctionDuration = _data[i] & 0xFF;
    ++i;
    final var postedSlotTail = _data[i] & 0xFF;
    ++i;
    final var bitFlags = _data[i] & 0xFF;
    ++i;
    final var padding = new byte[1];
    Borsh.readArray(padding, _data, i);
    return new Order(slot,
                     price,
                     baseAssetAmount,
                     baseAssetAmountFilled,
                     quoteAssetAmountFilled,
                     triggerPrice,
                     auctionStartPrice,
                     auctionEndPrice,
                     maxTs,
                     oraclePriceOffset,
                     orderId,
                     marketIndex,
                     status,
                     orderType,
                     marketType,
                     userOrderId,
                     existingPositionDirection,
                     direction,
                     reduceOnly,
                     postOnly,
                     immediateOrCancel,
                     triggerCondition,
                     auctionDuration,
                     postedSlotTail,
                     bitFlags,
                     padding);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt64LE(_data, i, slot);
    i += 8;
    putInt64LE(_data, i, price);
    i += 8;
    putInt64LE(_data, i, baseAssetAmount);
    i += 8;
    putInt64LE(_data, i, baseAssetAmountFilled);
    i += 8;
    putInt64LE(_data, i, quoteAssetAmountFilled);
    i += 8;
    putInt64LE(_data, i, triggerPrice);
    i += 8;
    putInt64LE(_data, i, auctionStartPrice);
    i += 8;
    putInt64LE(_data, i, auctionEndPrice);
    i += 8;
    putInt64LE(_data, i, maxTs);
    i += 8;
    putInt32LE(_data, i, oraclePriceOffset);
    i += 4;
    putInt32LE(_data, i, orderId);
    i += 4;
    putInt16LE(_data, i, marketIndex);
    i += 2;
    i += status.write(_data, i);
    i += orderType.write(_data, i);
    i += marketType.write(_data, i);
    _data[i] = (byte) userOrderId;
    ++i;
    i += existingPositionDirection.write(_data, i);
    i += direction.write(_data, i);
    _data[i] = (byte) (reduceOnly ? 1 : 0);
    ++i;
    _data[i] = (byte) (postOnly ? 1 : 0);
    ++i;
    _data[i] = (byte) (immediateOrCancel ? 1 : 0);
    ++i;
    i += triggerCondition.write(_data, i);
    _data[i] = (byte) auctionDuration;
    ++i;
    _data[i] = (byte) postedSlotTail;
    ++i;
    _data[i] = (byte) bitFlags;
    ++i;
    i += Borsh.writeArrayChecked(padding, 1, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

package software.sava.idl.clients.drift.gen.events;

import java.util.OptionalInt;
import java.util.OptionalLong;

import software.sava.core.accounts.PublicKey;
import software.sava.core.borsh.Borsh;
import software.sava.core.programs.Discriminator;
import software.sava.idl.clients.drift.gen.types.MarketType;
import software.sava.idl.clients.drift.gen.types.OrderAction;
import software.sava.idl.clients.drift.gen.types.OrderActionExplanation;
import software.sava.idl.clients.drift.gen.types.PositionDirection;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt16LE;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt16LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record OrderActionRecord(Discriminator discriminator,
                                long ts,
                                OrderAction action,
                                OrderActionExplanation actionExplanation,
                                int marketIndex,
                                MarketType marketType,
                                PublicKey filler,
                                OptionalLong fillerReward,
                                OptionalLong fillRecordId,
                                OptionalLong baseAssetAmountFilled,
                                OptionalLong quoteAssetAmountFilled,
                                OptionalLong takerFee,
                                OptionalLong makerFee,
                                OptionalInt referrerReward,
                                OptionalLong quoteAssetAmountSurplus,
                                OptionalLong spotFulfillmentMethodFee,
                                PublicKey taker,
                                OptionalInt takerOrderId,
                                PositionDirection takerOrderDirection,
                                OptionalLong takerOrderBaseAssetAmount,
                                OptionalLong takerOrderCumulativeBaseAssetAmountFilled,
                                OptionalLong takerOrderCumulativeQuoteAssetAmountFilled,
                                PublicKey maker,
                                OptionalInt makerOrderId,
                                PositionDirection makerOrderDirection,
                                OptionalLong makerOrderBaseAssetAmount,
                                OptionalLong makerOrderCumulativeBaseAssetAmountFilled,
                                OptionalLong makerOrderCumulativeQuoteAssetAmountFilled,
                                long oraclePrice,
                                int bitFlags,
                                OptionalLong takerExistingQuoteEntryAmount,
                                OptionalLong takerExistingBaseAssetAmount,
                                OptionalLong makerExistingQuoteEntryAmount,
                                OptionalLong makerExistingBaseAssetAmount,
                                OptionalLong triggerPrice,
                                OptionalInt builderIdx,
                                OptionalLong builderFee) implements DriftEvent {

  public static final Discriminator DISCRIMINATOR = toDiscriminator(218, 84, 214, 163, 196, 206, 189, 250);

  public static OrderActionRecord read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createDiscriminator(_data, _offset, 8);
    int i = _offset + discriminator.length();
    final var ts = getInt64LE(_data, i);
    i += 8;
    final var action = OrderAction.read(_data, i);
    i += Borsh.len(action);
    final var actionExplanation = OrderActionExplanation.read(_data, i);
    i += Borsh.len(actionExplanation);
    final var marketIndex = getInt16LE(_data, i);
    i += 2;
    final var marketType = MarketType.read(_data, i);
    i += Borsh.len(marketType);
    final PublicKey filler;
    if (_data[i] == 0) {
      filler = null;
      ++i;
    ;
    } else {
      ++i;
    ;
      filler = readPubKey(_data, i);
      i += 32;
    }
    final OptionalLong fillerReward;
    if (_data[i] == 0) {
      fillerReward = OptionalLong.empty();
      ++i;
    ;
    } else {
      ++i;
    ;
      fillerReward = OptionalLong.of(getInt64LE(_data, i));
      i += 8;
    }
    final OptionalLong fillRecordId;
    if (_data[i] == 0) {
      fillRecordId = OptionalLong.empty();
      ++i;
    ;
    } else {
      ++i;
    ;
      fillRecordId = OptionalLong.of(getInt64LE(_data, i));
      i += 8;
    }
    final OptionalLong baseAssetAmountFilled;
    if (_data[i] == 0) {
      baseAssetAmountFilled = OptionalLong.empty();
      ++i;
    ;
    } else {
      ++i;
    ;
      baseAssetAmountFilled = OptionalLong.of(getInt64LE(_data, i));
      i += 8;
    }
    final OptionalLong quoteAssetAmountFilled;
    if (_data[i] == 0) {
      quoteAssetAmountFilled = OptionalLong.empty();
      ++i;
    ;
    } else {
      ++i;
    ;
      quoteAssetAmountFilled = OptionalLong.of(getInt64LE(_data, i));
      i += 8;
    }
    final OptionalLong takerFee;
    if (_data[i] == 0) {
      takerFee = OptionalLong.empty();
      ++i;
    ;
    } else {
      ++i;
    ;
      takerFee = OptionalLong.of(getInt64LE(_data, i));
      i += 8;
    }
    final OptionalLong makerFee;
    if (_data[i] == 0) {
      makerFee = OptionalLong.empty();
      ++i;
    ;
    } else {
      ++i;
    ;
      makerFee = OptionalLong.of(getInt64LE(_data, i));
      i += 8;
    }
    final OptionalInt referrerReward;
    if (_data[i] == 0) {
      referrerReward = OptionalInt.empty();
      ++i;
    ;
    } else {
      ++i;
    ;
      referrerReward = OptionalInt.of(getInt32LE(_data, i));
      i += 4;
    }
    final OptionalLong quoteAssetAmountSurplus;
    if (_data[i] == 0) {
      quoteAssetAmountSurplus = OptionalLong.empty();
      ++i;
    ;
    } else {
      ++i;
    ;
      quoteAssetAmountSurplus = OptionalLong.of(getInt64LE(_data, i));
      i += 8;
    }
    final OptionalLong spotFulfillmentMethodFee;
    if (_data[i] == 0) {
      spotFulfillmentMethodFee = OptionalLong.empty();
      ++i;
    ;
    } else {
      ++i;
    ;
      spotFulfillmentMethodFee = OptionalLong.of(getInt64LE(_data, i));
      i += 8;
    }
    final PublicKey taker;
    if (_data[i] == 0) {
      taker = null;
      ++i;
    ;
    } else {
      ++i;
    ;
      taker = readPubKey(_data, i);
      i += 32;
    }
    final OptionalInt takerOrderId;
    if (_data[i] == 0) {
      takerOrderId = OptionalInt.empty();
      ++i;
    ;
    } else {
      ++i;
    ;
      takerOrderId = OptionalInt.of(getInt32LE(_data, i));
      i += 4;
    }
    final PositionDirection takerOrderDirection;
    if (_data[i] == 0) {
      takerOrderDirection = null;
      ++i;
    ;
    } else {
      ++i;
    ;
      takerOrderDirection = PositionDirection.read(_data, i);
      i += Borsh.len(takerOrderDirection);
    }
    final OptionalLong takerOrderBaseAssetAmount;
    if (_data[i] == 0) {
      takerOrderBaseAssetAmount = OptionalLong.empty();
      ++i;
    ;
    } else {
      ++i;
    ;
      takerOrderBaseAssetAmount = OptionalLong.of(getInt64LE(_data, i));
      i += 8;
    }
    final OptionalLong takerOrderCumulativeBaseAssetAmountFilled;
    if (_data[i] == 0) {
      takerOrderCumulativeBaseAssetAmountFilled = OptionalLong.empty();
      ++i;
    ;
    } else {
      ++i;
    ;
      takerOrderCumulativeBaseAssetAmountFilled = OptionalLong.of(getInt64LE(_data, i));
      i += 8;
    }
    final OptionalLong takerOrderCumulativeQuoteAssetAmountFilled;
    if (_data[i] == 0) {
      takerOrderCumulativeQuoteAssetAmountFilled = OptionalLong.empty();
      ++i;
    ;
    } else {
      ++i;
    ;
      takerOrderCumulativeQuoteAssetAmountFilled = OptionalLong.of(getInt64LE(_data, i));
      i += 8;
    }
    final PublicKey maker;
    if (_data[i] == 0) {
      maker = null;
      ++i;
    ;
    } else {
      ++i;
    ;
      maker = readPubKey(_data, i);
      i += 32;
    }
    final OptionalInt makerOrderId;
    if (_data[i] == 0) {
      makerOrderId = OptionalInt.empty();
      ++i;
    ;
    } else {
      ++i;
    ;
      makerOrderId = OptionalInt.of(getInt32LE(_data, i));
      i += 4;
    }
    final PositionDirection makerOrderDirection;
    if (_data[i] == 0) {
      makerOrderDirection = null;
      ++i;
    ;
    } else {
      ++i;
    ;
      makerOrderDirection = PositionDirection.read(_data, i);
      i += Borsh.len(makerOrderDirection);
    }
    final OptionalLong makerOrderBaseAssetAmount;
    if (_data[i] == 0) {
      makerOrderBaseAssetAmount = OptionalLong.empty();
      ++i;
    ;
    } else {
      ++i;
    ;
      makerOrderBaseAssetAmount = OptionalLong.of(getInt64LE(_data, i));
      i += 8;
    }
    final OptionalLong makerOrderCumulativeBaseAssetAmountFilled;
    if (_data[i] == 0) {
      makerOrderCumulativeBaseAssetAmountFilled = OptionalLong.empty();
      ++i;
    ;
    } else {
      ++i;
    ;
      makerOrderCumulativeBaseAssetAmountFilled = OptionalLong.of(getInt64LE(_data, i));
      i += 8;
    }
    final OptionalLong makerOrderCumulativeQuoteAssetAmountFilled;
    if (_data[i] == 0) {
      makerOrderCumulativeQuoteAssetAmountFilled = OptionalLong.empty();
      ++i;
    ;
    } else {
      ++i;
    ;
      makerOrderCumulativeQuoteAssetAmountFilled = OptionalLong.of(getInt64LE(_data, i));
      i += 8;
    }
    final var oraclePrice = getInt64LE(_data, i);
    i += 8;
    final var bitFlags = _data[i] & 0xFF;
    ++i;
    final OptionalLong takerExistingQuoteEntryAmount;
    if (_data[i] == 0) {
      takerExistingQuoteEntryAmount = OptionalLong.empty();
      ++i;
    ;
    } else {
      ++i;
    ;
      takerExistingQuoteEntryAmount = OptionalLong.of(getInt64LE(_data, i));
      i += 8;
    }
    final OptionalLong takerExistingBaseAssetAmount;
    if (_data[i] == 0) {
      takerExistingBaseAssetAmount = OptionalLong.empty();
      ++i;
    ;
    } else {
      ++i;
    ;
      takerExistingBaseAssetAmount = OptionalLong.of(getInt64LE(_data, i));
      i += 8;
    }
    final OptionalLong makerExistingQuoteEntryAmount;
    if (_data[i] == 0) {
      makerExistingQuoteEntryAmount = OptionalLong.empty();
      ++i;
    ;
    } else {
      ++i;
    ;
      makerExistingQuoteEntryAmount = OptionalLong.of(getInt64LE(_data, i));
      i += 8;
    }
    final OptionalLong makerExistingBaseAssetAmount;
    if (_data[i] == 0) {
      makerExistingBaseAssetAmount = OptionalLong.empty();
      ++i;
    ;
    } else {
      ++i;
    ;
      makerExistingBaseAssetAmount = OptionalLong.of(getInt64LE(_data, i));
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
    final OptionalInt builderIdx;
    if (_data[i] == 0) {
      builderIdx = OptionalInt.empty();
      ++i;
    ;
    } else {
      ++i;
    ;
      builderIdx = OptionalInt.of(_data[i] & 0xFF);
      ++i;
    }
    final OptionalLong builderFee;
    if (_data[i] == 0) {
      builderFee = OptionalLong.empty();
    } else {
      ++i;
    ;
      builderFee = OptionalLong.of(getInt64LE(_data, i));
    }
    return new OrderActionRecord(discriminator,
                                 ts,
                                 action,
                                 actionExplanation,
                                 marketIndex,
                                 marketType,
                                 filler,
                                 fillerReward,
                                 fillRecordId,
                                 baseAssetAmountFilled,
                                 quoteAssetAmountFilled,
                                 takerFee,
                                 makerFee,
                                 referrerReward,
                                 quoteAssetAmountSurplus,
                                 spotFulfillmentMethodFee,
                                 taker,
                                 takerOrderId,
                                 takerOrderDirection,
                                 takerOrderBaseAssetAmount,
                                 takerOrderCumulativeBaseAssetAmountFilled,
                                 takerOrderCumulativeQuoteAssetAmountFilled,
                                 maker,
                                 makerOrderId,
                                 makerOrderDirection,
                                 makerOrderBaseAssetAmount,
                                 makerOrderCumulativeBaseAssetAmountFilled,
                                 makerOrderCumulativeQuoteAssetAmountFilled,
                                 oraclePrice,
                                 bitFlags,
                                 takerExistingQuoteEntryAmount,
                                 takerExistingBaseAssetAmount,
                                 makerExistingQuoteEntryAmount,
                                 makerExistingBaseAssetAmount,
                                 triggerPrice,
                                 builderIdx,
                                 builderFee);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    putInt64LE(_data, i, ts);
    i += 8;
    i += Borsh.write(action, _data, i);
    i += Borsh.write(actionExplanation, _data, i);
    putInt16LE(_data, i, marketIndex);
    i += 2;
    i += Borsh.write(marketType, _data, i);
    i += Borsh.writeOptional(filler, _data, i);
    i += Borsh.writeOptional(fillerReward, _data, i);
    i += Borsh.writeOptional(fillRecordId, _data, i);
    i += Borsh.writeOptional(baseAssetAmountFilled, _data, i);
    i += Borsh.writeOptional(quoteAssetAmountFilled, _data, i);
    i += Borsh.writeOptional(takerFee, _data, i);
    i += Borsh.writeOptional(makerFee, _data, i);
    i += Borsh.writeOptional(referrerReward, _data, i);
    i += Borsh.writeOptional(quoteAssetAmountSurplus, _data, i);
    i += Borsh.writeOptional(spotFulfillmentMethodFee, _data, i);
    i += Borsh.writeOptional(taker, _data, i);
    i += Borsh.writeOptional(takerOrderId, _data, i);
    i += Borsh.writeOptional(takerOrderDirection, _data, i);
    i += Borsh.writeOptional(takerOrderBaseAssetAmount, _data, i);
    i += Borsh.writeOptional(takerOrderCumulativeBaseAssetAmountFilled, _data, i);
    i += Borsh.writeOptional(takerOrderCumulativeQuoteAssetAmountFilled, _data, i);
    i += Borsh.writeOptional(maker, _data, i);
    i += Borsh.writeOptional(makerOrderId, _data, i);
    i += Borsh.writeOptional(makerOrderDirection, _data, i);
    i += Borsh.writeOptional(makerOrderBaseAssetAmount, _data, i);
    i += Borsh.writeOptional(makerOrderCumulativeBaseAssetAmountFilled, _data, i);
    i += Borsh.writeOptional(makerOrderCumulativeQuoteAssetAmountFilled, _data, i);
    putInt64LE(_data, i, oraclePrice);
    i += 8;
    _data[i] = (byte) bitFlags;
    ++i;
    i += Borsh.writeOptional(takerExistingQuoteEntryAmount, _data, i);
    i += Borsh.writeOptional(takerExistingBaseAssetAmount, _data, i);
    i += Borsh.writeOptional(makerExistingQuoteEntryAmount, _data, i);
    i += Borsh.writeOptional(makerExistingBaseAssetAmount, _data, i);
    i += Borsh.writeOptional(triggerPrice, _data, i);
    i += Borsh.writeOptionalbyte(builderIdx, _data, i);
    i += Borsh.writeOptional(builderFee, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return discriminator.length() + 8
         + Borsh.len(action)
         + Borsh.len(actionExplanation)
         + 2
         + Borsh.len(marketType)
         + (filler == null ? 1 : (1 + 32))
         + (fillerReward == null || fillerReward.isEmpty() ? 1 : (1 + 8))
         + (fillRecordId == null || fillRecordId.isEmpty() ? 1 : (1 + 8))
         + (baseAssetAmountFilled == null || baseAssetAmountFilled.isEmpty() ? 1 : (1 + 8))
         + (quoteAssetAmountFilled == null || quoteAssetAmountFilled.isEmpty() ? 1 : (1 + 8))
         + (takerFee == null || takerFee.isEmpty() ? 1 : (1 + 8))
         + (makerFee == null || makerFee.isEmpty() ? 1 : (1 + 8))
         + (referrerReward == null || referrerReward.isEmpty() ? 1 : (1 + 4))
         + (quoteAssetAmountSurplus == null || quoteAssetAmountSurplus.isEmpty() ? 1 : (1 + 8))
         + (spotFulfillmentMethodFee == null || spotFulfillmentMethodFee.isEmpty() ? 1 : (1 + 8))
         + (taker == null ? 1 : (1 + 32))
         + (takerOrderId == null || takerOrderId.isEmpty() ? 1 : (1 + 4))
         + (takerOrderDirection == null ? 1 : (1 + Borsh.len(takerOrderDirection)))
         + (takerOrderBaseAssetAmount == null || takerOrderBaseAssetAmount.isEmpty() ? 1 : (1 + 8))
         + (takerOrderCumulativeBaseAssetAmountFilled == null || takerOrderCumulativeBaseAssetAmountFilled.isEmpty() ? 1 : (1 + 8))
         + (takerOrderCumulativeQuoteAssetAmountFilled == null || takerOrderCumulativeQuoteAssetAmountFilled.isEmpty() ? 1 : (1 + 8))
         + (maker == null ? 1 : (1 + 32))
         + (makerOrderId == null || makerOrderId.isEmpty() ? 1 : (1 + 4))
         + (makerOrderDirection == null ? 1 : (1 + Borsh.len(makerOrderDirection)))
         + (makerOrderBaseAssetAmount == null || makerOrderBaseAssetAmount.isEmpty() ? 1 : (1 + 8))
         + (makerOrderCumulativeBaseAssetAmountFilled == null || makerOrderCumulativeBaseAssetAmountFilled.isEmpty() ? 1 : (1 + 8))
         + (makerOrderCumulativeQuoteAssetAmountFilled == null || makerOrderCumulativeQuoteAssetAmountFilled.isEmpty() ? 1 : (1 + 8))
         + 8
         + 1
         + (takerExistingQuoteEntryAmount == null || takerExistingQuoteEntryAmount.isEmpty() ? 1 : (1 + 8))
         + (takerExistingBaseAssetAmount == null || takerExistingBaseAssetAmount.isEmpty() ? 1 : (1 + 8))
         + (makerExistingQuoteEntryAmount == null || makerExistingQuoteEntryAmount.isEmpty() ? 1 : (1 + 8))
         + (makerExistingBaseAssetAmount == null || makerExistingBaseAssetAmount.isEmpty() ? 1 : (1 + 8))
         + (triggerPrice == null || triggerPrice.isEmpty() ? 1 : (1 + 8))
         + (builderIdx == null || builderIdx.isEmpty() ? 1 : (1 + 1))
         + (builderFee == null || builderFee.isEmpty() ? 1 : (1 + 8));
  }
}

package software.sava.idl.clients.phoenix.perpetuals.gen.types;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;
import software.sava.idl.clients.phoenix.perpetuals.gen.types.BaseLots;
import software.sava.idl.clients.phoenix.perpetuals.gen.types.Direction;
import software.sava.idl.clients.phoenix.perpetuals.gen.types.Side;
import software.sava.idl.clients.phoenix.perpetuals.gen.types.StopLossOrderKind;
import software.sava.idl.clients.phoenix.perpetuals.gen.types.Ticks;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

/// MarketEvent::StopLossPlaced Borsh variant 38.
/// Payload type: StopLossPlacedEvent.
///
public record StopLossPlacedEvent(Discriminator discriminator,
                                  PublicKey trader,
                                  long sequenceNumber,
                                  long prevSequenceNumberSlot,
                                  long assetId,
                                  Ticks triggerPrice,
                                  Ticks executionPrice,
                                  BaseLots tradeSize,
                                  Side tradeSide,
                                  Direction executionDirection,
                                  int positionSequenceNumber,
                                  long placeSlot,
                                  PublicKey fundingKey,
                                  StopLossOrderKind orderKind) implements EternalEvent {

  public static final int BYTES = 132;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(38, 0, 0, 0, 0, 0, 0, 0);

  public static final int TRADER_OFFSET = 8;
  public static final int SEQUENCE_NUMBER_OFFSET = 40;
  public static final int PREV_SEQUENCE_NUMBER_SLOT_OFFSET = 48;
  public static final int ASSET_ID_OFFSET = 56;
  public static final int TRIGGER_PRICE_OFFSET = 64;
  public static final int EXECUTION_PRICE_OFFSET = 72;
  public static final int TRADE_SIZE_OFFSET = 80;
  public static final int TRADE_SIDE_OFFSET = 88;
  public static final int EXECUTION_DIRECTION_OFFSET = 89;
  public static final int POSITION_SEQUENCE_NUMBER_OFFSET = 90;
  public static final int PLACE_SLOT_OFFSET = 91;
  public static final int FUNDING_KEY_OFFSET = 99;
  public static final int ORDER_KIND_OFFSET = 131;

  public static StopLossPlacedEvent read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var trader = readPubKey(_data, i);
    i += 32;
    final var sequenceNumber = getInt64LE(_data, i);
    i += 8;
    final var prevSequenceNumberSlot = getInt64LE(_data, i);
    i += 8;
    final var assetId = getInt64LE(_data, i);
    i += 8;
    final var triggerPrice = Ticks.read(_data, i);
    i += triggerPrice.l();
    final var executionPrice = Ticks.read(_data, i);
    i += executionPrice.l();
    final var tradeSize = BaseLots.read(_data, i);
    i += tradeSize.l();
    final var tradeSide = Side.read(_data, i);
    i += tradeSide.l();
    final var executionDirection = Direction.read(_data, i);
    i += executionDirection.l();
    final var positionSequenceNumber = _data[i] & 0xFF;
    ++i;
    final var placeSlot = getInt64LE(_data, i);
    i += 8;
    final var fundingKey = readPubKey(_data, i);
    i += 32;
    final var orderKind = StopLossOrderKind.read(_data, i);
    return new StopLossPlacedEvent(discriminator,
                                   trader,
                                   sequenceNumber,
                                   prevSequenceNumberSlot,
                                   assetId,
                                   triggerPrice,
                                   executionPrice,
                                   tradeSize,
                                   tradeSide,
                                   executionDirection,
                                   positionSequenceNumber,
                                   placeSlot,
                                   fundingKey,
                                   orderKind);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    trader.write(_data, i);
    i += 32;
    putInt64LE(_data, i, sequenceNumber);
    i += 8;
    putInt64LE(_data, i, prevSequenceNumberSlot);
    i += 8;
    putInt64LE(_data, i, assetId);
    i += 8;
    i += triggerPrice.write(_data, i);
    i += executionPrice.write(_data, i);
    i += tradeSize.write(_data, i);
    i += tradeSide.write(_data, i);
    i += executionDirection.write(_data, i);
    _data[i] = (byte) positionSequenceNumber;
    ++i;
    putInt64LE(_data, i, placeSlot);
    i += 8;
    fundingKey.write(_data, i);
    i += 32;
    i += orderKind.write(_data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

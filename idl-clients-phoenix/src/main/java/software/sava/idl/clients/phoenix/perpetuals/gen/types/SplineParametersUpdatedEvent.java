package software.sava.idl.clients.phoenix.perpetuals.gen.types;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;
import software.sava.idl.clients.core.gen.SerDeUtil;
import software.sava.idl.clients.phoenix.perpetuals.gen.types.Symbol;
import software.sava.idl.clients.phoenix.perpetuals.gen.types.TickRegion;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

/// MarketEvent::SplineParametersUpdated Borsh variant 23.
/// Payload type: SplineParametersUpdatedEvent.
///
public record SplineParametersUpdatedEvent(Discriminator discriminator,
                                           PublicKey trader,
                                           long sequenceNumber,
                                           long prevSequenceNumberSlot,
                                           PublicKey authority,
                                           PublicKey market,
                                           Symbol symbol,
                                           long midPrice,
                                           TickRegion[] bidRegions,
                                           TickRegion[] askRegions) implements EternalEvent {

  public static final int BYTES = 1104;
  public static final int BID_REGIONS_LEN = 10;
  public static final int ASK_REGIONS_LEN = 10;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(23, 0, 0, 0, 0, 0, 0, 0);

  public static final int TRADER_OFFSET = 8;
  public static final int SEQUENCE_NUMBER_OFFSET = 40;
  public static final int PREV_SEQUENCE_NUMBER_SLOT_OFFSET = 48;
  public static final int AUTHORITY_OFFSET = 56;
  public static final int MARKET_OFFSET = 88;
  public static final int SYMBOL_OFFSET = 120;
  public static final int MID_PRICE_OFFSET = 136;
  public static final int BID_REGIONS_OFFSET = 144;
  public static final int ASK_REGIONS_OFFSET = 624;

  public static SplineParametersUpdatedEvent read(final byte[] _data, final int _offset) {
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
    final var authority = readPubKey(_data, i);
    i += 32;
    final var market = readPubKey(_data, i);
    i += 32;
    final var symbol = Symbol.read(_data, i);
    i += symbol.l();
    final var midPrice = getInt64LE(_data, i);
    i += 8;
    final var bidRegions = new TickRegion[10];
    i += SerDeUtil.readArray(bidRegions, TickRegion::read, _data, i);
    final var askRegions = new TickRegion[10];
    SerDeUtil.readArray(askRegions, TickRegion::read, _data, i);
    return new SplineParametersUpdatedEvent(discriminator,
                                            trader,
                                            sequenceNumber,
                                            prevSequenceNumberSlot,
                                            authority,
                                            market,
                                            symbol,
                                            midPrice,
                                            bidRegions,
                                            askRegions);
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
    authority.write(_data, i);
    i += 32;
    market.write(_data, i);
    i += 32;
    i += symbol.write(_data, i);
    putInt64LE(_data, i, midPrice);
    i += 8;
    i += SerDeUtil.writeArrayChecked(bidRegions, 10, _data, i);
    i += SerDeUtil.writeArrayChecked(askRegions, 10, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

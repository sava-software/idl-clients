package software.sava.idl.clients.phoenix.perpetuals.gen.types;

import software.sava.core.programs.Discriminator;
import software.sava.idl.clients.phoenix.perpetuals.gen.types.MarketStatus;

import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

/// MarketEvent::MarketTombstoned Borsh variant 64.
/// Payload type: MarketTombstonedEvent.
///
public record MarketTombstonedEvent(Discriminator discriminator,
                                    MarketStatus previousMarketStatus,
                                    long finalSequenceNumber,
                                    long finalTradeSequenceNumber,
                                    long finalOrderSequenceNumber) implements EternalEvent {

  public static final int BYTES = 33;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(64, 0, 0, 0, 0, 0, 0, 0);

  public static final int PREVIOUS_MARKET_STATUS_OFFSET = 8;
  public static final int FINAL_SEQUENCE_NUMBER_OFFSET = 9;
  public static final int FINAL_TRADE_SEQUENCE_NUMBER_OFFSET = 17;
  public static final int FINAL_ORDER_SEQUENCE_NUMBER_OFFSET = 25;

  public static MarketTombstonedEvent read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var previousMarketStatus = MarketStatus.read(_data, i);
    i += previousMarketStatus.l();
    final var finalSequenceNumber = getInt64LE(_data, i);
    i += 8;
    final var finalTradeSequenceNumber = getInt64LE(_data, i);
    i += 8;
    final var finalOrderSequenceNumber = getInt64LE(_data, i);
    return new MarketTombstonedEvent(discriminator,
                                     previousMarketStatus,
                                     finalSequenceNumber,
                                     finalTradeSequenceNumber,
                                     finalOrderSequenceNumber);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    i += previousMarketStatus.write(_data, i);
    putInt64LE(_data, i, finalSequenceNumber);
    i += 8;
    putInt64LE(_data, i, finalTradeSequenceNumber);
    i += 8;
    putInt64LE(_data, i, finalOrderSequenceNumber);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

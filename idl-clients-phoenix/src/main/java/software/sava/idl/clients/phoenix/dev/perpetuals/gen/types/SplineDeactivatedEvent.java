package software.sava.idl.clients.phoenix.dev.perpetuals.gen.types;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;
import software.sava.idl.clients.phoenix.dev.perpetuals.gen.types.Symbol;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

/// MarketEvent::SplineDeactivated Borsh variant 21.
/// Payload type: SplineDeactivatedEvent.
///
public record SplineDeactivatedEvent(Discriminator discriminator,
                                     PublicKey trader,
                                     long sequenceNumber,
                                     long prevSequenceNumberSlot,
                                     PublicKey authority,
                                     PublicKey market,
                                     Symbol symbol) implements EternalEvent {

  public static final int BYTES = 136;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(21, 0, 0, 0, 0, 0, 0, 0);

  public static final int TRADER_OFFSET = 8;
  public static final int SEQUENCE_NUMBER_OFFSET = 40;
  public static final int PREV_SEQUENCE_NUMBER_SLOT_OFFSET = 48;
  public static final int AUTHORITY_OFFSET = 56;
  public static final int MARKET_OFFSET = 88;
  public static final int SYMBOL_OFFSET = 120;

  public static SplineDeactivatedEvent read(final byte[] _data, final int _offset) {
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
    return new SplineDeactivatedEvent(discriminator,
                                      trader,
                                      sequenceNumber,
                                      prevSequenceNumberSlot,
                                      authority,
                                      market,
                                      symbol);
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
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

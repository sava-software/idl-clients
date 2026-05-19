package software.sava.idl.clients.phoenix.perpetuals.gen.types;

import software.sava.core.programs.Discriminator;
import software.sava.idl.clients.phoenix.perpetuals.gen.types.MarketStatus;

import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

/// MarketEvent::MarketStatusChanged Borsh variant 25.
/// Payload type: MarketStatusChangedEvent.
///
public record MarketStatusChangedEvent(Discriminator discriminator, MarketStatus previousMarketStatus, MarketStatus newMarketStatus) implements EternalEvent {

  public static final int BYTES = 10;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(25, 0, 0, 0, 0, 0, 0, 0);

  public static final int PREVIOUS_MARKET_STATUS_OFFSET = 8;
  public static final int NEW_MARKET_STATUS_OFFSET = 9;

  public static MarketStatusChangedEvent read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var previousMarketStatus = MarketStatus.read(_data, i);
    i += previousMarketStatus.l();
    final var newMarketStatus = MarketStatus.read(_data, i);
    return new MarketStatusChangedEvent(discriminator, previousMarketStatus, newMarketStatus);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    i += previousMarketStatus.write(_data, i);
    i += newMarketStatus.write(_data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

package software.sava.idl.clients.phoenix.dev.perpetuals.gen.types;

import software.sava.core.programs.Discriminator;
import software.sava.idl.clients.phoenix.dev.perpetuals.gen.types.MarketStatus;
import software.sava.idl.clients.phoenix.dev.perpetuals.gen.types.Ticks;

import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

/// MarketEvent::MarketClosed Borsh variant 50.
/// Payload type: MarketClosedEvent.
///
public record MarketClosedEvent(Discriminator discriminator, MarketStatus previousMarketStatus, Ticks finalizedMarkPrice) implements EternalEvent {

  public static final int BYTES = 17;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(50, 0, 0, 0, 0, 0, 0, 0);

  public static final int PREVIOUS_MARKET_STATUS_OFFSET = 8;
  public static final int FINALIZED_MARK_PRICE_OFFSET = 9;

  public static MarketClosedEvent read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var previousMarketStatus = MarketStatus.read(_data, i);
    i += previousMarketStatus.l();
    final var finalizedMarkPrice = Ticks.read(_data, i);
    return new MarketClosedEvent(discriminator, previousMarketStatus, finalizedMarkPrice);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    i += previousMarketStatus.write(_data, i);
    i += finalizedMarkPrice.write(_data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

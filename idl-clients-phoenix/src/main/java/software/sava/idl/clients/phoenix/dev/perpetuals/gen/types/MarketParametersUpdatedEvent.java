package software.sava.idl.clients.phoenix.dev.perpetuals.gen.types;

import software.sava.core.programs.Discriminator;

import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

/// MarketEvent::MarketParametersUpdated Borsh variant 26.
/// Payload type: MarketParametersUpdatedEvent.
public record MarketParametersUpdatedEvent(Discriminator discriminator) implements EternalEvent {

  public static final Discriminator DISCRIMINATOR = toDiscriminator(26, 0, 0, 0, 0, 0, 0, 0);

  public static MarketParametersUpdatedEvent read(final byte[] _data, final int _offset) {
    return _data == null || _data.length == 0
        ? null
        : new MarketParametersUpdatedEvent(createAnchorDiscriminator(_data, _offset));
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    discriminator.write(_data, _offset);
    return 8;
  }

  @Override
  public int l() {
    return 8;
  }
}

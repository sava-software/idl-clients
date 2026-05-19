package software.sava.idl.clients.phoenix.perpetuals.gen.types;

import software.sava.core.programs.Discriminator;

import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

/// MarketEvent::MarketAdded Borsh variant 24.
/// Payload type: MarketAddedEvent.
public record MarketAddedEvent(Discriminator discriminator) implements EternalEvent {

  public static final Discriminator DISCRIMINATOR = toDiscriminator(24, 0, 0, 0, 0, 0, 0, 0);

  public static MarketAddedEvent read(final byte[] _data, final int _offset) {
    return _data == null || _data.length == 0
        ? null
        : new MarketAddedEvent(createAnchorDiscriminator(_data, _offset));
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

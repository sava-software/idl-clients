package software.sava.idl.clients.cctp.message_transmitter.v2.gen.events;

import software.sava.core.programs.Discriminator;

import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record Pause(Discriminator discriminator) implements MessageTransmitterV2Event {

  public static final Discriminator DISCRIMINATOR = toDiscriminator(194, 251, 232, 196, 118, 95, 111, 219);

  public static Pause read(final byte[] _data, final int _offset) {
    return _data == null || _data.length == 0
        ? null
        : new Pause(createAnchorDiscriminator(_data, _offset));
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

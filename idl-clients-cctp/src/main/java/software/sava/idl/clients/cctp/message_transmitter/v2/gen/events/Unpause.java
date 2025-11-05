package software.sava.idl.clients.cctp.message_transmitter.v2.gen.events;

import software.sava.core.programs.Discriminator;

import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record Unpause(Discriminator discriminator) implements MessageTransmitterV2Event {

  public static final Discriminator DISCRIMINATOR = toDiscriminator(241, 149, 104, 90, 199, 136, 219, 146);

  public static Unpause read(final byte[] _data, final int _offset) {
    return _data == null || _data.length == 0
        ? null
        : new Unpause(createAnchorDiscriminator(_data, _offset));
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

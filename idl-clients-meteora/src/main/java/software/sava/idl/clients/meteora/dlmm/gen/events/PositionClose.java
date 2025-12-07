package software.sava.idl.clients.meteora.dlmm.gen.events;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record PositionClose(Discriminator discriminator, PublicKey position, PublicKey owner) implements LbClmmEvent {

  public static final int BYTES = 72;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(255, 196, 16, 107, 28, 202, 53, 128);

  public static PositionClose read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var position = readPubKey(_data, i);
    i += 32;
    final var owner = readPubKey(_data, i);
    return new PositionClose(discriminator, position, owner);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    position.write(_data, i);
    i += 32;
    owner.write(_data, i);
    i += 32;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

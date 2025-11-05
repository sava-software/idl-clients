package software.sava.idl.clients.cctp.message_transmitter.v2.gen.events;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record OwnershipTransferred(Discriminator discriminator, PublicKey previousOwner, PublicKey newOwner) implements MessageTransmitterV2Event {

  public static final int BYTES = 72;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(172, 61, 205, 183, 250, 50, 38, 98);

  public static OwnershipTransferred read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var previousOwner = readPubKey(_data, i);
    i += 32;
    final var newOwner = readPubKey(_data, i);
    return new OwnershipTransferred(discriminator, previousOwner, newOwner);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    previousOwner.write(_data, i);
    i += 32;
    newOwner.write(_data, i);
    i += 32;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

package software.sava.idl.clients.cctp.message_transmitter.v2.gen.events;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record AttesterEnabled(Discriminator discriminator, PublicKey attester) implements MessageTransmitterV2Event {

  public static final int BYTES = 40;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(88, 57, 14, 133, 5, 219, 62, 190);

  public static AttesterEnabled read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var attester = readPubKey(_data, i);
    return new AttesterEnabled(discriminator, attester);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    attester.write(_data, i);
    i += 32;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

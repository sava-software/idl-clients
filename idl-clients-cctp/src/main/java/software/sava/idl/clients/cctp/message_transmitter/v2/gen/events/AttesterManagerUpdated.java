package software.sava.idl.clients.cctp.message_transmitter.v2.gen.events;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record AttesterManagerUpdated(Discriminator discriminator, PublicKey previousAttesterManager, PublicKey newAttesterManager) implements MessageTransmitterV2Event {

  public static final int BYTES = 72;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(5, 97, 191, 108, 44, 189, 69, 88);

  public static final int PREVIOUS_ATTESTER_MANAGER_OFFSET = 8;
  public static final int NEW_ATTESTER_MANAGER_OFFSET = 40;

  public static AttesterManagerUpdated read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var previousAttesterManager = readPubKey(_data, i);
    i += 32;
    final var newAttesterManager = readPubKey(_data, i);
    return new AttesterManagerUpdated(discriminator, previousAttesterManager, newAttesterManager);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    previousAttesterManager.write(_data, i);
    i += 32;
    newAttesterManager.write(_data, i);
    i += 32;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

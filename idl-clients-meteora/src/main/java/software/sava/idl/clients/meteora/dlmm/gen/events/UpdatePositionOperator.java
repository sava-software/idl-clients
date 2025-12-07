package software.sava.idl.clients.meteora.dlmm.gen.events;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record UpdatePositionOperator(Discriminator discriminator,
                                     PublicKey position,
                                     PublicKey oldOperator,
                                     PublicKey newOperator) implements LbClmmEvent {

  public static final int BYTES = 104;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(39, 115, 48, 204, 246, 47, 66, 57);

  public static UpdatePositionOperator read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var position = readPubKey(_data, i);
    i += 32;
    final var oldOperator = readPubKey(_data, i);
    i += 32;
    final var newOperator = readPubKey(_data, i);
    return new UpdatePositionOperator(discriminator, position, oldOperator, newOperator);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    position.write(_data, i);
    i += 32;
    oldOperator.write(_data, i);
    i += 32;
    newOperator.write(_data, i);
    i += 32;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

package software.sava.idl.clients.meteora.dlmm.gen.events;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record SetPositionPermissionlessOperationBitsEvt(Discriminator discriminator,
                                                        PublicKey position,
                                                        PublicKey owner,
                                                        int oldBits,
                                                        int newBits) implements LbClmmEvent {

  public static final int BYTES = 74;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(195, 229, 147, 245, 29, 125, 48, 168);

  public static final int POSITION_OFFSET = 8;
  public static final int OWNER_OFFSET = 40;
  public static final int OLD_BITS_OFFSET = 72;
  public static final int NEW_BITS_OFFSET = 73;

  public static SetPositionPermissionlessOperationBitsEvt read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var position = readPubKey(_data, i);
    i += 32;
    final var owner = readPubKey(_data, i);
    i += 32;
    final var oldBits = _data[i] & 0xFF;
    ++i;
    final var newBits = _data[i] & 0xFF;
    return new SetPositionPermissionlessOperationBitsEvt(discriminator,
                                                         position,
                                                         owner,
                                                         oldBits,
                                                         newBits);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    position.write(_data, i);
    i += 32;
    owner.write(_data, i);
    i += 32;
    _data[i] = (byte) oldBits;
    ++i;
    _data[i] = (byte) newBits;
    ++i;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

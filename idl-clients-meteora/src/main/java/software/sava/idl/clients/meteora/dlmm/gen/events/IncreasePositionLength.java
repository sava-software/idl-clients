package software.sava.idl.clients.meteora.dlmm.gen.events;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt16LE;
import static software.sava.core.encoding.ByteUtil.putInt16LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record IncreasePositionLength(Discriminator discriminator,
                                     PublicKey lbPair,
                                     PublicKey position,
                                     PublicKey owner,
                                     int lengthToAdd,
                                     int side) implements LbClmmEvent {

  public static final int BYTES = 107;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(157, 239, 42, 204, 30, 56, 223, 46);

  public static IncreasePositionLength read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var lbPair = readPubKey(_data, i);
    i += 32;
    final var position = readPubKey(_data, i);
    i += 32;
    final var owner = readPubKey(_data, i);
    i += 32;
    final var lengthToAdd = getInt16LE(_data, i);
    i += 2;
    final var side = _data[i] & 0xFF;
    return new IncreasePositionLength(discriminator,
                                      lbPair,
                                      position,
                                      owner,
                                      lengthToAdd,
                                      side);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    lbPair.write(_data, i);
    i += 32;
    position.write(_data, i);
    i += 32;
    owner.write(_data, i);
    i += 32;
    putInt16LE(_data, i, lengthToAdd);
    i += 2;
    _data[i] = (byte) side;
    ++i;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

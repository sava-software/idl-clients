package software.sava.idl.clients.meteora.dlmm.gen.events;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt16LE;
import static software.sava.core.encoding.ByteUtil.putInt16LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record DecreasePositionLength(Discriminator discriminator,
                                     PublicKey lbPair,
                                     PublicKey position,
                                     PublicKey owner,
                                     int lengthToRemove,
                                     int side) implements LbClmmEvent {

  public static final int BYTES = 107;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(52, 118, 235, 85, 172, 169, 15, 128);

  public static final int LB_PAIR_OFFSET = 8;
  public static final int POSITION_OFFSET = 40;
  public static final int OWNER_OFFSET = 72;
  public static final int LENGTH_TO_REMOVE_OFFSET = 104;
  public static final int SIDE_OFFSET = 106;

  public static DecreasePositionLength read(final byte[] _data, final int _offset) {
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
    final var lengthToRemove = getInt16LE(_data, i);
    i += 2;
    final var side = _data[i] & 0xFF;
    return new DecreasePositionLength(discriminator,
                                      lbPair,
                                      position,
                                      owner,
                                      lengthToRemove,
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
    putInt16LE(_data, i, lengthToRemove);
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

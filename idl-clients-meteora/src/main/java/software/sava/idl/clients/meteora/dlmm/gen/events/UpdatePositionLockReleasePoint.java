package software.sava.idl.clients.meteora.dlmm.gen.events;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record UpdatePositionLockReleasePoint(Discriminator discriminator,
                                             PublicKey position,
                                             long currentPoint,
                                             long newLockReleasePoint,
                                             long oldLockReleasePoint,
                                             PublicKey sender) implements LbClmmEvent {

  public static final int BYTES = 96;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(133, 214, 66, 224, 64, 12, 7, 191);

  public static UpdatePositionLockReleasePoint read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var position = readPubKey(_data, i);
    i += 32;
    final var currentPoint = getInt64LE(_data, i);
    i += 8;
    final var newLockReleasePoint = getInt64LE(_data, i);
    i += 8;
    final var oldLockReleasePoint = getInt64LE(_data, i);
    i += 8;
    final var sender = readPubKey(_data, i);
    return new UpdatePositionLockReleasePoint(discriminator,
                                              position,
                                              currentPoint,
                                              newLockReleasePoint,
                                              oldLockReleasePoint,
                                              sender);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    position.write(_data, i);
    i += 32;
    putInt64LE(_data, i, currentPoint);
    i += 8;
    putInt64LE(_data, i, newLockReleasePoint);
    i += 8;
    putInt64LE(_data, i, oldLockReleasePoint);
    i += 8;
    sender.write(_data, i);
    i += 32;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

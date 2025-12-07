package software.sava.idl.clients.meteora.dlmm.gen.events;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record ClaimFee2(Discriminator discriminator,
                        PublicKey lbPair,
                        PublicKey position,
                        PublicKey owner,
                        long feeX,
                        long feeY,
                        int activeBinId) implements LbClmmEvent {

  public static final int BYTES = 124;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(232, 171, 242, 97, 58, 77, 35, 45);

  public static ClaimFee2 read(final byte[] _data, final int _offset) {
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
    final var feeX = getInt64LE(_data, i);
    i += 8;
    final var feeY = getInt64LE(_data, i);
    i += 8;
    final var activeBinId = getInt32LE(_data, i);
    return new ClaimFee2(discriminator,
                         lbPair,
                         position,
                         owner,
                         feeX,
                         feeY,
                         activeBinId);
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
    putInt64LE(_data, i, feeX);
    i += 8;
    putInt64LE(_data, i, feeY);
    i += 8;
    putInt32LE(_data, i, activeBinId);
    i += 4;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

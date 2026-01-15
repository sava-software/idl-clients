package software.sava.idl.clients.meteora.dlmm.gen.events;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record ClaimFee(Discriminator discriminator,
                       PublicKey lbPair,
                       PublicKey position,
                       PublicKey owner,
                       long feeX,
                       long feeY) implements LbClmmEvent {

  public static final int BYTES = 120;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(75, 122, 154, 48, 140, 74, 123, 163);

  public static final int LB_PAIR_OFFSET = 8;
  public static final int POSITION_OFFSET = 40;
  public static final int OWNER_OFFSET = 72;
  public static final int FEE_X_OFFSET = 104;
  public static final int FEE_Y_OFFSET = 112;

  public static ClaimFee read(final byte[] _data, final int _offset) {
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
    return new ClaimFee(discriminator,
                        lbPair,
                        position,
                        owner,
                        feeX,
                        feeY);
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
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

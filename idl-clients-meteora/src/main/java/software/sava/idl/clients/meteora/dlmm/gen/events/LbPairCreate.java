package software.sava.idl.clients.meteora.dlmm.gen.events;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt16LE;
import static software.sava.core.encoding.ByteUtil.putInt16LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record LbPairCreate(Discriminator discriminator,
                           PublicKey lbPair,
                           int binStep,
                           PublicKey tokenX,
                           PublicKey tokenY) implements LbClmmEvent {

  public static final int BYTES = 106;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(185, 74, 252, 125, 27, 215, 188, 111);

  public static final int LB_PAIR_OFFSET = 8;
  public static final int BIN_STEP_OFFSET = 40;
  public static final int TOKEN_X_OFFSET = 42;
  public static final int TOKEN_Y_OFFSET = 74;

  public static LbPairCreate read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var lbPair = readPubKey(_data, i);
    i += 32;
    final var binStep = getInt16LE(_data, i);
    i += 2;
    final var tokenX = readPubKey(_data, i);
    i += 32;
    final var tokenY = readPubKey(_data, i);
    return new LbPairCreate(discriminator,
                            lbPair,
                            binStep,
                            tokenX,
                            tokenY);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    lbPair.write(_data, i);
    i += 32;
    putInt16LE(_data, i, binStep);
    i += 2;
    tokenX.write(_data, i);
    i += 32;
    tokenY.write(_data, i);
    i += 32;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

package software.sava.idl.clients.meteora.dlmm.gen.events;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record UpdateRewardFunder(Discriminator discriminator,
                                 PublicKey lbPair,
                                 long rewardIndex,
                                 PublicKey oldFunder,
                                 PublicKey newFunder) implements LbClmmEvent {

  public static final int BYTES = 112;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(224, 178, 174, 74, 252, 165, 85, 180);

  public static UpdateRewardFunder read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var lbPair = readPubKey(_data, i);
    i += 32;
    final var rewardIndex = getInt64LE(_data, i);
    i += 8;
    final var oldFunder = readPubKey(_data, i);
    i += 32;
    final var newFunder = readPubKey(_data, i);
    return new UpdateRewardFunder(discriminator,
                                  lbPair,
                                  rewardIndex,
                                  oldFunder,
                                  newFunder);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    lbPair.write(_data, i);
    i += 32;
    putInt64LE(_data, i, rewardIndex);
    i += 8;
    oldFunder.write(_data, i);
    i += 32;
    newFunder.write(_data, i);
    i += 32;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

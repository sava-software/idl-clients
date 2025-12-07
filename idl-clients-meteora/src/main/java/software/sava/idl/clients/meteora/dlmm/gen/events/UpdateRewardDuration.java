package software.sava.idl.clients.meteora.dlmm.gen.events;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record UpdateRewardDuration(Discriminator discriminator,
                                   PublicKey lbPair,
                                   long rewardIndex,
                                   long oldRewardDuration,
                                   long newRewardDuration) implements LbClmmEvent {

  public static final int BYTES = 64;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(223, 245, 224, 153, 49, 29, 163, 172);

  public static UpdateRewardDuration read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var lbPair = readPubKey(_data, i);
    i += 32;
    final var rewardIndex = getInt64LE(_data, i);
    i += 8;
    final var oldRewardDuration = getInt64LE(_data, i);
    i += 8;
    final var newRewardDuration = getInt64LE(_data, i);
    return new UpdateRewardDuration(discriminator,
                                    lbPair,
                                    rewardIndex,
                                    oldRewardDuration,
                                    newRewardDuration);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    lbPair.write(_data, i);
    i += 32;
    putInt64LE(_data, i, rewardIndex);
    i += 8;
    putInt64LE(_data, i, oldRewardDuration);
    i += 8;
    putInt64LE(_data, i, newRewardDuration);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

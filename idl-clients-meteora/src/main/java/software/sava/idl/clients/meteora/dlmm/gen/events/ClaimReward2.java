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

public record ClaimReward2(Discriminator discriminator,
                           PublicKey lbPair,
                           PublicKey position,
                           PublicKey owner,
                           long rewardIndex,
                           long totalReward,
                           int activeBinId) implements LbClmmEvent {

  public static final int BYTES = 124;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(27, 143, 244, 33, 80, 43, 110, 146);

  public static ClaimReward2 read(final byte[] _data, final int _offset) {
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
    final var rewardIndex = getInt64LE(_data, i);
    i += 8;
    final var totalReward = getInt64LE(_data, i);
    i += 8;
    final var activeBinId = getInt32LE(_data, i);
    return new ClaimReward2(discriminator,
                            lbPair,
                            position,
                            owner,
                            rewardIndex,
                            totalReward,
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
    putInt64LE(_data, i, rewardIndex);
    i += 8;
    putInt64LE(_data, i, totalReward);
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

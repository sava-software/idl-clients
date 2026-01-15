package software.sava.idl.clients.meteora.dlmm.gen.events;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record InitializeReward(Discriminator discriminator,
                               PublicKey lbPair,
                               PublicKey rewardMint,
                               PublicKey funder,
                               long rewardIndex,
                               long rewardDuration) implements LbClmmEvent {

  public static final int BYTES = 120;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(211, 153, 88, 62, 149, 60, 177, 70);

  public static final int LB_PAIR_OFFSET = 8;
  public static final int REWARD_MINT_OFFSET = 40;
  public static final int FUNDER_OFFSET = 72;
  public static final int REWARD_INDEX_OFFSET = 104;
  public static final int REWARD_DURATION_OFFSET = 112;

  public static InitializeReward read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var lbPair = readPubKey(_data, i);
    i += 32;
    final var rewardMint = readPubKey(_data, i);
    i += 32;
    final var funder = readPubKey(_data, i);
    i += 32;
    final var rewardIndex = getInt64LE(_data, i);
    i += 8;
    final var rewardDuration = getInt64LE(_data, i);
    return new InitializeReward(discriminator,
                                lbPair,
                                rewardMint,
                                funder,
                                rewardIndex,
                                rewardDuration);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    lbPair.write(_data, i);
    i += 32;
    rewardMint.write(_data, i);
    i += 32;
    funder.write(_data, i);
    i += 32;
    putInt64LE(_data, i, rewardIndex);
    i += 8;
    putInt64LE(_data, i, rewardDuration);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

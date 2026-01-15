package software.sava.idl.clients.meteora.dlmm.gen.events;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record FundReward(Discriminator discriminator,
                         PublicKey lbPair,
                         PublicKey funder,
                         long rewardIndex,
                         long amount) implements LbClmmEvent {

  public static final int BYTES = 88;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(246, 228, 58, 130, 145, 170, 79, 204);

  public static final int LB_PAIR_OFFSET = 8;
  public static final int FUNDER_OFFSET = 40;
  public static final int REWARD_INDEX_OFFSET = 72;
  public static final int AMOUNT_OFFSET = 80;

  public static FundReward read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var lbPair = readPubKey(_data, i);
    i += 32;
    final var funder = readPubKey(_data, i);
    i += 32;
    final var rewardIndex = getInt64LE(_data, i);
    i += 8;
    final var amount = getInt64LE(_data, i);
    return new FundReward(discriminator,
                          lbPair,
                          funder,
                          rewardIndex,
                          amount);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    lbPair.write(_data, i);
    i += 32;
    funder.write(_data, i);
    i += 32;
    putInt64LE(_data, i, rewardIndex);
    i += 8;
    putInt64LE(_data, i, amount);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

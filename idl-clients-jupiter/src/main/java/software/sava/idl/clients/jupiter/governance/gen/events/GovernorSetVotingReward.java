package software.sava.idl.clients.jupiter.governance.gen.events;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record GovernorSetVotingReward(Discriminator discriminator,
                                      PublicKey governor,
                                      PublicKey rewardMint,
                                      long rewardPerProposal) implements GovernEvent {

  public static final int BYTES = 80;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(234, 121, 246, 143, 42, 244, 8, 229);

  public static GovernorSetVotingReward read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createDiscriminator(_data, _offset, 8);
    int i = _offset + discriminator.length();
    final var governor = readPubKey(_data, i);
    i += 32;
    final var rewardMint = readPubKey(_data, i);
    i += 32;
    final var rewardPerProposal = getInt64LE(_data, i);
    return new GovernorSetVotingReward(discriminator, governor, rewardMint, rewardPerProposal);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    governor.write(_data, i);
    i += 32;
    rewardMint.write(_data, i);
    i += 32;
    putInt64LE(_data, i, rewardPerProposal);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

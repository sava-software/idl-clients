package software.sava.idl.clients.jupiter.governance.gen.events;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record ClaimRewardEvent(Discriminator discriminator,
                               PublicKey governor,
                               PublicKey voter,
                               PublicKey proposal,
                               long votingReward) implements GovernEvent {

  public static final int BYTES = 112;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(234, 121, 246, 143, 42, 244, 8, 229);

  public static final int GOVERNOR_OFFSET = 8;
  public static final int VOTER_OFFSET = 40;
  public static final int PROPOSAL_OFFSET = 72;
  public static final int VOTING_REWARD_OFFSET = 104;

  public static ClaimRewardEvent read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createDiscriminator(_data, _offset, 8);
    int i = _offset + discriminator.length();
    final var governor = readPubKey(_data, i);
    i += 32;
    final var voter = readPubKey(_data, i);
    i += 32;
    final var proposal = readPubKey(_data, i);
    i += 32;
    final var votingReward = getInt64LE(_data, i);
    return new ClaimRewardEvent(discriminator,
                                governor,
                                voter,
                                proposal,
                                votingReward);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    governor.write(_data, i);
    i += 32;
    voter.write(_data, i);
    i += 32;
    proposal.write(_data, i);
    i += 32;
    putInt64LE(_data, i, votingReward);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

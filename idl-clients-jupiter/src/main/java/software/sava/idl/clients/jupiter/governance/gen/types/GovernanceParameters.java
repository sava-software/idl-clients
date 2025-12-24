package software.sava.idl.clients.jupiter.governance.gen.types;

import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

/// Governance parameters.
///
/// @param votingDelay The delay before voting on a proposal may take place, once proposed, in seconds
/// @param votingPeriod The duration of voting on a proposal, in seconds
/// @param quorumVotes The number of votes in support of a proposal required in order for a quorum to be reached and for a vote to succeed
/// @param timelockDelaySeconds The timelock delay of the DAO's created proposals.
public record GovernanceParameters(long votingDelay,
                                   long votingPeriod,
                                   long quorumVotes,
                                   long timelockDelaySeconds) implements SerDe {

  public static final int BYTES = 32;

  public static GovernanceParameters read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var votingDelay = getInt64LE(_data, i);
    i += 8;
    final var votingPeriod = getInt64LE(_data, i);
    i += 8;
    final var quorumVotes = getInt64LE(_data, i);
    i += 8;
    final var timelockDelaySeconds = getInt64LE(_data, i);
    return new GovernanceParameters(votingDelay,
                                    votingPeriod,
                                    quorumVotes,
                                    timelockDelaySeconds);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt64LE(_data, i, votingDelay);
    i += 8;
    putInt64LE(_data, i, votingPeriod);
    i += 8;
    putInt64LE(_data, i, quorumVotes);
    i += 8;
    putInt64LE(_data, i, timelockDelaySeconds);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

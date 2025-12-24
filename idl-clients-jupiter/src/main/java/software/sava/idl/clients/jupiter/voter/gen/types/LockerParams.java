package software.sava.idl.clients.jupiter.voter.gen.types;

import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

/// Contains parameters for the Locker.
///
/// @param maxStakeVoteMultiplier The weight of a maximum vote lock relative to the total number of tokens locked.
///                               For example, veCRV is 10 because 1 CRV locked for 4 years = 10 veCRV.
/// @param minStakeDuration Minimum staking duration.
/// @param maxStakeDuration Maximum staking duration.
/// @param proposalActivationMinVotes Minimum number of votes required to activate a proposal.
public record LockerParams(int maxStakeVoteMultiplier,
                           long minStakeDuration,
                           long maxStakeDuration,
                           long proposalActivationMinVotes) implements SerDe {

  public static final int BYTES = 25;

  public static LockerParams read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var maxStakeVoteMultiplier = _data[i] & 0xFF;
    ++i;
    final var minStakeDuration = getInt64LE(_data, i);
    i += 8;
    final var maxStakeDuration = getInt64LE(_data, i);
    i += 8;
    final var proposalActivationMinVotes = getInt64LE(_data, i);
    return new LockerParams(maxStakeVoteMultiplier,
                            minStakeDuration,
                            maxStakeDuration,
                            proposalActivationMinVotes);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    _data[i] = (byte) maxStakeVoteMultiplier;
    ++i;
    putInt64LE(_data, i, minStakeDuration);
    i += 8;
    putInt64LE(_data, i, maxStakeDuration);
    i += 8;
    putInt64LE(_data, i, proposalActivationMinVotes);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

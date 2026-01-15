package software.sava.idl.clients.jupiter.governance.gen.types;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

/// Governance parameters.
///
/// @param rewardMint Reward mint
/// @param rewardVault Reward vault
/// @param rewardPerProposal Total reward per proposal
public record VotingReward(PublicKey rewardMint,
                           PublicKey rewardVault,
                           long rewardPerProposal) implements SerDe {

  public static final int BYTES = 72;

  public static final int REWARD_MINT_OFFSET = 0;
  public static final int REWARD_VAULT_OFFSET = 32;
  public static final int REWARD_PER_PROPOSAL_OFFSET = 64;

  public static VotingReward read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var rewardMint = readPubKey(_data, i);
    i += 32;
    final var rewardVault = readPubKey(_data, i);
    i += 32;
    final var rewardPerProposal = getInt64LE(_data, i);
    return new VotingReward(rewardMint, rewardVault, rewardPerProposal);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    rewardMint.write(_data, i);
    i += 32;
    rewardVault.write(_data, i);
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

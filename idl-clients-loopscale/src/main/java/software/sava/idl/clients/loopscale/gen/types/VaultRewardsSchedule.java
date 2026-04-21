package software.sava.idl.clients.loopscale.gen.types;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.accounts.PublicKey.readPubKey;

public record VaultRewardsSchedule(PublicKey rewardMint,
                                   PodDecimal totalWeightedStakeSupply,
                                   PodU64 rewardStartTime,
                                   PodU64 rewardEndTime,
                                   PodU64 totalEmissionsAmount,
                                   PodDecimal emissionsPerSecond,
                                   PodDecimal rewardIndex,
                                   PodU64 lastRewardIndexUpdateTime,
                                   PodU64 emissionsClaimed,
                                   PodU64 createdAt,
                                   PodU32CBPS[] durationStakeWeights) implements SerDe {

  public static final int BYTES = 172;
  public static final int DURATION_STAKE_WEIGHTS_LEN = 5;

  public static final int REWARD_MINT_OFFSET = 0;
  public static final int TOTAL_WEIGHTED_STAKE_SUPPLY_OFFSET = 32;
  public static final int REWARD_START_TIME_OFFSET = 56;
  public static final int REWARD_END_TIME_OFFSET = 64;
  public static final int TOTAL_EMISSIONS_AMOUNT_OFFSET = 72;
  public static final int EMISSIONS_PER_SECOND_OFFSET = 80;
  public static final int REWARD_INDEX_OFFSET = 104;
  public static final int LAST_REWARD_INDEX_UPDATE_TIME_OFFSET = 128;
  public static final int EMISSIONS_CLAIMED_OFFSET = 136;
  public static final int CREATED_AT_OFFSET = 144;
  public static final int DURATION_STAKE_WEIGHTS_OFFSET = 152;

  public static VaultRewardsSchedule read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var rewardMint = readPubKey(_data, i);
    i += 32;
    final var totalWeightedStakeSupply = PodDecimal.read(_data, i);
    i += totalWeightedStakeSupply.l();
    final var rewardStartTime = PodU64.read(_data, i);
    i += rewardStartTime.l();
    final var rewardEndTime = PodU64.read(_data, i);
    i += rewardEndTime.l();
    final var totalEmissionsAmount = PodU64.read(_data, i);
    i += totalEmissionsAmount.l();
    final var emissionsPerSecond = PodDecimal.read(_data, i);
    i += emissionsPerSecond.l();
    final var rewardIndex = PodDecimal.read(_data, i);
    i += rewardIndex.l();
    final var lastRewardIndexUpdateTime = PodU64.read(_data, i);
    i += lastRewardIndexUpdateTime.l();
    final var emissionsClaimed = PodU64.read(_data, i);
    i += emissionsClaimed.l();
    final var createdAt = PodU64.read(_data, i);
    i += createdAt.l();
    final var durationStakeWeights = new PodU32CBPS[5];
    SerDeUtil.readArray(durationStakeWeights, PodU32CBPS::read, _data, i);
    return new VaultRewardsSchedule(rewardMint,
                                    totalWeightedStakeSupply,
                                    rewardStartTime,
                                    rewardEndTime,
                                    totalEmissionsAmount,
                                    emissionsPerSecond,
                                    rewardIndex,
                                    lastRewardIndexUpdateTime,
                                    emissionsClaimed,
                                    createdAt,
                                    durationStakeWeights);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    rewardMint.write(_data, i);
    i += 32;
    i += totalWeightedStakeSupply.write(_data, i);
    i += rewardStartTime.write(_data, i);
    i += rewardEndTime.write(_data, i);
    i += totalEmissionsAmount.write(_data, i);
    i += emissionsPerSecond.write(_data, i);
    i += rewardIndex.write(_data, i);
    i += lastRewardIndexUpdateTime.write(_data, i);
    i += emissionsClaimed.write(_data, i);
    i += createdAt.write(_data, i);
    i += SerDeUtil.writeArrayChecked(durationStakeWeights, 5, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

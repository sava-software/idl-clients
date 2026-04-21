package software.sava.idl.clients.loopscale.gen.types;

import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

public record CreateRewardsScheduleParams(PodDecimal totalWeightedStakeSupply,
                                          PodU64 rewardStartTime,
                                          PodU64 rewardEndTime,
                                          PodU32CBPS[] durationStakeWeights) implements SerDe {

  public static final int BYTES = 60;
  public static final int DURATION_STAKE_WEIGHTS_LEN = 5;

  public static final int TOTAL_WEIGHTED_STAKE_SUPPLY_OFFSET = 0;
  public static final int REWARD_START_TIME_OFFSET = 24;
  public static final int REWARD_END_TIME_OFFSET = 32;
  public static final int DURATION_STAKE_WEIGHTS_OFFSET = 40;

  public static CreateRewardsScheduleParams read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var totalWeightedStakeSupply = PodDecimal.read(_data, i);
    i += totalWeightedStakeSupply.l();
    final var rewardStartTime = PodU64.read(_data, i);
    i += rewardStartTime.l();
    final var rewardEndTime = PodU64.read(_data, i);
    i += rewardEndTime.l();
    final var durationStakeWeights = new PodU32CBPS[5];
    SerDeUtil.readArray(durationStakeWeights, PodU32CBPS::read, _data, i);
    return new CreateRewardsScheduleParams(totalWeightedStakeSupply,
                                           rewardStartTime,
                                           rewardEndTime,
                                           durationStakeWeights);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += totalWeightedStakeSupply.write(_data, i);
    i += rewardStartTime.write(_data, i);
    i += rewardEndTime.write(_data, i);
    i += SerDeUtil.writeArrayChecked(durationStakeWeights, 5, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

package software.sava.idl.clients.drift.gen.types;

import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

public record FeeStructure(FeeTier[] feeTiers,
                           OrderFillerRewardStructure fillerRewardStructure,
                           long referrerRewardEpochUpperBound,
                           long flatFillerFee) implements SerDe {

  public static final int BYTES = 360;
  public static final int FEE_TIERS_LEN = 10;

  public static final int FEE_TIERS_OFFSET = 0;
  public static final int FILLER_REWARD_STRUCTURE_OFFSET = 320;
  public static final int REFERRER_REWARD_EPOCH_UPPER_BOUND_OFFSET = 344;
  public static final int FLAT_FILLER_FEE_OFFSET = 352;

  public static FeeStructure read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var feeTiers = new FeeTier[10];
    i += SerDeUtil.readArray(feeTiers, FeeTier::read, _data, i);
    final var fillerRewardStructure = OrderFillerRewardStructure.read(_data, i);
    i += fillerRewardStructure.l();
    final var referrerRewardEpochUpperBound = getInt64LE(_data, i);
    i += 8;
    final var flatFillerFee = getInt64LE(_data, i);
    return new FeeStructure(feeTiers,
                            fillerRewardStructure,
                            referrerRewardEpochUpperBound,
                            flatFillerFee);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += SerDeUtil.writeArrayChecked(feeTiers, 10, _data, i);
    i += fillerRewardStructure.write(_data, i);
    putInt64LE(_data, i, referrerRewardEpochUpperBound);
    i += 8;
    putInt64LE(_data, i, flatFillerFee);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

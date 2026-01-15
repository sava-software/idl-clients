package software.sava.idl.clients.kamino.farms.gen.types;

import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

public record RewardPerTimeUnitPoint(long tsStart, long rewardPerTimeUnit) implements SerDe {

  public static final int BYTES = 16;

  public static final int TS_START_OFFSET = 0;
  public static final int REWARD_PER_TIME_UNIT_OFFSET = 8;

  public static RewardPerTimeUnitPoint read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var tsStart = getInt64LE(_data, i);
    i += 8;
    final var rewardPerTimeUnit = getInt64LE(_data, i);
    return new RewardPerTimeUnitPoint(tsStart, rewardPerTimeUnit);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt64LE(_data, i, tsStart);
    i += 8;
    putInt64LE(_data, i, rewardPerTimeUnit);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

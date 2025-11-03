package software.sava.idl.clients.kamino.farms.gen.types;

import software.sava.core.borsh.Borsh;

import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

public record RewardPerTimeUnitPoint(long tsStart, long rewardPerTimeUnit) implements Borsh {

  public static final int BYTES = 16;

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

package software.sava.idl.clients.loopscale.gen.types;

import java.math.BigInteger;

import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.core.encoding.ByteUtil.getInt128LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt128LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

public record RaydiumAmmV3StatesPersonalPositionPositionRewardInfo(BigInteger growthInsideLastX64, long rewardAmountOwed) implements SerDe {

  public static final int BYTES = 24;

  public static final int GROWTH_INSIDE_LAST_X_66_OFFSET = 0;
  public static final int REWARD_AMOUNT_OWED_OFFSET = 16;

  public static RaydiumAmmV3StatesPersonalPositionPositionRewardInfo read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var growthInsideLastX64 = getInt128LE(_data, i);
    i += 16;
    final var rewardAmountOwed = getInt64LE(_data, i);
    return new RaydiumAmmV3StatesPersonalPositionPositionRewardInfo(growthInsideLastX64, rewardAmountOwed);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt128LE(_data, i, growthInsideLastX64);
    i += 16;
    putInt64LE(_data, i, rewardAmountOwed);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

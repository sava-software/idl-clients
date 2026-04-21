package software.sava.idl.clients.loopscale.gen.types;

import java.math.BigInteger;

import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.core.encoding.ByteUtil.getInt128LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt128LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

public record WhirlpoolStatePositionPositionRewardInfo(BigInteger growthInsideCheckpoint, long amountOwed) implements SerDe {

  public static final int BYTES = 24;

  public static final int GROWTH_INSIDE_CHECKPOINT_OFFSET = 0;
  public static final int AMOUNT_OWED_OFFSET = 16;

  public static WhirlpoolStatePositionPositionRewardInfo read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var growthInsideCheckpoint = getInt128LE(_data, i);
    i += 16;
    final var amountOwed = getInt64LE(_data, i);
    return new WhirlpoolStatePositionPositionRewardInfo(growthInsideCheckpoint, amountOwed);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt128LE(_data, i, growthInsideCheckpoint);
    i += 16;
    putInt64LE(_data, i, amountOwed);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

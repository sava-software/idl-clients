package software.sava.idl.clients.orca.whirlpools.gen.types;

import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

public record AdaptiveFeeVariables(long lastReferenceUpdateTimestamp,
                                   long lastMajorSwapTimestamp,
                                   int volatilityReference,
                                   int tickGroupIndexReference,
                                   int volatilityAccumulator,
                                   byte[] reserved) implements SerDe {

  public static final int BYTES = 44;
  public static final int RESERVED_LEN = 16;

  public static final int LAST_REFERENCE_UPDATE_TIMESTAMP_OFFSET = 0;
  public static final int LAST_MAJOR_SWAP_TIMESTAMP_OFFSET = 8;
  public static final int VOLATILITY_REFERENCE_OFFSET = 16;
  public static final int TICK_GROUP_INDEX_REFERENCE_OFFSET = 20;
  public static final int VOLATILITY_ACCUMULATOR_OFFSET = 24;
  public static final int RESERVED_OFFSET = 28;

  public static AdaptiveFeeVariables read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var lastReferenceUpdateTimestamp = getInt64LE(_data, i);
    i += 8;
    final var lastMajorSwapTimestamp = getInt64LE(_data, i);
    i += 8;
    final var volatilityReference = getInt32LE(_data, i);
    i += 4;
    final var tickGroupIndexReference = getInt32LE(_data, i);
    i += 4;
    final var volatilityAccumulator = getInt32LE(_data, i);
    i += 4;
    final var reserved = new byte[16];
    SerDeUtil.readArray(reserved, _data, i);
    return new AdaptiveFeeVariables(lastReferenceUpdateTimestamp,
                                    lastMajorSwapTimestamp,
                                    volatilityReference,
                                    tickGroupIndexReference,
                                    volatilityAccumulator,
                                    reserved);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt64LE(_data, i, lastReferenceUpdateTimestamp);
    i += 8;
    putInt64LE(_data, i, lastMajorSwapTimestamp);
    i += 8;
    putInt32LE(_data, i, volatilityReference);
    i += 4;
    putInt32LE(_data, i, tickGroupIndexReference);
    i += 4;
    putInt32LE(_data, i, volatilityAccumulator);
    i += 4;
    i += SerDeUtil.writeArrayChecked(reserved, 16, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

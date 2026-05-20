package software.sava.idl.clients.orca.whirlpools.gen.types;

import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.encoding.ByteUtil.getInt16LE;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt16LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;

public record AdaptiveFeeConstants(int filterPeriod,
                                   int decayPeriod,
                                   int reductionFactor,
                                   int adaptiveFeeControlFactor,
                                   int maxVolatilityAccumulator,
                                   int tickGroupSize,
                                   int majorSwapThresholdTicks,
                                   byte[] reserved) implements SerDe {

  public static final int BYTES = 34;
  public static final int RESERVED_LEN = 16;

  public static final int FILTER_PERIOD_OFFSET = 0;
  public static final int DECAY_PERIOD_OFFSET = 2;
  public static final int REDUCTION_FACTOR_OFFSET = 4;
  public static final int ADAPTIVE_FEE_CONTROL_FACTOR_OFFSET = 6;
  public static final int MAX_VOLATILITY_ACCUMULATOR_OFFSET = 10;
  public static final int TICK_GROUP_SIZE_OFFSET = 14;
  public static final int MAJOR_SWAP_THRESHOLD_TICKS_OFFSET = 16;
  public static final int RESERVED_OFFSET = 18;

  public static AdaptiveFeeConstants read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var filterPeriod = getInt16LE(_data, i);
    i += 2;
    final var decayPeriod = getInt16LE(_data, i);
    i += 2;
    final var reductionFactor = getInt16LE(_data, i);
    i += 2;
    final var adaptiveFeeControlFactor = getInt32LE(_data, i);
    i += 4;
    final var maxVolatilityAccumulator = getInt32LE(_data, i);
    i += 4;
    final var tickGroupSize = getInt16LE(_data, i);
    i += 2;
    final var majorSwapThresholdTicks = getInt16LE(_data, i);
    i += 2;
    final var reserved = new byte[16];
    SerDeUtil.readArray(reserved, _data, i);
    return new AdaptiveFeeConstants(filterPeriod,
                                    decayPeriod,
                                    reductionFactor,
                                    adaptiveFeeControlFactor,
                                    maxVolatilityAccumulator,
                                    tickGroupSize,
                                    majorSwapThresholdTicks,
                                    reserved);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt16LE(_data, i, filterPeriod);
    i += 2;
    putInt16LE(_data, i, decayPeriod);
    i += 2;
    putInt16LE(_data, i, reductionFactor);
    i += 2;
    putInt32LE(_data, i, adaptiveFeeControlFactor);
    i += 4;
    putInt32LE(_data, i, maxVolatilityAccumulator);
    i += 4;
    putInt16LE(_data, i, tickGroupSize);
    i += 2;
    putInt16LE(_data, i, majorSwapThresholdTicks);
    i += 2;
    i += SerDeUtil.writeArrayChecked(reserved, 16, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

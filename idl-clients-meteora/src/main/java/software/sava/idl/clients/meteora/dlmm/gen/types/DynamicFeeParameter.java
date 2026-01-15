package software.sava.idl.clients.meteora.dlmm.gen.types;

import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.core.encoding.ByteUtil.getInt16LE;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt16LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;

/// @param filterPeriod Filter period determine high frequency trading time window.
/// @param decayPeriod Decay period determine when the volatile fee start decay / decrease.
/// @param reductionFactor Reduction factor controls the volatile fee rate decrement rate.
/// @param variableFeeControl Used to scale the variable fee component depending on the dynamic of the market
/// @param maxVolatilityAccumulator Maximum number of bin crossed can be accumulated. Used to cap volatile fee rate.
public record DynamicFeeParameter(int filterPeriod,
                                  int decayPeriod,
                                  int reductionFactor,
                                  int variableFeeControl,
                                  int maxVolatilityAccumulator) implements SerDe {

  public static final int BYTES = 14;

  public static final int FILTER_PERIOD_OFFSET = 0;
  public static final int DECAY_PERIOD_OFFSET = 2;
  public static final int REDUCTION_FACTOR_OFFSET = 4;
  public static final int VARIABLE_FEE_CONTROL_OFFSET = 6;
  public static final int MAX_VOLATILITY_ACCUMULATOR_OFFSET = 10;

  public static DynamicFeeParameter read(final byte[] _data, final int _offset) {
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
    final var variableFeeControl = getInt32LE(_data, i);
    i += 4;
    final var maxVolatilityAccumulator = getInt32LE(_data, i);
    return new DynamicFeeParameter(filterPeriod,
                                   decayPeriod,
                                   reductionFactor,
                                   variableFeeControl,
                                   maxVolatilityAccumulator);
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
    putInt32LE(_data, i, variableFeeControl);
    i += 4;
    putInt32LE(_data, i, maxVolatilityAccumulator);
    i += 4;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

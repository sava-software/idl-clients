package software.sava.idl.clients.meteora.dlmm.gen.types;

import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.encoding.ByteUtil.getInt16LE;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt16LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;

/// Parameter that set by the protocol
///
/// @param baseFactor Used for base fee calculation. base_fee_rate = base_factor * bin_step * 10 * 10^base_fee_power_factor
/// @param filterPeriod Filter period determine high frequency trading time window.
/// @param decayPeriod Decay period determine when the volatile fee start decay / decrease.
/// @param reductionFactor Reduction factor controls the volatile fee rate decrement rate.
/// @param variableFeeControl Used to scale the variable fee component depending on the dynamic of the market
/// @param maxVolatilityAccumulator Maximum number of bin crossed can be accumulated. Used to cap volatile fee rate.
/// @param minBinId Min bin id supported by the pool based on the configured bin step.
/// @param maxBinId Max bin id supported by the pool based on the configured bin step.
/// @param protocolShare Portion of swap fees retained by the protocol by controlling protocol_share parameter. protocol_swap_fee = protocol_share * total_swap_fee
/// @param baseFeePowerFactor Base fee power factor
/// @param functionType function type
/// @param padding Padding for bytemuck safe alignment
public record StaticParameters(int baseFactor,
                               int filterPeriod,
                               int decayPeriod,
                               int reductionFactor,
                               int variableFeeControl,
                               int maxVolatilityAccumulator,
                               int minBinId,
                               int maxBinId,
                               int protocolShare,
                               int baseFeePowerFactor,
                               int functionType,
                               byte[] padding) implements SerDe {

  public static final int BYTES = 32;
  public static final int PADDING_LEN = 4;

  public static final int BASE_FACTOR_OFFSET = 0;
  public static final int FILTER_PERIOD_OFFSET = 2;
  public static final int DECAY_PERIOD_OFFSET = 4;
  public static final int REDUCTION_FACTOR_OFFSET = 6;
  public static final int VARIABLE_FEE_CONTROL_OFFSET = 8;
  public static final int MAX_VOLATILITY_ACCUMULATOR_OFFSET = 12;
  public static final int MIN_BIN_ID_OFFSET = 16;
  public static final int MAX_BIN_ID_OFFSET = 20;
  public static final int PROTOCOL_SHARE_OFFSET = 24;
  public static final int BASE_FEE_POWER_FACTOR_OFFSET = 26;
  public static final int FUNCTION_TYPE_OFFSET = 27;
  public static final int PADDING_OFFSET = 28;

  public static StaticParameters read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var baseFactor = getInt16LE(_data, i);
    i += 2;
    final var filterPeriod = getInt16LE(_data, i);
    i += 2;
    final var decayPeriod = getInt16LE(_data, i);
    i += 2;
    final var reductionFactor = getInt16LE(_data, i);
    i += 2;
    final var variableFeeControl = getInt32LE(_data, i);
    i += 4;
    final var maxVolatilityAccumulator = getInt32LE(_data, i);
    i += 4;
    final var minBinId = getInt32LE(_data, i);
    i += 4;
    final var maxBinId = getInt32LE(_data, i);
    i += 4;
    final var protocolShare = getInt16LE(_data, i);
    i += 2;
    final var baseFeePowerFactor = _data[i] & 0xFF;
    ++i;
    final var functionType = _data[i] & 0xFF;
    ++i;
    final var padding = new byte[4];
    SerDeUtil.readArray(padding, _data, i);
    return new StaticParameters(baseFactor,
                                filterPeriod,
                                decayPeriod,
                                reductionFactor,
                                variableFeeControl,
                                maxVolatilityAccumulator,
                                minBinId,
                                maxBinId,
                                protocolShare,
                                baseFeePowerFactor,
                                functionType,
                                padding);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt16LE(_data, i, baseFactor);
    i += 2;
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
    putInt32LE(_data, i, minBinId);
    i += 4;
    putInt32LE(_data, i, maxBinId);
    i += 4;
    putInt16LE(_data, i, protocolShare);
    i += 2;
    _data[i] = (byte) baseFeePowerFactor;
    ++i;
    _data[i] = (byte) functionType;
    ++i;
    i += SerDeUtil.writeArrayChecked(padding, 4, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

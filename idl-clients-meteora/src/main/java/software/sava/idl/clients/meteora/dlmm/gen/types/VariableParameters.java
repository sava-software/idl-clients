package software.sava.idl.clients.meteora.dlmm.gen.types;

import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

/// Parameters that changes based on dynamic of the market
///
/// @param volatilityAccumulator Volatility accumulator measure the number of bin crossed since reference bin ID. Normally (without filter period taken into consideration), reference bin ID is the active bin of last swap.
///                              It affects the variable fee rate
/// @param volatilityReference Volatility reference is decayed volatility accumulator. It is always <= volatility_accumulator
/// @param indexReference Active bin id of last swap.
/// @param padding Padding for bytemuck safe alignment
/// @param lastUpdateTimestamp Last timestamp the variable parameters was updated
/// @param padding1 Padding for bytemuck safe alignment
public record VariableParameters(int volatilityAccumulator,
                                 int volatilityReference,
                                 int indexReference,
                                 byte[] padding,
                                 long lastUpdateTimestamp,
                                 byte[] padding1) implements SerDe {

  public static final int BYTES = 32;
  public static final int PADDING_LEN = 4;
  public static final int PADDING_1_LEN = 8;

  public static final int VOLATILITY_ACCUMULATOR_OFFSET = 0;
  public static final int VOLATILITY_REFERENCE_OFFSET = 4;
  public static final int INDEX_REFERENCE_OFFSET = 8;
  public static final int PADDING_OFFSET = 12;
  public static final int LAST_UPDATE_TIMESTAMP_OFFSET = 16;
  public static final int PADDING_1_OFFSET = 24;

  public static VariableParameters read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var volatilityAccumulator = getInt32LE(_data, i);
    i += 4;
    final var volatilityReference = getInt32LE(_data, i);
    i += 4;
    final var indexReference = getInt32LE(_data, i);
    i += 4;
    final var padding = new byte[4];
    i += SerDeUtil.readArray(padding, _data, i);
    final var lastUpdateTimestamp = getInt64LE(_data, i);
    i += 8;
    final var padding1 = new byte[8];
    SerDeUtil.readArray(padding1, _data, i);
    return new VariableParameters(volatilityAccumulator,
                                  volatilityReference,
                                  indexReference,
                                  padding,
                                  lastUpdateTimestamp,
                                  padding1);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt32LE(_data, i, volatilityAccumulator);
    i += 4;
    putInt32LE(_data, i, volatilityReference);
    i += 4;
    putInt32LE(_data, i, indexReference);
    i += 4;
    i += SerDeUtil.writeArrayChecked(padding, 4, _data, i);
    putInt64LE(_data, i, lastUpdateTimestamp);
    i += 8;
    i += SerDeUtil.writeArrayChecked(padding1, 8, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

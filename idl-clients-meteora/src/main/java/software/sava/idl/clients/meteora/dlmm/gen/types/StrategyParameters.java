package software.sava.idl.clients.meteora.dlmm.gen.types;

import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;

/// @param minBinId min bin id
/// @param maxBinId max bin id
/// @param strategyType strategy type
/// @param parameteres parameters
public record StrategyParameters(int minBinId,
                                 int maxBinId,
                                 StrategyType strategyType,
                                 byte[] parameteres) implements SerDe {

  public static final int BYTES = 73;
  public static final int PARAMETERES_LEN = 64;

  public static StrategyParameters read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var minBinId = getInt32LE(_data, i);
    i += 4;
    final var maxBinId = getInt32LE(_data, i);
    i += 4;
    final var strategyType = StrategyType.read(_data, i);
    i += strategyType.l();
    final var parameteres = new byte[64];
    SerDeUtil.readArray(parameteres, _data, i);
    return new StrategyParameters(minBinId,
                                  maxBinId,
                                  strategyType,
                                  parameteres);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt32LE(_data, i, minBinId);
    i += 4;
    putInt32LE(_data, i, maxBinId);
    i += 4;
    i += strategyType.write(_data, i);
    i += SerDeUtil.writeArrayChecked(parameteres, 64, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

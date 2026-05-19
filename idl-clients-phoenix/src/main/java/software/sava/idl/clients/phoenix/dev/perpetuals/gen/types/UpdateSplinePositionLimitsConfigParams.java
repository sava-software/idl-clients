package software.sava.idl.clients.phoenix.dev.perpetuals.gen.types;

import java.util.OptionalInt;

import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.encoding.ByteUtil.getInt32LE;

/// Borsh payload for updating spline position-size limits and leverage decrease configuration.
///
public record UpdateSplinePositionLimitsConfigParams(PositionSizeLimit maxPositionSize, OptionalInt leverageDecreaseInBps) implements SerDe {

  public static final int MAX_POSITION_SIZE_OFFSET = 1;

  public static UpdateSplinePositionLimitsConfigParams read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final PositionSizeLimit maxPositionSize;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      maxPositionSize = null;
      ++i;
    } else {
      ++i;
      maxPositionSize = PositionSizeLimit.read(_data, i);
      i += maxPositionSize.l();
    }
    final OptionalInt leverageDecreaseInBps;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      leverageDecreaseInBps = OptionalInt.empty();
    } else {
      ++i;
      leverageDecreaseInBps = OptionalInt.of(getInt32LE(_data, i));
    }
    return new UpdateSplinePositionLimitsConfigParams(maxPositionSize, leverageDecreaseInBps);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += SerDeUtil.writeOptional(1, maxPositionSize, _data, i);
    i += SerDeUtil.writeOptional(1, leverageDecreaseInBps, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return (maxPositionSize == null ? 1 : (1 + maxPositionSize.l())) + (leverageDecreaseInBps == null || leverageDecreaseInBps.isEmpty() ? 1 : (1 + 4));
  }
}

package software.sava.idl.clients.phoenix.dev.perpetuals.gen.types;

import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;

public record PositionSizeLimits(int _long, int _short) implements SerDe {

  public static final int BYTES = 8;

  public static final int _LONG_OFFSET = 0;
  public static final int _SHORT_OFFSET = 4;

  public static PositionSizeLimits read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var _long = getInt32LE(_data, i);
    i += 4;
    final var _short = getInt32LE(_data, i);
    return new PositionSizeLimits(_long, _short);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt32LE(_data, i, _long);
    i += 4;
    putInt32LE(_data, i, _short);
    i += 4;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

package software.sava.idl.clients.loopscale.gen.types;

import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

/// this is the scaled representation of a whole number. The whole number is scaled by 10^18 to avoid floating point errors when performing arithmetic operations.
///
public record PodDecimal(byte[] _array) implements SerDe {

  public static final int BYTES = 24;
  public static final int _ARRAY_LEN = 24;

  public static final int _ARRAY_OFFSET = 0;

  public static PodDecimal read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var _array = new byte[24];
    SerDeUtil.readArray(_array, _data, _offset);
    return new PodDecimal(_array);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += SerDeUtil.writeArrayChecked(_array, 24, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

package software.sava.idl.clients.loopscale.gen.types;

import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

/// helper type to store u32 cbps values
///
public record PodU32CBPS(byte[] _array) implements SerDe {

  public static final int BYTES = 4;
  public static final int _ARRAY_LEN = 4;

  public static final int _ARRAY_OFFSET = 0;

  public static PodU32CBPS read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var _array = new byte[4];
    SerDeUtil.readArray(_array, _data, _offset);
    return new PodU32CBPS(_array);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += SerDeUtil.writeArrayChecked(_array, 4, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

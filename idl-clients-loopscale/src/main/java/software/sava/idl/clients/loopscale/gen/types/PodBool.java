package software.sava.idl.clients.loopscale.gen.types;

import software.sava.idl.clients.core.gen.SerDe;

/// Represents a bool stored as a byte
///
public record PodBool(int _u8) implements SerDe {

  public static final int BYTES = 1;

  public static final int _U_8_OFFSET = 0;

  public static PodBool read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var _u8 = _data[_offset] & 0xFF;
    return new PodBool(_u8);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    _data[i] = (byte) _u8;
    ++i;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

package software.sava.idl.clients.squads.v4.gen.types;

import software.sava.idl.clients.core.gen.SerDe;

/// Bitmask for permissions.
///
public record Permissions(int mask) implements SerDe {

  public static final int BYTES = 1;

  public static final int MASK_OFFSET = 0;

  public static Permissions read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var mask = _data[_offset] & 0xFF;
    return new Permissions(mask);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    _data[i] = (byte) mask;
    ++i;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

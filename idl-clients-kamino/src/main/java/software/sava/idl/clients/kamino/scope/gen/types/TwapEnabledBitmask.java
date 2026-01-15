package software.sava.idl.clients.kamino.scope.gen.types;

import software.sava.idl.clients.core.gen.SerDe;

public record TwapEnabledBitmask(int bitmask) implements SerDe {

  public static final int BYTES = 1;

  public static final int BITMASK_OFFSET = 0;

  public static TwapEnabledBitmask read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var bitmask = _data[_offset] & 0xFF;
    return new TwapEnabledBitmask(bitmask);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    _data[i] = (byte) bitmask;
    ++i;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

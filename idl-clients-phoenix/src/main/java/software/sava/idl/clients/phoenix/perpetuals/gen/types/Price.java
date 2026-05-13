package software.sava.idl.clients.phoenix.perpetuals.gen.types;

import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

/// Price representation composed of a mantissa (`value`) and decimal exponent (`expo`).
///
public record Price(long value, int expo) implements SerDe {

  public static final int BYTES = 9;

  public static final int VALUE_OFFSET = 0;
  public static final int EXPO_OFFSET = 8;

  public static Price read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var value = getInt64LE(_data, i);
    i += 8;
    final var expo = _data[i] & 0xFF;
    return new Price(value, expo);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt64LE(_data, i, value);
    i += 8;
    _data[i] = (byte) expo;
    ++i;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

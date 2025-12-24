package software.sava.idl.clients.kamino.lend.gen.types;

import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

/// Last update state
///
/// @param slot Last slot when updated
/// @param stale True when marked stale, false when slot updated
/// @param priceStatus Status of the prices used to calculate the last update
public record LastUpdate(long slot,
                         int stale,
                         int priceStatus,
                         byte[] placeholder) implements SerDe {

  public static final int BYTES = 16;
  public static final int PLACEHOLDER_LEN = 6;

  public static LastUpdate read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var slot = getInt64LE(_data, i);
    i += 8;
    final var stale = _data[i] & 0xFF;
    ++i;
    final var priceStatus = _data[i] & 0xFF;
    ++i;
    final var placeholder = new byte[6];
    SerDeUtil.readArray(placeholder, _data, i);
    return new LastUpdate(slot,
                          stale,
                          priceStatus,
                          placeholder);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt64LE(_data, i, slot);
    i += 8;
    _data[i] = (byte) stale;
    ++i;
    _data[i] = (byte) priceStatus;
    ++i;
    i += SerDeUtil.writeArrayChecked(placeholder, 6, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

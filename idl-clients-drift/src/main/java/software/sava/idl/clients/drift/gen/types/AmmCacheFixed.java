package software.sava.idl.clients.drift.gen.types;

import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;

public record AmmCacheFixed(int bump,
                            byte[] pad,
                            int len) implements SerDe {

  public static final int BYTES = 8;
  public static final int PAD_LEN = 3;

  public static AmmCacheFixed read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var bump = _data[i] & 0xFF;
    ++i;
    final var pad = new byte[3];
    i += SerDeUtil.readArray(pad, _data, i);
    final var len = getInt32LE(_data, i);
    return new AmmCacheFixed(bump, pad, len);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    _data[i] = (byte) bump;
    ++i;
    i += SerDeUtil.writeArrayChecked(pad, 3, _data, i);
    putInt32LE(_data, i, len);
    i += 4;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

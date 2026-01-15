package software.sava.idl.clients.marinade.stake_pool.gen.types;

import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;

public record U32ValueChange(int old, int _new) implements SerDe {

  public static final int BYTES = 8;

  public static final int OLD_OFFSET = 0;
  public static final int _NEW_OFFSET = 4;

  public static U32ValueChange read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var old = getInt32LE(_data, i);
    i += 4;
    final var _new = getInt32LE(_data, i);
    return new U32ValueChange(old, _new);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt32LE(_data, i, old);
    i += 4;
    putInt32LE(_data, i, _new);
    i += 4;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

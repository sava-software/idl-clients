package software.sava.idl.clients.marinade.stake_pool.gen.types;

import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

public record U64ValueChange(long old, long _new) implements SerDe {

  public static final int BYTES = 16;

  public static final int OLD_OFFSET = 0;
  public static final int _NEW_OFFSET = 8;

  public static U64ValueChange read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var old = getInt64LE(_data, i);
    i += 8;
    final var _new = getInt64LE(_data, i);
    return new U64ValueChange(old, _new);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt64LE(_data, i, old);
    i += 8;
    putInt64LE(_data, i, _new);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

package software.sava.idl.clients.marinade.stake_pool.gen.types;

import software.sava.core.borsh.Borsh;

import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;

public record Fee(int basisPoints) implements Borsh {

  public static final int BYTES = 4;

  public static Fee read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var basisPoints = getInt32LE(_data, _offset);
    return new Fee(basisPoints);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt32LE(_data, i, basisPoints);
    i += 4;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

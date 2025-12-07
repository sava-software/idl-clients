package software.sava.idl.clients.marinade.stake_pool.gen.types;

import software.sava.core.borsh.Borsh;

public record BoolValueChange(boolean old, boolean _new) implements Borsh {

  public static final int BYTES = 2;

  public static BoolValueChange read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var old = _data[i] == 1;
    ++i;
    final var _new = _data[i] == 1;
    return new BoolValueChange(old, _new);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    _data[i] = (byte) (old ? 1 : 0);
    ++i;
    _data[i] = (byte) (_new ? 1 : 0);
    ++i;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

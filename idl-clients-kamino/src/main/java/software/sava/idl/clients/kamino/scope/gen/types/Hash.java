package software.sava.idl.clients.kamino.scope.gen.types;

import software.sava.core.borsh.Borsh;

public record Hash(byte[] data) implements Borsh {

  public static final int BYTES = 32;
  public static final int DATA_LEN = 32;

  public static Hash read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var data = new byte[32];
    Borsh.readArray(data, _data, _offset);
    return new Hash(data);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += Borsh.writeArrayChecked(data, 32, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

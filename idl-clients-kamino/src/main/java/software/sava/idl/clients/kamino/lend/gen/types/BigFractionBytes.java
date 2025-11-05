package software.sava.idl.clients.kamino.lend.gen.types;

import software.sava.core.borsh.Borsh;

public record BigFractionBytes(long[] value, long[] padding) implements Borsh {

  public static final int BYTES = 48;
  public static final int VALUE_LEN = 4;
  public static final int PADDING_LEN = 2;

  public static BigFractionBytes read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var value = new long[4];
    i += Borsh.readArray(value, _data, i);
    final var padding = new long[2];
    Borsh.readArray(padding, _data, i);
    return new BigFractionBytes(value, padding);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += Borsh.writeArrayChecked(value, 4, _data, i);
    i += Borsh.writeArrayChecked(padding, 2, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

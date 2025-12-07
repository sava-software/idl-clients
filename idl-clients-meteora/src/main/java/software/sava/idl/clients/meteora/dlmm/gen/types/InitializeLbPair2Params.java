package software.sava.idl.clients.meteora.dlmm.gen.types;

import software.sava.core.borsh.Borsh;

import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;

/// @param activeId Pool price
/// @param padding Padding, for future use
public record InitializeLbPair2Params(int activeId, byte[] padding) implements Borsh {

  public static final int BYTES = 100;
  public static final int PADDING_LEN = 96;

  public static InitializeLbPair2Params read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var activeId = getInt32LE(_data, i);
    i += 4;
    final var padding = new byte[96];
    Borsh.readArray(padding, _data, i);
    return new InitializeLbPair2Params(activeId, padding);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt32LE(_data, i, activeId);
    i += 4;
    i += Borsh.writeArrayChecked(padding, 96, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

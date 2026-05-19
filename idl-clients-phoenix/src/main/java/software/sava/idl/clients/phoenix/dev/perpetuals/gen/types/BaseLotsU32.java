package software.sava.idl.clients.phoenix.dev.perpetuals.gen.types;

import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;

/// u32-backed base lot wrapper.
///
public record BaseLotsU32(int inner) implements SerDe {

  public static final int BYTES = 4;

  public static final int INNER_OFFSET = 0;

  public static BaseLotsU32 read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var inner = getInt32LE(_data, _offset);
    return new BaseLotsU32(inner);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt32LE(_data, i, inner);
    i += 4;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

package software.sava.idl.clients.metaplex.token.metadata.gen.types;

import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

public record Uses(UseMethod useMethod,
                   long remaining,
                   long total) implements SerDe {

  public static final int BYTES = 17;

  public static final int USE_METHOD_OFFSET = 0;
  public static final int REMAINING_OFFSET = 1;
  public static final int TOTAL_OFFSET = 9;

  public static Uses read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var useMethod = UseMethod.read(_data, i);
    i += useMethod.l();
    final var remaining = getInt64LE(_data, i);
    i += 8;
    final var total = getInt64LE(_data, i);
    return new Uses(useMethod, remaining, total);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += useMethod.write(_data, i);
    putInt64LE(_data, i, remaining);
    i += 8;
    putInt64LE(_data, i, total);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

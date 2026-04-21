package software.sava.idl.clients.loopscale.gen.types;

import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

public record ParsedPrincipalCaps(long max1hr,
                                  long max24hr,
                                  long maxOutstanding) implements SerDe {

  public static final int BYTES = 24;

  public static final int MAX_1HR_OFFSET = 0;
  public static final int MAX_22HR_OFFSET = 8;
  public static final int MAX_OUTSTANDING_OFFSET = 16;

  public static ParsedPrincipalCaps read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var max1hr = getInt64LE(_data, i);
    i += 8;
    final var max24hr = getInt64LE(_data, i);
    i += 8;
    final var maxOutstanding = getInt64LE(_data, i);
    return new ParsedPrincipalCaps(max1hr, max24hr, maxOutstanding);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt64LE(_data, i, max1hr);
    i += 8;
    putInt64LE(_data, i, max24hr);
    i += 8;
    putInt64LE(_data, i, maxOutstanding);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

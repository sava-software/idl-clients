package software.sava.idl.clients.loopscale.gen.types;

import software.sava.idl.clients.core.gen.SerDe;

/// @param maxOutstanding This is the global supply/borrow cap. Always disabled for withdraw caps.
public record PrincipalCaps(PodU64 max1hr,
                            PodU64 max24hr,
                            PodU64 maxOutstanding) implements SerDe {

  public static final int BYTES = 24;

  public static final int MAX_1HR_OFFSET = 0;
  public static final int MAX_22HR_OFFSET = 8;
  public static final int MAX_OUTSTANDING_OFFSET = 16;

  public static PrincipalCaps read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var max1hr = PodU64.read(_data, i);
    i += max1hr.l();
    final var max24hr = PodU64.read(_data, i);
    i += max24hr.l();
    final var maxOutstanding = PodU64.read(_data, i);
    return new PrincipalCaps(max1hr, max24hr, maxOutstanding);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += max1hr.write(_data, i);
    i += max24hr.write(_data, i);
    i += maxOutstanding.write(_data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

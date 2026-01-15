package software.sava.idl.clients.kamino.lend.gen.types;

import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

public record BorrowRateCurve(CurvePoint[] points) implements SerDe {

  public static final int BYTES = 88;
  public static final int POINTS_LEN = 11;

  public static final int POINTS_OFFSET = 0;

  public static BorrowRateCurve read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var points = new CurvePoint[11];
    SerDeUtil.readArray(points, CurvePoint::read, _data, _offset);
    return new BorrowRateCurve(points);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += SerDeUtil.writeArrayChecked(points, 11, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

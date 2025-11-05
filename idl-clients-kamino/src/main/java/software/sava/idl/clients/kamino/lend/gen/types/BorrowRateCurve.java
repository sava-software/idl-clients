package software.sava.idl.clients.kamino.lend.gen.types;

import software.sava.core.borsh.Borsh;

public record BorrowRateCurve(CurvePoint[] points) implements Borsh {

  public static final int BYTES = 88;
  public static final int POINTS_LEN = 11;

  public static BorrowRateCurve read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var points = new CurvePoint[11];
    Borsh.readArray(points, CurvePoint::read, _data, _offset);
    return new BorrowRateCurve(points);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += Borsh.writeArrayChecked(points, 11, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

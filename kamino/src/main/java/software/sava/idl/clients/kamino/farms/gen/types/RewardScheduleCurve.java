package software.sava.idl.clients.kamino.farms.gen.types;

import software.sava.core.borsh.Borsh;

/// @param points This is a stepwise function, meaning that each point represents
///               how many rewards are issued per time unit since the beginning
///               of that point until the beginning of the next point.
///               This is not a linear curve, there is no interpolation going on.
///               A curve can be t0, 100, t1, 50, t2, 0
///               meaning that from t0 to t1, 100 rewards are issued per time unit,
///               from t1 to t2, 50 rewards are issued per time unit, and after t2 it stops
///               Another curve, can be t0, 100, u64::max, 0
///               meaning that from t0 to u64::max, 100 rewards are issued per time unit
public record RewardScheduleCurve(RewardPerTimeUnitPoint[] points) implements Borsh {

  public static final int BYTES = 320;
  public static final int POINTS_LEN = 20;

  public static RewardScheduleCurve read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var points = new RewardPerTimeUnitPoint[20];
    Borsh.readArray(points, RewardPerTimeUnitPoint::read, _data, _offset);
    return new RewardScheduleCurve(points);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += Borsh.writeArrayChecked(points, 20, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

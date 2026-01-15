package software.sava.idl.clients.kamino.lend.gen.types;

import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;

public record CurvePoint(int utilizationRateBps, int borrowRateBps) implements SerDe {

  public static final int BYTES = 8;

  public static final int UTILIZATION_RATE_BPS_OFFSET = 0;
  public static final int BORROW_RATE_BPS_OFFSET = 4;

  public static CurvePoint read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var utilizationRateBps = getInt32LE(_data, i);
    i += 4;
    final var borrowRateBps = getInt32LE(_data, i);
    return new CurvePoint(utilizationRateBps, borrowRateBps);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt32LE(_data, i, utilizationRateBps);
    i += 4;
    putInt32LE(_data, i, borrowRateBps);
    i += 4;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

package software.sava.idl.clients.kamino.scope.gen.types;

import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.core.encoding.ByteUtil.getInt16LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt16LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

public record DiscountToMaturityData(int discountPerYearBps, long maturityTimestamp) implements SerDe {

  public static final int BYTES = 10;

  public static DiscountToMaturityData read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var discountPerYearBps = getInt16LE(_data, i);
    i += 2;
    final var maturityTimestamp = getInt64LE(_data, i);
    return new DiscountToMaturityData(discountPerYearBps, maturityTimestamp);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt16LE(_data, i, discountPerYearBps);
    i += 2;
    putInt64LE(_data, i, maturityTimestamp);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

package software.sava.idl.clients.kamino.lend.gen.types;

import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

/// @param lower Lower value of acceptable price
/// @param upper Upper value of acceptable price
/// @param exp Number of decimals of the previously defined values
public record PriceHeuristic(long lower,
                             long upper,
                             long exp) implements SerDe {

  public static final int BYTES = 24;

  public static PriceHeuristic read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var lower = getInt64LE(_data, i);
    i += 8;
    final var upper = getInt64LE(_data, i);
    i += 8;
    final var exp = getInt64LE(_data, i);
    return new PriceHeuristic(lower, upper, exp);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt64LE(_data, i, lower);
    i += 8;
    putInt64LE(_data, i, upper);
    i += 8;
    putInt64LE(_data, i, exp);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

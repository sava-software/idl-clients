package software.sava.idl.clients.drift.gen.types;

import java.math.BigInteger;

import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.encoding.ByteUtil.getInt128LE;
import static software.sava.core.encoding.ByteUtil.getInt16LE;
import static software.sava.core.encoding.ByteUtil.putInt128LE;
import static software.sava.core.encoding.ByteUtil.putInt16LE;

/// @param scaledBalance To get the pool's token amount, you must multiply the scaled balance by the market's cumulative
///                      deposit interest
///                      precision: SPOT_BALANCE_PRECISION
/// @param marketIndex The spot market the pool is for
public record PoolBalance(BigInteger scaledBalance,
                          int marketIndex,
                          byte[] padding) implements SerDe {

  public static final int BYTES = 24;
  public static final int PADDING_LEN = 6;

  public static PoolBalance read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var scaledBalance = getInt128LE(_data, i);
    i += 16;
    final var marketIndex = getInt16LE(_data, i);
    i += 2;
    final var padding = new byte[6];
    SerDeUtil.readArray(padding, _data, i);
    return new PoolBalance(scaledBalance, marketIndex, padding);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt128LE(_data, i, scaledBalance);
    i += 16;
    putInt16LE(_data, i, marketIndex);
    i += 2;
    i += SerDeUtil.writeArrayChecked(padding, 6, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

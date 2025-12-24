package software.sava.idl.clients.drift.vaults.gen.types;

import java.math.BigInteger;

import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.core.encoding.ByteUtil.getInt128LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt128LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

/// @param shares request shares of vault withdraw
/// @param value requested value (in vault spot_market_index) of shares for withdraw
/// @param ts request ts of vault withdraw
public record WithdrawRequest(BigInteger shares,
                              long value,
                              long ts) implements SerDe {

  public static final int BYTES = 32;

  public static WithdrawRequest read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var shares = getInt128LE(_data, i);
    i += 16;
    final var value = getInt64LE(_data, i);
    i += 8;
    final var ts = getInt64LE(_data, i);
    return new WithdrawRequest(shares, value, ts);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt128LE(_data, i, shares);
    i += 16;
    putInt64LE(_data, i, value);
    i += 8;
    putInt64LE(_data, i, ts);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

package software.sava.idl.clients.drift.gen.types;

import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

public record PriceDivergenceGuardRails(long markOraclePercentDivergence, long oracleTwap5minPercentDivergence) implements SerDe {

  public static final int BYTES = 16;

  public static final int MARK_ORACLE_PERCENT_DIVERGENCE_OFFSET = 0;
  public static final int ORACLE_TWAP_5MIN_PERCENT_DIVERGENCE_OFFSET = 8;

  public static PriceDivergenceGuardRails read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var markOraclePercentDivergence = getInt64LE(_data, i);
    i += 8;
    final var oracleTwap5minPercentDivergence = getInt64LE(_data, i);
    return new PriceDivergenceGuardRails(markOraclePercentDivergence, oracleTwap5minPercentDivergence);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt64LE(_data, i, markOraclePercentDivergence);
    i += 8;
    putInt64LE(_data, i, oracleTwap5minPercentDivergence);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

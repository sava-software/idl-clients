package software.sava.idl.clients.drift.gen.types;

import software.sava.core.borsh.Borsh;

import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

/// @param lastOraclePrice precision: PRICE_PRECISION
/// @param lastOracleConf precision: PRICE_PRECISION
/// @param lastOracleDelay number of slots since last update
/// @param lastOraclePriceTwap precision: PRICE_PRECISION
/// @param lastOraclePriceTwap5min precision: PRICE_PRECISION
/// @param lastOraclePriceTwapTs unix_timestamp of last snapshot
public record HistoricalOracleData(long lastOraclePrice,
                                   long lastOracleConf,
                                   long lastOracleDelay,
                                   long lastOraclePriceTwap,
                                   long lastOraclePriceTwap5min,
                                   long lastOraclePriceTwapTs) implements Borsh {

  public static final int BYTES = 48;

  public static HistoricalOracleData read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var lastOraclePrice = getInt64LE(_data, i);
    i += 8;
    final var lastOracleConf = getInt64LE(_data, i);
    i += 8;
    final var lastOracleDelay = getInt64LE(_data, i);
    i += 8;
    final var lastOraclePriceTwap = getInt64LE(_data, i);
    i += 8;
    final var lastOraclePriceTwap5min = getInt64LE(_data, i);
    i += 8;
    final var lastOraclePriceTwapTs = getInt64LE(_data, i);
    return new HistoricalOracleData(lastOraclePrice,
                                    lastOracleConf,
                                    lastOracleDelay,
                                    lastOraclePriceTwap,
                                    lastOraclePriceTwap5min,
                                    lastOraclePriceTwapTs);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt64LE(_data, i, lastOraclePrice);
    i += 8;
    putInt64LE(_data, i, lastOracleConf);
    i += 8;
    putInt64LE(_data, i, lastOracleDelay);
    i += 8;
    putInt64LE(_data, i, lastOraclePriceTwap);
    i += 8;
    putInt64LE(_data, i, lastOraclePriceTwap5min);
    i += 8;
    putInt64LE(_data, i, lastOraclePriceTwapTs);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

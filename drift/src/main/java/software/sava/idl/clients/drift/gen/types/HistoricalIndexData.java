package software.sava.idl.clients.drift.gen.types;

import software.sava.core.borsh.Borsh;

import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

/// @param lastIndexBidPrice precision: PRICE_PRECISION
/// @param lastIndexAskPrice precision: PRICE_PRECISION
/// @param lastIndexPriceTwap precision: PRICE_PRECISION
/// @param lastIndexPriceTwap5min precision: PRICE_PRECISION
/// @param lastIndexPriceTwapTs unix_timestamp of last snapshot
public record HistoricalIndexData(long lastIndexBidPrice,
                                  long lastIndexAskPrice,
                                  long lastIndexPriceTwap,
                                  long lastIndexPriceTwap5min,
                                  long lastIndexPriceTwapTs) implements Borsh {

  public static final int BYTES = 40;

  public static HistoricalIndexData read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var lastIndexBidPrice = getInt64LE(_data, i);
    i += 8;
    final var lastIndexAskPrice = getInt64LE(_data, i);
    i += 8;
    final var lastIndexPriceTwap = getInt64LE(_data, i);
    i += 8;
    final var lastIndexPriceTwap5min = getInt64LE(_data, i);
    i += 8;
    final var lastIndexPriceTwapTs = getInt64LE(_data, i);
    return new HistoricalIndexData(lastIndexBidPrice,
                                   lastIndexAskPrice,
                                   lastIndexPriceTwap,
                                   lastIndexPriceTwap5min,
                                   lastIndexPriceTwapTs);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt64LE(_data, i, lastIndexBidPrice);
    i += 8;
    putInt64LE(_data, i, lastIndexAskPrice);
    i += 8;
    putInt64LE(_data, i, lastIndexPriceTwap);
    i += 8;
    putInt64LE(_data, i, lastIndexPriceTwap5min);
    i += 8;
    putInt64LE(_data, i, lastIndexPriceTwapTs);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

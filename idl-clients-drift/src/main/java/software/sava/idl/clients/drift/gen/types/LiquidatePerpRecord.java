package software.sava.idl.clients.drift.gen.types;

import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.core.encoding.ByteUtil.getInt16LE;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt16LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

/// @param lpShares precision: AMM_RESERVE_PRECISION
/// @param liquidatorFee precision: QUOTE_PRECISION
/// @param ifFee precision: QUOTE_PRECISION
public record LiquidatePerpRecord(int marketIndex,
                                  long oraclePrice,
                                  long baseAssetAmount,
                                  long quoteAssetAmount,
                                  long lpShares,
                                  long fillRecordId,
                                  int userOrderId,
                                  int liquidatorOrderId,
                                  long liquidatorFee,
                                  long ifFee) implements SerDe {

  public static final int BYTES = 66;

  public static final int MARKET_INDEX_OFFSET = 0;
  public static final int ORACLE_PRICE_OFFSET = 2;
  public static final int BASE_ASSET_AMOUNT_OFFSET = 10;
  public static final int QUOTE_ASSET_AMOUNT_OFFSET = 18;
  public static final int LP_SHARES_OFFSET = 26;
  public static final int FILL_RECORD_ID_OFFSET = 34;
  public static final int USER_ORDER_ID_OFFSET = 42;
  public static final int LIQUIDATOR_ORDER_ID_OFFSET = 46;
  public static final int LIQUIDATOR_FEE_OFFSET = 50;
  public static final int IF_FEE_OFFSET = 58;

  public static LiquidatePerpRecord read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var marketIndex = getInt16LE(_data, i);
    i += 2;
    final var oraclePrice = getInt64LE(_data, i);
    i += 8;
    final var baseAssetAmount = getInt64LE(_data, i);
    i += 8;
    final var quoteAssetAmount = getInt64LE(_data, i);
    i += 8;
    final var lpShares = getInt64LE(_data, i);
    i += 8;
    final var fillRecordId = getInt64LE(_data, i);
    i += 8;
    final var userOrderId = getInt32LE(_data, i);
    i += 4;
    final var liquidatorOrderId = getInt32LE(_data, i);
    i += 4;
    final var liquidatorFee = getInt64LE(_data, i);
    i += 8;
    final var ifFee = getInt64LE(_data, i);
    return new LiquidatePerpRecord(marketIndex,
                                   oraclePrice,
                                   baseAssetAmount,
                                   quoteAssetAmount,
                                   lpShares,
                                   fillRecordId,
                                   userOrderId,
                                   liquidatorOrderId,
                                   liquidatorFee,
                                   ifFee);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt16LE(_data, i, marketIndex);
    i += 2;
    putInt64LE(_data, i, oraclePrice);
    i += 8;
    putInt64LE(_data, i, baseAssetAmount);
    i += 8;
    putInt64LE(_data, i, quoteAssetAmount);
    i += 8;
    putInt64LE(_data, i, lpShares);
    i += 8;
    putInt64LE(_data, i, fillRecordId);
    i += 8;
    putInt32LE(_data, i, userOrderId);
    i += 4;
    putInt32LE(_data, i, liquidatorOrderId);
    i += 4;
    putInt64LE(_data, i, liquidatorFee);
    i += 8;
    putInt64LE(_data, i, ifFee);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

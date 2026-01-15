package software.sava.idl.clients.drift.gen.types;

import java.math.BigInteger;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt128LE;
import static software.sava.core.encoding.ByteUtil.getInt16LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt128LE;
import static software.sava.core.encoding.ByteUtil.putInt16LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

/// @param position BASE PRECISION
public record CacheInfo(PublicKey oracle,
                        BigInteger lastFeePoolTokenAmount,
                        BigInteger lastNetPnlPoolTokenAmount,
                        BigInteger lastExchangeFees,
                        BigInteger lastSettleAmmExFees,
                        BigInteger lastSettleAmmPnl,
                        long position,
                        long slot,
                        long lastSettleAmount,
                        long lastSettleSlot,
                        long lastSettleTs,
                        long quoteOwedFromLpPool,
                        long ammInventoryLimit,
                        long oraclePrice,
                        long oracleSlot,
                        int marketIndex,
                        int oracleSource,
                        int oracleValidity,
                        int lpStatusForPerpMarket,
                        int ammPositionScalar,
                        byte[] padding) implements SerDe {

  public static final int BYTES = 224;
  public static final int PADDING_LEN = 34;

  public static final int ORACLE_OFFSET = 0;
  public static final int LAST_FEE_POOL_TOKEN_AMOUNT_OFFSET = 32;
  public static final int LAST_NET_PNL_POOL_TOKEN_AMOUNT_OFFSET = 48;
  public static final int LAST_EXCHANGE_FEES_OFFSET = 64;
  public static final int LAST_SETTLE_AMM_EX_FEES_OFFSET = 80;
  public static final int LAST_SETTLE_AMM_PNL_OFFSET = 96;
  public static final int POSITION_OFFSET = 112;
  public static final int SLOT_OFFSET = 120;
  public static final int LAST_SETTLE_AMOUNT_OFFSET = 128;
  public static final int LAST_SETTLE_SLOT_OFFSET = 136;
  public static final int LAST_SETTLE_TS_OFFSET = 144;
  public static final int QUOTE_OWED_FROM_LP_POOL_OFFSET = 152;
  public static final int AMM_INVENTORY_LIMIT_OFFSET = 160;
  public static final int ORACLE_PRICE_OFFSET = 168;
  public static final int ORACLE_SLOT_OFFSET = 176;
  public static final int MARKET_INDEX_OFFSET = 184;
  public static final int ORACLE_SOURCE_OFFSET = 186;
  public static final int ORACLE_VALIDITY_OFFSET = 187;
  public static final int LP_STATUS_FOR_PERP_MARKET_OFFSET = 188;
  public static final int AMM_POSITION_SCALAR_OFFSET = 189;
  public static final int PADDING_OFFSET = 190;

  public static CacheInfo read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var oracle = readPubKey(_data, i);
    i += 32;
    final var lastFeePoolTokenAmount = getInt128LE(_data, i);
    i += 16;
    final var lastNetPnlPoolTokenAmount = getInt128LE(_data, i);
    i += 16;
    final var lastExchangeFees = getInt128LE(_data, i);
    i += 16;
    final var lastSettleAmmExFees = getInt128LE(_data, i);
    i += 16;
    final var lastSettleAmmPnl = getInt128LE(_data, i);
    i += 16;
    final var position = getInt64LE(_data, i);
    i += 8;
    final var slot = getInt64LE(_data, i);
    i += 8;
    final var lastSettleAmount = getInt64LE(_data, i);
    i += 8;
    final var lastSettleSlot = getInt64LE(_data, i);
    i += 8;
    final var lastSettleTs = getInt64LE(_data, i);
    i += 8;
    final var quoteOwedFromLpPool = getInt64LE(_data, i);
    i += 8;
    final var ammInventoryLimit = getInt64LE(_data, i);
    i += 8;
    final var oraclePrice = getInt64LE(_data, i);
    i += 8;
    final var oracleSlot = getInt64LE(_data, i);
    i += 8;
    final var marketIndex = getInt16LE(_data, i);
    i += 2;
    final var oracleSource = _data[i] & 0xFF;
    ++i;
    final var oracleValidity = _data[i] & 0xFF;
    ++i;
    final var lpStatusForPerpMarket = _data[i] & 0xFF;
    ++i;
    final var ammPositionScalar = _data[i] & 0xFF;
    ++i;
    final var padding = new byte[34];
    SerDeUtil.readArray(padding, _data, i);
    return new CacheInfo(oracle,
                         lastFeePoolTokenAmount,
                         lastNetPnlPoolTokenAmount,
                         lastExchangeFees,
                         lastSettleAmmExFees,
                         lastSettleAmmPnl,
                         position,
                         slot,
                         lastSettleAmount,
                         lastSettleSlot,
                         lastSettleTs,
                         quoteOwedFromLpPool,
                         ammInventoryLimit,
                         oraclePrice,
                         oracleSlot,
                         marketIndex,
                         oracleSource,
                         oracleValidity,
                         lpStatusForPerpMarket,
                         ammPositionScalar,
                         padding);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    oracle.write(_data, i);
    i += 32;
    putInt128LE(_data, i, lastFeePoolTokenAmount);
    i += 16;
    putInt128LE(_data, i, lastNetPnlPoolTokenAmount);
    i += 16;
    putInt128LE(_data, i, lastExchangeFees);
    i += 16;
    putInt128LE(_data, i, lastSettleAmmExFees);
    i += 16;
    putInt128LE(_data, i, lastSettleAmmPnl);
    i += 16;
    putInt64LE(_data, i, position);
    i += 8;
    putInt64LE(_data, i, slot);
    i += 8;
    putInt64LE(_data, i, lastSettleAmount);
    i += 8;
    putInt64LE(_data, i, lastSettleSlot);
    i += 8;
    putInt64LE(_data, i, lastSettleTs);
    i += 8;
    putInt64LE(_data, i, quoteOwedFromLpPool);
    i += 8;
    putInt64LE(_data, i, ammInventoryLimit);
    i += 8;
    putInt64LE(_data, i, oraclePrice);
    i += 8;
    putInt64LE(_data, i, oracleSlot);
    i += 8;
    putInt16LE(_data, i, marketIndex);
    i += 2;
    _data[i] = (byte) oracleSource;
    ++i;
    _data[i] = (byte) oracleValidity;
    ++i;
    _data[i] = (byte) lpStatusForPerpMarket;
    ++i;
    _data[i] = (byte) ammPositionScalar;
    ++i;
    i += SerDeUtil.writeArrayChecked(padding, 34, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

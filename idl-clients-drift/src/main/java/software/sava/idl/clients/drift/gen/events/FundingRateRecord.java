package software.sava.idl.clients.drift.gen.events;

import java.math.BigInteger;

import software.sava.core.programs.Discriminator;

import static software.sava.core.encoding.ByteUtil.getInt128LE;
import static software.sava.core.encoding.ByteUtil.getInt16LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt128LE;
import static software.sava.core.encoding.ByteUtil.putInt16LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record FundingRateRecord(Discriminator discriminator,
                                long ts,
                                long recordId,
                                int marketIndex,
                                long fundingRate,
                                BigInteger fundingRateLong,
                                BigInteger fundingRateShort,
                                BigInteger cumulativeFundingRateLong,
                                BigInteger cumulativeFundingRateShort,
                                long oraclePriceTwap,
                                long markPriceTwap,
                                long periodRevenue,
                                BigInteger baseAssetAmountWithAmm,
                                BigInteger baseAssetAmountWithUnsettledLp) implements DriftEvent {

  public static final int BYTES = 154;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(218, 84, 214, 163, 196, 206, 189, 250);

  public static final int TS_OFFSET = 8;
  public static final int RECORD_ID_OFFSET = 16;
  public static final int MARKET_INDEX_OFFSET = 24;
  public static final int FUNDING_RATE_OFFSET = 26;
  public static final int FUNDING_RATE_LONG_OFFSET = 34;
  public static final int FUNDING_RATE_SHORT_OFFSET = 50;
  public static final int CUMULATIVE_FUNDING_RATE_LONG_OFFSET = 66;
  public static final int CUMULATIVE_FUNDING_RATE_SHORT_OFFSET = 82;
  public static final int ORACLE_PRICE_TWAP_OFFSET = 98;
  public static final int MARK_PRICE_TWAP_OFFSET = 106;
  public static final int PERIOD_REVENUE_OFFSET = 114;
  public static final int BASE_ASSET_AMOUNT_WITH_AMM_OFFSET = 122;
  public static final int BASE_ASSET_AMOUNT_WITH_UNSETTLED_LP_OFFSET = 138;

  public static FundingRateRecord read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createDiscriminator(_data, _offset, 8);
    int i = _offset + discriminator.length();
    final var ts = getInt64LE(_data, i);
    i += 8;
    final var recordId = getInt64LE(_data, i);
    i += 8;
    final var marketIndex = getInt16LE(_data, i);
    i += 2;
    final var fundingRate = getInt64LE(_data, i);
    i += 8;
    final var fundingRateLong = getInt128LE(_data, i);
    i += 16;
    final var fundingRateShort = getInt128LE(_data, i);
    i += 16;
    final var cumulativeFundingRateLong = getInt128LE(_data, i);
    i += 16;
    final var cumulativeFundingRateShort = getInt128LE(_data, i);
    i += 16;
    final var oraclePriceTwap = getInt64LE(_data, i);
    i += 8;
    final var markPriceTwap = getInt64LE(_data, i);
    i += 8;
    final var periodRevenue = getInt64LE(_data, i);
    i += 8;
    final var baseAssetAmountWithAmm = getInt128LE(_data, i);
    i += 16;
    final var baseAssetAmountWithUnsettledLp = getInt128LE(_data, i);
    return new FundingRateRecord(discriminator,
                                 ts,
                                 recordId,
                                 marketIndex,
                                 fundingRate,
                                 fundingRateLong,
                                 fundingRateShort,
                                 cumulativeFundingRateLong,
                                 cumulativeFundingRateShort,
                                 oraclePriceTwap,
                                 markPriceTwap,
                                 periodRevenue,
                                 baseAssetAmountWithAmm,
                                 baseAssetAmountWithUnsettledLp);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    putInt64LE(_data, i, ts);
    i += 8;
    putInt64LE(_data, i, recordId);
    i += 8;
    putInt16LE(_data, i, marketIndex);
    i += 2;
    putInt64LE(_data, i, fundingRate);
    i += 8;
    putInt128LE(_data, i, fundingRateLong);
    i += 16;
    putInt128LE(_data, i, fundingRateShort);
    i += 16;
    putInt128LE(_data, i, cumulativeFundingRateLong);
    i += 16;
    putInt128LE(_data, i, cumulativeFundingRateShort);
    i += 16;
    putInt64LE(_data, i, oraclePriceTwap);
    i += 8;
    putInt64LE(_data, i, markPriceTwap);
    i += 8;
    putInt64LE(_data, i, periodRevenue);
    i += 8;
    putInt128LE(_data, i, baseAssetAmountWithAmm);
    i += 16;
    putInt128LE(_data, i, baseAssetAmountWithUnsettledLp);
    i += 16;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

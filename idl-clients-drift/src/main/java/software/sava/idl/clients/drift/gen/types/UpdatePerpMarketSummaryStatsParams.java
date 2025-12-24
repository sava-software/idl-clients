package software.sava.idl.clients.drift.gen.types;

import java.lang.Boolean;

import java.util.OptionalLong;

import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.encoding.ByteUtil.getInt64LE;

public record UpdatePerpMarketSummaryStatsParams(OptionalLong quoteAssetAmountWithUnsettledLp,
                                                 OptionalLong netUnsettledFundingPnl,
                                                 Boolean updateAmmSummaryStats,
                                                 Boolean excludeTotalLiqFee) implements SerDe {

  public static UpdatePerpMarketSummaryStatsParams read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final OptionalLong quoteAssetAmountWithUnsettledLp;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      quoteAssetAmountWithUnsettledLp = OptionalLong.empty();
      ++i;
    } else {
      ++i;
      quoteAssetAmountWithUnsettledLp = OptionalLong.of(getInt64LE(_data, i));
      i += 8;
    }
    final OptionalLong netUnsettledFundingPnl;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      netUnsettledFundingPnl = OptionalLong.empty();
      ++i;
    } else {
      ++i;
      netUnsettledFundingPnl = OptionalLong.of(getInt64LE(_data, i));
      i += 8;
    }
    final Boolean updateAmmSummaryStats;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      updateAmmSummaryStats = null;
      ++i;
    } else {
      ++i;
      updateAmmSummaryStats = _data[i] == 1;
      ++i;
    }
    final Boolean excludeTotalLiqFee;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      excludeTotalLiqFee = null;
    } else {
      ++i;
      excludeTotalLiqFee = _data[i] == 1;
    }
    return new UpdatePerpMarketSummaryStatsParams(quoteAssetAmountWithUnsettledLp,
                                                  netUnsettledFundingPnl,
                                                  updateAmmSummaryStats,
                                                  excludeTotalLiqFee);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += SerDeUtil.writeOptional(1, quoteAssetAmountWithUnsettledLp, _data, i);
    i += SerDeUtil.writeOptional(1, netUnsettledFundingPnl, _data, i);
    i += SerDeUtil.writeOptional(1, updateAmmSummaryStats, _data, i);
    i += SerDeUtil.writeOptional(1, excludeTotalLiqFee, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return (quoteAssetAmountWithUnsettledLp == null || quoteAssetAmountWithUnsettledLp.isEmpty() ? 1 : (1 + 8)) + (netUnsettledFundingPnl == null || netUnsettledFundingPnl.isEmpty() ? 1 : (1 + 8)) + (updateAmmSummaryStats == null ? 1 : (1 + 1)) + (excludeTotalLiqFee == null ? 1 : (1 + 1));
  }
}

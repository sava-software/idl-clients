package software.sava.idl.clients.drift.gen.types;

import java.lang.Boolean;

import java.util.OptionalLong;

import software.sava.core.borsh.Borsh;

import static software.sava.core.encoding.ByteUtil.getInt64LE;

public record UpdatePerpMarketSummaryStatsParams(OptionalLong quoteAssetAmountWithUnsettledLp,
                                                 OptionalLong netUnsettledFundingPnl,
                                                 Boolean updateAmmSummaryStats,
                                                 Boolean excludeTotalLiqFee) implements Borsh {

  public static UpdatePerpMarketSummaryStatsParams read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final OptionalLong quoteAssetAmountWithUnsettledLp;
    if (_data[i] == 0) {
      quoteAssetAmountWithUnsettledLp = OptionalLong.empty();
      ++i;
    } else {
      ++i;
      quoteAssetAmountWithUnsettledLp = OptionalLong.of(getInt64LE(_data, i));
      i += 8;
    }
    final OptionalLong netUnsettledFundingPnl;
    if (_data[i] == 0) {
      netUnsettledFundingPnl = OptionalLong.empty();
      ++i;
    } else {
      ++i;
      netUnsettledFundingPnl = OptionalLong.of(getInt64LE(_data, i));
      i += 8;
    }
    final Boolean updateAmmSummaryStats;
    if (_data[i] == 0) {
      updateAmmSummaryStats = null;
      ++i;
    } else {
      ++i;
      updateAmmSummaryStats = _data[i] == 1;
      ++i;
    }
    final Boolean excludeTotalLiqFee;
    if (_data[i] == 0) {
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
    i += Borsh.writeOptional(quoteAssetAmountWithUnsettledLp, _data, i);
    i += Borsh.writeOptional(netUnsettledFundingPnl, _data, i);
    i += Borsh.writeOptional(updateAmmSummaryStats, _data, i);
    i += Borsh.writeOptional(excludeTotalLiqFee, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return (quoteAssetAmountWithUnsettledLp == null || quoteAssetAmountWithUnsettledLp.isEmpty() ? 1 : (1 + 8)) + (netUnsettledFundingPnl == null || netUnsettledFundingPnl.isEmpty() ? 1 : (1 + 8)) + (updateAmmSummaryStats == null ? 1 : (1 + 1)) + (excludeTotalLiqFee == null ? 1 : (1 + 1));
  }
}

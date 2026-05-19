package software.sava.idl.clients.phoenix.perpetuals.gen.types;

import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

/// Borsh payload for updating funding parameters for a perp asset.
///
public record UpdateFundingParametersInstruction(Symbol perpAssetSymbol,
                                                 FundingRateUnitInSeconds fundingIntervalSeconds,
                                                 FundingRateUnitInSeconds fundingPeriodSeconds,
                                                 SignedQuoteLotsPerBaseLot maxFundingRate) implements SerDe {

  public static final int PERP_ASSET_SYMBOL_OFFSET = 0;
  public static final int FUNDING_INTERVAL_SECONDS_OFFSET = 17;

  public static UpdateFundingParametersInstruction read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var perpAssetSymbol = Symbol.read(_data, i);
    i += perpAssetSymbol.l();
    final FundingRateUnitInSeconds fundingIntervalSeconds;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      fundingIntervalSeconds = null;
      ++i;
    } else {
      ++i;
      fundingIntervalSeconds = FundingRateUnitInSeconds.read(_data, i);
      i += fundingIntervalSeconds.l();
    }
    final FundingRateUnitInSeconds fundingPeriodSeconds;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      fundingPeriodSeconds = null;
      ++i;
    } else {
      ++i;
      fundingPeriodSeconds = FundingRateUnitInSeconds.read(_data, i);
      i += fundingPeriodSeconds.l();
    }
    final SignedQuoteLotsPerBaseLot maxFundingRate;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      maxFundingRate = null;
    } else {
      ++i;
      maxFundingRate = SignedQuoteLotsPerBaseLot.read(_data, i);
    }
    return new UpdateFundingParametersInstruction(perpAssetSymbol,
                                                  fundingIntervalSeconds,
                                                  fundingPeriodSeconds,
                                                  maxFundingRate);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += perpAssetSymbol.write(_data, i);
    i += SerDeUtil.writeOptional(1, fundingIntervalSeconds, _data, i);
    i += SerDeUtil.writeOptional(1, fundingPeriodSeconds, _data, i);
    i += SerDeUtil.writeOptional(1, maxFundingRate, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return perpAssetSymbol.l() + (fundingIntervalSeconds == null ? 1 : (1 + fundingIntervalSeconds.l())) + (fundingPeriodSeconds == null ? 1 : (1 + fundingPeriodSeconds.l())) + (maxFundingRate == null ? 1 : (1 + maxFundingRate.l()));
  }
}

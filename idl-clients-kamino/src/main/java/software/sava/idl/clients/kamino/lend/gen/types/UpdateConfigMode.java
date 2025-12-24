package software.sava.idl.clients.kamino.lend.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

public enum UpdateConfigMode implements RustEnum {

  UpdateLoanToValuePct,
  UpdateMaxLiquidationBonusBps,
  UpdateLiquidationThresholdPct,
  UpdateProtocolLiquidationFee,
  UpdateProtocolTakeRate,
  UpdateFeesOriginationFee,
  UpdateFeesFlashLoanFee,
  DeprecatedUpdateFeesReferralFeeBps,
  UpdateDepositLimit,
  UpdateBorrowLimit,
  UpdateTokenInfoLowerHeuristic,
  UpdateTokenInfoUpperHeuristic,
  UpdateTokenInfoExpHeuristic,
  UpdateTokenInfoTwapDivergence,
  UpdateTokenInfoScopeTwap,
  UpdateTokenInfoScopeChain,
  UpdateTokenInfoName,
  UpdateTokenInfoPriceMaxAge,
  UpdateTokenInfoTwapMaxAge,
  UpdateScopePriceFeed,
  UpdatePythPrice,
  UpdateSwitchboardFeed,
  UpdateSwitchboardTwapFeed,
  UpdateBorrowRateCurve,
  UpdateEntireReserveConfig,
  UpdateDebtWithdrawalCap,
  UpdateDepositWithdrawalCap,
  DeprecatedUpdateDebtWithdrawalCapCurrentTotal,
  DeprecatedUpdateDepositWithdrawalCapCurrentTotal,
  UpdateBadDebtLiquidationBonusBps,
  UpdateMinLiquidationBonusBps,
  UpdateDeleveragingMarginCallPeriod,
  UpdateBorrowFactor,
  DeprecatedUpdateAssetTier,
  UpdateElevationGroup,
  UpdateDeleveragingThresholdDecreaseBpsPerDay,
  DeprecatedUpdateMultiplierSideBoost,
  DeprecatedUpdateMultiplierTagBoost,
  UpdateReserveStatus,
  UpdateFarmCollateral,
  UpdateFarmDebt,
  UpdateDisableUsageAsCollateralOutsideEmode,
  UpdateBlockBorrowingAboveUtilizationPct,
  UpdateBlockPriceUsage,
  UpdateBorrowLimitOutsideElevationGroup,
  UpdateBorrowLimitsInElevationGroupAgainstThisReserve,
  UpdateHostFixedInterestRateBps,
  UpdateAutodeleverageEnabled,
  UpdateDeleveragingBonusIncreaseBpsPerDay,
  UpdateProtocolOrderExecutionFee,
  UpdateProposerAuthorityLock,
  UpdateMinDeleveragingBonusBps,
  UpdateBlockCTokenUsage;

  public static UpdateConfigMode read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, UpdateConfigMode.values(), _data, _offset);
  }
}
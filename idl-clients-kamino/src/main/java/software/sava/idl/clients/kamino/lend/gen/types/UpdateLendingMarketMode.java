package software.sava.idl.clients.kamino.lend.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

public enum UpdateLendingMarketMode implements RustEnum {

  UpdateOwner,
  UpdateEmergencyMode,
  UpdateLiquidationCloseFactor,
  UpdateLiquidationMaxValue,
  DeprecatedUpdateGlobalUnhealthyBorrow,
  UpdateGlobalAllowedBorrow,
  UpdateRiskCouncil,
  UpdateMinFullLiquidationThreshold,
  UpdateInsolvencyRiskLtv,
  UpdateElevationGroup,
  UpdateReferralFeeBps,
  DeprecatedUpdateMultiplierPoints,
  UpdatePriceRefreshTriggerToMaxAgePct,
  UpdateAutodeleverageEnabled,
  UpdateBorrowingDisabled,
  UpdateMinNetValueObligationPostAction,
  UpdateMinValueLtvSkipPriorityLiqCheck,
  UpdateMinValueBfSkipPriorityLiqCheck,
  UpdatePaddingFields,
  UpdateName,
  UpdateIndividualAutodeleverageMarginCallPeriodSecs,
  UpdateInitialDepositAmount,
  UpdateObligationOrderExecutionEnabled,
  UpdateImmutableFlag,
  UpdateObligationOrderCreationEnabled,
  UpdateProposerAuthority,
  UpdatePriceTriggeredLiquidationDisabled,
  UpdateMatureReserveDebtLiquidationEnabled,
  UpdateObligationBorrowDebtTermLiquidationEnabled,
  UpdateBorrowOrderCreationEnabled,
  UpdateBorrowOrderExecutionEnabled,
  UpdateMinBorrowOrderFillValue;

  public static UpdateLendingMarketMode read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, UpdateLendingMarketMode.values(), _data, _offset);
  }
}
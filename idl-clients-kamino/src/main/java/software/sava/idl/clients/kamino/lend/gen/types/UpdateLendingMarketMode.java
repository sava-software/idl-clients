package software.sava.idl.clients.kamino.lend.gen.types;

import software.sava.core.borsh.Borsh;

public enum UpdateLendingMarketMode implements Borsh.Enum {

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
  UpdatePriceTriggeredLiquidationDisabled;

  public static UpdateLendingMarketMode read(final byte[] _data, final int _offset) {
    return Borsh.read(UpdateLendingMarketMode.values(), _data, _offset);
  }
}
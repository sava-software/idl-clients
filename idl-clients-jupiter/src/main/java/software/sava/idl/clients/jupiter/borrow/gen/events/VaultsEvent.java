package software.sava.idl.clients.jupiter.borrow.gen.events;

import software.sava.idl.clients.core.gen.SerDe;

public sealed interface VaultsEvent extends SerDe permits
    LogAbsorb,
    LogClosePosition,
    LogInitBranch,
    LogInitTick,
    LogInitTickHasDebtArray,
    LogInitTickIdLiquidation,
    LogInitVaultConfig,
    LogInitVaultState,
    LogLiquidate,
    LogLiquidateInfo,
    LogLiquidationRoundingDiff,
    LogOperate,
    LogRebalance,
    LogUpdateAuthority,
    LogUpdateAuths,
    LogUpdateBorrowFee,
    LogUpdateBorrowRateMagnifier,
    LogUpdateCollateralFactor,
    LogUpdateCoreSettings,
    LogUpdateExchangePrices,
    LogUpdateLiquidationMaxLimit,
    LogUpdateLiquidationPenalty,
    LogUpdateLiquidationThreshold,
    LogUpdateLookupTable,
    LogUpdateOracle,
    LogUpdateRebalancer,
    LogUpdateSupplyRateMagnifier,
    LogUpdateWithdrawGap,
    LogUserPosition {

  static VaultsEvent read(final byte[] _data, final int _offset) {
    if (LogAbsorb.DISCRIMINATOR.equals(_data, _offset)) {
      return LogAbsorb.read(_data, _offset);
    } else if (LogClosePosition.DISCRIMINATOR.equals(_data, _offset)) {
      return LogClosePosition.read(_data, _offset);
    } else if (LogInitBranch.DISCRIMINATOR.equals(_data, _offset)) {
      return LogInitBranch.read(_data, _offset);
    } else if (LogInitTick.DISCRIMINATOR.equals(_data, _offset)) {
      return LogInitTick.read(_data, _offset);
    } else if (LogInitTickHasDebtArray.DISCRIMINATOR.equals(_data, _offset)) {
      return LogInitTickHasDebtArray.read(_data, _offset);
    } else if (LogInitTickIdLiquidation.DISCRIMINATOR.equals(_data, _offset)) {
      return LogInitTickIdLiquidation.read(_data, _offset);
    } else if (LogInitVaultConfig.DISCRIMINATOR.equals(_data, _offset)) {
      return LogInitVaultConfig.read(_data, _offset);
    } else if (LogInitVaultState.DISCRIMINATOR.equals(_data, _offset)) {
      return LogInitVaultState.read(_data, _offset);
    } else if (LogLiquidate.DISCRIMINATOR.equals(_data, _offset)) {
      return LogLiquidate.read(_data, _offset);
    } else if (LogLiquidateInfo.DISCRIMINATOR.equals(_data, _offset)) {
      return LogLiquidateInfo.read(_data, _offset);
    } else if (LogLiquidationRoundingDiff.DISCRIMINATOR.equals(_data, _offset)) {
      return LogLiquidationRoundingDiff.read(_data, _offset);
    } else if (LogOperate.DISCRIMINATOR.equals(_data, _offset)) {
      return LogOperate.read(_data, _offset);
    } else if (LogRebalance.DISCRIMINATOR.equals(_data, _offset)) {
      return LogRebalance.read(_data, _offset);
    } else if (LogUpdateAuthority.DISCRIMINATOR.equals(_data, _offset)) {
      return LogUpdateAuthority.read(_data, _offset);
    } else if (LogUpdateAuths.DISCRIMINATOR.equals(_data, _offset)) {
      return LogUpdateAuths.read(_data, _offset);
    } else if (LogUpdateBorrowFee.DISCRIMINATOR.equals(_data, _offset)) {
      return LogUpdateBorrowFee.read(_data, _offset);
    } else if (LogUpdateBorrowRateMagnifier.DISCRIMINATOR.equals(_data, _offset)) {
      return LogUpdateBorrowRateMagnifier.read(_data, _offset);
    } else if (LogUpdateCollateralFactor.DISCRIMINATOR.equals(_data, _offset)) {
      return LogUpdateCollateralFactor.read(_data, _offset);
    } else if (LogUpdateCoreSettings.DISCRIMINATOR.equals(_data, _offset)) {
      return LogUpdateCoreSettings.read(_data, _offset);
    } else if (LogUpdateExchangePrices.DISCRIMINATOR.equals(_data, _offset)) {
      return LogUpdateExchangePrices.read(_data, _offset);
    } else if (LogUpdateLiquidationMaxLimit.DISCRIMINATOR.equals(_data, _offset)) {
      return LogUpdateLiquidationMaxLimit.read(_data, _offset);
    } else if (LogUpdateLiquidationPenalty.DISCRIMINATOR.equals(_data, _offset)) {
      return LogUpdateLiquidationPenalty.read(_data, _offset);
    } else if (LogUpdateLiquidationThreshold.DISCRIMINATOR.equals(_data, _offset)) {
      return LogUpdateLiquidationThreshold.read(_data, _offset);
    } else if (LogUpdateLookupTable.DISCRIMINATOR.equals(_data, _offset)) {
      return LogUpdateLookupTable.read(_data, _offset);
    } else if (LogUpdateOracle.DISCRIMINATOR.equals(_data, _offset)) {
      return LogUpdateOracle.read(_data, _offset);
    } else if (LogUpdateRebalancer.DISCRIMINATOR.equals(_data, _offset)) {
      return LogUpdateRebalancer.read(_data, _offset);
    } else if (LogUpdateSupplyRateMagnifier.DISCRIMINATOR.equals(_data, _offset)) {
      return LogUpdateSupplyRateMagnifier.read(_data, _offset);
    } else if (LogUpdateWithdrawGap.DISCRIMINATOR.equals(_data, _offset)) {
      return LogUpdateWithdrawGap.read(_data, _offset);
    } else if (LogUserPosition.DISCRIMINATOR.equals(_data, _offset)) {
      return LogUserPosition.read(_data, _offset);
    } else {
      return null;
    }
  }

  static VaultsEvent read(final byte[] _data) {
    return read(_data, 0);
  }

  static VaultsEvent readCPI(final byte[] _data, final int _offset) {
    return read(_data, _offset + 8);
  }

  static VaultsEvent readCPI(final byte[] _data) {
    return read(_data, 8);
  }
}
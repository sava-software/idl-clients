package software.sava.idl.clients.kamino.vaults.gen.types;

import software.sava.core.borsh.Borsh;
import software.sava.idl.clients.kamino.lend.gen.types.BorrowRateCurve;
import software.sava.idl.clients.kamino.lend.gen.types.ReserveFees;
import software.sava.idl.clients.kamino.lend.gen.types.TokenInfo;
import software.sava.idl.clients.kamino.lend.gen.types.WithdrawalCaps;

import static software.sava.core.encoding.ByteUtil.getInt16LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt16LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

/// Reserve configuration values
///
/// @param status Status of the reserve Active/Obsolete/Hidden
/// @param assetTier Asset tier -> 0 - regular (collateral & debt), 1 - isolated collateral, 2 - isolated debt
/// @param hostFixedInterestRateBps Flat rate that goes to the host
/// @param minDeleveragingBonusBps Starting bonus for deleveraging-related liquidations, in bps.
/// @param blockCtokenUsage Boolean flag to block minting/redeeming of ctokens
///                         Blocks usage of ctokens (minting or withdrawing from obligation)
///                         Effectively blocks deposit_reserve_liquidity and withdraw_obligation_collateral
/// @param reserved1 Past reserved space - feel free to reuse.
/// @param protocolOrderExecutionFeePct Cut of the order execution bonus that the protocol receives, as a percentage
/// @param protocolTakeRatePct Protocol take rate is the amount borrowed interest protocol receives, as a percentage
/// @param protocolLiquidationFeePct Cut of the liquidation bonus that the protocol receives, as a percentage
/// @param loanToValuePct Target ratio of the value of borrows to deposits, as a percentage
///                       0 if use as collateral is disabled
/// @param liquidationThresholdPct Loan to value ratio at which an obligation can be liquidated, as percentage
/// @param minLiquidationBonusBps Minimum bonus a liquidator receives when repaying part of an unhealthy obligation, as bps
/// @param maxLiquidationBonusBps Maximum bonus a liquidator receives when repaying part of an unhealthy obligation, as bps
/// @param badDebtLiquidationBonusBps Bad debt liquidation bonus for an undercollateralized obligation, as bps
/// @param deleveragingMarginCallPeriodSecs Time in seconds that must pass before redemptions are enabled after the deposit limit is
///                                         crossed.
///                                         Only relevant when `autodeleverage_enabled == 1`, and must not be 0 in such case.
/// @param deleveragingThresholdDecreaseBpsPerDay The rate at which the deleveraging threshold decreases, in bps per day.
///                                               Only relevant when `autodeleverage_enabled == 1`, and must not be 0 in such case.
/// @param fees Program owner fees assessed, separate from gains due to interest accrual
/// @param borrowRateCurve Borrow rate curve based on utilization
/// @param borrowFactorPct Borrow factor in percentage - used for risk adjustment
/// @param depositLimit Maximum deposit limit of liquidity in native units, u64::MAX for inf
/// @param borrowLimit Maximum amount borrowed, u64::MAX for inf, 0 to disable borrows (protected deposits)
/// @param tokenInfo Token id from TokenInfos struct
/// @param depositWithdrawalCap Deposit withdrawal caps - deposit & redeem
/// @param debtWithdrawalCap Debt withdrawal caps - borrow & repay
/// @param utilizationLimitBlockBorrowingAbovePct Utilization (in percentage) above which borrowing is blocked. 0 to disable.
/// @param autodeleverageEnabled Whether this reserve should be subject to auto-deleveraging after deposit or borrow limit is
///                              crossed.
///                              Besides this flag, the lending market's flag also needs to be enabled (logical `AND`).
///                              **NOTE:** the manual "target LTV" deleveraging (enabled by the risk council for individual
///                              obligations) is NOT affected by this flag.
/// @param proposerAuthorityLocked Boolean flag indicating whether the reserve is locked for the proposer authority.
///                                
///                                Once the proposer have finished preparing the reserve, it must be locked to prevent
///                                further changes to the reserve configuration allowing review and voting on the proposal
///                                without alteration during the voting period.
/// @param borrowLimitOutsideElevationGroup Maximum amount liquidity of this reserve borrowed outside all elevation groups
///                                         - u64::MAX for inf
///                                         - 0 to disable borrows outside elevation groups
/// @param borrowLimitAgainstThisCollateralInElevationGroup Defines the maximum amount (in lamports of elevation group debt asset)
///                                                         that can be borrowed when this reserve is used as collateral.
///                                                         - u64::MAX for inf
///                                                         - 0 to disable borrows in this elevation group (expected value for the debt asset)
/// @param deleveragingBonusIncreaseBpsPerDay The rate at which the deleveraging-related liquidation bonus increases, in bps per day.
///                                           Only relevant when `autodeleverage_enabled == 1`, and must not be 0 in such case.
public record ReserveConfig(int status,
                            int assetTier,
                            int hostFixedInterestRateBps,
                            int minDeleveragingBonusBps,
                            int blockCtokenUsage,
                            byte[] reserved1,
                            int protocolOrderExecutionFeePct,
                            int protocolTakeRatePct,
                            int protocolLiquidationFeePct,
                            int loanToValuePct,
                            int liquidationThresholdPct,
                            int minLiquidationBonusBps,
                            int maxLiquidationBonusBps,
                            int badDebtLiquidationBonusBps,
                            long deleveragingMarginCallPeriodSecs,
                            long deleveragingThresholdDecreaseBpsPerDay,
                            ReserveFees fees,
                            BorrowRateCurve borrowRateCurve,
                            long borrowFactorPct,
                            long depositLimit,
                            long borrowLimit,
                            TokenInfo tokenInfo,
                            WithdrawalCaps depositWithdrawalCap,
                            WithdrawalCaps debtWithdrawalCap,
                            byte[] elevationGroups,
                            int disableUsageAsCollOutsideEmode,
                            int utilizationLimitBlockBorrowingAbovePct,
                            int autodeleverageEnabled,
                            int proposerAuthorityLocked,
                            long borrowLimitOutsideElevationGroup,
                            long[] borrowLimitAgainstThisCollateralInElevationGroup,
                            long deleveragingBonusIncreaseBpsPerDay) implements Borsh {

  public static final int BYTES = 920;
  public static final int RESERVED_1_LEN = 6;
  public static final int ELEVATION_GROUPS_LEN = 20;
  public static final int BORROW_LIMIT_AGAINST_THIS_COLLATERAL_IN_ELEVATION_GROUP_LEN = 32;

  public static ReserveConfig read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var status = _data[i] & 0xFF;
    ++i;
    final var assetTier = _data[i] & 0xFF;
    ++i;
    final var hostFixedInterestRateBps = getInt16LE(_data, i);
    i += 2;
    final var minDeleveragingBonusBps = getInt16LE(_data, i);
    i += 2;
    final var blockCtokenUsage = _data[i] & 0xFF;
    ++i;
    final var reserved1 = new byte[6];
    i += Borsh.readArray(reserved1, _data, i);
    final var protocolOrderExecutionFeePct = _data[i] & 0xFF;
    ++i;
    final var protocolTakeRatePct = _data[i] & 0xFF;
    ++i;
    final var protocolLiquidationFeePct = _data[i] & 0xFF;
    ++i;
    final var loanToValuePct = _data[i] & 0xFF;
    ++i;
    final var liquidationThresholdPct = _data[i] & 0xFF;
    ++i;
    final var minLiquidationBonusBps = getInt16LE(_data, i);
    i += 2;
    final var maxLiquidationBonusBps = getInt16LE(_data, i);
    i += 2;
    final var badDebtLiquidationBonusBps = getInt16LE(_data, i);
    i += 2;
    final var deleveragingMarginCallPeriodSecs = getInt64LE(_data, i);
    i += 8;
    final var deleveragingThresholdDecreaseBpsPerDay = getInt64LE(_data, i);
    i += 8;
    final var fees = ReserveFees.read(_data, i);
    i += fees.l();
    final var borrowRateCurve = BorrowRateCurve.read(_data, i);
    i += borrowRateCurve.l();
    final var borrowFactorPct = getInt64LE(_data, i);
    i += 8;
    final var depositLimit = getInt64LE(_data, i);
    i += 8;
    final var borrowLimit = getInt64LE(_data, i);
    i += 8;
    final var tokenInfo = TokenInfo.read(_data, i);
    i += tokenInfo.l();
    final var depositWithdrawalCap = WithdrawalCaps.read(_data, i);
    i += depositWithdrawalCap.l();
    final var debtWithdrawalCap = WithdrawalCaps.read(_data, i);
    i += debtWithdrawalCap.l();
    final var elevationGroups = new byte[20];
    i += Borsh.readArray(elevationGroups, _data, i);
    final var disableUsageAsCollOutsideEmode = _data[i] & 0xFF;
    ++i;
    final var utilizationLimitBlockBorrowingAbovePct = _data[i] & 0xFF;
    ++i;
    final var autodeleverageEnabled = _data[i] & 0xFF;
    ++i;
    final var proposerAuthorityLocked = _data[i] & 0xFF;
    ++i;
    final var borrowLimitOutsideElevationGroup = getInt64LE(_data, i);
    i += 8;
    final var borrowLimitAgainstThisCollateralInElevationGroup = new long[32];
    i += Borsh.readArray(borrowLimitAgainstThisCollateralInElevationGroup, _data, i);
    final var deleveragingBonusIncreaseBpsPerDay = getInt64LE(_data, i);
    return new ReserveConfig(status,
                             assetTier,
                             hostFixedInterestRateBps,
                             minDeleveragingBonusBps,
                             blockCtokenUsage,
                             reserved1,
                             protocolOrderExecutionFeePct,
                             protocolTakeRatePct,
                             protocolLiquidationFeePct,
                             loanToValuePct,
                             liquidationThresholdPct,
                             minLiquidationBonusBps,
                             maxLiquidationBonusBps,
                             badDebtLiquidationBonusBps,
                             deleveragingMarginCallPeriodSecs,
                             deleveragingThresholdDecreaseBpsPerDay,
                             fees,
                             borrowRateCurve,
                             borrowFactorPct,
                             depositLimit,
                             borrowLimit,
                             tokenInfo,
                             depositWithdrawalCap,
                             debtWithdrawalCap,
                             elevationGroups,
                             disableUsageAsCollOutsideEmode,
                             utilizationLimitBlockBorrowingAbovePct,
                             autodeleverageEnabled,
                             proposerAuthorityLocked,
                             borrowLimitOutsideElevationGroup,
                             borrowLimitAgainstThisCollateralInElevationGroup,
                             deleveragingBonusIncreaseBpsPerDay);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    _data[i] = (byte) status;
    ++i;
    _data[i] = (byte) assetTier;
    ++i;
    putInt16LE(_data, i, hostFixedInterestRateBps);
    i += 2;
    putInt16LE(_data, i, minDeleveragingBonusBps);
    i += 2;
    _data[i] = (byte) blockCtokenUsage;
    ++i;
    i += Borsh.writeArrayChecked(reserved1, 6, _data, i);
    _data[i] = (byte) protocolOrderExecutionFeePct;
    ++i;
    _data[i] = (byte) protocolTakeRatePct;
    ++i;
    _data[i] = (byte) protocolLiquidationFeePct;
    ++i;
    _data[i] = (byte) loanToValuePct;
    ++i;
    _data[i] = (byte) liquidationThresholdPct;
    ++i;
    putInt16LE(_data, i, minLiquidationBonusBps);
    i += 2;
    putInt16LE(_data, i, maxLiquidationBonusBps);
    i += 2;
    putInt16LE(_data, i, badDebtLiquidationBonusBps);
    i += 2;
    putInt64LE(_data, i, deleveragingMarginCallPeriodSecs);
    i += 8;
    putInt64LE(_data, i, deleveragingThresholdDecreaseBpsPerDay);
    i += 8;
    i += fees.write(_data, i);
    i += borrowRateCurve.write(_data, i);
    putInt64LE(_data, i, borrowFactorPct);
    i += 8;
    putInt64LE(_data, i, depositLimit);
    i += 8;
    putInt64LE(_data, i, borrowLimit);
    i += 8;
    i += tokenInfo.write(_data, i);
    i += depositWithdrawalCap.write(_data, i);
    i += debtWithdrawalCap.write(_data, i);
    i += Borsh.writeArrayChecked(elevationGroups, 20, _data, i);
    _data[i] = (byte) disableUsageAsCollOutsideEmode;
    ++i;
    _data[i] = (byte) utilizationLimitBlockBorrowingAbovePct;
    ++i;
    _data[i] = (byte) autodeleverageEnabled;
    ++i;
    _data[i] = (byte) proposerAuthorityLocked;
    ++i;
    putInt64LE(_data, i, borrowLimitOutsideElevationGroup);
    i += 8;
    i += Borsh.writeArrayChecked(borrowLimitAgainstThisCollateralInElevationGroup, 32, _data, i);
    putInt64LE(_data, i, deleveragingBonusIncreaseBpsPerDay);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

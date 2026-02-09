package software.sava.idl.clients.kamino.lend.gen.types;

import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.encoding.ByteUtil.getInt16LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt16LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

/// Reserve configuration values
///
/// @param status Status of the reserve Active/Obsolete/Hidden
/// @param paddingDeprecatedAssetTier Asset tier -> 0 - regular (collateral & debt), 1 - isolated collateral, 2 - isolated debt
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
///                              **NOTE:** the manual "target LTV" deleveraging is NOT affected by this flag.
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
/// @param debtMaturityTimestamp The timestamp at which all Obligation::borrows using this reserve become liquidatable
///                              (on the same terms as reserve-wide deleveraging).
///                              Inactive when zeroed (i.e. debt never matures).
///                              
///                              Note: this feature is independent of Self::debt_term_seconds - the liquidation mechanism
///                              is based directly on the timestamp defined here, on Reserve's level.
/// @param debtTermSeconds The duration after which any debt coming from this Reserve must be repaid.
///                        Inactive when zeroed (i.e. funds can be borrowed indefinitely).
///                        
///                        Note: this feature is independent of Self::debt_maturity_timestamp - the liquidation
///                        mechanism is based on the ObligationLiquidity::first_borrowed_at_timestamp.
public record ReserveConfig(int status,
                            int paddingDeprecatedAssetTier,
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
                            long deleveragingBonusIncreaseBpsPerDay,
                            long debtMaturityTimestamp,
                            long debtTermSeconds) implements SerDe {

  public static final int BYTES = 936;
  public static final int RESERVED_1_LEN = 6;
  public static final int ELEVATION_GROUPS_LEN = 20;
  public static final int BORROW_LIMIT_AGAINST_THIS_COLLATERAL_IN_ELEVATION_GROUP_LEN = 32;

  public static final int STATUS_OFFSET = 0;
  public static final int PADDING_DEPRECATED_ASSET_TIER_OFFSET = 1;
  public static final int HOST_FIXED_INTEREST_RATE_BPS_OFFSET = 2;
  public static final int MIN_DELEVERAGING_BONUS_BPS_OFFSET = 4;
  public static final int BLOCK_CTOKEN_USAGE_OFFSET = 6;
  public static final int RESERVED_1_OFFSET = 7;
  public static final int PROTOCOL_ORDER_EXECUTION_FEE_PCT_OFFSET = 13;
  public static final int PROTOCOL_TAKE_RATE_PCT_OFFSET = 14;
  public static final int PROTOCOL_LIQUIDATION_FEE_PCT_OFFSET = 15;
  public static final int LOAN_TO_VALUE_PCT_OFFSET = 16;
  public static final int LIQUIDATION_THRESHOLD_PCT_OFFSET = 17;
  public static final int MIN_LIQUIDATION_BONUS_BPS_OFFSET = 18;
  public static final int MAX_LIQUIDATION_BONUS_BPS_OFFSET = 20;
  public static final int BAD_DEBT_LIQUIDATION_BONUS_BPS_OFFSET = 22;
  public static final int DELEVERAGING_MARGIN_CALL_PERIOD_SECS_OFFSET = 24;
  public static final int DELEVERAGING_THRESHOLD_DECREASE_BPS_PER_DAY_OFFSET = 32;
  public static final int FEES_OFFSET = 40;
  public static final int BORROW_RATE_CURVE_OFFSET = 64;
  public static final int BORROW_FACTOR_PCT_OFFSET = 152;
  public static final int DEPOSIT_LIMIT_OFFSET = 160;
  public static final int BORROW_LIMIT_OFFSET = 168;
  public static final int TOKEN_INFO_OFFSET = 176;
  public static final int DEPOSIT_WITHDRAWAL_CAP_OFFSET = 560;
  public static final int DEBT_WITHDRAWAL_CAP_OFFSET = 592;
  public static final int ELEVATION_GROUPS_OFFSET = 624;
  public static final int DISABLE_USAGE_AS_COLL_OUTSIDE_EMODE_OFFSET = 644;
  public static final int UTILIZATION_LIMIT_BLOCK_BORROWING_ABOVE_PCT_OFFSET = 645;
  public static final int AUTODELEVERAGE_ENABLED_OFFSET = 646;
  public static final int PROPOSER_AUTHORITY_LOCKED_OFFSET = 647;
  public static final int BORROW_LIMIT_OUTSIDE_ELEVATION_GROUP_OFFSET = 648;
  public static final int BORROW_LIMIT_AGAINST_THIS_COLLATERAL_IN_ELEVATION_GROUP_OFFSET = 656;
  public static final int DELEVERAGING_BONUS_INCREASE_BPS_PER_DAY_OFFSET = 912;
  public static final int DEBT_MATURITY_TIMESTAMP_OFFSET = 920;
  public static final int DEBT_TERM_SECONDS_OFFSET = 928;

  public static ReserveConfig read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var status = _data[i] & 0xFF;
    ++i;
    final var paddingDeprecatedAssetTier = _data[i] & 0xFF;
    ++i;
    final var hostFixedInterestRateBps = getInt16LE(_data, i);
    i += 2;
    final var minDeleveragingBonusBps = getInt16LE(_data, i);
    i += 2;
    final var blockCtokenUsage = _data[i] & 0xFF;
    ++i;
    final var reserved1 = new byte[6];
    i += SerDeUtil.readArray(reserved1, _data, i);
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
    i += SerDeUtil.readArray(elevationGroups, _data, i);
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
    i += SerDeUtil.readArray(borrowLimitAgainstThisCollateralInElevationGroup, _data, i);
    final var deleveragingBonusIncreaseBpsPerDay = getInt64LE(_data, i);
    i += 8;
    final var debtMaturityTimestamp = getInt64LE(_data, i);
    i += 8;
    final var debtTermSeconds = getInt64LE(_data, i);
    return new ReserveConfig(status,
                             paddingDeprecatedAssetTier,
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
                             deleveragingBonusIncreaseBpsPerDay,
                             debtMaturityTimestamp,
                             debtTermSeconds);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    _data[i] = (byte) status;
    ++i;
    _data[i] = (byte) paddingDeprecatedAssetTier;
    ++i;
    putInt16LE(_data, i, hostFixedInterestRateBps);
    i += 2;
    putInt16LE(_data, i, minDeleveragingBonusBps);
    i += 2;
    _data[i] = (byte) blockCtokenUsage;
    ++i;
    i += SerDeUtil.writeArrayChecked(reserved1, 6, _data, i);
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
    i += SerDeUtil.writeArrayChecked(elevationGroups, 20, _data, i);
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
    i += SerDeUtil.writeArrayChecked(borrowLimitAgainstThisCollateralInElevationGroup, 32, _data, i);
    putInt64LE(_data, i, deleveragingBonusIncreaseBpsPerDay);
    i += 8;
    putInt64LE(_data, i, debtMaturityTimestamp);
    i += 8;
    putInt64LE(_data, i, debtTermSeconds);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

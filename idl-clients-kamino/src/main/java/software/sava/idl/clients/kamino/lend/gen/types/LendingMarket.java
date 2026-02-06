package software.sava.idl.clients.kamino.lend.gen.types;

import java.math.BigInteger;

import java.util.function.BiFunction;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;
import software.sava.core.rpc.Filter;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;
import software.sava.rpc.json.http.response.AccountInfo;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt128LE;
import static software.sava.core.encoding.ByteUtil.getInt16LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt128LE;
import static software.sava.core.encoding.ByteUtil.putInt16LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

/// @param version Version of lending market
/// @param bumpSeed Bump seed for derived authority address
/// @param lendingMarketOwner Owner authority which can add new reserves
/// @param lendingMarketOwnerCached Temporary cache of the lending market owner, used in update_lending_market_owner
/// @param quoteCurrency Currency market prices are quoted in
///                      e.g. "USD" null padded (`*b"USD\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0"`) or a SPL token mint pubkey
/// @param referralFeeBps Referral fee for the lending market, as bps out of the total protocol fee
/// @param autodeleverageEnabled Whether the obligations on this market should be subject to auto-deleveraging after deposit
///                              or borrow limit is crossed.
///                              Besides this flag, the particular reserve's flag also needs to be enabled (logical `AND`).
///                              **NOTE:** this also affects the individual "target LTV" deleveraging.
/// @param priceRefreshTriggerToMaxAgePct Refresh price from oracle only if it's older than this percentage of the price max age.
///                                       e.g. if the max age is set to 100s and this is set to 80%, the price will be refreshed if it's older than 80s.
///                                       Price is always refreshed if this set to 0.
/// @param liquidationMaxDebtCloseFactorPct Percentage of the total borrowed value in an obligation available for liquidation
/// @param insolvencyRiskUnhealthyLtvPct Minimum acceptable unhealthy LTV before max_debt_close_factor_pct becomes 100%
/// @param minFullLiquidationValueThreshold Minimum liquidation value threshold triggering full liquidation for an obligation
/// @param maxLiquidatableDebtMarketValueAtOnce Max allowed liquidation value in one ix call
/// @param reserved0 DEPRECATED Global maximum unhealthy borrow value allowed for any obligation
/// @param globalAllowedBorrowValue Global maximum allowed borrow value allowed for any obligation
/// @param riskCouncil The address of the risk council, in charge of making parameter and risk decisions on behalf of the protocol
/// @param reserved1 DEPRECATED Reward points multiplier per obligation type
/// @param elevationGroups Elevation groups are used to group together reserves that have the same risk parameters and can bump the ltv and liquidation threshold
/// @param minNetValueInObligationSf Min net value accepted to be found in a position after any lending action in an obligation (scaled by quote currency decimals)
/// @param minValueSkipLiquidationLtvChecks Minimum value to enforce smallest ltv priority checks on the collateral reserves on liquidation
/// @param name Market name, zero-padded.
/// @param minValueSkipLiquidationBfChecks Minimum value to enforce highest borrow factor priority checks on the debt reserves on liquidation
/// @param individualAutodeleverageMarginCallPeriodSecs Time (in seconds) that must pass before liquidation is allowed on an obligation that has
///                                                     been individually marked for auto-deleveraging (by the risk council).
/// @param minInitialDepositAmount Minimum amount of deposit at creation of a reserve to prevent artificial inflation
///                                Note: this amount cannot be recovered, the ctoken associated are never minted
/// @param obligationOrderExecutionEnabled Whether the obligation orders should be evaluated during liquidations.
/// @param immutable Whether the lending market is set as immutable.
/// @param obligationOrderCreationEnabled Whether new obligation orders can be created.
///                                       Note: updating or cancelling existing orders is *not* affected by this flag.
/// @param priceTriggeredLiquidationDisabled Whether the liquidation operations that are triggered by price changes should be disabled.
///                                          This includes regular liquidation (i.e. LTV exceeding the unhealthy threshold) and some
///                                          obligation orders' execution.
///                                          
///                                          *Caution:* this flag is *disabling* the liquidations when `1` - contrary to all the other
///                                          liquidation-driving flags (see e.g. Self::autodeleverage_enabled).
/// @param matureReserveDebtLiquidationEnabled Whether the debts that reached their reserve's ReserveConfig::debt_maturity_timestamp can
///                                            be liquidated.
/// @param obligationBorrowDebtTermLiquidationEnabled Whether the Obligation::borrows that reached their ReserveConfig::debt_term_seconds can
///                                                   be liquidated.
/// @param borrowOrderCreationEnabled Whether new borrow orders can be created.
///                                   Note: updating or cancelling existing orders is *not* affected by this flag.
/// @param borrowOrderExecutionEnabled Whether the existing borrow orders can be filled.
/// @param proposerAuthority Authority that can propose creating of new reserves but cannot enable them.
/// @param minBorrowOrderFillValue Minimum value that can be filled in a single `fill_borrow_order()` call, in full units of
///                                the quote currency (e.g. `2` means "$2", not "2 lamports of USDC").
public record LendingMarket(PublicKey _address,
                            Discriminator discriminator,
                            long version,
                            long bumpSeed,
                            PublicKey lendingMarketOwner,
                            PublicKey lendingMarketOwnerCached,
                            byte[] quoteCurrency,
                            int referralFeeBps,
                            int emergencyMode,
                            int autodeleverageEnabled,
                            int borrowDisabled,
                            int priceRefreshTriggerToMaxAgePct,
                            int liquidationMaxDebtCloseFactorPct,
                            int insolvencyRiskUnhealthyLtvPct,
                            long minFullLiquidationValueThreshold,
                            long maxLiquidatableDebtMarketValueAtOnce,
                            byte[] reserved0,
                            long globalAllowedBorrowValue,
                            PublicKey riskCouncil,
                            byte[] reserved1,
                            ElevationGroup[] elevationGroups,
                            long[] elevationGroupPadding,
                            BigInteger minNetValueInObligationSf,
                            long minValueSkipLiquidationLtvChecks,
                            byte[] name,
                            long minValueSkipLiquidationBfChecks,
                            long individualAutodeleverageMarginCallPeriodSecs,
                            long minInitialDepositAmount,
                            int obligationOrderExecutionEnabled,
                            int immutable,
                            int obligationOrderCreationEnabled,
                            int priceTriggeredLiquidationDisabled,
                            int matureReserveDebtLiquidationEnabled,
                            int obligationBorrowDebtTermLiquidationEnabled,
                            int borrowOrderCreationEnabled,
                            int borrowOrderExecutionEnabled,
                            PublicKey proposerAuthority,
                            long minBorrowOrderFillValue,
                            long[] padding1) implements SerDe {

  public static final int BYTES = 4664;
  public static final int QUOTE_CURRENCY_LEN = 32;
  public static final int RESERVED_0_LEN = 8;
  public static final int RESERVED_1_LEN = 8;
  public static final int ELEVATION_GROUPS_LEN = 32;
  public static final int ELEVATION_GROUP_PADDING_LEN = 90;
  public static final int NAME_LEN = 32;
  public static final int PADDING_1_LEN = 164;
  public static final Filter SIZE_FILTER = Filter.createDataSizeFilter(BYTES);

  public static final Discriminator DISCRIMINATOR = toDiscriminator(246, 114, 50, 98, 72, 157, 28, 120);
  public static final Filter DISCRIMINATOR_FILTER = Filter.createMemCompFilter(0, DISCRIMINATOR.data());

  public static final int VERSION_OFFSET = 8;
  public static final int BUMP_SEED_OFFSET = 16;
  public static final int LENDING_MARKET_OWNER_OFFSET = 24;
  public static final int LENDING_MARKET_OWNER_CACHED_OFFSET = 56;
  public static final int QUOTE_CURRENCY_OFFSET = 88;
  public static final int REFERRAL_FEE_BPS_OFFSET = 120;
  public static final int EMERGENCY_MODE_OFFSET = 122;
  public static final int AUTODELEVERAGE_ENABLED_OFFSET = 123;
  public static final int BORROW_DISABLED_OFFSET = 124;
  public static final int PRICE_REFRESH_TRIGGER_TO_MAX_AGE_PCT_OFFSET = 125;
  public static final int LIQUIDATION_MAX_DEBT_CLOSE_FACTOR_PCT_OFFSET = 126;
  public static final int INSOLVENCY_RISK_UNHEALTHY_LTV_PCT_OFFSET = 127;
  public static final int MIN_FULL_LIQUIDATION_VALUE_THRESHOLD_OFFSET = 128;
  public static final int MAX_LIQUIDATABLE_DEBT_MARKET_VALUE_AT_ONCE_OFFSET = 136;
  public static final int RESERVED_0_OFFSET = 144;
  public static final int GLOBAL_ALLOWED_BORROW_VALUE_OFFSET = 152;
  public static final int RISK_COUNCIL_OFFSET = 160;
  public static final int RESERVED_1_OFFSET = 192;
  public static final int ELEVATION_GROUPS_OFFSET = 200;
  public static final int ELEVATION_GROUP_PADDING_OFFSET = 2504;
  public static final int MIN_NET_VALUE_IN_OBLIGATION_SF_OFFSET = 3224;
  public static final int MIN_VALUE_SKIP_LIQUIDATION_LTV_CHECKS_OFFSET = 3240;
  public static final int NAME_OFFSET = 3248;
  public static final int MIN_VALUE_SKIP_LIQUIDATION_BF_CHECKS_OFFSET = 3280;
  public static final int INDIVIDUAL_AUTODELEVERAGE_MARGIN_CALL_PERIOD_SECS_OFFSET = 3288;
  public static final int MIN_INITIAL_DEPOSIT_AMOUNT_OFFSET = 3296;
  public static final int OBLIGATION_ORDER_EXECUTION_ENABLED_OFFSET = 3304;
  public static final int IMMUTABLE_OFFSET = 3305;
  public static final int OBLIGATION_ORDER_CREATION_ENABLED_OFFSET = 3306;
  public static final int PRICE_TRIGGERED_LIQUIDATION_DISABLED_OFFSET = 3307;
  public static final int MATURE_RESERVE_DEBT_LIQUIDATION_ENABLED_OFFSET = 3308;
  public static final int OBLIGATION_BORROW_DEBT_TERM_LIQUIDATION_ENABLED_OFFSET = 3309;
  public static final int BORROW_ORDER_CREATION_ENABLED_OFFSET = 3310;
  public static final int BORROW_ORDER_EXECUTION_ENABLED_OFFSET = 3311;
  public static final int PROPOSER_AUTHORITY_OFFSET = 3312;
  public static final int MIN_BORROW_ORDER_FILL_VALUE_OFFSET = 3344;
  public static final int PADDING_1_OFFSET = 3352;

  public static Filter createVersionFilter(final long version) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, version);
    return Filter.createMemCompFilter(VERSION_OFFSET, _data);
  }

  public static Filter createBumpSeedFilter(final long bumpSeed) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, bumpSeed);
    return Filter.createMemCompFilter(BUMP_SEED_OFFSET, _data);
  }

  public static Filter createLendingMarketOwnerFilter(final PublicKey lendingMarketOwner) {
    return Filter.createMemCompFilter(LENDING_MARKET_OWNER_OFFSET, lendingMarketOwner);
  }

  public static Filter createLendingMarketOwnerCachedFilter(final PublicKey lendingMarketOwnerCached) {
    return Filter.createMemCompFilter(LENDING_MARKET_OWNER_CACHED_OFFSET, lendingMarketOwnerCached);
  }

  public static Filter createReferralFeeBpsFilter(final int referralFeeBps) {
    final byte[] _data = new byte[2];
    putInt16LE(_data, 0, referralFeeBps);
    return Filter.createMemCompFilter(REFERRAL_FEE_BPS_OFFSET, _data);
  }

  public static Filter createEmergencyModeFilter(final int emergencyMode) {
    return Filter.createMemCompFilter(EMERGENCY_MODE_OFFSET, new byte[]{(byte) emergencyMode});
  }

  public static Filter createAutodeleverageEnabledFilter(final int autodeleverageEnabled) {
    return Filter.createMemCompFilter(AUTODELEVERAGE_ENABLED_OFFSET, new byte[]{(byte) autodeleverageEnabled});
  }

  public static Filter createBorrowDisabledFilter(final int borrowDisabled) {
    return Filter.createMemCompFilter(BORROW_DISABLED_OFFSET, new byte[]{(byte) borrowDisabled});
  }

  public static Filter createPriceRefreshTriggerToMaxAgePctFilter(final int priceRefreshTriggerToMaxAgePct) {
    return Filter.createMemCompFilter(PRICE_REFRESH_TRIGGER_TO_MAX_AGE_PCT_OFFSET, new byte[]{(byte) priceRefreshTriggerToMaxAgePct});
  }

  public static Filter createLiquidationMaxDebtCloseFactorPctFilter(final int liquidationMaxDebtCloseFactorPct) {
    return Filter.createMemCompFilter(LIQUIDATION_MAX_DEBT_CLOSE_FACTOR_PCT_OFFSET, new byte[]{(byte) liquidationMaxDebtCloseFactorPct});
  }

  public static Filter createInsolvencyRiskUnhealthyLtvPctFilter(final int insolvencyRiskUnhealthyLtvPct) {
    return Filter.createMemCompFilter(INSOLVENCY_RISK_UNHEALTHY_LTV_PCT_OFFSET, new byte[]{(byte) insolvencyRiskUnhealthyLtvPct});
  }

  public static Filter createMinFullLiquidationValueThresholdFilter(final long minFullLiquidationValueThreshold) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, minFullLiquidationValueThreshold);
    return Filter.createMemCompFilter(MIN_FULL_LIQUIDATION_VALUE_THRESHOLD_OFFSET, _data);
  }

  public static Filter createMaxLiquidatableDebtMarketValueAtOnceFilter(final long maxLiquidatableDebtMarketValueAtOnce) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, maxLiquidatableDebtMarketValueAtOnce);
    return Filter.createMemCompFilter(MAX_LIQUIDATABLE_DEBT_MARKET_VALUE_AT_ONCE_OFFSET, _data);
  }

  public static Filter createGlobalAllowedBorrowValueFilter(final long globalAllowedBorrowValue) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, globalAllowedBorrowValue);
    return Filter.createMemCompFilter(GLOBAL_ALLOWED_BORROW_VALUE_OFFSET, _data);
  }

  public static Filter createRiskCouncilFilter(final PublicKey riskCouncil) {
    return Filter.createMemCompFilter(RISK_COUNCIL_OFFSET, riskCouncil);
  }

  public static Filter createMinNetValueInObligationSfFilter(final BigInteger minNetValueInObligationSf) {
    final byte[] _data = new byte[16];
    putInt128LE(_data, 0, minNetValueInObligationSf);
    return Filter.createMemCompFilter(MIN_NET_VALUE_IN_OBLIGATION_SF_OFFSET, _data);
  }

  public static Filter createMinValueSkipLiquidationLtvChecksFilter(final long minValueSkipLiquidationLtvChecks) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, minValueSkipLiquidationLtvChecks);
    return Filter.createMemCompFilter(MIN_VALUE_SKIP_LIQUIDATION_LTV_CHECKS_OFFSET, _data);
  }

  public static Filter createMinValueSkipLiquidationBfChecksFilter(final long minValueSkipLiquidationBfChecks) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, minValueSkipLiquidationBfChecks);
    return Filter.createMemCompFilter(MIN_VALUE_SKIP_LIQUIDATION_BF_CHECKS_OFFSET, _data);
  }

  public static Filter createIndividualAutodeleverageMarginCallPeriodSecsFilter(final long individualAutodeleverageMarginCallPeriodSecs) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, individualAutodeleverageMarginCallPeriodSecs);
    return Filter.createMemCompFilter(INDIVIDUAL_AUTODELEVERAGE_MARGIN_CALL_PERIOD_SECS_OFFSET, _data);
  }

  public static Filter createMinInitialDepositAmountFilter(final long minInitialDepositAmount) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, minInitialDepositAmount);
    return Filter.createMemCompFilter(MIN_INITIAL_DEPOSIT_AMOUNT_OFFSET, _data);
  }

  public static Filter createObligationOrderExecutionEnabledFilter(final int obligationOrderExecutionEnabled) {
    return Filter.createMemCompFilter(OBLIGATION_ORDER_EXECUTION_ENABLED_OFFSET, new byte[]{(byte) obligationOrderExecutionEnabled});
  }

  public static Filter createImmutableFilter(final int immutable) {
    return Filter.createMemCompFilter(IMMUTABLE_OFFSET, new byte[]{(byte) immutable});
  }

  public static Filter createObligationOrderCreationEnabledFilter(final int obligationOrderCreationEnabled) {
    return Filter.createMemCompFilter(OBLIGATION_ORDER_CREATION_ENABLED_OFFSET, new byte[]{(byte) obligationOrderCreationEnabled});
  }

  public static Filter createPriceTriggeredLiquidationDisabledFilter(final int priceTriggeredLiquidationDisabled) {
    return Filter.createMemCompFilter(PRICE_TRIGGERED_LIQUIDATION_DISABLED_OFFSET, new byte[]{(byte) priceTriggeredLiquidationDisabled});
  }

  public static Filter createMatureReserveDebtLiquidationEnabledFilter(final int matureReserveDebtLiquidationEnabled) {
    return Filter.createMemCompFilter(MATURE_RESERVE_DEBT_LIQUIDATION_ENABLED_OFFSET, new byte[]{(byte) matureReserveDebtLiquidationEnabled});
  }

  public static Filter createObligationBorrowDebtTermLiquidationEnabledFilter(final int obligationBorrowDebtTermLiquidationEnabled) {
    return Filter.createMemCompFilter(OBLIGATION_BORROW_DEBT_TERM_LIQUIDATION_ENABLED_OFFSET, new byte[]{(byte) obligationBorrowDebtTermLiquidationEnabled});
  }

  public static Filter createBorrowOrderCreationEnabledFilter(final int borrowOrderCreationEnabled) {
    return Filter.createMemCompFilter(BORROW_ORDER_CREATION_ENABLED_OFFSET, new byte[]{(byte) borrowOrderCreationEnabled});
  }

  public static Filter createBorrowOrderExecutionEnabledFilter(final int borrowOrderExecutionEnabled) {
    return Filter.createMemCompFilter(BORROW_ORDER_EXECUTION_ENABLED_OFFSET, new byte[]{(byte) borrowOrderExecutionEnabled});
  }

  public static Filter createProposerAuthorityFilter(final PublicKey proposerAuthority) {
    return Filter.createMemCompFilter(PROPOSER_AUTHORITY_OFFSET, proposerAuthority);
  }

  public static Filter createMinBorrowOrderFillValueFilter(final long minBorrowOrderFillValue) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, minBorrowOrderFillValue);
    return Filter.createMemCompFilter(MIN_BORROW_ORDER_FILL_VALUE_OFFSET, _data);
  }

  public static LendingMarket read(final byte[] _data, final int _offset) {
    return read(null, _data, _offset);
  }

  public static LendingMarket read(final AccountInfo<byte[]> accountInfo) {
    return read(accountInfo.pubKey(), accountInfo.data(), 0);
  }

  public static LendingMarket read(final PublicKey _address, final byte[] _data) {
    return read(_address, _data, 0);
  }

  public static final BiFunction<PublicKey, byte[], LendingMarket> FACTORY = LendingMarket::read;

  public static LendingMarket read(final PublicKey _address, final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var version = getInt64LE(_data, i);
    i += 8;
    final var bumpSeed = getInt64LE(_data, i);
    i += 8;
    final var lendingMarketOwner = readPubKey(_data, i);
    i += 32;
    final var lendingMarketOwnerCached = readPubKey(_data, i);
    i += 32;
    final var quoteCurrency = new byte[32];
    i += SerDeUtil.readArray(quoteCurrency, _data, i);
    final var referralFeeBps = getInt16LE(_data, i);
    i += 2;
    final var emergencyMode = _data[i] & 0xFF;
    ++i;
    final var autodeleverageEnabled = _data[i] & 0xFF;
    ++i;
    final var borrowDisabled = _data[i] & 0xFF;
    ++i;
    final var priceRefreshTriggerToMaxAgePct = _data[i] & 0xFF;
    ++i;
    final var liquidationMaxDebtCloseFactorPct = _data[i] & 0xFF;
    ++i;
    final var insolvencyRiskUnhealthyLtvPct = _data[i] & 0xFF;
    ++i;
    final var minFullLiquidationValueThreshold = getInt64LE(_data, i);
    i += 8;
    final var maxLiquidatableDebtMarketValueAtOnce = getInt64LE(_data, i);
    i += 8;
    final var reserved0 = new byte[8];
    i += SerDeUtil.readArray(reserved0, _data, i);
    final var globalAllowedBorrowValue = getInt64LE(_data, i);
    i += 8;
    final var riskCouncil = readPubKey(_data, i);
    i += 32;
    final var reserved1 = new byte[8];
    i += SerDeUtil.readArray(reserved1, _data, i);
    final var elevationGroups = new ElevationGroup[32];
    i += SerDeUtil.readArray(elevationGroups, ElevationGroup::read, _data, i);
    final var elevationGroupPadding = new long[90];
    i += SerDeUtil.readArray(elevationGroupPadding, _data, i);
    final var minNetValueInObligationSf = getInt128LE(_data, i);
    i += 16;
    final var minValueSkipLiquidationLtvChecks = getInt64LE(_data, i);
    i += 8;
    final var name = new byte[32];
    i += SerDeUtil.readArray(name, _data, i);
    final var minValueSkipLiquidationBfChecks = getInt64LE(_data, i);
    i += 8;
    final var individualAutodeleverageMarginCallPeriodSecs = getInt64LE(_data, i);
    i += 8;
    final var minInitialDepositAmount = getInt64LE(_data, i);
    i += 8;
    final var obligationOrderExecutionEnabled = _data[i] & 0xFF;
    ++i;
    final var immutable = _data[i] & 0xFF;
    ++i;
    final var obligationOrderCreationEnabled = _data[i] & 0xFF;
    ++i;
    final var priceTriggeredLiquidationDisabled = _data[i] & 0xFF;
    ++i;
    final var matureReserveDebtLiquidationEnabled = _data[i] & 0xFF;
    ++i;
    final var obligationBorrowDebtTermLiquidationEnabled = _data[i] & 0xFF;
    ++i;
    final var borrowOrderCreationEnabled = _data[i] & 0xFF;
    ++i;
    final var borrowOrderExecutionEnabled = _data[i] & 0xFF;
    ++i;
    final var proposerAuthority = readPubKey(_data, i);
    i += 32;
    final var minBorrowOrderFillValue = getInt64LE(_data, i);
    i += 8;
    final var padding1 = new long[164];
    SerDeUtil.readArray(padding1, _data, i);
    return new LendingMarket(_address,
                             discriminator,
                             version,
                             bumpSeed,
                             lendingMarketOwner,
                             lendingMarketOwnerCached,
                             quoteCurrency,
                             referralFeeBps,
                             emergencyMode,
                             autodeleverageEnabled,
                             borrowDisabled,
                             priceRefreshTriggerToMaxAgePct,
                             liquidationMaxDebtCloseFactorPct,
                             insolvencyRiskUnhealthyLtvPct,
                             minFullLiquidationValueThreshold,
                             maxLiquidatableDebtMarketValueAtOnce,
                             reserved0,
                             globalAllowedBorrowValue,
                             riskCouncil,
                             reserved1,
                             elevationGroups,
                             elevationGroupPadding,
                             minNetValueInObligationSf,
                             minValueSkipLiquidationLtvChecks,
                             name,
                             minValueSkipLiquidationBfChecks,
                             individualAutodeleverageMarginCallPeriodSecs,
                             minInitialDepositAmount,
                             obligationOrderExecutionEnabled,
                             immutable,
                             obligationOrderCreationEnabled,
                             priceTriggeredLiquidationDisabled,
                             matureReserveDebtLiquidationEnabled,
                             obligationBorrowDebtTermLiquidationEnabled,
                             borrowOrderCreationEnabled,
                             borrowOrderExecutionEnabled,
                             proposerAuthority,
                             minBorrowOrderFillValue,
                             padding1);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    putInt64LE(_data, i, version);
    i += 8;
    putInt64LE(_data, i, bumpSeed);
    i += 8;
    lendingMarketOwner.write(_data, i);
    i += 32;
    lendingMarketOwnerCached.write(_data, i);
    i += 32;
    i += SerDeUtil.writeArrayChecked(quoteCurrency, 32, _data, i);
    putInt16LE(_data, i, referralFeeBps);
    i += 2;
    _data[i] = (byte) emergencyMode;
    ++i;
    _data[i] = (byte) autodeleverageEnabled;
    ++i;
    _data[i] = (byte) borrowDisabled;
    ++i;
    _data[i] = (byte) priceRefreshTriggerToMaxAgePct;
    ++i;
    _data[i] = (byte) liquidationMaxDebtCloseFactorPct;
    ++i;
    _data[i] = (byte) insolvencyRiskUnhealthyLtvPct;
    ++i;
    putInt64LE(_data, i, minFullLiquidationValueThreshold);
    i += 8;
    putInt64LE(_data, i, maxLiquidatableDebtMarketValueAtOnce);
    i += 8;
    i += SerDeUtil.writeArrayChecked(reserved0, 8, _data, i);
    putInt64LE(_data, i, globalAllowedBorrowValue);
    i += 8;
    riskCouncil.write(_data, i);
    i += 32;
    i += SerDeUtil.writeArrayChecked(reserved1, 8, _data, i);
    i += SerDeUtil.writeArrayChecked(elevationGroups, 32, _data, i);
    i += SerDeUtil.writeArrayChecked(elevationGroupPadding, 90, _data, i);
    putInt128LE(_data, i, minNetValueInObligationSf);
    i += 16;
    putInt64LE(_data, i, minValueSkipLiquidationLtvChecks);
    i += 8;
    i += SerDeUtil.writeArrayChecked(name, 32, _data, i);
    putInt64LE(_data, i, minValueSkipLiquidationBfChecks);
    i += 8;
    putInt64LE(_data, i, individualAutodeleverageMarginCallPeriodSecs);
    i += 8;
    putInt64LE(_data, i, minInitialDepositAmount);
    i += 8;
    _data[i] = (byte) obligationOrderExecutionEnabled;
    ++i;
    _data[i] = (byte) immutable;
    ++i;
    _data[i] = (byte) obligationOrderCreationEnabled;
    ++i;
    _data[i] = (byte) priceTriggeredLiquidationDisabled;
    ++i;
    _data[i] = (byte) matureReserveDebtLiquidationEnabled;
    ++i;
    _data[i] = (byte) obligationBorrowDebtTermLiquidationEnabled;
    ++i;
    _data[i] = (byte) borrowOrderCreationEnabled;
    ++i;
    _data[i] = (byte) borrowOrderExecutionEnabled;
    ++i;
    proposerAuthority.write(_data, i);
    i += 32;
    putInt64LE(_data, i, minBorrowOrderFillValue);
    i += 8;
    i += SerDeUtil.writeArrayChecked(padding1, 164, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

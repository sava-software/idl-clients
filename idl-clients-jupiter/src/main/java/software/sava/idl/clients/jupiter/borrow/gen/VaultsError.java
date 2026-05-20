package software.sava.idl.clients.jupiter.borrow.gen;

import software.sava.idl.clients.core.gen.ProgramError;

public sealed interface VaultsError extends ProgramError permits
    VaultsError.VaultNextTickNotFound,
    VaultsError.VaultInvalidPositionMint,
    VaultsError.VaultTickIdLiquidationMismatch,
    VaultsError.VaultInvalidPositionTokenAmount,
    VaultsError.VaultInvalidRemainingAccountsIndices,
    VaultsError.VaultTickHasDebtVaultIdMismatch,
    VaultsError.VaultBranchVaultIdMismatch,
    VaultsError.VaultTickVaultIdMismatch,
    VaultsError.VaultInvalidDecimals,
    VaultsError.VaultInvalidOperateAmount,
    VaultsError.VaultTickIsEmpty,
    VaultsError.VaultPositionAboveCF,
    VaultsError.VaultTopTickDoesNotExist,
    VaultsError.VaultExcessSlippageLiquidation,
    VaultsError.VaultNotRebalancer,
    VaultsError.VaultTokenNotInitialized,
    VaultsError.VaultUserCollateralDebtExceed,
    VaultsError.VaultExcessCollateralWithdrawal,
    VaultsError.VaultExcessDebtPayback,
    VaultsError.VaultWithdrawMoreThanOperateLimit,
    VaultsError.VaultInvalidLiquidationAmt,
    VaultsError.VaultLiquidationResult,
    VaultsError.VaultBranchDebtTooLow,
    VaultsError.VaultTickDebtTooLow,
    VaultsError.VaultLiquidityExchangePriceUnexpected,
    VaultsError.VaultUserDebtTooLow,
    VaultsError.VaultInvalidPaybackOrDeposit,
    VaultsError.VaultInvalidLiquidation,
    VaultsError.VaultNothingToRebalance,
    VaultsError.VaultLiquidationReverts,
    VaultsError.VaultInvalidOraclePrice,
    VaultsError.VaultBranchNotFound,
    VaultsError.VaultTickNotFound,
    VaultsError.VaultTickHasDebtNotFound,
    VaultsError.VaultTickMismatch,
    VaultsError.VaultInvalidVaultId,
    VaultsError.VaultInvalidNextPositionId,
    VaultsError.VaultInvalidPositionId,
    VaultsError.VaultPositionNotEmpty,
    VaultsError.VaultInvalidSupplyMint,
    VaultsError.VaultInvalidBorrowMint,
    VaultsError.VaultInvalidOracle,
    VaultsError.VaultInvalidTick,
    VaultsError.VaultInvalidLiquidityProgram,
    VaultsError.VaultInvalidPositionAuthority,
    VaultsError.VaultOracleNotValid,
    VaultsError.VaultBranchOwnerNotValid,
    VaultsError.VaultTickHasDebtOwnerNotValid,
    VaultsError.VaultTickOwnerNotValid,
    VaultsError.VaultLiquidateRemainingAccountsTooShort,
    VaultsError.VaultOperateRemainingAccountsTooShort,
    VaultsError.VaultInvalidZerothBranch,
    VaultsError.VaultCpiToLiquidityFailed,
    VaultsError.VaultCpiToOracleFailed,
    VaultsError.VaultOnlyAuthority,
    VaultsError.VaultNewBranchInvalid,
    VaultsError.VaultTickHasDebtIndexMismatch,
    VaultsError.VaultTickHasDebtOutOfRange,
    VaultsError.VaultUserSupplyPositionRequired,
    VaultsError.VaultClaimAccountRequired,
    VaultsError.VaultRecipientWithdrawAccountRequired,
    VaultsError.VaultRecipientBorrowAccountRequired,
    VaultsError.VaultPositionAboveLiquidationThreshold,
    VaultsError.VaultAdminValueAboveLimit,
    VaultsError.VaultAdminOnlyAuths,
    VaultsError.VaultAdminAddressZeroNotAllowed,
    VaultsError.VaultAdminVaultIdMismatch,
    VaultsError.VaultAdminTotalIdsMismatch,
    VaultsError.VaultAdminTickMismatch,
    VaultsError.VaultAdminLiquidityProgramMismatch,
    VaultsError.VaultAdminMaxAuthCountReached,
    VaultsError.VaultAdminInvalidParams,
    VaultsError.VaultAdminOnlyAuthority,
    VaultsError.VaultAdminOracleProgramMismatch {

  static VaultsError getInstance(final int errorCode) {
    return switch (errorCode) {
      case 6000 -> VaultNextTickNotFound.INSTANCE;
      case 6001 -> VaultInvalidPositionMint.INSTANCE;
      case 6002 -> VaultTickIdLiquidationMismatch.INSTANCE;
      case 6003 -> VaultInvalidPositionTokenAmount.INSTANCE;
      case 6004 -> VaultInvalidRemainingAccountsIndices.INSTANCE;
      case 6005 -> VaultTickHasDebtVaultIdMismatch.INSTANCE;
      case 6006 -> VaultBranchVaultIdMismatch.INSTANCE;
      case 6007 -> VaultTickVaultIdMismatch.INSTANCE;
      case 6008 -> VaultInvalidDecimals.INSTANCE;
      case 6009 -> VaultInvalidOperateAmount.INSTANCE;
      case 6010 -> VaultTickIsEmpty.INSTANCE;
      case 6011 -> VaultPositionAboveCF.INSTANCE;
      case 6012 -> VaultTopTickDoesNotExist.INSTANCE;
      case 6013 -> VaultExcessSlippageLiquidation.INSTANCE;
      case 6014 -> VaultNotRebalancer.INSTANCE;
      case 6015 -> VaultTokenNotInitialized.INSTANCE;
      case 6016 -> VaultUserCollateralDebtExceed.INSTANCE;
      case 6017 -> VaultExcessCollateralWithdrawal.INSTANCE;
      case 6018 -> VaultExcessDebtPayback.INSTANCE;
      case 6019 -> VaultWithdrawMoreThanOperateLimit.INSTANCE;
      case 6020 -> VaultInvalidLiquidationAmt.INSTANCE;
      case 6021 -> VaultLiquidationResult.INSTANCE;
      case 6022 -> VaultBranchDebtTooLow.INSTANCE;
      case 6023 -> VaultTickDebtTooLow.INSTANCE;
      case 6024 -> VaultLiquidityExchangePriceUnexpected.INSTANCE;
      case 6025 -> VaultUserDebtTooLow.INSTANCE;
      case 6026 -> VaultInvalidPaybackOrDeposit.INSTANCE;
      case 6027 -> VaultInvalidLiquidation.INSTANCE;
      case 6028 -> VaultNothingToRebalance.INSTANCE;
      case 6029 -> VaultLiquidationReverts.INSTANCE;
      case 6030 -> VaultInvalidOraclePrice.INSTANCE;
      case 6031 -> VaultBranchNotFound.INSTANCE;
      case 6032 -> VaultTickNotFound.INSTANCE;
      case 6033 -> VaultTickHasDebtNotFound.INSTANCE;
      case 6034 -> VaultTickMismatch.INSTANCE;
      case 6035 -> VaultInvalidVaultId.INSTANCE;
      case 6036 -> VaultInvalidNextPositionId.INSTANCE;
      case 6037 -> VaultInvalidPositionId.INSTANCE;
      case 6038 -> VaultPositionNotEmpty.INSTANCE;
      case 6039 -> VaultInvalidSupplyMint.INSTANCE;
      case 6040 -> VaultInvalidBorrowMint.INSTANCE;
      case 6041 -> VaultInvalidOracle.INSTANCE;
      case 6042 -> VaultInvalidTick.INSTANCE;
      case 6043 -> VaultInvalidLiquidityProgram.INSTANCE;
      case 6044 -> VaultInvalidPositionAuthority.INSTANCE;
      case 6045 -> VaultOracleNotValid.INSTANCE;
      case 6046 -> VaultBranchOwnerNotValid.INSTANCE;
      case 6047 -> VaultTickHasDebtOwnerNotValid.INSTANCE;
      case 6048 -> VaultTickOwnerNotValid.INSTANCE;
      case 6049 -> VaultLiquidateRemainingAccountsTooShort.INSTANCE;
      case 6050 -> VaultOperateRemainingAccountsTooShort.INSTANCE;
      case 6051 -> VaultInvalidZerothBranch.INSTANCE;
      case 6052 -> VaultCpiToLiquidityFailed.INSTANCE;
      case 6053 -> VaultCpiToOracleFailed.INSTANCE;
      case 6054 -> VaultOnlyAuthority.INSTANCE;
      case 6055 -> VaultNewBranchInvalid.INSTANCE;
      case 6056 -> VaultTickHasDebtIndexMismatch.INSTANCE;
      case 6057 -> VaultTickHasDebtOutOfRange.INSTANCE;
      case 6058 -> VaultUserSupplyPositionRequired.INSTANCE;
      case 6059 -> VaultClaimAccountRequired.INSTANCE;
      case 6060 -> VaultRecipientWithdrawAccountRequired.INSTANCE;
      case 6061 -> VaultRecipientBorrowAccountRequired.INSTANCE;
      case 6062 -> VaultPositionAboveLiquidationThreshold.INSTANCE;
      case 6063 -> VaultAdminValueAboveLimit.INSTANCE;
      case 6064 -> VaultAdminOnlyAuths.INSTANCE;
      case 6065 -> VaultAdminAddressZeroNotAllowed.INSTANCE;
      case 6066 -> VaultAdminVaultIdMismatch.INSTANCE;
      case 6067 -> VaultAdminTotalIdsMismatch.INSTANCE;
      case 6068 -> VaultAdminTickMismatch.INSTANCE;
      case 6069 -> VaultAdminLiquidityProgramMismatch.INSTANCE;
      case 6070 -> VaultAdminMaxAuthCountReached.INSTANCE;
      case 6071 -> VaultAdminInvalidParams.INSTANCE;
      case 6072 -> VaultAdminOnlyAuthority.INSTANCE;
      case 6073 -> VaultAdminOracleProgramMismatch.INSTANCE;
      default -> null;
    };
  }

  record VaultNextTickNotFound(int code, String msg) implements VaultsError {

    public static final VaultNextTickNotFound INSTANCE = new VaultNextTickNotFound(
        6000, "VAULT_NEXT_TICK_NOT_FOUND"
    );
  }

  record VaultInvalidPositionMint(int code, String msg) implements VaultsError {

    public static final VaultInvalidPositionMint INSTANCE = new VaultInvalidPositionMint(
        6001, "VAULT_INVALID_POSITION_MINT"
    );
  }

  record VaultTickIdLiquidationMismatch(int code, String msg) implements VaultsError {

    public static final VaultTickIdLiquidationMismatch INSTANCE = new VaultTickIdLiquidationMismatch(
        6002, "VAULT_TICK_ID_LIQUIDATION_MISMATCH"
    );
  }

  record VaultInvalidPositionTokenAmount(int code, String msg) implements VaultsError {

    public static final VaultInvalidPositionTokenAmount INSTANCE = new VaultInvalidPositionTokenAmount(
        6003, "VAULT_INVALID_POSITION_TOKEN_AMOUNT"
    );
  }

  record VaultInvalidRemainingAccountsIndices(int code, String msg) implements VaultsError {

    public static final VaultInvalidRemainingAccountsIndices INSTANCE = new VaultInvalidRemainingAccountsIndices(
        6004, "VAULT_INVALID_REMAINING_ACCOUNTS_INDICES"
    );
  }

  record VaultTickHasDebtVaultIdMismatch(int code, String msg) implements VaultsError {

    public static final VaultTickHasDebtVaultIdMismatch INSTANCE = new VaultTickHasDebtVaultIdMismatch(
        6005, "VAULT_TICK_HAS_DEBT_VAULT_ID_MISMATCH"
    );
  }

  record VaultBranchVaultIdMismatch(int code, String msg) implements VaultsError {

    public static final VaultBranchVaultIdMismatch INSTANCE = new VaultBranchVaultIdMismatch(
        6006, "VAULT_BRANCH_VAULT_ID_MISMATCH"
    );
  }

  record VaultTickVaultIdMismatch(int code, String msg) implements VaultsError {

    public static final VaultTickVaultIdMismatch INSTANCE = new VaultTickVaultIdMismatch(
        6007, "VAULT_TICK_VAULT_ID_MISMATCH"
    );
  }

  record VaultInvalidDecimals(int code, String msg) implements VaultsError {

    public static final VaultInvalidDecimals INSTANCE = new VaultInvalidDecimals(
        6008, "VAULT_INVALID_DECIMALS"
    );
  }

  record VaultInvalidOperateAmount(int code, String msg) implements VaultsError {

    public static final VaultInvalidOperateAmount INSTANCE = new VaultInvalidOperateAmount(
        6009, "VAULT_INVALID_OPERATE_AMOUNT"
    );
  }

  record VaultTickIsEmpty(int code, String msg) implements VaultsError {

    public static final VaultTickIsEmpty INSTANCE = new VaultTickIsEmpty(
        6010, "VAULT_TICK_IS_EMPTY"
    );
  }

  record VaultPositionAboveCF(int code, String msg) implements VaultsError {

    public static final VaultPositionAboveCF INSTANCE = new VaultPositionAboveCF(
        6011, "VAULT_POSITION_ABOVE_CF"
    );
  }

  record VaultTopTickDoesNotExist(int code, String msg) implements VaultsError {

    public static final VaultTopTickDoesNotExist INSTANCE = new VaultTopTickDoesNotExist(
        6012, "VAULT_TOP_TICK_DOES_NOT_EXIST"
    );
  }

  record VaultExcessSlippageLiquidation(int code, String msg) implements VaultsError {

    public static final VaultExcessSlippageLiquidation INSTANCE = new VaultExcessSlippageLiquidation(
        6013, "VAULT_EXCESS_SLIPPAGE_LIQUIDATION"
    );
  }

  record VaultNotRebalancer(int code, String msg) implements VaultsError {

    public static final VaultNotRebalancer INSTANCE = new VaultNotRebalancer(
        6014, "VAULT_NOT_REBALANCER"
    );
  }

  record VaultTokenNotInitialized(int code, String msg) implements VaultsError {

    public static final VaultTokenNotInitialized INSTANCE = new VaultTokenNotInitialized(
        6015, "VAULT_TOKEN_NOT_INITIALIZED"
    );
  }

  record VaultUserCollateralDebtExceed(int code, String msg) implements VaultsError {

    public static final VaultUserCollateralDebtExceed INSTANCE = new VaultUserCollateralDebtExceed(
        6016, "VAULT_USER_COLLATERAL_DEBT_EXCEED"
    );
  }

  record VaultExcessCollateralWithdrawal(int code, String msg) implements VaultsError {

    public static final VaultExcessCollateralWithdrawal INSTANCE = new VaultExcessCollateralWithdrawal(
        6017, "VAULT_EXCESS_COLLATERAL_WITHDRAWAL"
    );
  }

  record VaultExcessDebtPayback(int code, String msg) implements VaultsError {

    public static final VaultExcessDebtPayback INSTANCE = new VaultExcessDebtPayback(
        6018, "VAULT_EXCESS_DEBT_PAYBACK"
    );
  }

  record VaultWithdrawMoreThanOperateLimit(int code, String msg) implements VaultsError {

    public static final VaultWithdrawMoreThanOperateLimit INSTANCE = new VaultWithdrawMoreThanOperateLimit(
        6019, "VAULT_WITHDRAW_MORE_THAN_OPERATE_LIMIT"
    );
  }

  record VaultInvalidLiquidationAmt(int code, String msg) implements VaultsError {

    public static final VaultInvalidLiquidationAmt INSTANCE = new VaultInvalidLiquidationAmt(
        6020, "VAULT_INVALID_LIQUIDATION_AMT"
    );
  }

  record VaultLiquidationResult(int code, String msg) implements VaultsError {

    public static final VaultLiquidationResult INSTANCE = new VaultLiquidationResult(
        6021, "VAULT_LIQUIDATION_RESULT"
    );
  }

  record VaultBranchDebtTooLow(int code, String msg) implements VaultsError {

    public static final VaultBranchDebtTooLow INSTANCE = new VaultBranchDebtTooLow(
        6022, "VAULT_BRANCH_DEBT_TOO_LOW"
    );
  }

  record VaultTickDebtTooLow(int code, String msg) implements VaultsError {

    public static final VaultTickDebtTooLow INSTANCE = new VaultTickDebtTooLow(
        6023, "VAULT_TICK_DEBT_TOO_LOW"
    );
  }

  record VaultLiquidityExchangePriceUnexpected(int code, String msg) implements VaultsError {

    public static final VaultLiquidityExchangePriceUnexpected INSTANCE = new VaultLiquidityExchangePriceUnexpected(
        6024, "VAULT_LIQUIDITY_EXCHANGE_PRICE_UNEXPECTED"
    );
  }

  record VaultUserDebtTooLow(int code, String msg) implements VaultsError {

    public static final VaultUserDebtTooLow INSTANCE = new VaultUserDebtTooLow(
        6025, "VAULT_USER_DEBT_TOO_LOW"
    );
  }

  record VaultInvalidPaybackOrDeposit(int code, String msg) implements VaultsError {

    public static final VaultInvalidPaybackOrDeposit INSTANCE = new VaultInvalidPaybackOrDeposit(
        6026, "VAULT_INVALID_PAYBACK_OR_DEPOSIT"
    );
  }

  record VaultInvalidLiquidation(int code, String msg) implements VaultsError {

    public static final VaultInvalidLiquidation INSTANCE = new VaultInvalidLiquidation(
        6027, "VAULT_INVALID_LIQUIDATION"
    );
  }

  record VaultNothingToRebalance(int code, String msg) implements VaultsError {

    public static final VaultNothingToRebalance INSTANCE = new VaultNothingToRebalance(
        6028, "VAULT_NOTHING_TO_REBALANCE"
    );
  }

  record VaultLiquidationReverts(int code, String msg) implements VaultsError {

    public static final VaultLiquidationReverts INSTANCE = new VaultLiquidationReverts(
        6029, "VAULT_LIQUIDATION_REVERTS"
    );
  }

  record VaultInvalidOraclePrice(int code, String msg) implements VaultsError {

    public static final VaultInvalidOraclePrice INSTANCE = new VaultInvalidOraclePrice(
        6030, "VAULT_INVALID_ORACLE_PRICE"
    );
  }

  record VaultBranchNotFound(int code, String msg) implements VaultsError {

    public static final VaultBranchNotFound INSTANCE = new VaultBranchNotFound(
        6031, "VAULT_BRANCH_NOT_FOUND"
    );
  }

  record VaultTickNotFound(int code, String msg) implements VaultsError {

    public static final VaultTickNotFound INSTANCE = new VaultTickNotFound(
        6032, "VAULT_TICK_NOT_FOUND"
    );
  }

  record VaultTickHasDebtNotFound(int code, String msg) implements VaultsError {

    public static final VaultTickHasDebtNotFound INSTANCE = new VaultTickHasDebtNotFound(
        6033, "VAULT_TICK_HAS_DEBT_NOT_FOUND"
    );
  }

  record VaultTickMismatch(int code, String msg) implements VaultsError {

    public static final VaultTickMismatch INSTANCE = new VaultTickMismatch(
        6034, "VAULT_TICK_MISMATCH"
    );
  }

  record VaultInvalidVaultId(int code, String msg) implements VaultsError {

    public static final VaultInvalidVaultId INSTANCE = new VaultInvalidVaultId(
        6035, "VAULT_INVALID_VAULT_ID"
    );
  }

  record VaultInvalidNextPositionId(int code, String msg) implements VaultsError {

    public static final VaultInvalidNextPositionId INSTANCE = new VaultInvalidNextPositionId(
        6036, "VAULT_INVALID_NEXT_POSITION_ID"
    );
  }

  record VaultInvalidPositionId(int code, String msg) implements VaultsError {

    public static final VaultInvalidPositionId INSTANCE = new VaultInvalidPositionId(
        6037, "VAULT_INVALID_POSITION_ID"
    );
  }

  record VaultPositionNotEmpty(int code, String msg) implements VaultsError {

    public static final VaultPositionNotEmpty INSTANCE = new VaultPositionNotEmpty(
        6038, "VAULT_POSITION_NOT_EMPTY"
    );
  }

  record VaultInvalidSupplyMint(int code, String msg) implements VaultsError {

    public static final VaultInvalidSupplyMint INSTANCE = new VaultInvalidSupplyMint(
        6039, "VAULT_INVALID_SUPPLY_MINT"
    );
  }

  record VaultInvalidBorrowMint(int code, String msg) implements VaultsError {

    public static final VaultInvalidBorrowMint INSTANCE = new VaultInvalidBorrowMint(
        6040, "VAULT_INVALID_BORROW_MINT"
    );
  }

  record VaultInvalidOracle(int code, String msg) implements VaultsError {

    public static final VaultInvalidOracle INSTANCE = new VaultInvalidOracle(
        6041, "VAULT_INVALID_ORACLE"
    );
  }

  record VaultInvalidTick(int code, String msg) implements VaultsError {

    public static final VaultInvalidTick INSTANCE = new VaultInvalidTick(
        6042, "VAULT_INVALID_TICK"
    );
  }

  record VaultInvalidLiquidityProgram(int code, String msg) implements VaultsError {

    public static final VaultInvalidLiquidityProgram INSTANCE = new VaultInvalidLiquidityProgram(
        6043, "VAULT_INVALID_LIQUIDITY_PROGRAM"
    );
  }

  record VaultInvalidPositionAuthority(int code, String msg) implements VaultsError {

    public static final VaultInvalidPositionAuthority INSTANCE = new VaultInvalidPositionAuthority(
        6044, "VAULT_INVALID_POSITION_AUTHORITY"
    );
  }

  record VaultOracleNotValid(int code, String msg) implements VaultsError {

    public static final VaultOracleNotValid INSTANCE = new VaultOracleNotValid(
        6045, "VAULT_ORACLE_NOT_VALID"
    );
  }

  record VaultBranchOwnerNotValid(int code, String msg) implements VaultsError {

    public static final VaultBranchOwnerNotValid INSTANCE = new VaultBranchOwnerNotValid(
        6046, "VAULT_BRANCH_OWNER_NOT_VALID"
    );
  }

  record VaultTickHasDebtOwnerNotValid(int code, String msg) implements VaultsError {

    public static final VaultTickHasDebtOwnerNotValid INSTANCE = new VaultTickHasDebtOwnerNotValid(
        6047, "VAULT_TICK_HAS_DEBT_OWNER_NOT_VALID"
    );
  }

  record VaultTickOwnerNotValid(int code, String msg) implements VaultsError {

    public static final VaultTickOwnerNotValid INSTANCE = new VaultTickOwnerNotValid(
        6048, "VAULT_TICK_DATA_OWNER_NOT_VALID"
    );
  }

  record VaultLiquidateRemainingAccountsTooShort(int code, String msg) implements VaultsError {

    public static final VaultLiquidateRemainingAccountsTooShort INSTANCE = new VaultLiquidateRemainingAccountsTooShort(
        6049, "VAULT_LIQUIDATE_REMAINING_ACCOUNTS_TOO_SHORT"
    );
  }

  record VaultOperateRemainingAccountsTooShort(int code, String msg) implements VaultsError {

    public static final VaultOperateRemainingAccountsTooShort INSTANCE = new VaultOperateRemainingAccountsTooShort(
        6050, "VAULT_OPERATE_REMAINING_ACCOUNTS_TOO_SHORT"
    );
  }

  record VaultInvalidZerothBranch(int code, String msg) implements VaultsError {

    public static final VaultInvalidZerothBranch INSTANCE = new VaultInvalidZerothBranch(
        6051, "VAULT_INVALID_ZEROTH_BRANCH"
    );
  }

  record VaultCpiToLiquidityFailed(int code, String msg) implements VaultsError {

    public static final VaultCpiToLiquidityFailed INSTANCE = new VaultCpiToLiquidityFailed(
        6052, "VAULT_CPI_TO_LIQUIDITY_FAILED"
    );
  }

  record VaultCpiToOracleFailed(int code, String msg) implements VaultsError {

    public static final VaultCpiToOracleFailed INSTANCE = new VaultCpiToOracleFailed(
        6053, "VAULT_CPI_TO_ORACLE_FAILED"
    );
  }

  record VaultOnlyAuthority(int code, String msg) implements VaultsError {

    public static final VaultOnlyAuthority INSTANCE = new VaultOnlyAuthority(
        6054, "VAULT_ONLY_AUTHORITY"
    );
  }

  record VaultNewBranchInvalid(int code, String msg) implements VaultsError {

    public static final VaultNewBranchInvalid INSTANCE = new VaultNewBranchInvalid(
        6055, "VAULT_NEW_BRANCH_INVALID"
    );
  }

  record VaultTickHasDebtIndexMismatch(int code, String msg) implements VaultsError {

    public static final VaultTickHasDebtIndexMismatch INSTANCE = new VaultTickHasDebtIndexMismatch(
        6056, "VAULT_TICK_HAS_DEBT_INDEX_MISMATCH"
    );
  }

  record VaultTickHasDebtOutOfRange(int code, String msg) implements VaultsError {

    public static final VaultTickHasDebtOutOfRange INSTANCE = new VaultTickHasDebtOutOfRange(
        6057, "VAULT_TICK_HAS_DEBT_OUT_OF_RANGE"
    );
  }

  record VaultUserSupplyPositionRequired(int code, String msg) implements VaultsError {

    public static final VaultUserSupplyPositionRequired INSTANCE = new VaultUserSupplyPositionRequired(
        6058, "VAULT_USER_SUPPLY_POSITION_REQUIRED"
    );
  }

  record VaultClaimAccountRequired(int code, String msg) implements VaultsError {

    public static final VaultClaimAccountRequired INSTANCE = new VaultClaimAccountRequired(
        6059, "VAULT_CLAIM_ACCOUNT_REQUIRED"
    );
  }

  record VaultRecipientWithdrawAccountRequired(int code, String msg) implements VaultsError {

    public static final VaultRecipientWithdrawAccountRequired INSTANCE = new VaultRecipientWithdrawAccountRequired(
        6060, "VAULT_RECIPIENT_WITHDRAW_ACCOUNT_REQUIRED"
    );
  }

  record VaultRecipientBorrowAccountRequired(int code, String msg) implements VaultsError {

    public static final VaultRecipientBorrowAccountRequired INSTANCE = new VaultRecipientBorrowAccountRequired(
        6061, "VAULT_RECIPIENT_BORROW_ACCOUNT_REQUIRED"
    );
  }

  record VaultPositionAboveLiquidationThreshold(int code, String msg) implements VaultsError {

    public static final VaultPositionAboveLiquidationThreshold INSTANCE = new VaultPositionAboveLiquidationThreshold(
        6062, "VAULT_POSITION_ABOVE_LIQUIDATION_THRESHOLD"
    );
  }

  record VaultAdminValueAboveLimit(int code, String msg) implements VaultsError {

    public static final VaultAdminValueAboveLimit INSTANCE = new VaultAdminValueAboveLimit(
        6063, "VAULT_ADMIN_VALUE_ABOVE_LIMIT"
    );
  }

  record VaultAdminOnlyAuths(int code, String msg) implements VaultsError {

    public static final VaultAdminOnlyAuths INSTANCE = new VaultAdminOnlyAuths(
        6064, "VAULT_ADMIN_ONLY_AUTH_ACCOUNTS"
    );
  }

  record VaultAdminAddressZeroNotAllowed(int code, String msg) implements VaultsError {

    public static final VaultAdminAddressZeroNotAllowed INSTANCE = new VaultAdminAddressZeroNotAllowed(
        6065, "VAULT_ADMIN_ADDRESS_ZERO_NOT_ALLOWED"
    );
  }

  record VaultAdminVaultIdMismatch(int code, String msg) implements VaultsError {

    public static final VaultAdminVaultIdMismatch INSTANCE = new VaultAdminVaultIdMismatch(
        6066, "VAULT_ADMIN_VAULT_ID_MISMATCH"
    );
  }

  record VaultAdminTotalIdsMismatch(int code, String msg) implements VaultsError {

    public static final VaultAdminTotalIdsMismatch INSTANCE = new VaultAdminTotalIdsMismatch(
        6067, "VAULT_ADMIN_TOTAL_IDS_MISMATCH"
    );
  }

  record VaultAdminTickMismatch(int code, String msg) implements VaultsError {

    public static final VaultAdminTickMismatch INSTANCE = new VaultAdminTickMismatch(
        6068, "VAULT_ADMIN_TICK_MISMATCH"
    );
  }

  record VaultAdminLiquidityProgramMismatch(int code, String msg) implements VaultsError {

    public static final VaultAdminLiquidityProgramMismatch INSTANCE = new VaultAdminLiquidityProgramMismatch(
        6069, "VAULT_ADMIN_LIQUIDITY_PROGRAM_MISMATCH"
    );
  }

  record VaultAdminMaxAuthCountReached(int code, String msg) implements VaultsError {

    public static final VaultAdminMaxAuthCountReached INSTANCE = new VaultAdminMaxAuthCountReached(
        6070, "VAULT_ADMIN_MAX_AUTH_COUNT_REACHED"
    );
  }

  record VaultAdminInvalidParams(int code, String msg) implements VaultsError {

    public static final VaultAdminInvalidParams INSTANCE = new VaultAdminInvalidParams(
        6071, "VAULT_ADMIN_INVALID_PARAMS"
    );
  }

  record VaultAdminOnlyAuthority(int code, String msg) implements VaultsError {

    public static final VaultAdminOnlyAuthority INSTANCE = new VaultAdminOnlyAuthority(
        6072, "VAULT_ADMIN_ONLY_AUTHORITY"
    );
  }

  record VaultAdminOracleProgramMismatch(int code, String msg) implements VaultsError {

    public static final VaultAdminOracleProgramMismatch INSTANCE = new VaultAdminOracleProgramMismatch(
        6073, "VAULT_ADMIN_ORACLE_PROGRAM_MISMATCH"
    );
  }
}

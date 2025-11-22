package software.sava.idl.clients.jupiter.lend_borrow.gen;

import software.sava.idl.clients.core.gen.ProgramError;

public sealed interface LendingError extends ProgramError permits
    LendingError.FTokenDepositInsignificant,
    LendingError.FTokenMinAmountOut,
    LendingError.FTokenMaxAmount,
    LendingError.FTokenInvalidParams,
    LendingError.FTokenRewardsRateModelAlreadySet,
    LendingError.FTokenMaxAuthCountReached,
    LendingError.FTokenLiquidityExchangePriceUnexpected,
    LendingError.FTokenCpiToLiquidityFailed,
    LendingError.FTokenOnlyAuth,
    LendingError.FTokenOnlyAuthority,
    LendingError.FTokenOnlyRebalancer,
    LendingError.FTokenUserSupplyPositionRequired,
    LendingError.FTokenLiquidityProgramMismatch {

  static LendingError getInstance(final int errorCode) {
    return switch (errorCode) {
      case 6000 -> FTokenDepositInsignificant.INSTANCE;
      case 6001 -> FTokenMinAmountOut.INSTANCE;
      case 6002 -> FTokenMaxAmount.INSTANCE;
      case 6003 -> FTokenInvalidParams.INSTANCE;
      case 6004 -> FTokenRewardsRateModelAlreadySet.INSTANCE;
      case 6005 -> FTokenMaxAuthCountReached.INSTANCE;
      case 6006 -> FTokenLiquidityExchangePriceUnexpected.INSTANCE;
      case 6007 -> FTokenCpiToLiquidityFailed.INSTANCE;
      case 6008 -> FTokenOnlyAuth.INSTANCE;
      case 6009 -> FTokenOnlyAuthority.INSTANCE;
      case 6010 -> FTokenOnlyRebalancer.INSTANCE;
      case 6011 -> FTokenUserSupplyPositionRequired.INSTANCE;
      case 6012 -> FTokenLiquidityProgramMismatch.INSTANCE;
      default -> null;
    };
  }

  record FTokenDepositInsignificant(int code, String msg) implements LendingError {

    public static final FTokenDepositInsignificant INSTANCE = new FTokenDepositInsignificant(
        6000, "F_TOKEN_DEPOSIT_INSIGNIFICANT"
    );
  }

  record FTokenMinAmountOut(int code, String msg) implements LendingError {

    public static final FTokenMinAmountOut INSTANCE = new FTokenMinAmountOut(
        6001, "F_TOKEN_MIN_AMOUNT_OUT"
    );
  }

  record FTokenMaxAmount(int code, String msg) implements LendingError {

    public static final FTokenMaxAmount INSTANCE = new FTokenMaxAmount(
        6002, "F_TOKEN_MAX_AMOUNT"
    );
  }

  record FTokenInvalidParams(int code, String msg) implements LendingError {

    public static final FTokenInvalidParams INSTANCE = new FTokenInvalidParams(
        6003, "F_TOKEN_INVALID_PARAMS"
    );
  }

  record FTokenRewardsRateModelAlreadySet(int code, String msg) implements LendingError {

    public static final FTokenRewardsRateModelAlreadySet INSTANCE = new FTokenRewardsRateModelAlreadySet(
        6004, "F_TOKEN_REWARDS_RATE_MODEL_ALREADY_SET"
    );
  }

  record FTokenMaxAuthCountReached(int code, String msg) implements LendingError {

    public static final FTokenMaxAuthCountReached INSTANCE = new FTokenMaxAuthCountReached(
        6005, "F_TOKEN_MAX_AUTH_COUNT"
    );
  }

  record FTokenLiquidityExchangePriceUnexpected(int code, String msg) implements LendingError {

    public static final FTokenLiquidityExchangePriceUnexpected INSTANCE = new FTokenLiquidityExchangePriceUnexpected(
        6006, "F_TOKEN_LIQUIDITY_EXCHANGE_PRICE_UNEXPECTED"
    );
  }

  record FTokenCpiToLiquidityFailed(int code, String msg) implements LendingError {

    public static final FTokenCpiToLiquidityFailed INSTANCE = new FTokenCpiToLiquidityFailed(
        6007, "F_TOKEN_CPI_TO_LIQUIDITY_FAILED"
    );
  }

  record FTokenOnlyAuth(int code, String msg) implements LendingError {

    public static final FTokenOnlyAuth INSTANCE = new FTokenOnlyAuth(
        6008, "F_TOKEN_ONLY_AUTH"
    );
  }

  record FTokenOnlyAuthority(int code, String msg) implements LendingError {

    public static final FTokenOnlyAuthority INSTANCE = new FTokenOnlyAuthority(
        6009, "F_TOKEN_ONLY_AUTHORITY"
    );
  }

  record FTokenOnlyRebalancer(int code, String msg) implements LendingError {

    public static final FTokenOnlyRebalancer INSTANCE = new FTokenOnlyRebalancer(
        6010, "F_TOKEN_ONLY_REBALANCER"
    );
  }

  record FTokenUserSupplyPositionRequired(int code, String msg) implements LendingError {

    public static final FTokenUserSupplyPositionRequired INSTANCE = new FTokenUserSupplyPositionRequired(
        6011, "F_TOKEN_USER_SUPPLY_POSITION_REQUIRED"
    );
  }

  record FTokenLiquidityProgramMismatch(int code, String msg) implements LendingError {

    public static final FTokenLiquidityProgramMismatch INSTANCE = new FTokenLiquidityProgramMismatch(
        6012, "F_TOKEN_LIQUIDITY_PROGRAM_MISMATCH"
    );
  }
}

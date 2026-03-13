package software.sava.idl.clients.jupiter.stable.gen;

import software.sava.idl.clients.core.gen.ProgramError;

public sealed interface JupStableError extends ProgramError permits
    JupStableError.SomeError,
    JupStableError.AdminArrayFull,
    JupStableError.NotAuthorized,
    JupStableError.BadInput,
    JupStableError.BenefactorDisabled,
    JupStableError.BenefactorActive,
    JupStableError.VaultNotActive,
    JupStableError.InsufficientAmount,
    JupStableError.InvalidFeeRate,
    JupStableError.MintLimitExceeded,
    JupStableError.RedeemLimitExceeded,
    JupStableError.SlippageToleranceExceeded,
    JupStableError.MathOverflow,
    JupStableError.InvalidLPMint,
    JupStableError.InvalidVaultMint,
    JupStableError.InvalidAuthority,
    JupStableError.InvalidVaultTokenAccount,
    JupStableError.InvalidTokenProgram,
    JupStableError.InvalidVaultFeeTokenAccount,
    JupStableError.BadOracle,
    JupStableError.NoValidPrice,
    JupStableError.InvalidBenefactor,
    JupStableError.InvalidCustodian,
    JupStableError.InvalidPeriodLimit,
    JupStableError.MissingOracleAccounts,
    JupStableError.NoOraclesFound,
    JupStableError.ZeroAmount,
    JupStableError.ProtocolPaused,
    JupStableError.OperatorDisabled,
    JupStableError.VaultDisabled,
    JupStableError.VaultEnabled,
    JupStableError.VaultIsDry,
    JupStableError.InvalidPegPriceUSD,
    JupStableError.NoValidOracle,
    JupStableError.PriceConfidenceTooWide,
    JupStableError.OperatorCannotDeleteItself {

  static JupStableError getInstance(final int errorCode) {
    return switch (errorCode) {
      case 6000 -> SomeError.INSTANCE;
      case 6001 -> AdminArrayFull.INSTANCE;
      case 6002 -> NotAuthorized.INSTANCE;
      case 6003 -> BadInput.INSTANCE;
      case 6004 -> BenefactorDisabled.INSTANCE;
      case 6005 -> BenefactorActive.INSTANCE;
      case 6006 -> VaultNotActive.INSTANCE;
      case 6007 -> InsufficientAmount.INSTANCE;
      case 6008 -> InvalidFeeRate.INSTANCE;
      case 6009 -> MintLimitExceeded.INSTANCE;
      case 6010 -> RedeemLimitExceeded.INSTANCE;
      case 6011 -> SlippageToleranceExceeded.INSTANCE;
      case 6012 -> MathOverflow.INSTANCE;
      case 6013 -> InvalidLPMint.INSTANCE;
      case 6014 -> InvalidVaultMint.INSTANCE;
      case 6015 -> InvalidAuthority.INSTANCE;
      case 6016 -> InvalidVaultTokenAccount.INSTANCE;
      case 6017 -> InvalidTokenProgram.INSTANCE;
      case 6018 -> InvalidVaultFeeTokenAccount.INSTANCE;
      case 6019 -> BadOracle.INSTANCE;
      case 6020 -> NoValidPrice.INSTANCE;
      case 6021 -> InvalidBenefactor.INSTANCE;
      case 6022 -> InvalidCustodian.INSTANCE;
      case 6023 -> InvalidPeriodLimit.INSTANCE;
      case 6024 -> MissingOracleAccounts.INSTANCE;
      case 6025 -> NoOraclesFound.INSTANCE;
      case 6026 -> ZeroAmount.INSTANCE;
      case 6027 -> ProtocolPaused.INSTANCE;
      case 6028 -> OperatorDisabled.INSTANCE;
      case 6029 -> VaultDisabled.INSTANCE;
      case 6030 -> VaultEnabled.INSTANCE;
      case 6031 -> VaultIsDry.INSTANCE;
      case 6032 -> InvalidPegPriceUSD.INSTANCE;
      case 6033 -> NoValidOracle.INSTANCE;
      case 6034 -> PriceConfidenceTooWide.INSTANCE;
      case 6035 -> OperatorCannotDeleteItself.INSTANCE;
      default -> null;
    };
  }

  record SomeError(int code, String msg) implements JupStableError {

    public static final SomeError INSTANCE = new SomeError(
        6000, ""
    );
  }

  record AdminArrayFull(int code, String msg) implements JupStableError {

    public static final AdminArrayFull INSTANCE = new AdminArrayFull(
        6001, "Admin Array Full"
    );
  }

  record NotAuthorized(int code, String msg) implements JupStableError {

    public static final NotAuthorized INSTANCE = new NotAuthorized(
        6002, "Not Authorized"
    );
  }

  record BadInput(int code, String msg) implements JupStableError {

    public static final BadInput INSTANCE = new BadInput(
        6003, "Bad Input"
    );
  }

  record BenefactorDisabled(int code, String msg) implements JupStableError {

    public static final BenefactorDisabled INSTANCE = new BenefactorDisabled(
        6004, "Benefactor Disabled"
    );
  }

  record BenefactorActive(int code, String msg) implements JupStableError {

    public static final BenefactorActive INSTANCE = new BenefactorActive(
        6005, "Benefactor Active"
    );
  }

  record VaultNotActive(int code, String msg) implements JupStableError {

    public static final VaultNotActive INSTANCE = new VaultNotActive(
        6006, "Vault Not Active"
    );
  }

  record InsufficientAmount(int code, String msg) implements JupStableError {

    public static final InsufficientAmount INSTANCE = new InsufficientAmount(
        6007, "Insufficient Amount"
    );
  }

  record InvalidFeeRate(int code, String msg) implements JupStableError {

    public static final InvalidFeeRate INSTANCE = new InvalidFeeRate(
        6008, "Invalid Fee Rate"
    );
  }

  record MintLimitExceeded(int code, String msg) implements JupStableError {

    public static final MintLimitExceeded INSTANCE = new MintLimitExceeded(
        6009, "Mint Limit Exceeded"
    );
  }

  record RedeemLimitExceeded(int code, String msg) implements JupStableError {

    public static final RedeemLimitExceeded INSTANCE = new RedeemLimitExceeded(
        6010, "Redeem Limit Exceeded"
    );
  }

  record SlippageToleranceExceeded(int code, String msg) implements JupStableError {

    public static final SlippageToleranceExceeded INSTANCE = new SlippageToleranceExceeded(
        6011, "Slippage Tolerance Exceeded"
    );
  }

  record MathOverflow(int code, String msg) implements JupStableError {

    public static final MathOverflow INSTANCE = new MathOverflow(
        6012, "Math Overflow"
    );
  }

  record InvalidLPMint(int code, String msg) implements JupStableError {

    public static final InvalidLPMint INSTANCE = new InvalidLPMint(
        6013, "Invalid LP Mint"
    );
  }

  record InvalidVaultMint(int code, String msg) implements JupStableError {

    public static final InvalidVaultMint INSTANCE = new InvalidVaultMint(
        6014, "Invalid Vault Mint"
    );
  }

  record InvalidAuthority(int code, String msg) implements JupStableError {

    public static final InvalidAuthority INSTANCE = new InvalidAuthority(
        6015, "Invalid Authority"
    );
  }

  record InvalidVaultTokenAccount(int code, String msg) implements JupStableError {

    public static final InvalidVaultTokenAccount INSTANCE = new InvalidVaultTokenAccount(
        6016, "Invalid Vault Token Account"
    );
  }

  record InvalidTokenProgram(int code, String msg) implements JupStableError {

    public static final InvalidTokenProgram INSTANCE = new InvalidTokenProgram(
        6017, "Invalid Token Program"
    );
  }

  record InvalidVaultFeeTokenAccount(int code, String msg) implements JupStableError {

    public static final InvalidVaultFeeTokenAccount INSTANCE = new InvalidVaultFeeTokenAccount(
        6018, "Invalid Vault Fee Token Account"
    );
  }

  record BadOracle(int code, String msg) implements JupStableError {

    public static final BadOracle INSTANCE = new BadOracle(
        6019, "Bad Oracle"
    );
  }

  record NoValidPrice(int code, String msg) implements JupStableError {

    public static final NoValidPrice INSTANCE = new NoValidPrice(
        6020, "No Valid Price"
    );
  }

  record InvalidBenefactor(int code, String msg) implements JupStableError {

    public static final InvalidBenefactor INSTANCE = new InvalidBenefactor(
        6021, "Invalid Benefactor"
    );
  }

  record InvalidCustodian(int code, String msg) implements JupStableError {

    public static final InvalidCustodian INSTANCE = new InvalidCustodian(
        6022, "Invalid Custodian"
    );
  }

  record InvalidPeriodLimit(int code, String msg) implements JupStableError {

    public static final InvalidPeriodLimit INSTANCE = new InvalidPeriodLimit(
        6023, "Invalid Rate Limit Window"
    );
  }

  record MissingOracleAccounts(int code, String msg) implements JupStableError {

    public static final MissingOracleAccounts INSTANCE = new MissingOracleAccounts(
        6024, "Missing Oracle Accounts"
    );
  }

  record NoOraclesFound(int code, String msg) implements JupStableError {

    public static final NoOraclesFound INSTANCE = new NoOraclesFound(
        6025, "No Oracles Found"
    );
  }

  record ZeroAmount(int code, String msg) implements JupStableError {

    public static final ZeroAmount INSTANCE = new ZeroAmount(
        6026, "Zero Amount"
    );
  }

  record ProtocolPaused(int code, String msg) implements JupStableError {

    public static final ProtocolPaused INSTANCE = new ProtocolPaused(
        6027, "Protocol Paused"
    );
  }

  record OperatorDisabled(int code, String msg) implements JupStableError {

    public static final OperatorDisabled INSTANCE = new OperatorDisabled(
        6028, "Operator Disabled"
    );
  }

  record VaultDisabled(int code, String msg) implements JupStableError {

    public static final VaultDisabled INSTANCE = new VaultDisabled(
        6029, "Vault Disabled"
    );
  }

  record VaultEnabled(int code, String msg) implements JupStableError {

    public static final VaultEnabled INSTANCE = new VaultEnabled(
        6030, "Vault Enabled"
    );
  }

  record VaultIsDry(int code, String msg) implements JupStableError {

    public static final VaultIsDry INSTANCE = new VaultIsDry(
        6031, "Vault Is Dry"
    );
  }

  record InvalidPegPriceUSD(int code, String msg) implements JupStableError {

    public static final InvalidPegPriceUSD INSTANCE = new InvalidPegPriceUSD(
        6032, "Invalid Peg Price USD"
    );
  }

  record NoValidOracle(int code, String msg) implements JupStableError {

    public static final NoValidOracle INSTANCE = new NoValidOracle(
        6033, "No Valid Oracle"
    );
  }

  record PriceConfidenceTooWide(int code, String msg) implements JupStableError {

    public static final PriceConfidenceTooWide INSTANCE = new PriceConfidenceTooWide(
        6034, "Price Confidence Too Wide"
    );
  }

  record OperatorCannotDeleteItself(int code, String msg) implements JupStableError {

    public static final OperatorCannotDeleteItself INSTANCE = new OperatorCannotDeleteItself(
        6035, "Operator Cannot Delete Itself"
    );
  }
}

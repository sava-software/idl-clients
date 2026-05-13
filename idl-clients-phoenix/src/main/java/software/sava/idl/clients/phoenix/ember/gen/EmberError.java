package software.sava.idl.clients.phoenix.ember.gen;

import software.sava.idl.clients.core.gen.ProgramError;

public sealed interface EmberError extends ProgramError permits
    EmberError.Unauthorized,
    EmberError.StateNotInitialized,
    EmberError.StatePdaMismatch,
    EmberError.StateAlreadyInitialized,
    EmberError.StateDataMismatch,
    EmberError.InputMintMismatch,
    EmberError.OutputMintMismatch,
    EmberError.InputMintDataLengthMismatch,
    EmberError.OutputMintDataLengthMismatch,
    EmberError.OutputMintMissingAuthority,
    EmberError.OutputMintAuthorityMismatch,
    EmberError.OutputMintAlreadyInitialized,
    EmberError.InputTokenAccountNotInitialized,
    EmberError.OutputTokenAccountNotInitialized,
    EmberError.InputTokenAccountMintMismatch,
    EmberError.OutputTokenAccountMintMismatch,
    EmberError.InputTokenAccountOwnerMismatch,
    EmberError.OutputTokenAccountOwnerMismatch,
    EmberError.InputTokenAccountDataLengthMismatch,
    EmberError.OutputTokenAccountDataLengthMismatch,
    EmberError.InsufficientBalance,
    EmberError.VaultPdaMismatch,
    EmberError.VaultAlreadyInitialized,
    EmberError.VaultDataLengthMismatch,
    EmberError.VaultMintMismatch,
    EmberError.VaultOwnerMismatch,
    EmberError.VaultNotInitialized {

  static EmberError getInstance(final int errorCode) {
    return switch (errorCode) {
      case 6000 -> Unauthorized.INSTANCE;
      case 6001 -> StateNotInitialized.INSTANCE;
      case 6002 -> StatePdaMismatch.INSTANCE;
      case 6003 -> StateAlreadyInitialized.INSTANCE;
      case 6004 -> StateDataMismatch.INSTANCE;
      case 6010 -> InputMintMismatch.INSTANCE;
      case 6011 -> OutputMintMismatch.INSTANCE;
      case 6012 -> InputMintDataLengthMismatch.INSTANCE;
      case 6013 -> OutputMintDataLengthMismatch.INSTANCE;
      case 6014 -> OutputMintMissingAuthority.INSTANCE;
      case 6015 -> OutputMintAuthorityMismatch.INSTANCE;
      case 6016 -> OutputMintAlreadyInitialized.INSTANCE;
      case 6020 -> InputTokenAccountNotInitialized.INSTANCE;
      case 6021 -> OutputTokenAccountNotInitialized.INSTANCE;
      case 6022 -> InputTokenAccountMintMismatch.INSTANCE;
      case 6023 -> OutputTokenAccountMintMismatch.INSTANCE;
      case 6024 -> InputTokenAccountOwnerMismatch.INSTANCE;
      case 6025 -> OutputTokenAccountOwnerMismatch.INSTANCE;
      case 6026 -> InputTokenAccountDataLengthMismatch.INSTANCE;
      case 6027 -> OutputTokenAccountDataLengthMismatch.INSTANCE;
      case 6028 -> InsufficientBalance.INSTANCE;
      case 6030 -> VaultPdaMismatch.INSTANCE;
      case 6031 -> VaultAlreadyInitialized.INSTANCE;
      case 6032 -> VaultDataLengthMismatch.INSTANCE;
      case 6033 -> VaultMintMismatch.INSTANCE;
      case 6034 -> VaultOwnerMismatch.INSTANCE;
      case 6035 -> VaultNotInitialized.INSTANCE;
      default -> null;
    };
  }

  record Unauthorized(int code, String msg) implements EmberError {

    public static final Unauthorized INSTANCE = new Unauthorized(
        6000, "Unauthorized"
    );
  }

  record StateNotInitialized(int code, String msg) implements EmberError {

    public static final StateNotInitialized INSTANCE = new StateNotInitialized(
        6001, "State account not initialized"
    );
  }

  record StatePdaMismatch(int code, String msg) implements EmberError {

    public static final StatePdaMismatch INSTANCE = new StatePdaMismatch(
        6002, "State account PDA mismatch"
    );
  }

  record StateAlreadyInitialized(int code, String msg) implements EmberError {

    public static final StateAlreadyInitialized INSTANCE = new StateAlreadyInitialized(
        6003, "State account already initialized"
    );
  }

  record StateDataMismatch(int code, String msg) implements EmberError {

    public static final StateDataMismatch INSTANCE = new StateDataMismatch(
        6004, "State account data mismatch"
    );
  }

  record InputMintMismatch(int code, String msg) implements EmberError {

    public static final InputMintMismatch INSTANCE = new InputMintMismatch(
        6010, "Input mint mismatch"
    );
  }

  record OutputMintMismatch(int code, String msg) implements EmberError {

    public static final OutputMintMismatch INSTANCE = new OutputMintMismatch(
        6011, "Output mint mismatch"
    );
  }

  record InputMintDataLengthMismatch(int code, String msg) implements EmberError {

    public static final InputMintDataLengthMismatch INSTANCE = new InputMintDataLengthMismatch(
        6012, "Input mint data length mismatch"
    );
  }

  record OutputMintDataLengthMismatch(int code, String msg) implements EmberError {

    public static final OutputMintDataLengthMismatch INSTANCE = new OutputMintDataLengthMismatch(
        6013, "Output mint data length mismatch"
    );
  }

  record OutputMintMissingAuthority(int code, String msg) implements EmberError {

    public static final OutputMintMissingAuthority INSTANCE = new OutputMintMissingAuthority(
        6014, "Output mint must have a mint authority"
    );
  }

  record OutputMintAuthorityMismatch(int code, String msg) implements EmberError {

    public static final OutputMintAuthorityMismatch INSTANCE = new OutputMintAuthorityMismatch(
        6015, "Output mint must be owned by the State account"
    );
  }

  record OutputMintAlreadyInitialized(int code, String msg) implements EmberError {

    public static final OutputMintAlreadyInitialized INSTANCE = new OutputMintAlreadyInitialized(
        6016, "Output mint account already initialized"
    );
  }

  record InputTokenAccountNotInitialized(int code, String msg) implements EmberError {

    public static final InputTokenAccountNotInitialized INSTANCE = new InputTokenAccountNotInitialized(
        6020, "Input token account not initialized"
    );
  }

  record OutputTokenAccountNotInitialized(int code, String msg) implements EmberError {

    public static final OutputTokenAccountNotInitialized INSTANCE = new OutputTokenAccountNotInitialized(
        6021, "Output token account not initialized"
    );
  }

  record InputTokenAccountMintMismatch(int code, String msg) implements EmberError {

    public static final InputTokenAccountMintMismatch INSTANCE = new InputTokenAccountMintMismatch(
        6022, "Input token account mint mismatch"
    );
  }

  record OutputTokenAccountMintMismatch(int code, String msg) implements EmberError {

    public static final OutputTokenAccountMintMismatch INSTANCE = new OutputTokenAccountMintMismatch(
        6023, "Output token account mint mismatch"
    );
  }

  record InputTokenAccountOwnerMismatch(int code, String msg) implements EmberError {

    public static final InputTokenAccountOwnerMismatch INSTANCE = new InputTokenAccountOwnerMismatch(
        6024, "Input token account owner mismatch"
    );
  }

  record OutputTokenAccountOwnerMismatch(int code, String msg) implements EmberError {

    public static final OutputTokenAccountOwnerMismatch INSTANCE = new OutputTokenAccountOwnerMismatch(
        6025, "Output token account owner mismatch"
    );
  }

  record InputTokenAccountDataLengthMismatch(int code, String msg) implements EmberError {

    public static final InputTokenAccountDataLengthMismatch INSTANCE = new InputTokenAccountDataLengthMismatch(
        6026, "Input token account data length mismatch"
    );
  }

  record OutputTokenAccountDataLengthMismatch(int code, String msg) implements EmberError {

    public static final OutputTokenAccountDataLengthMismatch INSTANCE = new OutputTokenAccountDataLengthMismatch(
        6027, "Output token account data length mismatch"
    );
  }

  record InsufficientBalance(int code, String msg) implements EmberError {

    public static final InsufficientBalance INSTANCE = new InsufficientBalance(
        6028, "Insufficient balance. Expected: {0}, Actual: {1}"
    );
  }

  record VaultPdaMismatch(int code, String msg) implements EmberError {

    public static final VaultPdaMismatch INSTANCE = new VaultPdaMismatch(
        6030, "Vault account PDA mismatch"
    );
  }

  record VaultAlreadyInitialized(int code, String msg) implements EmberError {

    public static final VaultAlreadyInitialized INSTANCE = new VaultAlreadyInitialized(
        6031, "Vault account already initialized"
    );
  }

  record VaultDataLengthMismatch(int code, String msg) implements EmberError {

    public static final VaultDataLengthMismatch INSTANCE = new VaultDataLengthMismatch(
        6032, "Vault account data length mismatch"
    );
  }

  record VaultMintMismatch(int code, String msg) implements EmberError {

    public static final VaultMintMismatch INSTANCE = new VaultMintMismatch(
        6033, "Vault account mint mismatch. Expected the vault mint to be the input mint"
    );
  }

  record VaultOwnerMismatch(int code, String msg) implements EmberError {

    public static final VaultOwnerMismatch INSTANCE = new VaultOwnerMismatch(
        6034, "Vault account owner mismatch. Expected to be owned by the State account"
    );
  }

  record VaultNotInitialized(int code, String msg) implements EmberError {

    public static final VaultNotInitialized INSTANCE = new VaultNotInitialized(
        6035, "Vault token account not initialized"
    );
  }
}

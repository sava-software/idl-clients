package software.sava.idl.clients.spl.token.gen;

import software.sava.idl.clients.core.gen.ProgramError;

public sealed interface TokenError extends ProgramError permits
    TokenError.NotRentExempt,
    TokenError.InsufficientFunds,
    TokenError.InvalidMint,
    TokenError.MintMismatch,
    TokenError.OwnerMismatch,
    TokenError.FixedSupply,
    TokenError.AlreadyInUse,
    TokenError.InvalidNumberOfProvidedSigners,
    TokenError.InvalidNumberOfRequiredSigners,
    TokenError.UninitializedState,
    TokenError.NativeNotSupported,
    TokenError.NonNativeHasBalance,
    TokenError.InvalidInstruction,
    TokenError.InvalidState,
    TokenError.Overflow,
    TokenError.AuthorityTypeNotSupported,
    TokenError.MintCannotFreeze,
    TokenError.AccountFrozen,
    TokenError.MintDecimalsMismatch,
    TokenError.NonNativeNotSupported {

  static TokenError getInstance(final int errorCode) {
    return switch (errorCode) {
      case 0 -> NotRentExempt.INSTANCE;
      case 1 -> InsufficientFunds.INSTANCE;
      case 2 -> InvalidMint.INSTANCE;
      case 3 -> MintMismatch.INSTANCE;
      case 4 -> OwnerMismatch.INSTANCE;
      case 5 -> FixedSupply.INSTANCE;
      case 6 -> AlreadyInUse.INSTANCE;
      case 7 -> InvalidNumberOfProvidedSigners.INSTANCE;
      case 8 -> InvalidNumberOfRequiredSigners.INSTANCE;
      case 9 -> UninitializedState.INSTANCE;
      case 10 -> NativeNotSupported.INSTANCE;
      case 11 -> NonNativeHasBalance.INSTANCE;
      case 12 -> InvalidInstruction.INSTANCE;
      case 13 -> InvalidState.INSTANCE;
      case 14 -> Overflow.INSTANCE;
      case 15 -> AuthorityTypeNotSupported.INSTANCE;
      case 16 -> MintCannotFreeze.INSTANCE;
      case 17 -> AccountFrozen.INSTANCE;
      case 18 -> MintDecimalsMismatch.INSTANCE;
      case 19 -> NonNativeNotSupported.INSTANCE;
      default -> null;
    };
  }

  record NotRentExempt(int code, String msg) implements TokenError {

    public static final NotRentExempt INSTANCE = new NotRentExempt(
        0, "Lamport balance below rent-exempt threshold"
    );
  }

  record InsufficientFunds(int code, String msg) implements TokenError {

    public static final InsufficientFunds INSTANCE = new InsufficientFunds(
        1, "Insufficient funds"
    );
  }

  record InvalidMint(int code, String msg) implements TokenError {

    public static final InvalidMint INSTANCE = new InvalidMint(
        2, "Invalid Mint"
    );
  }

  record MintMismatch(int code, String msg) implements TokenError {

    public static final MintMismatch INSTANCE = new MintMismatch(
        3, "Account not associated with this Mint"
    );
  }

  record OwnerMismatch(int code, String msg) implements TokenError {

    public static final OwnerMismatch INSTANCE = new OwnerMismatch(
        4, "Owner does not match"
    );
  }

  record FixedSupply(int code, String msg) implements TokenError {

    public static final FixedSupply INSTANCE = new FixedSupply(
        5, "Fixed supply"
    );
  }

  record AlreadyInUse(int code, String msg) implements TokenError {

    public static final AlreadyInUse INSTANCE = new AlreadyInUse(
        6, "Already in use"
    );
  }

  record InvalidNumberOfProvidedSigners(int code, String msg) implements TokenError {

    public static final InvalidNumberOfProvidedSigners INSTANCE = new InvalidNumberOfProvidedSigners(
        7, "Invalid number of provided signers"
    );
  }

  record InvalidNumberOfRequiredSigners(int code, String msg) implements TokenError {

    public static final InvalidNumberOfRequiredSigners INSTANCE = new InvalidNumberOfRequiredSigners(
        8, "Invalid number of required signers"
    );
  }

  record UninitializedState(int code, String msg) implements TokenError {

    public static final UninitializedState INSTANCE = new UninitializedState(
        9, "State is unititialized"
    );
  }

  record NativeNotSupported(int code, String msg) implements TokenError {

    public static final NativeNotSupported INSTANCE = new NativeNotSupported(
        10, "Instruction does not support native tokens"
    );
  }

  record NonNativeHasBalance(int code, String msg) implements TokenError {

    public static final NonNativeHasBalance INSTANCE = new NonNativeHasBalance(
        11, "Non-native account can only be closed if its balance is zero"
    );
  }

  record InvalidInstruction(int code, String msg) implements TokenError {

    public static final InvalidInstruction INSTANCE = new InvalidInstruction(
        12, "Invalid instruction"
    );
  }

  record InvalidState(int code, String msg) implements TokenError {

    public static final InvalidState INSTANCE = new InvalidState(
        13, "State is invalid for requested operation"
    );
  }

  record Overflow(int code, String msg) implements TokenError {

    public static final Overflow INSTANCE = new Overflow(
        14, "Operation overflowed"
    );
  }

  record AuthorityTypeNotSupported(int code, String msg) implements TokenError {

    public static final AuthorityTypeNotSupported INSTANCE = new AuthorityTypeNotSupported(
        15, "Account does not support specified authority type"
    );
  }

  record MintCannotFreeze(int code, String msg) implements TokenError {

    public static final MintCannotFreeze INSTANCE = new MintCannotFreeze(
        16, "This token mint cannot freeze accounts"
    );
  }

  record AccountFrozen(int code, String msg) implements TokenError {

    public static final AccountFrozen INSTANCE = new AccountFrozen(
        17, "Account is frozen"
    );
  }

  record MintDecimalsMismatch(int code, String msg) implements TokenError {

    public static final MintDecimalsMismatch INSTANCE = new MintDecimalsMismatch(
        18, "The provided decimals value different from the Mint decimals"
    );
  }

  record NonNativeNotSupported(int code, String msg) implements TokenError {

    public static final NonNativeNotSupported INSTANCE = new NonNativeNotSupported(
        19, "Instruction does not support non-native tokens"
    );
  }
}

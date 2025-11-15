package software.sava.idl.clients.spl.system.gen;

import software.sava.idl.clients.core.gen.ProgramError;

public sealed interface SystemError extends ProgramError permits
    SystemError.AccountAlreadyInUse,
    SystemError.ResultWithNegativeLamports,
    SystemError.InvalidProgramId,
    SystemError.InvalidAccountDataLength,
    SystemError.MaxSeedLengthExceeded,
    SystemError.AddressWithSeedMismatch,
    SystemError.NonceNoRecentBlockhashes,
    SystemError.NonceBlockhashNotExpired,
    SystemError.NonceUnexpectedBlockhashValue {

  static SystemError getInstance(final int errorCode) {
    return switch (errorCode) {
      case 0 -> AccountAlreadyInUse.INSTANCE;
      case 1 -> ResultWithNegativeLamports.INSTANCE;
      case 2 -> InvalidProgramId.INSTANCE;
      case 3 -> InvalidAccountDataLength.INSTANCE;
      case 4 -> MaxSeedLengthExceeded.INSTANCE;
      case 5 -> AddressWithSeedMismatch.INSTANCE;
      case 6 -> NonceNoRecentBlockhashes.INSTANCE;
      case 7 -> NonceBlockhashNotExpired.INSTANCE;
      case 8 -> NonceUnexpectedBlockhashValue.INSTANCE;
      default -> null;
    };
  }

  record AccountAlreadyInUse(int code, String msg) implements SystemError {

    public static final AccountAlreadyInUse INSTANCE = new AccountAlreadyInUse(
        0, "an account with the same address already exists"
    );
  }

  record ResultWithNegativeLamports(int code, String msg) implements SystemError {

    public static final ResultWithNegativeLamports INSTANCE = new ResultWithNegativeLamports(
        1, "account does not have enough SOL to perform the operation"
    );
  }

  record InvalidProgramId(int code, String msg) implements SystemError {

    public static final InvalidProgramId INSTANCE = new InvalidProgramId(
        2, "cannot assign account to this program id"
    );
  }

  record InvalidAccountDataLength(int code, String msg) implements SystemError {

    public static final InvalidAccountDataLength INSTANCE = new InvalidAccountDataLength(
        3, "cannot allocate account data of this length"
    );
  }

  record MaxSeedLengthExceeded(int code, String msg) implements SystemError {

    public static final MaxSeedLengthExceeded INSTANCE = new MaxSeedLengthExceeded(
        4, "length of requested seed is too long"
    );
  }

  record AddressWithSeedMismatch(int code, String msg) implements SystemError {

    public static final AddressWithSeedMismatch INSTANCE = new AddressWithSeedMismatch(
        5, "provided address does not match addressed derived from seed"
    );
  }

  record NonceNoRecentBlockhashes(int code, String msg) implements SystemError {

    public static final NonceNoRecentBlockhashes INSTANCE = new NonceNoRecentBlockhashes(
        6, "advancing stored nonce requires a populated RecentBlockhashes sysvar"
    );
  }

  record NonceBlockhashNotExpired(int code, String msg) implements SystemError {

    public static final NonceBlockhashNotExpired INSTANCE = new NonceBlockhashNotExpired(
        7, "stored nonce is still in recent_blockhashes"
    );
  }

  record NonceUnexpectedBlockhashValue(int code, String msg) implements SystemError {

    public static final NonceUnexpectedBlockhashValue INSTANCE = new NonceUnexpectedBlockhashValue(
        8, "specified nonce does not match stored nonce"
    );
  }
}

package software.sava.idl.clients.oracles.pyth.push.gen;

import software.sava.idl.clients.core.gen.ProgramError;

public sealed interface PythPushOracleError extends ProgramError permits
    PythPushOracleError.UpdatesNotMonotonic,
    PythPushOracleError.PriceFeedMessageMismatch {

  static PythPushOracleError getInstance(final int errorCode) {
    return switch (errorCode) {
      case 6000 -> UpdatesNotMonotonic.INSTANCE;
      case 6001 -> PriceFeedMessageMismatch.INSTANCE;
      default -> null;
    };
  }

  record UpdatesNotMonotonic(int code, String msg) implements PythPushOracleError {

    public static final UpdatesNotMonotonic INSTANCE = new UpdatesNotMonotonic(
        6000, "Updates must be monotonically increasing"
    );
  }

  record PriceFeedMessageMismatch(int code, String msg) implements PythPushOracleError {

    public static final PriceFeedMessageMismatch INSTANCE = new PriceFeedMessageMismatch(
        6001, "Trying to update price feed with the wrong feed id"
    );
  }
}

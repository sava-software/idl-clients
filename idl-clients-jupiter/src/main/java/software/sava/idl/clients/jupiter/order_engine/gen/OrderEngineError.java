package software.sava.idl.clients.jupiter.order_engine.gen;

import software.sava.idl.clients.core.gen.ProgramError;

public sealed interface OrderEngineError extends ProgramError permits
    OrderEngineError.InvalidCalculation,
    OrderEngineError.MissingTemporaryWrappedSolTokenAccount {

  static OrderEngineError getInstance(final int errorCode) {
    return switch (errorCode) {
      case 6000 -> InvalidCalculation.INSTANCE;
      case 6001 -> MissingTemporaryWrappedSolTokenAccount.INSTANCE;
      default -> null;
    };
  }

  record InvalidCalculation(int code, String msg) implements OrderEngineError {

    public static final InvalidCalculation INSTANCE = new InvalidCalculation(
        6000, "null"
    );
  }

  record MissingTemporaryWrappedSolTokenAccount(int code, String msg) implements OrderEngineError {

    public static final MissingTemporaryWrappedSolTokenAccount INSTANCE = new MissingTemporaryWrappedSolTokenAccount(
        6001, "null"
    );
  }
}

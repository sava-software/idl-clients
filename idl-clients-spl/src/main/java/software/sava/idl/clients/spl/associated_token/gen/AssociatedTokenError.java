package software.sava.idl.clients.spl.associated_token.gen;

import software.sava.idl.clients.core.gen.ProgramError;

public sealed interface AssociatedTokenError extends ProgramError permits
    AssociatedTokenError.InvalidOwner {

  static AssociatedTokenError getInstance(final int errorCode) {
    return switch (errorCode) {
      case 0 -> InvalidOwner.INSTANCE;
      default -> null;
    };
  }

  record InvalidOwner(int code, String msg) implements AssociatedTokenError {

    public static final InvalidOwner INSTANCE = new InvalidOwner(
        0, "Associated token account owner does not match address derivation"
    );
  }
}

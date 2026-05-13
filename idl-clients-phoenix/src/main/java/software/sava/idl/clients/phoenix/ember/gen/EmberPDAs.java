package software.sava.idl.clients.phoenix.ember.gen;

import java.util.List;

import software.sava.core.accounts.ProgramDerivedAddress;
import software.sava.core.accounts.PublicKey;

import static java.nio.charset.StandardCharsets.US_ASCII;

public final class EmberPDAs {

  public static ProgramDerivedAddress statePDA(final PublicKey program) {
    return PublicKey.findProgramAddress(List.of(
      "state".getBytes(US_ASCII)
    ), program);
  }

  public static ProgramDerivedAddress vaultPDA(final PublicKey program) {
    return PublicKey.findProgramAddress(List.of(
      "vault".getBytes(US_ASCII)
    ), program);
  }

  private EmberPDAs() {
  }
}

package software.sava.idl.clients.phoenix.perpetuals.gen;

import java.util.List;

import software.sava.core.accounts.ProgramDerivedAddress;
import software.sava.core.accounts.PublicKey;

import static java.nio.charset.StandardCharsets.US_ASCII;

public final class EternalPDAs {

  public static ProgramDerivedAddress globalConfigurationPDA(final PublicKey program) {
    return PublicKey.findProgramAddress(List.of(
      "global".getBytes(US_ASCII)
    ), program);
  }

  public static ProgramDerivedAddress phoenixLogAuthorityPDA(final PublicKey program) {
    return PublicKey.findProgramAddress(List.of(
      "log".getBytes(US_ASCII)
    ), program);
  }

  public static ProgramDerivedAddress traderAccountPDA(final PublicKey program,
                                                       final PublicKey traderWalletAccount,
                                                       final byte[] paramsTraderPdaIndex,
                                                       final byte[] paramsTraderSubaccountIndex) {
    return PublicKey.findProgramAddress(List.of(
      "trader".getBytes(US_ASCII),
      traderWalletAccount.toByteArray(),
      paramsTraderPdaIndex,
      paramsTraderSubaccountIndex
    ), program);
  }

  private EternalPDAs() {
  }
}

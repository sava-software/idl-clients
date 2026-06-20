package software.sava.idl.clients.marginfi.v2;

import software.sava.core.accounts.ProgramDerivedAddress;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.idl.clients.marginfi.v2.gen.MarginfiPDAs;

public interface MarginfiAccounts {

  MarginfiAccounts MAIN_NET = createAccounts(
      "MFv2hWf31Z9kbCa1snEPYctwafyhdvnV7FZnsebVacA",
      "4qp6Fx6tnZkY5Wropq9wUYgtFxXKwE6viZxFHg3rdAG8"
  );

  static MarginfiAccounts createAccounts(final PublicKey marginfiProgram, final PublicKey marginfiGroup) {
    return new MarginfiAccountsRecord(
        AccountMeta.createInvoked(marginfiProgram),
        marginfiGroup,
        MarginfiPDAs.feeStatePDA(marginfiProgram).publicKey()
    );
  }

  static MarginfiAccounts createAccounts(final String marginfiProgram, final String marginfiGroup) {
    return createAccounts(
        PublicKey.fromBase58Encoded(marginfiProgram),
        PublicKey.fromBase58Encoded(marginfiGroup)
    );
  }

  AccountMeta invokedMarginfiProgram();

  default PublicKey marginfiProgram() {
    return invokedMarginfiProgram().publicKey();
  }

  PublicKey marginfiGroup();

  PublicKey feeState();

  default ProgramDerivedAddress bankLiquidityVaultPDA(final PublicKey bank) {
    return MarginfiPDAs.bankLiquidityVaultPDA(marginfiProgram(), bank);
  }

  default ProgramDerivedAddress bankLiquidityVaultAuthorityPDA(final PublicKey bank) {
    return MarginfiPDAs.bankLiquidityVaultAuthorityPDA(marginfiProgram(), bank);
  }
}

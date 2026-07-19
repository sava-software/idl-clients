package software.sava.idl.clients.phoenix;

import software.sava.core.accounts.ProgramDerivedAddress;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.idl.clients.phoenix.ember.gen.EmberPDAs;
import software.sava.idl.clients.phoenix.perpetuals.gen.EternalPDAs;

public interface PhoenixAccounts {

  PhoenixAccounts MAIN_NET = createAccounts(
      "EMBERpYNE6ehWmXymZZS2skiFmCa9V5dp14e1iduM5qy",
      "PhUsd11YkbjSaWjFncfAAmatntsjx3MgDR9B6g1ks3A",
      "EtrnLzgbS7nMMy5fbD42kXiUzGg8XQzJ972Xtk1cjWih",
      "EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v"
  );

  static PhoenixAccounts createAccounts(final PublicKey emberProgram,
                                        final PublicKey emberUSDCMint,
                                        final PublicKey eternalProgram,
                                        final PublicKey usdcMint) {
    return new PhoenixAccountsRecord(
        AccountMeta.createInvoked(emberProgram),
        EmberPDAs.statePDA(emberProgram).publicKey(),
        EmberPDAs.vaultPDA(emberProgram).publicKey(),
        emberUSDCMint,
        AccountMeta.createInvoked(eternalProgram),
        EternalPDAs.globalConfigurationPDA(eternalProgram).publicKey(),
        EternalPDAs.phoenixLogAuthorityPDA(eternalProgram).publicKey(),
        EternalPDAs.globalTraderIndexHeaderPDA(eternalProgram).publicKey(),
        EternalPDAs.activeTraderBufferHeaderPDA(eternalProgram).publicKey(),
        usdcMint
    );
  }

  static PhoenixAccounts createAccounts(final String emberProgram,
                                        final String emberUSDCMint,
                                        final String eternalProgram,
                                        final String usdcMint) {
    return createAccounts(
        PublicKey.fromBase58Encoded(emberProgram),
        PublicKey.fromBase58Encoded(emberUSDCMint),
        PublicKey.fromBase58Encoded(eternalProgram),
        PublicKey.fromBase58Encoded(usdcMint)
    );
  }

  /// The protocol's vault for `mint`, held by the Eternal (perpetuals) program.
  ///
  /// Seeds `["vault", mint]` — the IDL does not declare this PDA, so the seeds
  /// are taken from the program's own SDK
  /// (`rise/rust/ix/src/constants.rs::get_global_vault_address`). It is
  /// per-mint, and distinct from the global *configuration* account.
  static ProgramDerivedAddress globalVaultPDA(final PublicKey mint, final PublicKey eternalProgram) {
    return PublicKey.findProgramAddress(
        java.util.List.of(
            "vault".getBytes(java.nio.charset.StandardCharsets.US_ASCII),
            mint.toByteArray()
        ),
        eternalProgram
    );
  }

  default ProgramDerivedAddress globalVaultPDA(final PublicKey mint) {
    return globalVaultPDA(mint, invokedEternalProgram().publicKey());
  }

  AccountMeta invokedEmberProgram();

  PublicKey emberStateProgram();

  PublicKey emberVaultProgram();

  PublicKey emberUSDCMint();

  AccountMeta invokedEternalProgram();

  PublicKey eternalGlobalConfig();

  PublicKey eternalLogAuthority();

  PublicKey globalTraderIndex();

  PublicKey activeTraderBuffer();

  PublicKey usdcMint();
}

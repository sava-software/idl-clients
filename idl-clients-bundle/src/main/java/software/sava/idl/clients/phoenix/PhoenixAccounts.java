package software.sava.idl.clients.phoenix;

import software.sava.core.accounts.ProgramDerivedAddress;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.idl.clients.phoenix.perpetuals.gen.EternalPDAs;

import java.util.List;

import static java.nio.charset.StandardCharsets.US_ASCII;

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
        emberStatePDA(eternalProgram, emberProgram).publicKey(),
        emberVaultPDA(eternalProgram, emberProgram).publicKey(),
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

  /// Ember's state account for the Phoenix deployment it wraps.
  ///
  /// Seeds `[phoenixProgram, "state"]` — where `phoenixProgram` is what this library
  /// calls the Eternal program, the name the Rust uses for the same key
  /// (`PROD_PHOENIX_PROGRAM_ID` is `EtrnLzgb…`).
  ///
  /// **The IDL is wrong here**, and it is the program's own published metadata, so the
  /// generated `EmberPDAs.statePDA` inherits the error: it declares the seed as `"state"`
  /// alone, which derives an address that has never existed. One Ember deployment serves
  /// both Phoenix deployments — mainnet and beta each get their own state, keyed on the
  /// program — so a single un-keyed account could not work. Seeds taken from the
  /// program's SDK (`rise/rust/ix/src/constants.rs::get_ember_state_address`) and pinned
  /// against the live accounts in `PhoenixAccountsTests`.
  static ProgramDerivedAddress emberStatePDA(final PublicKey phoenixProgram,
                                             final PublicKey emberProgram) {
    return PublicKey.findProgramAddress(
        List.of(phoenixProgram.toByteArray(), "state".getBytes(US_ASCII)),
        emberProgram
    );
  }

  /// Ember's token vault for the Phoenix deployment it wraps.
  ///
  /// Seeds `[phoenixProgram, "vault"]`; see [#emberStatePDA(PublicKey, PublicKey)] for
  /// why the IDL's `"vault"`-only seed does not derive it.
  static ProgramDerivedAddress emberVaultPDA(final PublicKey phoenixProgram,
                                             final PublicKey emberProgram) {
    return PublicKey.findProgramAddress(
        List.of(phoenixProgram.toByteArray(), "vault".getBytes(US_ASCII)),
        emberProgram
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
        List.of("vault".getBytes(US_ASCII), mint.toByteArray()),
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

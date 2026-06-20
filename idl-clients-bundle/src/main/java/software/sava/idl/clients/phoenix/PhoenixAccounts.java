package software.sava.idl.clients.phoenix;

import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.idl.clients.phoenix.ember.gen.EmberPDAs;
import software.sava.idl.clients.phoenix.perpetuals.gen.EternalPDAs;

public interface PhoenixAccounts {

  PhoenixAccounts MAIN_NET = createAccounts(
      "EMBERpYNE6ehWmXymZZS2skiFmCa9V5dp14e1iduM5qy",
      "PhUsd11YkbjSaWjFncfAAmatntsjx3MgDR9B6g1ks3A",
      "EtrnLzgbS7nMMy5fbD42kXiUzGg8XQzJ972Xtk1cjWih"
  );

  static PhoenixAccounts createAccounts(final PublicKey emberProgram,
                                        final PublicKey emberUSDCMint,
                                        final PublicKey eternalProgram) {
    return new PhoenixAccountsRecord(
        AccountMeta.createInvoked(emberProgram),
        EmberPDAs.statePDA(emberProgram).publicKey(),
        EmberPDAs.vaultPDA(emberProgram).publicKey(),
        emberUSDCMint,
        AccountMeta.createInvoked(eternalProgram),
        EternalPDAs.globalConfigurationPDA(eternalProgram).publicKey(),
        EternalPDAs.phoenixLogAuthorityPDA(eternalProgram).publicKey(),
        EternalPDAs.globalTraderIndexHeaderPDA(eternalProgram).publicKey(),
        EternalPDAs.activeTraderBufferHeaderPDA(eternalProgram).publicKey()
    );
  }

  static PhoenixAccounts createAccounts(final String emberProgram,
                                        final String emberUSDCMint,
                                        final String eternalProgram) {
    return createAccounts(
        PublicKey.fromBase58Encoded(emberProgram),
        PublicKey.fromBase58Encoded(emberUSDCMint),
        PublicKey.fromBase58Encoded(eternalProgram)
    );
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
}

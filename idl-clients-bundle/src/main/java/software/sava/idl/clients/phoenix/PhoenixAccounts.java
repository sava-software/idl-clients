package software.sava.idl.clients.phoenix;

import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.idl.clients.phoenix.ember.gen.EmberPDAs;
import software.sava.idl.clients.phoenix.perpetuals.gen.EternalPDAs;

public interface PhoenixAccounts {

  PhoenixAccounts MAIN_NET = createAccounts(
      "KLend2g3cP87fffoy8q1mQqGKjrxjC8boSyAYavgmjD",
      "PhUsd11YkbjSaWjFncfAAmatntsjx3MgDR9B6g1ks3A",
      "284iwGtA9X9aLy3KsyV8uT2pXLARhYbiSi5SiM2g47M2",
      "HCrPXLByGqRh2szQi3gj7oRdRVBNi1gccAyn4CQCT3HK",
      "2U32rSzzrQS3eVmGHsnbw5kcqKF3wQXpHGd3hMq5YJok"
  );

  static PhoenixAccounts createAccounts(final PublicKey emberProgram,
                                        final PublicKey emberUSDCMint,
                                        final PublicKey eternalProgram,
                                        final PublicKey globalTraderIndex,
                                        final PublicKey activeTraderBuffer) {
    return new PhoenixAccountsRecord(
        AccountMeta.createInvoked(emberProgram),
        EmberPDAs.statePDA(emberProgram).publicKey(),
        EmberPDAs.vaultPDA(emberProgram).publicKey(),
        emberUSDCMint,
        AccountMeta.createInvoked(eternalProgram),
        EternalPDAs.globalConfigurationPDA(eternalProgram).publicKey(),
        EternalPDAs.phoenixLogAuthorityPDA(eternalProgram).publicKey(),
        globalTraderIndex,
        activeTraderBuffer
    );
  }

  static PhoenixAccounts createAccounts(final String emberProgram,
                                        final String emberUSDCMint,
                                        final String eternalProgram,
                                        final String globalTraderIndex,
                                        final String activeTraderBuffer) {
    return createAccounts(
        PublicKey.fromBase58Encoded(emberProgram),
        PublicKey.fromBase58Encoded(emberUSDCMint),
        PublicKey.fromBase58Encoded(eternalProgram),
        PublicKey.fromBase58Encoded(globalTraderIndex),
        PublicKey.fromBase58Encoded(activeTraderBuffer)
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

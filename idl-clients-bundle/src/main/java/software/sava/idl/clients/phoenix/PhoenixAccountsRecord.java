package software.sava.idl.clients.phoenix;

import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.meta.AccountMeta;

public record PhoenixAccountsRecord(AccountMeta invokedEmberProgram,
                                    PublicKey emberStateProgram,
                                    PublicKey emberVaultProgram,
                                    PublicKey emberUSDCMint,
                                    AccountMeta invokedEternalProgram,
                                    PublicKey eternalGlobalConfig,
                                    PublicKey eternalLogAuthority,
                                    PublicKey globalTraderIndex,
                                    PublicKey activeTraderBuffer,
                                    PublicKey usdcMint) implements PhoenixAccounts {
}

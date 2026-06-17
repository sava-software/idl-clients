package software.sava.idl.clients.orca;

import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.meta.AccountMeta;

public record OrcaAccountsRecord(AccountMeta invokedWhirlpoolProgram,
                                 PublicKey whirlpoolNftUpdateAuthority,
                                 PublicKey tokenMetadataProgram) implements OrcaAccounts {
}

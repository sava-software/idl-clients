package software.sava.idl.clients.drift;

import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.meta.AccountMeta;

import java.util.List;

record DriftAccountsRecord(AccountMeta invokedDriftProgram,
                           PublicKey driftSignerPDA,
                           PublicKey marketLookupTable,
                           List<PublicKey> marketLookupTables,
                           PublicKey serumLookupTable,
                           PublicKey stateKey,
                           AccountMeta invokedDriftVaultsProgram) implements DriftAccounts {
}

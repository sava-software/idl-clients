package software.sava.idl.clients.metaplex;

import software.sava.core.accounts.PublicKey;

record MetaplexAccountsRecord(PublicKey tokenMetadataProgram) implements MetaplexAccounts {
}

package software.sava.idl.clients.marginfi.v2;

import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.meta.AccountMeta;

public record MarginfiAccountsRecord(AccountMeta invokedMarginfiProgram,
                                     PublicKey marginfiGroup,
                                     PublicKey feeState) implements MarginfiAccounts {

}

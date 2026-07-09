package software.sava.idl.clients.loopscale;

import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.meta.AccountMeta;

public record LoopscaleAccountsRecord(AccountMeta invokedLoopscaleProgram,
                                      PublicKey eventAuthority,
                                      PublicKey protocolAdmin,
                                      PublicKey protocolAdminState) implements LoopscaleAccounts {

}

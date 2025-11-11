package software.sava.idl.clients.spl;

import software.sava.core.accounts.PublicKey;
import software.sava.core.tx.Instruction;

public interface SPLAccountClient {

  Instruction createATAForOwnerFundedByFeePayer(final boolean idempotent,
                                                final PublicKey ata,
                                                final PublicKey mint,
                                                final PublicKey tokenProgram);
}

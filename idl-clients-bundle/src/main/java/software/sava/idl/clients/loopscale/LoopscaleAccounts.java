package software.sava.idl.clients.loopscale;

import software.sava.core.accounts.ProgramDerivedAddress;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.meta.AccountMeta;

public interface LoopscaleAccounts {

  LoopscaleAccounts MAIN_NET = createAccounts(
      "1oopBoJG58DgkUVKkEzKgyG9dvRmpgeEm1AVjoHkF78",
      "CyNKPfqsSLAejjZtEeNG3pR4SkPhSPHXdGhuNTyudrNs"
  );

  static LoopscaleAccounts createAccounts(final PublicKey loopscaleProgram, final PublicKey protocolAdmin) {
    return new LoopscaleAccountsRecord(
        AccountMeta.createInvoked(loopscaleProgram),
        LoopscalePDAs.eventAuthority(loopscaleProgram).publicKey(),
        protocolAdmin,
        LoopscalePDAs.protocolAdminState(loopscaleProgram).publicKey()
    );
  }

  static LoopscaleAccounts createAccounts(final String loopscaleProgram, final String protocolAdmin) {
    return createAccounts(
        PublicKey.fromBase58Encoded(loopscaleProgram),
        PublicKey.fromBase58Encoded(protocolAdmin)
    );
  }

  AccountMeta invokedLoopscaleProgram();

  default PublicKey loopscaleProgram() {
    return invokedLoopscaleProgram().publicKey();
  }

  PublicKey eventAuthority();

  PublicKey protocolAdmin();

  PublicKey protocolAdminState();

  default ProgramDerivedAddress loanPda(final PublicKey borrower, final long nonce) {
    return LoopscalePDAs.loan(borrower, nonce, loopscaleProgram());
  }
}

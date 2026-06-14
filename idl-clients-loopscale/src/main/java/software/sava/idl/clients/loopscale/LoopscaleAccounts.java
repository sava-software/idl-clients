package software.sava.idl.clients.loopscale;

import software.sava.core.accounts.ProgramDerivedAddress;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.meta.AccountMeta;

public interface LoopscaleAccounts {

  LoopscaleAccounts MAIN_NET = createAccounts(
      "1oopBoJG58DgkUVKkEzKgyG9dvRmpgeEm1AVjoHkF78",
      "CyNKPfqsSLAejjZtEeNG3pR4SkPhSPHXdGhuNTyudrNs"
  );

  static LoopscaleAccounts createAccounts(final PublicKey loopscaleProgram, final PublicKey bsAuth) {
    return new LoopscaleAccountsRecord(
        AccountMeta.createInvoked(loopscaleProgram),
        LoopscalePDAs.eventAuthority(loopscaleProgram).publicKey(),
        bsAuth
    );
  }

  static LoopscaleAccounts createAccounts(final String loopscaleProgram, final String bsAuth) {
    return createAccounts(
        PublicKey.fromBase58Encoded(loopscaleProgram),
        PublicKey.fromBase58Encoded(bsAuth)
    );
  }

  AccountMeta invokedLoopscaleProgram();

  default PublicKey loopscaleProgram() {
    return invokedLoopscaleProgram().publicKey();
  }

  PublicKey eventAuthority();

  PublicKey bsAuth();

  default ProgramDerivedAddress loanPda(final PublicKey borrower, final long nonce) {
    return LoopscalePDAs.loan(borrower, nonce, loopscaleProgram());
  }
}

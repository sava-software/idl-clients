package software.sava.idl.clients.loopscale;

import software.sava.core.accounts.ProgramDerivedAddress;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.encoding.ByteUtil;

import java.util.List;

import static java.nio.charset.StandardCharsets.US_ASCII;

public interface LoopscaleAccounts {

  LoopscaleAccounts MAIN_NET = createAccounts(
      "1oopBoJG58DgkUVKkEzKgyG9dvRmpgeEm1AVjoHkF78",
      "CyNKPfqsSLAejjZtEeNG3pR4SkPhSPHXdGhuNTyudrNs"
  );

  static LoopscaleAccounts createAccounts(final PublicKey loopscaleProgram, final PublicKey bsAuth) {
    return new LoopscaleAccountsRecord(
        AccountMeta.createInvoked(loopscaleProgram),
        eventAuthority(loopscaleProgram).publicKey(),
        bsAuth
    );
  }

  static LoopscaleAccounts createAccounts(final String loopscaleProgram, final String bsAuth) {
    return createAccounts(
        PublicKey.fromBase58Encoded(loopscaleProgram),
        PublicKey.fromBase58Encoded(bsAuth)
    );
  }

  static ProgramDerivedAddress eventAuthority(final PublicKey programId) {
    return PublicKey.findProgramAddress(
        List.of("__event_authority".getBytes(US_ASCII)),
        programId
    );
  }

  static ProgramDerivedAddress loanPda(final PublicKey borrower,
                                       final long nonce,
                                       final PublicKey programId) {
    final byte[] nonceBytes = new byte[Long.BYTES];
    ByteUtil.putInt64LE(nonceBytes, 0, nonce);
    return PublicKey.findProgramAddress(
        List.of(
            borrower.toByteArray(),
            nonceBytes
        ),
        programId
    );
  }

  AccountMeta invokedLoopscaleProgram();

  default PublicKey loopscaleProgram() {
    return invokedLoopscaleProgram().publicKey();
  }

  PublicKey eventAuthority();

  PublicKey bsAuth();

  default ProgramDerivedAddress loanPda(final PublicKey borrower, final long nonce) {
    return loanPda(borrower, nonce, loopscaleProgram());
  }
}

package software.sava.idl.clients.cctp.message_transmitter.v2.gen;

import java.util.List;

import software.sava.core.accounts.ProgramDerivedAddress;
import software.sava.core.accounts.PublicKey;

import static java.nio.charset.StandardCharsets.US_ASCII;

public final class MessageTransmitterV2PDAs {

  public static ProgramDerivedAddress authorityPdaPDA(final PublicKey program,
                                                      final PublicKey receiverAccount) {
    return PublicKey.findProgramAddress(List.of(
      "message_transmitter_authority".getBytes(US_ASCII),
      receiverAccount.toByteArray()
    ), program);
  }

  public static ProgramDerivedAddress eventAuthorityPDA(final PublicKey program) {
    return PublicKey.findProgramAddress(List.of(
      "__event_authority".getBytes(US_ASCII)
    ), program);
  }

  public static ProgramDerivedAddress messageTransmitterPDA(final PublicKey program) {
    return PublicKey.findProgramAddress(List.of(
      "message_transmitter".getBytes(US_ASCII)
    ), program);
  }

  public static ProgramDerivedAddress senderAuthorityPdaPDA(final PublicKey senderProgram) {
    return PublicKey.findProgramAddress(List.of(
      "sender_authority".getBytes(US_ASCII)
    ), senderProgram);
  }

  private MessageTransmitterV2PDAs() {
  }
}

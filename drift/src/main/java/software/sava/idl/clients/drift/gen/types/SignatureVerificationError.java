package software.sava.idl.clients.drift.gen.types;

import software.sava.core.borsh.Borsh;

public enum SignatureVerificationError implements Borsh.Enum {

  InvalidEd25519InstructionProgramId,
  InvalidEd25519InstructionDataLength,
  InvalidSignatureIndex,
  InvalidSignatureOffset,
  InvalidPublicKeyOffset,
  InvalidMessageOffset,
  InvalidMessageDataSize,
  InvalidInstructionIndex,
  MessageOffsetOverflow,
  InvalidMessageHex,
  InvalidMessageData,
  LoadInstructionAtFailed;

  public static SignatureVerificationError read(final byte[] _data, final int _offset) {
    return Borsh.read(SignatureVerificationError.values(), _data, _offset);
  }
}
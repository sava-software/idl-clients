package software.sava.idl.clients.drift.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

public enum SignatureVerificationError implements RustEnum {

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
    return SerDeUtil.read(1, SignatureVerificationError.values(), _data, _offset);
  }
}
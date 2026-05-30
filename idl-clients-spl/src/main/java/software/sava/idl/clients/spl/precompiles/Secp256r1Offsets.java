package software.sava.idl.clients.spl.precompiles;

import java.util.List;

import static software.sava.core.encoding.ByteUtil.getInt16LE;

/// Parsed Secp256r1 signature offsets record. Layout is identical to {@link Ed25519Offsets}
/// but the public key is 33 bytes (compressed) instead of 32.
public record Secp256r1Offsets(int signatureOffset,
                               int signatureInstructionIndex,
                               int publicKeyOffset,
                               int publicKeyInstructionIndex,
                               int messageDataOffset,
                               int messageDataSize,
                               int messageInstructionIndex) {

  public static Secp256r1Offsets read(final byte[] data, final int offset) {
    return new Secp256r1Offsets(
        getInt16LE(data, offset) & 0xFFFF,
        getInt16LE(data, offset + 2) & 0xFFFF,
        getInt16LE(data, offset + 4) & 0xFFFF,
        getInt16LE(data, offset + 6) & 0xFFFF,
        getInt16LE(data, offset + 8) & 0xFFFF,
        getInt16LE(data, offset + 10) & 0xFFFF,
        getInt16LE(data, offset + 12) & 0xFFFF
    );
  }

  /// Parses the Secp256r1 precompile instruction data into one or more {@link Secp256r1Offsets}.
  /// The instruction's header is {@code u8 count + u8 padding}, identical to Ed25519.
  public static Secp256r1Offsets[] parseOffsets(final byte[] data, final int offset) {
    final int count = data[offset] & 0xFF;
    final var offsets = new Secp256r1Offsets[count];
    int i = offset + SignatureVerifyProgram.SECP256R1_DATA_HEADER_LEN;
    for (int s = 0; s < count; s++) {
      offsets[s] = read(data, i);
      i += SignatureVerifyProgram.SECP256R1_OFFSETS_LEN;
    }
    return offsets;
  }

  public byte[] resolvePublicKey(final byte[] selfData, final List<byte[]> txInstructionData) {
    return SignatureVerifyProgram.slice(selfData, txInstructionData, publicKeyInstructionIndex, publicKeyOffset, SignatureVerifyProgram.SECP256R1_PUBKEY_LEN, SignatureVerifyProgram.SECP256R1_INSTRUCTION_INDEX_CURRENT);
  }

  public byte[] resolveSignature(final byte[] selfData, final List<byte[]> txInstructionData) {
    return SignatureVerifyProgram.slice(selfData, txInstructionData, signatureInstructionIndex, signatureOffset, SignatureVerifyProgram.SECP256R1_SIGNATURE_LEN, SignatureVerifyProgram.SECP256R1_INSTRUCTION_INDEX_CURRENT);
  }

  public byte[] resolveMessage(final byte[] selfData, final List<byte[]> txInstructionData) {
    return SignatureVerifyProgram.slice(selfData, txInstructionData, messageInstructionIndex, messageDataOffset, messageDataSize, SignatureVerifyProgram.SECP256R1_INSTRUCTION_INDEX_CURRENT);
  }
}

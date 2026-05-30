package software.sava.idl.clients.spl.precompiles;

import software.sava.core.tx.Transaction;

import java.util.List;

import static software.sava.core.accounts.PublicKey.PUBLIC_KEY_LENGTH;
import static software.sava.core.encoding.ByteUtil.getInt16LE;

/// Parsed Ed25519 signature offsets record (one per signature in the instruction).
///
/// Each {@code *InstructionIndex} field is the transaction-instruction index that holds the
/// corresponding region (signature / public key / message). The value
/// {@link #ED25519_INSTRUCTION_INDEX_CURRENT} (0xFFFF) means "this instruction's own data".
///
/// Use {@link #resolvePublicKey(byte[], List)}, {@link #resolveSignature(byte[], List)}, and
/// {@link #resolveMessage(byte[], List)} to extract the referenced bytes given the precompile
/// instruction's own data plus the full list of transaction-instruction data buffers.
public record Ed25519Offsets(int signatureOffset,
                             int signatureInstructionIndex,
                             int publicKeyOffset,
                             int publicKeyInstructionIndex,
                             int messageDataOffset,
                             int messageDataSize,
                             int messageInstructionIndex) {

  public static Ed25519Offsets read(final byte[] data, final int offset) {
    return new Ed25519Offsets(
        getInt16LE(data, offset) & 0xFFFF,
        getInt16LE(data, offset + 2) & 0xFFFF,
        getInt16LE(data, offset + 4) & 0xFFFF,
        getInt16LE(data, offset + 6) & 0xFFFF,
        getInt16LE(data, offset + 8) & 0xFFFF,
        getInt16LE(data, offset + 10) & 0xFFFF,
        getInt16LE(data, offset + 12) & 0xFFFF
    );
  }

  /// Parses the Ed25519 precompile instruction data into one or more {@link Ed25519Offsets}.
  /// The instruction's header is {@code u8 count + u8 padding}; the example at
  /// {@code symlinks/deps/solana-programs/.../Ed25519SignatureOffsets.java} reads count as a
  /// {@code u16} which is incorrect — this parser uses the correct layout.
  public static Ed25519Offsets[] parseOffsets(final byte[] data, final int offset) {
    final int count = data[offset] & 0xFF;
    final var offsets = new Ed25519Offsets[count];
    int i = offset + SignatureVerifyProgram.ED25519_DATA_HEADER_LEN;
    for (int s = 0; s < count; s++) {
      offsets[s] = read(data, i);
      i += SignatureVerifyProgram.ED25519_OFFSETS_LEN;
    }
    return offsets;
  }

  public byte[] resolvePublicKey(final byte[] selfData, final List<byte[]> txInstructionData) {
    return SignatureVerifyProgram.slice(selfData, txInstructionData, publicKeyInstructionIndex, publicKeyOffset, PUBLIC_KEY_LENGTH, SignatureVerifyProgram.ED25519_INSTRUCTION_INDEX_CURRENT);
  }

  public byte[] resolveSignature(final byte[] selfData, final List<byte[]> txInstructionData) {
    return SignatureVerifyProgram.slice(selfData, txInstructionData, signatureInstructionIndex, signatureOffset, Transaction.SIGNATURE_LENGTH, SignatureVerifyProgram.ED25519_INSTRUCTION_INDEX_CURRENT);
  }

  public byte[] resolveMessage(final byte[] selfData, final List<byte[]> txInstructionData) {
    return SignatureVerifyProgram.slice(selfData, txInstructionData, messageInstructionIndex, messageDataOffset, messageDataSize, SignatureVerifyProgram.ED25519_INSTRUCTION_INDEX_CURRENT);
  }
}

package software.sava.idl.clients.spl.precompiles;

import java.util.List;

import static software.sava.core.encoding.ByteUtil.getInt16LE;
import static software.sava.idl.clients.spl.precompiles.SignatureVerifyProgram.SECP256K1_DATA_HEADER_LEN;
import static software.sava.idl.clients.spl.precompiles.SignatureVerifyProgram.SECP256K1_OFFSETS_LEN;

/// Parsed Secp256k1 signature offsets record (one per signature in the instruction).
///
/// Unlike Ed25519 and Secp256r1, Secp256k1 has **no** "current instruction" sentinel: the
/// {@code *InstructionIndex} fields are literal {@code u8} transaction-instruction indexes
/// and must match the actual instruction position.
public record Secp256k1Offsets(int signatureOffset,
                               int signatureInstructionIndex,
                               int ethAddressOffset,
                               int ethAddressInstructionIndex,
                               int messageDataOffset,
                               int messageDataSize,
                               int messageInstructionIndex) {

  public static Secp256k1Offsets read(final byte[] data, final int offset) {
    return new Secp256k1Offsets(
        getInt16LE(data, offset) & 0xFFFF,
        data[offset + 2] & 0xFF,
        getInt16LE(data, offset + 3) & 0xFFFF,
        data[offset + 5] & 0xFF,
        getInt16LE(data, offset + 6) & 0xFFFF,
        getInt16LE(data, offset + 8) & 0xFFFF,
        data[offset + 10] & 0xFF
    );
  }

  /// Parses the Secp256k1 precompile instruction data into one or more {@link Secp256k1Offsets}.
  /// The instruction's header is a single {@code u8 count} (no padding).
  public static Secp256k1Offsets[] parseOffsets(final byte[] data, final int offset) {
    final int count = data[offset] & 0xFF;
    final var offsets = new Secp256k1Offsets[count];
    int i = offset + SECP256K1_DATA_HEADER_LEN;
    for (int s = 0; s < count; s++) {
      offsets[s] = Secp256k1Offsets.read(data, i);
      i += SECP256K1_OFFSETS_LEN;
    }
    return offsets;
  }

  public byte[] resolveEthAddress(final List<byte[]> txInstructionData) {
    return SignatureVerifyProgram.slice(null, txInstructionData, ethAddressInstructionIndex, ethAddressOffset, SignatureVerifyProgram.SECP256K1_ETH_ADDRESS_LEN, -1);
  }

  /// Returns the 64-byte signature followed by the 1-byte recovery id (65 bytes total).
  /// The Secp256k1 precompile stores recovery id immediately after the signature.
  public byte[] resolveSignatureAndRecoveryId(final List<byte[]> txInstructionData) {
    return SignatureVerifyProgram.slice(null, txInstructionData, signatureInstructionIndex, signatureOffset, SignatureVerifyProgram.SECP256K1_SIGNATURE_LEN + SignatureVerifyProgram.SECP256K1_RECOVERY_ID_LEN, -1);
  }

  public byte[] resolveMessage(final List<byte[]> txInstructionData) {
    return SignatureVerifyProgram.slice(null, txInstructionData, messageInstructionIndex, messageDataOffset, messageDataSize, -1);
  }
}

package software.sava.idl.clients.spl.precompiles;

import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.tx.Instruction;
import software.sava.core.tx.Transaction;

import java.util.List;

import static software.sava.core.accounts.PublicKey.PUBLIC_KEY_LENGTH;
import static software.sava.core.accounts.PublicKey.fromBase58Encoded;
import static software.sava.core.encoding.ByteUtil.putInt16LE;

/// Static helper functions for building instructions for Solana's native (precompiled)
/// signature verification programs.
public final class SignatureVerifyProgram {

  public static final PublicKey SECP256R1_PROGRAM = fromBase58Encoded("Secp256r1SigVerify1111111111111111111111111");

  /// The message length, signature offset and related fields are encoded as unsigned 16-bit
  /// little-endian integers, so no individual field may exceed this value.
  public static final int MAX_OFFSET = 0xFFFF;

  // u8 count + u8 padding
  public static final int ED25519_DATA_HEADER_LEN = 2;
  // 7 little-endian u16 offset/index fields
  public static final int ED25519_OFFSETS_LEN = 14;
  // index into the current instruction's data
  public static final int ED25519_INSTRUCTION_INDEX_CURRENT = 0xFFFF;

  // Secp256k1
  public static final int SECP256K1_ETH_ADDRESS_LEN = 20;
  public static final int SECP256K1_SIGNATURE_LEN = 64;
  public static final int SECP256K1_RECOVERY_ID_LEN = 1;
  // u8 count only
  public static final int SECP256K1_DATA_HEADER_LEN = 1;
  // 5 little-endian u16 fields + 1 u8 instruction index field
  public static final int SECP256K1_OFFSETS_LEN = 11;

  // Secp256r1
  public static final int SECP256R1_PUBKEY_LEN = 33;
  public static final int SECP256R1_SIGNATURE_LEN = 64;
  // u8 count + u8 padding
  public static final int SECP256R1_DATA_HEADER_LEN = 2;
  // 7 little-endian u16 offset/index fields
  public static final int SECP256R1_OFFSETS_LEN = 14;
  public static final int SECP256R1_INSTRUCTION_INDEX_CURRENT = 0xFFFF;

  private SignatureVerifyProgram() {
  }

  private static void validateMessageLength(final byte[] message) {
    if (message.length > MAX_OFFSET) {
      throw new IllegalArgumentException(
          "Message must not exceed " + MAX_OFFSET + " bytes, but was " + message.length);
    }
  }

  private static void validateOffset(final String name, final int value) {
    if (value < 0 || value > MAX_OFFSET) {
      throw new IllegalArgumentException(
          name + " must be in the range [0, " + MAX_OFFSET + "], but was " + value);
    }
  }

  // ---------------------------------------------------------------------------
  // Ed25519
  // ---------------------------------------------------------------------------

  /// Builds an Ed25519 verification instruction for a single {@code (publicKey, signature, message)}
  /// tuple, with all data colocated in this instruction.
  ///
  /// @param ed25519Program the invoked Ed25519 precompile (no accounts are attached)
  /// @param publicKey      32-byte Ed25519 public key
  /// @param signature      64-byte signature
  /// @param message        the signed message bytes
  public static Instruction verifyEd25519(final AccountMeta ed25519Program,
                                          final PublicKey publicKey,
                                          final byte[] signature,
                                          final byte[] message) {
    if (signature.length != Transaction.SIGNATURE_LENGTH) {
      throw new IllegalArgumentException("Ed25519 signature must be " + Transaction.SIGNATURE_LENGTH + " bytes");
    }
    validateMessageLength(message);

    final int publicKeyOffset = ED25519_DATA_HEADER_LEN + ED25519_OFFSETS_LEN;
    final int signatureOffset = publicKeyOffset + PUBLIC_KEY_LENGTH;
    final int messageOffset = signatureOffset + Transaction.SIGNATURE_LENGTH;

    final byte[] data = new byte[messageOffset + message.length];

    // header
    data[0] = 1; // number of signatures
    data[1] = 0; // padding

    putInt16LE(data, ED25519_DATA_HEADER_LEN, signatureOffset);
    putInt16LE(data, ED25519_DATA_HEADER_LEN + 2, ED25519_INSTRUCTION_INDEX_CURRENT);
    putInt16LE(data, ED25519_DATA_HEADER_LEN + 4, publicKeyOffset);
    putInt16LE(data, ED25519_DATA_HEADER_LEN + 6, ED25519_INSTRUCTION_INDEX_CURRENT);
    putInt16LE(data, ED25519_DATA_HEADER_LEN + 8, messageOffset);
    putInt16LE(data, ED25519_DATA_HEADER_LEN + 10, message.length);
    putInt16LE(data, ED25519_DATA_HEADER_LEN + 12, ED25519_INSTRUCTION_INDEX_CURRENT);

    publicKey.write(data, publicKeyOffset);
    System.arraycopy(signature, 0, data, signatureOffset, Transaction.SIGNATURE_LENGTH);
    System.arraycopy(message, 0, data, messageOffset, message.length);

    return Instruction.createInstruction(ed25519Program, List.of(), data);
  }

  /// Builds an Ed25519 verification instruction for a single {@code (publicKey, signature, message)}
  /// tuple, where the public key and signature are colocated in this instruction but the signed
  /// message lives in **another** instruction's data within the same transaction.
  ///
  /// The Ed25519 offsets struct carries a dedicated {@code message_instruction_index} alongside
  /// {@code message_data_offset}/{@code message_data_size}, so the message does not have to be
  /// duplicated inside this instruction. This is useful when the signed bytes are already carried
  /// by another instruction in the transaction (e.g. a program or memo instruction). The message
  /// is therefore not included in the data this builder produces.
  ///
  /// @param ed25519Program          the invoked Ed25519 precompile (no accounts are attached)
  /// @param publicKey               32-byte Ed25519 public key
  /// @param signature               64-byte signature
  /// @param messageInstructionIndex the transaction index of the instruction holding the message
  ///                                (use {@link #ED25519_INSTRUCTION_INDEX_CURRENT} to reference
  ///                                this instruction's own data)
  /// @param messageOffset           the byte offset of the message within that instruction's data
  /// @param messageSize             the length of the message in bytes
  public static Instruction verifyEd25519(final AccountMeta ed25519Program,
                                          final PublicKey publicKey,
                                          final byte[] signature,
                                          final int messageInstructionIndex,
                                          final int messageOffset,
                                          final int messageSize) {
    if (signature.length != Transaction.SIGNATURE_LENGTH) {
      throw new IllegalArgumentException("Ed25519 signature must be " + Transaction.SIGNATURE_LENGTH + " bytes");
    }
    validateOffset("Ed25519 message instruction index", messageInstructionIndex);
    validateOffset("Ed25519 message offset", messageOffset);
    validateOffset("Ed25519 message size", messageSize);

    final int publicKeyOffset = ED25519_DATA_HEADER_LEN + ED25519_OFFSETS_LEN;
    final int signatureOffset = publicKeyOffset + PUBLIC_KEY_LENGTH;

    final byte[] data = new byte[signatureOffset + Transaction.SIGNATURE_LENGTH];

    // header
    data[0] = 1; // number of signatures
    data[1] = 0; // padding

    putInt16LE(data, ED25519_DATA_HEADER_LEN, signatureOffset);
    putInt16LE(data, ED25519_DATA_HEADER_LEN + 2, ED25519_INSTRUCTION_INDEX_CURRENT);
    putInt16LE(data, ED25519_DATA_HEADER_LEN + 4, publicKeyOffset);
    putInt16LE(data, ED25519_DATA_HEADER_LEN + 6, ED25519_INSTRUCTION_INDEX_CURRENT);
    putInt16LE(data, ED25519_DATA_HEADER_LEN + 8, messageOffset);
    putInt16LE(data, ED25519_DATA_HEADER_LEN + 10, messageSize);
    putInt16LE(data, ED25519_DATA_HEADER_LEN + 12, messageInstructionIndex);

    publicKey.write(data, publicKeyOffset);
    System.arraycopy(signature, 0, data, signatureOffset, Transaction.SIGNATURE_LENGTH);

    return Instruction.createInstruction(ed25519Program, List.of(), data);
  }

  /// Builds an Ed25519 verification instruction for a single {@code (publicKey, signature, message)}
  /// tuple, where each of the public key, signature, and message can independently live in
  /// **this** instruction's data or in any other instruction's data within the same transaction.
  ///
  /// The Ed25519 offsets struct carries an independent {@code *_instruction_index} field for
  /// each of the three regions, so callers can mix and match: pass
  /// {@link #ED25519_INSTRUCTION_INDEX_CURRENT} to reference this instruction's own data, or
  /// any other transaction-instruction index to point at another instruction's data. None of
  /// the three regions is written into this builder's output — callers are responsible for
  /// ensuring the referenced bytes exist at the offsets they specify.
  ///
  /// @param ed25519Program            the invoked Ed25519 precompile (no accounts are attached)
  /// @param publicKeyInstructionIndex the transaction index of the instruction holding the public key
  ///                                  (use {@link #ED25519_INSTRUCTION_INDEX_CURRENT} for this instruction)
  /// @param publicKeyOffset           the byte offset of the public key within that instruction's data
  /// @param signatureInstructionIndex the transaction index of the instruction holding the signature
  ///                                  (use {@link #ED25519_INSTRUCTION_INDEX_CURRENT} for this instruction)
  /// @param signatureOffset           the byte offset of the signature within that instruction's data
  /// @param messageInstructionIndex   the transaction index of the instruction holding the message
  ///                                  (use {@link #ED25519_INSTRUCTION_INDEX_CURRENT} for this instruction)
  /// @param messageOffset             the byte offset of the message within that instruction's data
  /// @param messageSize               the length of the message in bytes
  public static Instruction verifyEd25519(final AccountMeta ed25519Program,
                                          final int publicKeyInstructionIndex,
                                          final int publicKeyOffset,
                                          final int signatureInstructionIndex,
                                          final int signatureOffset,
                                          final int messageInstructionIndex,
                                          final int messageOffset,
                                          final int messageSize) {
    validateOffset("Ed25519 public key instruction index", publicKeyInstructionIndex);
    validateOffset("Ed25519 public key offset", publicKeyOffset);
    validateOffset("Ed25519 signature instruction index", signatureInstructionIndex);
    validateOffset("Ed25519 signature offset", signatureOffset);
    validateOffset("Ed25519 message instruction index", messageInstructionIndex);
    validateOffset("Ed25519 message offset", messageOffset);
    validateOffset("Ed25519 message size", messageSize);

    final byte[] data = new byte[ED25519_DATA_HEADER_LEN + ED25519_OFFSETS_LEN];

    // header
    data[0] = 1; // number of signatures
    data[1] = 0; // padding

    putInt16LE(data, ED25519_DATA_HEADER_LEN, signatureOffset);
    putInt16LE(data, ED25519_DATA_HEADER_LEN + 2, signatureInstructionIndex);
    putInt16LE(data, ED25519_DATA_HEADER_LEN + 4, publicKeyOffset);
    putInt16LE(data, ED25519_DATA_HEADER_LEN + 6, publicKeyInstructionIndex);
    putInt16LE(data, ED25519_DATA_HEADER_LEN + 8, messageOffset);
    putInt16LE(data, ED25519_DATA_HEADER_LEN + 10, messageSize);
    putInt16LE(data, ED25519_DATA_HEADER_LEN + 12, messageInstructionIndex);

    return Instruction.createInstruction(ed25519Program, List.of(), data);
  }

  // ---------------------------------------------------------------------------
  // Secp256k1
  // ---------------------------------------------------------------------------

  /// Builds a Secp256k1 verification instruction for a single
  /// {@code (ethAddress, signature, recoveryId, message)} tuple, with all data colocated
  /// in this instruction.
  ///
  /// Unlike the Ed25519 and Secp256r1 precompiles, the Secp256k1 program has no
  /// "current instruction" sentinel: it always reads the literal instruction indexes
  /// encoded in the offsets struct. This overload hardcodes those indexes to {@code 0},
  /// so the produced instruction **must be placed at index 0 of the transaction**.
  /// Use {@link #verifySecp256k1(AccountMeta, byte[], byte[], byte, byte[], int)} to
  /// place the instruction at any other position.
  ///
  /// @param secp256k1Program the invoked Secp256k1 precompile (no accounts are attached)
  /// @param ethAddress       20-byte Ethereum address (keccak of the uncompressed pubkey)
  /// @param signature        64-byte signature
  /// @param recoveryId       the recovery id byte (0-3)
  /// @param message          the signed message bytes
  public static Instruction verifySecp256k1(final AccountMeta secp256k1Program,
                                            final byte[] ethAddress,
                                            final byte[] signature,
                                            final byte recoveryId,
                                            final byte[] message) {
    return verifySecp256k1(secp256k1Program, ethAddress, signature, recoveryId, message, 0);
  }

  /// Builds a Secp256k1 verification instruction for a single
  /// {@code (ethAddress, signature, recoveryId, message)} tuple, with all data colocated
  /// in this instruction.
  ///
  /// The Secp256k1 program has no "current instruction" sentinel, so the index of the
  /// instruction that holds the data must be encoded explicitly. Since all data is
  /// colocated in the produced instruction, {@code instructionIndex} must equal the
  /// position at which this instruction will be inserted into the transaction.
  ///
  /// @param secp256k1Program the invoked Secp256k1 precompile (no accounts are attached)
  /// @param ethAddress       20-byte Ethereum address (keccak of the uncompressed pubkey)
  /// @param signature        64-byte signature
  /// @param recoveryId       the recovery id byte (0-3)
  /// @param message          the signed message bytes
  /// @param instructionIndex the transaction index at which this instruction will be placed
  public static Instruction verifySecp256k1(final AccountMeta secp256k1Program,
                                            final byte[] ethAddress,
                                            final byte[] signature,
                                            final byte recoveryId,
                                            final byte[] message,
                                            final int instructionIndex) {
    if (ethAddress.length != SECP256K1_ETH_ADDRESS_LEN) {
      throw new IllegalArgumentException("Secp256k1 eth address must be " + SECP256K1_ETH_ADDRESS_LEN + " bytes");
    }
    if (signature.length != SECP256K1_SIGNATURE_LEN) {
      throw new IllegalArgumentException("Secp256k1 signature must be " + SECP256K1_SIGNATURE_LEN + " bytes");
    }
    if (recoveryId < 0 || recoveryId > 3) {
      throw new IllegalArgumentException("Secp256k1 recovery id must be in the range [0, 3], but was " + recoveryId);
    }
    if (instructionIndex < 0 || instructionIndex > 0xFF) {
      throw new IllegalArgumentException("Secp256k1 instruction index must be in the range [0, 255], but was " + instructionIndex);
    }
    validateMessageLength(message);

    final int ethAddressOffset = SECP256K1_DATA_HEADER_LEN + SECP256K1_OFFSETS_LEN;
    final int signatureOffset = ethAddressOffset + SECP256K1_ETH_ADDRESS_LEN;
    final int recoveryIdOffset = signatureOffset + SECP256K1_SIGNATURE_LEN;
    final int messageOffset = recoveryIdOffset + SECP256K1_RECOVERY_ID_LEN;

    final byte[] data = new byte[messageOffset + message.length];

    data[0] = 1; // number of signatures

    final byte instructionIndexByte = (byte) instructionIndex;
    putInt16LE(data, SECP256K1_DATA_HEADER_LEN, signatureOffset);
    data[SECP256K1_DATA_HEADER_LEN + 2] = instructionIndexByte; // signature instruction index
    putInt16LE(data, SECP256K1_DATA_HEADER_LEN + 3, ethAddressOffset);
    data[SECP256K1_DATA_HEADER_LEN + 5] = instructionIndexByte; // eth address instruction index
    putInt16LE(data, SECP256K1_DATA_HEADER_LEN + 6, messageOffset);
    putInt16LE(data, SECP256K1_DATA_HEADER_LEN + 8, message.length);
    data[SECP256K1_DATA_HEADER_LEN + 10] = instructionIndexByte; // message instruction index

    System.arraycopy(ethAddress, 0, data, ethAddressOffset, SECP256K1_ETH_ADDRESS_LEN);
    System.arraycopy(signature, 0, data, signatureOffset, SECP256K1_SIGNATURE_LEN);
    data[recoveryIdOffset] = recoveryId;
    System.arraycopy(message, 0, data, messageOffset, message.length);

    return Instruction.createInstruction(secp256k1Program, List.of(), data);
  }

  // ---------------------------------------------------------------------------
  // Secp256r1
  // ---------------------------------------------------------------------------

  /// Builds a Secp256r1 verification instruction for a single
  /// {@code (publicKey, signature, message)} tuple, with all data colocated in this instruction.
  ///
  /// @param secp256r1Program the invoked Secp256r1 precompile (no accounts are attached)
  /// @param publicKey        33-byte compressed Secp256r1 public key
  /// @param signature        64-byte signature
  /// @param message          the signed message bytes
  public static Instruction verifySecp256r1(final AccountMeta secp256r1Program,
                                            final byte[] publicKey,
                                            final byte[] signature,
                                            final byte[] message) {
    if (publicKey.length != SECP256R1_PUBKEY_LEN) {
      throw new IllegalArgumentException("Secp256r1 public key must be " + SECP256R1_PUBKEY_LEN + " bytes");
    }
    if (signature.length != SECP256R1_SIGNATURE_LEN) {
      throw new IllegalArgumentException("Secp256r1 signature must be " + SECP256R1_SIGNATURE_LEN + " bytes");
    }
    validateMessageLength(message);

    final int publicKeyOffset = SECP256R1_DATA_HEADER_LEN + SECP256R1_OFFSETS_LEN;
    final int signatureOffset = publicKeyOffset + SECP256R1_PUBKEY_LEN;
    final int messageOffset = signatureOffset + SECP256R1_SIGNATURE_LEN;

    final byte[] data = new byte[messageOffset + message.length];

    data[0] = 1; // number of signatures
    data[1] = 0; // padding

    putInt16LE(data, SECP256R1_DATA_HEADER_LEN, signatureOffset);
    putInt16LE(data, SECP256R1_DATA_HEADER_LEN + 2, SECP256R1_INSTRUCTION_INDEX_CURRENT);
    putInt16LE(data, SECP256R1_DATA_HEADER_LEN + 4, publicKeyOffset);
    putInt16LE(data, SECP256R1_DATA_HEADER_LEN + 6, SECP256R1_INSTRUCTION_INDEX_CURRENT);
    putInt16LE(data, SECP256R1_DATA_HEADER_LEN + 8, messageOffset);
    putInt16LE(data, SECP256R1_DATA_HEADER_LEN + 10, message.length);
    putInt16LE(data, SECP256R1_DATA_HEADER_LEN + 12, SECP256R1_INSTRUCTION_INDEX_CURRENT);

    System.arraycopy(publicKey, 0, data, publicKeyOffset, SECP256R1_PUBKEY_LEN);
    System.arraycopy(signature, 0, data, signatureOffset, SECP256R1_SIGNATURE_LEN);
    System.arraycopy(message, 0, data, messageOffset, message.length);

    return Instruction.createInstruction(secp256r1Program, List.of(), data);
  }

  /// Builds a Secp256r1 verification instruction for a single {@code (publicKey, signature, message)}
  /// tuple, where the public key and signature are colocated in this instruction but the signed
  /// message lives in **another** instruction's data within the same transaction.
  ///
  /// Like Ed25519, the Secp256r1 offsets struct carries a dedicated {@code message_instruction_index}
  /// alongside {@code message_data_offset}/{@code message_data_size}, so the message does not have
  /// to be duplicated inside this instruction. The message is therefore not included in the data
  /// this builder produces.
  ///
  /// @param secp256r1Program        the invoked Secp256r1 precompile (no accounts are attached)
  /// @param publicKey               33-byte compressed Secp256r1 public key
  /// @param signature               64-byte signature
  /// @param messageInstructionIndex the transaction index of the instruction holding the message
  ///                                (use {@link #SECP256R1_INSTRUCTION_INDEX_CURRENT} to reference
  ///                                this instruction's own data)
  /// @param messageOffset           the byte offset of the message within that instruction's data
  /// @param messageSize             the length of the message in bytes
  public static Instruction verifySecp256r1(final AccountMeta secp256r1Program,
                                            final byte[] publicKey,
                                            final byte[] signature,
                                            final int messageInstructionIndex,
                                            final int messageOffset,
                                            final int messageSize) {
    if (publicKey.length != SECP256R1_PUBKEY_LEN) {
      throw new IllegalArgumentException("Secp256r1 public key must be " + SECP256R1_PUBKEY_LEN + " bytes");
    }
    if (signature.length != SECP256R1_SIGNATURE_LEN) {
      throw new IllegalArgumentException("Secp256r1 signature must be " + SECP256R1_SIGNATURE_LEN + " bytes");
    }
    validateOffset("Secp256r1 message instruction index", messageInstructionIndex);
    validateOffset("Secp256r1 message offset", messageOffset);
    validateOffset("Secp256r1 message size", messageSize);

    final int publicKeyOffset = SECP256R1_DATA_HEADER_LEN + SECP256R1_OFFSETS_LEN;
    final int signatureOffset = publicKeyOffset + SECP256R1_PUBKEY_LEN;

    final byte[] data = new byte[signatureOffset + SECP256R1_SIGNATURE_LEN];

    data[0] = 1; // number of signatures
    data[1] = 0; // padding

    putInt16LE(data, SECP256R1_DATA_HEADER_LEN, signatureOffset);
    putInt16LE(data, SECP256R1_DATA_HEADER_LEN + 2, SECP256R1_INSTRUCTION_INDEX_CURRENT);
    putInt16LE(data, SECP256R1_DATA_HEADER_LEN + 4, publicKeyOffset);
    putInt16LE(data, SECP256R1_DATA_HEADER_LEN + 6, SECP256R1_INSTRUCTION_INDEX_CURRENT);
    putInt16LE(data, SECP256R1_DATA_HEADER_LEN + 8, messageOffset);
    putInt16LE(data, SECP256R1_DATA_HEADER_LEN + 10, messageSize);
    putInt16LE(data, SECP256R1_DATA_HEADER_LEN + 12, messageInstructionIndex);

    System.arraycopy(publicKey, 0, data, publicKeyOffset, SECP256R1_PUBKEY_LEN);
    System.arraycopy(signature, 0, data, signatureOffset, SECP256R1_SIGNATURE_LEN);

    return Instruction.createInstruction(secp256r1Program, List.of(), data);
  }

  /// Resolves a `(instructionIndex, offset, length)` window against either the precompile
  /// instruction's own data or another instruction's data in the same transaction.
  ///
  /// @param currentSentinel the index value meaning "this instruction's own data" (0xFFFF for
  ///                        Ed25519 and Secp256r1), or `-1` for Secp256k1, which has no such
  ///                        sentinel. Every caller derives `instructionIndex` from a masked
  ///                        read, so it is never negative and `-1` therefore matches nothing —
  ///                        no separate guard for the no-sentinel case is needed.
  static byte[] slice(final byte[] selfData,
                      final List<byte[]> txInstructionData,
                      final int instructionIndex,
                      final int offset,
                      final int length,
                      final int currentSentinel) {
    final byte[] source;
    if (instructionIndex == currentSentinel) {
      if (selfData == null) {
        throw new IllegalArgumentException("'current instruction' data not provided");
      }
      source = selfData;
    } else {
      if (txInstructionData == null || instructionIndex >= txInstructionData.size()) {
        throw new IllegalArgumentException("instruction index " + instructionIndex + " out of bounds");
      }
      source = txInstructionData.get(instructionIndex);
    }
    final byte[] out = new byte[length];
    System.arraycopy(source, offset, out, 0, length);
    return out;
  }

}

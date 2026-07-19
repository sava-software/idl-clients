package software.sava.idl.clients.spl.precompiles;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static software.sava.core.encoding.ByteUtil.putInt16LE;
import static software.sava.idl.clients.spl.precompiles.SignatureVerifyProgram.*;

/// Unit coverage for the three offsets records and the shared [SignatureVerifyProgram#slice]
/// resolver. `PrecompileOffsetsFuzz` already establishes that hostile bytes produce a
/// [RuntimeException] rather than a crash; these tests pin the *values* — which byte of the
/// record lands in which component, and which buffer a component resolves against. A precompile
/// dereferences these offsets directly, so an off-by-two here checks a signature against the
/// wrong bytes rather than failing to parse.
final class PrecompileOffsetsTests {

  /// Every field gets a distinct value so that a mutated read offset lands on a different
  /// number instead of an identical one. The values are also distinct from the base offset
  /// the record is written at, so a dropped or negated base is visible too.
  private static final int SIG_OFFSET = 0x1101;
  private static final int SIG_IX_INDEX = 0x22;
  private static final int KEY_OFFSET = 0x3303;
  private static final int KEY_IX_INDEX = 0x44;
  private static final int MSG_OFFSET = 0x5505;
  private static final int MSG_SIZE = 0x6606;
  private static final int MSG_IX_INDEX = 0x77;

  /// A non-zero base, so `data[offset]` mutated to `data[0]` reads a different byte.
  private static final int BASE = 5;

  private static byte[] bytes(final int len, final int seed) {
    final byte[] out = new byte[len];
    for (int i = 0; i < len; i++) {
      out[i] = (byte) (seed + i);
    }
    return out;
  }

  /// 14-byte Ed25519/Secp256r1 offsets record: 7 little-endian u16 fields.
  private static void writeWideRecord(final byte[] data, final int offset, final int bump) {
    putInt16LE(data, offset, SIG_OFFSET + bump);
    putInt16LE(data, offset + 2, SIG_IX_INDEX + bump);
    putInt16LE(data, offset + 4, KEY_OFFSET + bump);
    putInt16LE(data, offset + 6, KEY_IX_INDEX + bump);
    putInt16LE(data, offset + 8, MSG_OFFSET + bump);
    putInt16LE(data, offset + 10, MSG_SIZE + bump);
    putInt16LE(data, offset + 12, MSG_IX_INDEX + bump);
  }

  /// 11-byte Secp256k1 offsets record: 4 u16 fields interleaved with 3 u8 instruction indexes.
  private static void writeNarrowRecord(final byte[] data, final int offset, final int bump) {
    putInt16LE(data, offset, SIG_OFFSET + bump);
    data[offset + 2] = (byte) (SIG_IX_INDEX + bump);
    putInt16LE(data, offset + 3, KEY_OFFSET + bump);
    data[offset + 5] = (byte) (KEY_IX_INDEX + bump);
    putInt16LE(data, offset + 6, MSG_OFFSET + bump);
    putInt16LE(data, offset + 8, MSG_SIZE + bump);
    data[offset + 10] = (byte) (MSG_IX_INDEX + bump);
  }

  // ---------------------------------------------------------------------------
  // read(): each component comes from its own byte position
  // ---------------------------------------------------------------------------

  @Test
  void ed25519ReadFieldPositions() {
    final byte[] data = new byte[BASE + ED25519_OFFSETS_LEN];
    writeWideRecord(data, BASE, 0);

    final var offsets = Ed25519Offsets.read(data, BASE);
    assertEquals(SIG_OFFSET, offsets.signatureOffset());
    assertEquals(SIG_IX_INDEX, offsets.signatureInstructionIndex());
    assertEquals(KEY_OFFSET, offsets.publicKeyOffset());
    assertEquals(KEY_IX_INDEX, offsets.publicKeyInstructionIndex());
    assertEquals(MSG_OFFSET, offsets.messageDataOffset());
    assertEquals(MSG_SIZE, offsets.messageDataSize());
    assertEquals(MSG_IX_INDEX, offsets.messageInstructionIndex());
  }

  @Test
  void secp256r1ReadFieldPositions() {
    final byte[] data = new byte[BASE + SECP256R1_OFFSETS_LEN];
    writeWideRecord(data, BASE, 0);

    final var offsets = Secp256r1Offsets.read(data, BASE);
    assertEquals(SIG_OFFSET, offsets.signatureOffset());
    assertEquals(SIG_IX_INDEX, offsets.signatureInstructionIndex());
    assertEquals(KEY_OFFSET, offsets.publicKeyOffset());
    assertEquals(KEY_IX_INDEX, offsets.publicKeyInstructionIndex());
    assertEquals(MSG_OFFSET, offsets.messageDataOffset());
    assertEquals(MSG_SIZE, offsets.messageDataSize());
    assertEquals(MSG_IX_INDEX, offsets.messageInstructionIndex());
  }

  @Test
  void secp256k1ReadFieldPositions() {
    final byte[] data = new byte[BASE + SECP256K1_OFFSETS_LEN];
    writeNarrowRecord(data, BASE, 0);

    final var offsets = Secp256k1Offsets.read(data, BASE);
    assertEquals(SIG_OFFSET, offsets.signatureOffset());
    assertEquals(SIG_IX_INDEX, offsets.signatureInstructionIndex());
    assertEquals(KEY_OFFSET, offsets.ethAddressOffset());
    assertEquals(KEY_IX_INDEX, offsets.ethAddressInstructionIndex());
    assertEquals(MSG_OFFSET, offsets.messageDataOffset());
    assertEquals(MSG_SIZE, offsets.messageDataSize());
    assertEquals(MSG_IX_INDEX, offsets.messageInstructionIndex());
  }

  /// The u16 fields are unsigned: 0xFFFF must widen to 65535, not to -1.
  @Test
  void wideFieldsAreUnsigned() {
    final byte[] data = new byte[ED25519_OFFSETS_LEN];
    for (int i = 0; i < data.length; i++) {
      data[i] = (byte) 0xFF;
    }
    final var ed25519 = Ed25519Offsets.read(data, 0);
    assertEquals(0xFFFF, ed25519.signatureOffset());
    assertEquals(0xFFFF, ed25519.messageInstructionIndex());

    final var secp256r1 = Secp256r1Offsets.read(data, 0);
    assertEquals(0xFFFF, secp256r1.signatureOffset());
    assertEquals(0xFFFF, secp256r1.messageInstructionIndex());

    final var secp256k1 = Secp256k1Offsets.read(data, 0);
    assertEquals(0xFFFF, secp256k1.signatureOffset());
    assertEquals(0xFF, secp256k1.messageInstructionIndex());
  }

  // ---------------------------------------------------------------------------
  // parseOffsets(): header count, record stride, and starting position
  // ---------------------------------------------------------------------------

  @Test
  void ed25519ParsesEveryRecordAtItsOwnStride() {
    final int count = 3;
    final byte[] data = new byte[BASE + ED25519_DATA_HEADER_LEN + (count * ED25519_OFFSETS_LEN)];
    data[BASE] = (byte) count;
    for (int s = 0; s < count; s++) {
      writeWideRecord(data, BASE + ED25519_DATA_HEADER_LEN + (s * ED25519_OFFSETS_LEN), s);
    }

    final var offsets = Ed25519Offsets.parseOffsets(data, BASE);
    assertEquals(count, offsets.length);
    for (int s = 0; s < count; s++) {
      assertEquals(SIG_OFFSET + s, offsets[s].signatureOffset(), "record " + s);
      assertEquals(MSG_IX_INDEX + s, offsets[s].messageInstructionIndex(), "record " + s);
    }
  }

  @Test
  void secp256r1ParsesEveryRecordAtItsOwnStride() {
    final int count = 3;
    final byte[] data = new byte[BASE + SECP256R1_DATA_HEADER_LEN + (count * SECP256R1_OFFSETS_LEN)];
    data[BASE] = (byte) count;
    for (int s = 0; s < count; s++) {
      writeWideRecord(data, BASE + SECP256R1_DATA_HEADER_LEN + (s * SECP256R1_OFFSETS_LEN), s);
    }

    final var offsets = Secp256r1Offsets.parseOffsets(data, BASE);
    assertEquals(count, offsets.length);
    for (int s = 0; s < count; s++) {
      assertEquals(SIG_OFFSET + s, offsets[s].signatureOffset(), "record " + s);
      assertEquals(MSG_IX_INDEX + s, offsets[s].messageInstructionIndex(), "record " + s);
    }
  }

  /// Secp256k1's header is a bare `u8 count` with no padding byte, so its first record starts
  /// one byte earlier than Ed25519's. Parsing at a non-zero base pins that difference.
  @Test
  void secp256k1ParsesEveryRecordAtItsOwnStride() {
    final int count = 3;
    final byte[] data = new byte[BASE + SECP256K1_DATA_HEADER_LEN + (count * SECP256K1_OFFSETS_LEN)];
    data[BASE] = (byte) count;
    for (int s = 0; s < count; s++) {
      writeNarrowRecord(data, BASE + SECP256K1_DATA_HEADER_LEN + (s * SECP256K1_OFFSETS_LEN), s);
    }

    final var offsets = Secp256k1Offsets.parseOffsets(data, BASE);
    assertEquals(count, offsets.length);
    for (int s = 0; s < count; s++) {
      assertEquals(SIG_OFFSET + s, offsets[s].signatureOffset(), "record " + s);
      assertEquals(MSG_IX_INDEX + s, offsets[s].messageInstructionIndex(), "record " + s);
    }
  }

  /// A zero count yields an empty array rather than a null or a phantom record.
  @Test
  void zeroCountParsesEmpty() {
    final byte[] data = new byte[64];
    assertEquals(0, Ed25519Offsets.parseOffsets(data, 0).length);
    assertEquals(0, Secp256k1Offsets.parseOffsets(data, 0).length);
    assertEquals(0, Secp256r1Offsets.parseOffsets(data, 0).length);
  }

  /// The header count is an unsigned byte: 0xFF is 255 records, not -1.
  @Test
  void headerCountIsUnsigned() {
    final byte[] data = new byte[ED25519_DATA_HEADER_LEN + (255 * ED25519_OFFSETS_LEN)];
    data[0] = (byte) 0xFF;
    assertEquals(255, Ed25519Offsets.parseOffsets(data, 0).length);
  }

  // ---------------------------------------------------------------------------
  // resolve*(): which buffer, which window
  // ---------------------------------------------------------------------------

  /// Builds Ed25519-shaped instruction data whose three regions all point at index
  /// `instructionIndex`, with the payloads laid out immediately after the offsets struct.
  private static byte[] ed25519Selfish(final int instructionIndex) {
    final int keyOffset = ED25519_DATA_HEADER_LEN + ED25519_OFFSETS_LEN;
    final int sigOffset = keyOffset + 32;
    final int msgOffset = sigOffset + 64;
    final int msgSize = 7;

    final byte[] data = new byte[msgOffset + msgSize];
    data[0] = 1;
    putInt16LE(data, ED25519_DATA_HEADER_LEN, sigOffset);
    putInt16LE(data, ED25519_DATA_HEADER_LEN + 2, instructionIndex);
    putInt16LE(data, ED25519_DATA_HEADER_LEN + 4, keyOffset);
    putInt16LE(data, ED25519_DATA_HEADER_LEN + 6, instructionIndex);
    putInt16LE(data, ED25519_DATA_HEADER_LEN + 8, msgOffset);
    putInt16LE(data, ED25519_DATA_HEADER_LEN + 10, msgSize);
    putInt16LE(data, ED25519_DATA_HEADER_LEN + 12, instructionIndex);

    System.arraycopy(bytes(32, 0x10), 0, data, keyOffset, 32);
    System.arraycopy(bytes(64, 0x40), 0, data, sigOffset, 64);
    System.arraycopy(bytes(msgSize, 0x01), 0, data, msgOffset, msgSize);
    return data;
  }

  /// The 0xFFFF sentinel resolves against the precompile instruction's own data, and the
  /// resolved windows are the exact payload bytes — not zeros, and not a neighbouring region.
  @Test
  void ed25519SentinelResolvesAgainstSelfData() {
    final byte[] selfData = ed25519Selfish(ED25519_INSTRUCTION_INDEX_CURRENT);
    final var offsets = Ed25519Offsets.parseOffsets(selfData, 0)[0];

    assertArrayEquals(bytes(32, 0x10), offsets.resolvePublicKey(selfData, null));
    assertArrayEquals(bytes(64, 0x40), offsets.resolveSignature(selfData, null));
    assertArrayEquals(bytes(7, 0x01), offsets.resolveMessage(selfData, null));
  }

  /// A non-sentinel index resolves against that transaction instruction's data, even when
  /// self data is also supplied — the index, not availability, picks the buffer.
  @Test
  void ed25519IndexResolvesAgainstTransactionData() {
    final byte[] referenced = ed25519Selfish(1);
    final var offsets = Ed25519Offsets.parseOffsets(referenced, 0)[0];
    final var txData = List.of(new byte[0], referenced);
    final byte[] decoySelfData = new byte[referenced.length];

    assertArrayEquals(bytes(32, 0x10), offsets.resolvePublicKey(decoySelfData, txData));
    assertArrayEquals(bytes(64, 0x40), offsets.resolveSignature(decoySelfData, txData));
    assertArrayEquals(bytes(7, 0x01), offsets.resolveMessage(decoySelfData, txData));
  }

  @Test
  void ed25519SentinelWithoutSelfDataRejected() {
    final byte[] selfData = ed25519Selfish(ED25519_INSTRUCTION_INDEX_CURRENT);
    final var offsets = Ed25519Offsets.parseOffsets(selfData, 0)[0];

    assertThrows(IllegalArgumentException.class, () -> offsets.resolvePublicKey(null, null));
    assertThrows(IllegalArgumentException.class, () -> offsets.resolveSignature(null, List.of()));
    assertThrows(IllegalArgumentException.class, () -> offsets.resolveMessage(null, List.of()));
  }

  /// An index at or past the end of the instruction list is a bounds error, not an
  /// [IndexOutOfBoundsException] escaping from the list access.
  @Test
  void ed25519OutOfBoundsIndexRejected() {
    final byte[] referenced = ed25519Selfish(2);
    final var offsets = Ed25519Offsets.parseOffsets(referenced, 0)[0];
    final var tooShort = List.of(new byte[0], referenced);

    assertThrows(IllegalArgumentException.class, () -> offsets.resolvePublicKey(referenced, tooShort));
    assertThrows(IllegalArgumentException.class, () -> offsets.resolveSignature(referenced, tooShort));
    assertThrows(IllegalArgumentException.class, () -> offsets.resolveMessage(referenced, tooShort));
  }

  @Test
  void ed25519MissingTransactionDataRejected() {
    final byte[] referenced = ed25519Selfish(1);
    final var offsets = Ed25519Offsets.parseOffsets(referenced, 0)[0];

    assertThrows(IllegalArgumentException.class, () -> offsets.resolvePublicKey(referenced, null));
    assertThrows(IllegalArgumentException.class, () -> offsets.resolveSignature(referenced, null));
    assertThrows(IllegalArgumentException.class, () -> offsets.resolveMessage(referenced, null));
  }

  /// Secp256r1 shares the sentinel convention but carries a 33-byte compressed key, so the
  /// signature and message windows sit one byte later than the Ed25519 equivalent.
  @Test
  void secp256r1ResolvesCompressedKeyWindow() {
    final int keyOffset = SECP256R1_DATA_HEADER_LEN + SECP256R1_OFFSETS_LEN;
    final int sigOffset = keyOffset + SECP256R1_PUBKEY_LEN;
    final int msgOffset = sigOffset + SECP256R1_SIGNATURE_LEN;
    final int msgSize = 5;

    final byte[] data = new byte[msgOffset + msgSize];
    data[0] = 1;
    putInt16LE(data, SECP256R1_DATA_HEADER_LEN, sigOffset);
    putInt16LE(data, SECP256R1_DATA_HEADER_LEN + 2, SECP256R1_INSTRUCTION_INDEX_CURRENT);
    putInt16LE(data, SECP256R1_DATA_HEADER_LEN + 4, keyOffset);
    putInt16LE(data, SECP256R1_DATA_HEADER_LEN + 6, SECP256R1_INSTRUCTION_INDEX_CURRENT);
    putInt16LE(data, SECP256R1_DATA_HEADER_LEN + 8, msgOffset);
    putInt16LE(data, SECP256R1_DATA_HEADER_LEN + 10, msgSize);
    putInt16LE(data, SECP256R1_DATA_HEADER_LEN + 12, SECP256R1_INSTRUCTION_INDEX_CURRENT);

    System.arraycopy(bytes(SECP256R1_PUBKEY_LEN, 0x20), 0, data, keyOffset, SECP256R1_PUBKEY_LEN);
    System.arraycopy(bytes(SECP256R1_SIGNATURE_LEN, 0x50), 0, data, sigOffset, SECP256R1_SIGNATURE_LEN);
    System.arraycopy(bytes(msgSize, 0x02), 0, data, msgOffset, msgSize);

    final var offsets = Secp256r1Offsets.parseOffsets(data, 0)[0];
    final byte[] key = offsets.resolvePublicKey(data, null);
    assertEquals(SECP256R1_PUBKEY_LEN, key.length);
    assertArrayEquals(bytes(SECP256R1_PUBKEY_LEN, 0x20), key);
    assertArrayEquals(bytes(SECP256R1_SIGNATURE_LEN, 0x50), offsets.resolveSignature(data, null));
    assertArrayEquals(bytes(msgSize, 0x02), offsets.resolveMessage(data, null));

    assertThrows(IllegalArgumentException.class, () -> offsets.resolvePublicKey(null, null));
    assertThrows(IllegalArgumentException.class, () -> offsets.resolveSignature(null, null));
    assertThrows(IllegalArgumentException.class, () -> offsets.resolveMessage(null, null));
  }

  /// Secp256k1 has no "current instruction" sentinel: every index is a literal transaction
  /// position, so 0xFF resolves against the instruction list like any other index rather than
  /// falling back to self data. Its signature accessor also returns the recovery id byte.
  @Test
  void secp256k1ResolvesAgainstLiteralIndexes() {
    final int ethOffset = SECP256K1_DATA_HEADER_LEN + SECP256K1_OFFSETS_LEN;
    final int sigOffset = ethOffset + SECP256K1_ETH_ADDRESS_LEN;
    final int recoveryIdOffset = sigOffset + SECP256K1_SIGNATURE_LEN;
    final int msgOffset = recoveryIdOffset + SECP256K1_RECOVERY_ID_LEN;
    final int msgSize = 6;
    final int instructionIndex = 1;

    final byte[] data = new byte[msgOffset + msgSize];
    data[0] = 1;
    putInt16LE(data, SECP256K1_DATA_HEADER_LEN, sigOffset);
    data[SECP256K1_DATA_HEADER_LEN + 2] = (byte) instructionIndex;
    putInt16LE(data, SECP256K1_DATA_HEADER_LEN + 3, ethOffset);
    data[SECP256K1_DATA_HEADER_LEN + 5] = (byte) instructionIndex;
    putInt16LE(data, SECP256K1_DATA_HEADER_LEN + 6, msgOffset);
    putInt16LE(data, SECP256K1_DATA_HEADER_LEN + 8, msgSize);
    data[SECP256K1_DATA_HEADER_LEN + 10] = (byte) instructionIndex;

    System.arraycopy(bytes(SECP256K1_ETH_ADDRESS_LEN, 0x30), 0, data, ethOffset, SECP256K1_ETH_ADDRESS_LEN);
    System.arraycopy(bytes(SECP256K1_SIGNATURE_LEN, 0x60), 0, data, sigOffset, SECP256K1_SIGNATURE_LEN);
    data[recoveryIdOffset] = 3;
    System.arraycopy(bytes(msgSize, 0x03), 0, data, msgOffset, msgSize);

    final var offsets = Secp256k1Offsets.parseOffsets(data, 0)[0];
    final var txData = List.of(new byte[0], data);

    assertArrayEquals(bytes(SECP256K1_ETH_ADDRESS_LEN, 0x30), offsets.resolveEthAddress(txData));
    assertArrayEquals(bytes(msgSize, 0x03), offsets.resolveMessage(txData));

    // 64-byte signature followed by the recovery id
    final byte[] sigAndRecoveryId = offsets.resolveSignatureAndRecoveryId(txData);
    assertEquals(SECP256K1_SIGNATURE_LEN + SECP256K1_RECOVERY_ID_LEN, sigAndRecoveryId.length);
    final byte[] expected = new byte[SECP256K1_SIGNATURE_LEN + SECP256K1_RECOVERY_ID_LEN];
    System.arraycopy(bytes(SECP256K1_SIGNATURE_LEN, 0x60), 0, expected, 0, SECP256K1_SIGNATURE_LEN);
    expected[SECP256K1_SIGNATURE_LEN] = 3;
    assertArrayEquals(expected, sigAndRecoveryId);

    // no sentinel: self data is never consulted, so an unbacked index is always a bounds error
    assertThrows(IllegalArgumentException.class, () -> offsets.resolveEthAddress(List.of(new byte[0])));
    assertThrows(IllegalArgumentException.class, () -> offsets.resolveSignatureAndRecoveryId(null));
    assertThrows(IllegalArgumentException.class, () -> offsets.resolveMessage(null));
  }

  /// 0xFFFF is a literal index for Secp256k1, not "this instruction" — it must not resolve.
  @Test
  void secp256k1HasNoCurrentInstructionSentinel() {
    final byte[] data = new byte[SECP256K1_DATA_HEADER_LEN + SECP256K1_OFFSETS_LEN + 128];
    data[0] = 1;
    data[SECP256K1_DATA_HEADER_LEN + 2] = (byte) 0xFF;
    data[SECP256K1_DATA_HEADER_LEN + 5] = (byte) 0xFF;
    data[SECP256K1_DATA_HEADER_LEN + 10] = (byte) 0xFF;

    final var offsets = Secp256k1Offsets.parseOffsets(data, 0)[0];
    assertEquals(0xFF, offsets.ethAddressInstructionIndex());
    assertThrows(IllegalArgumentException.class, () -> offsets.resolveEthAddress(List.of(data)));
  }
}

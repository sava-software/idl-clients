package software.sava.idl.clients.spl.precompiles;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.tx.Transaction;
import software.sava.core.tx.TransactionSkeleton;

import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static software.sava.core.accounts.PublicKey.PUBLIC_KEY_LENGTH;
import static software.sava.core.encoding.ByteUtil.getInt16LE;

final class SignatureVerifyProgramTests {

  private static final SolanaAccounts ACCOUNTS = SolanaAccounts.MAIN_NET;

  private static int u16(final byte[] data, final int off) {
    return getInt16LE(data, off) & 0xFFFF;
  }

  @Test
  void parseVerifySig() {
    final var solanaAccounts = SolanaAccounts.MAIN_NET;
    final byte[] data = Base64.getDecoder().decode("""
        AUtAF4nEONye36nRnwuYCENB4v/gTRGheOvwMjIVy1p3Yt4zltjgbsCnBCE3uhsLTweJ5tujQnKEYb+8QzIO5QmAAQAEDd99Z6UTtp321u6/nTONgzk6qxRNj3/PQET8wiSKG75OChN4yTK1i4Ts7wfwuUWF/22HgBxfvHy4wJkougO65l5HpxONNGN6Ay/cZJu5nmMlLMI2jWOk0taG946X9TqDkGAx9IGNoT76IajAgBQHvC+5sxDdeqP4YtI6BtMBK/7TaUbQ1l56peGYIXNmUajgksMDPXaVUUsTvhDULJozudh4Uhyxec67hYm1VqLV7JTSSYaC/fm7KvWtZOSRzEFT2psYjg+Nu8EC4plOCb/FWX8SdqwCcE5VHPV7HcWGGgwesy4L8IUj7e3FCMnR6r6qBfZdrIMXFf5D/ynxUZ8TH/u179HvS/yl6+DHyVpN4ldhAOUDy80kQDbvw3yoZsx2EQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAwZGb+UhFzL/7K26csOb57yM5bvF9xJrLEObOkAAAAADfUbWfJP7vhL5Qo+DjUD/BXB0SSf0imT8ynBEgAAAAAlU276eyWDJinopP+ITNpZv4YDRUa5LgXlWH4mFSlP2mw8Vskr+FFvpu5o1/xQk1AQdVIMptuQCkV1p28WYSMkGCgAJA0CcAAAAAAAACgAFAoAaBgAJAgAFDAIAAAAgTgAAAAAAAAsAEAEADAAEAEwABABuAKIABAAMCxIIAgQADhUXERMNkQIgT2WLGQZiDwQBAAC28Xp2BheE25F/tVr8jYRs0srNO7eQpMq1pCq5hEXZALIvnSuPzICL9aGXoGFbABb8OtGtZrHhSJrsmSJDOCwLSBXCpGw3/FxrF3HFpf6Ve3Z5YAFL5BVQT5J7s3HwEGSiAGM4ZDVhNjVlMjIzNGY1NWQwMDAxMDEwMDgwOTY5ODAwMDAwMDAwMDBhN2NjNzYwYTAwMDAwMDAwMDAwMDAxMDAwMDAwMDAwMDAxMDAwMDAwMDAwMTE0MDE1YTliODUwYTAwMDAwMDAwMDFmODRkODIwYTAwMDAwMDAwMDAwMGViYTQ2ZTE0MDAwMDAwMDA0NjU0N2E2Mzc1NDIzNjU0MDAwMAAMEhIDBggCBAAUFRYXERMPEA0BBzAQGnuDXh2vYgEBANcRxzjdCwAAAFjngwoAAAAAAAAAAQEAAAAAAAAARlR6Y3VCNlQC3E42Kl7pkP+vCgX6bK+0TFMtWHz2EUnYgJSxOMI1cDcBAQajBQNcg1vLygm9ic+3HuQ3aieF/D5bxgfvpmUf3AN/Y9/3gKtTZAAEMTApKg==
        """.stripTrailing());

    final var skeleton = TransactionSkeleton.deserializeSkeleton(data);
    final var instructions = skeleton.parseInstructionsWithoutAccounts();

    var verifySigIx = instructions[3];
    assertEquals(solanaAccounts.ed25519Program(), verifySigIx.programId().publicKey());

    byte[] ixData = verifySigIx.copyData();
    var signatureOffsetsArray = Ed25519Offsets.parseOffsets(ixData, 0);
    assertEquals(1, signatureOffsetsArray.length);

    var signatureOffsets = signatureOffsetsArray[0];
    assertEquals(12, signatureOffsets.signatureOffset());
    assertEquals(4, signatureOffsets.signatureInstructionIndex());
    assertEquals(76, signatureOffsets.publicKeyOffset());
    assertEquals(4, signatureOffsets.publicKeyInstructionIndex());
    assertEquals(110, signatureOffsets.messageDataOffset());
    assertEquals(162, signatureOffsets.messageDataSize());
    assertEquals(4, signatureOffsets.messageInstructionIndex());

    var referenceInstruction = instructions[4];
    byte[] referenceData = referenceInstruction.copyData();

    var publicKey = PublicKey.readPubKey(referenceData, signatureOffsets.publicKeyOffset());
    assertEquals("5rPbkdhCWfpBthPM8E3GW7cQAjiWZmHpsFN3kpZvozCb", publicKey.toBase58());

    byte[] sig = Arrays.copyOfRange(referenceData, signatureOffsets.signatureOffset(), signatureOffsets.signatureOffset() + Transaction.SIGNATURE_LENGTH);
    assertArrayEquals(
        Base64.getDecoder().decode("tvF6dgYXhNuRf7Va/I2EbNLKzTu3kKTKtaQquYRF2QCyL50rj8yAi/Whl6BhWwAW/DrRrWax4Uia7JkiQzgsCw=="),
        sig
    );

    byte[] msg = Arrays.copyOfRange(referenceData, signatureOffsets.messageDataOffset(), signatureOffsets.messageDataOffset() + signatureOffsets.messageDataSize());
    assertArrayEquals(
        Base64.getDecoder().decode("YzhkNWE2NWUyMjM0ZjU1ZDAwMDEwMTAwODA5Njk4MDAwMDAwMDAwMGE3Y2M3NjBhMDAwMDAwMDAwMDAwMDEwMDAwMDAwMDAwMDEwMDAwMDAwMDAxMTQwMTVhOWI4NTBhMDAwMDAwMDAwMWY4NGQ4MjBhMDAwMDAwMDAwMDAwZWJhNDZlMTQwMDAwMDAwMDQ2NTQ3YTYzNzU0MjM2NTQwMDAw"),
        msg
    );

    assertTrue(PublicKey.verifySignature(
        publicKey.toByteArray(), 0,
        msg, 0, msg.length,
        sig
    ));
  }

  @Test
  void ed25519Layout() {
    final var publicKey = PublicKey.fromBase58Encoded("3ntfH5pyhTGePb2cv2gqhyBmZHVW3EggCnbq1ND7YmgX");
    final byte[] signature = new byte[Transaction.SIGNATURE_LENGTH];
    for (int i = 0; i < signature.length; i++) {
      signature[i] = (byte) i;
    }
    final byte[] message = "hello world".getBytes();

    final var ix = SignatureVerifyProgram.verifyEd25519(ACCOUNTS.invokedEd25519Program(), publicKey, signature, message);
    assertEquals(ACCOUNTS.invokedEd25519Program(), ix.programId());
    assertEquals(0, ix.accounts().size());

    final byte[] data = ix.data();
    final int publicKeyOffset = SignatureVerifyProgram.ED25519_DATA_HEADER_LEN + SignatureVerifyProgram.ED25519_OFFSETS_LEN;
    final int signatureOffset = publicKeyOffset + PUBLIC_KEY_LENGTH;
    final int messageOffset = signatureOffset + Transaction.SIGNATURE_LENGTH;
    assertEquals(messageOffset + message.length, data.length);

    // header
    assertEquals(1, data[0]);
    assertEquals(0, data[1]);

    // offsets struct (7 u16 fields)
    final int base = SignatureVerifyProgram.ED25519_DATA_HEADER_LEN;
    assertEquals(signatureOffset, u16(data, base));
    assertEquals(SignatureVerifyProgram.ED25519_INSTRUCTION_INDEX_CURRENT, u16(data, base + 2));
    assertEquals(publicKeyOffset, u16(data, base + 4));
    assertEquals(SignatureVerifyProgram.ED25519_INSTRUCTION_INDEX_CURRENT, u16(data, base + 6));
    assertEquals(messageOffset, u16(data, base + 8));
    assertEquals(message.length, u16(data, base + 10));
    assertEquals(SignatureVerifyProgram.ED25519_INSTRUCTION_INDEX_CURRENT, u16(data, base + 12));

    // payload
    final byte[] pubKeyBytes = new byte[PUBLIC_KEY_LENGTH];
    System.arraycopy(data, publicKeyOffset, pubKeyBytes, 0, PUBLIC_KEY_LENGTH);
    assertArrayEquals(publicKey.toByteArray(), pubKeyBytes);

    final byte[] sigBytes = new byte[Transaction.SIGNATURE_LENGTH];
    System.arraycopy(data, signatureOffset, sigBytes, 0, Transaction.SIGNATURE_LENGTH);
    assertArrayEquals(signature, sigBytes);

    final byte[] msgBytes = new byte[message.length];
    System.arraycopy(data, messageOffset, msgBytes, 0, message.length);
    assertArrayEquals(message, msgBytes);
  }

  @Test
  void ed25519ExternalMessage() {
    final var publicKey = PublicKey.fromBase58Encoded("3ntfH5pyhTGePb2cv2gqhyBmZHVW3EggCnbq1ND7YmgX");
    final byte[] signature = new byte[Transaction.SIGNATURE_LENGTH];
    for (int i = 0; i < signature.length; i++) {
      signature[i] = (byte) i;
    }
    final int messageInstructionIndex = 2;
    final int messageOffset = 37;
    final int messageSize = 128;

    final var ix = SignatureVerifyProgram.verifyEd25519(
        ACCOUNTS.invokedEd25519Program(), publicKey, signature, messageInstructionIndex, messageOffset, messageSize);
    final byte[] data = ix.data();

    final int publicKeyOffset = SignatureVerifyProgram.ED25519_DATA_HEADER_LEN + SignatureVerifyProgram.ED25519_OFFSETS_LEN;
    final int signatureOffset = publicKeyOffset + PUBLIC_KEY_LENGTH;
    // The message is not colocated, so the data ends right after the signature.
    assertEquals(signatureOffset + Transaction.SIGNATURE_LENGTH, data.length);

    assertEquals(1, data[0]);
    assertEquals(0, data[1]);

    final int base = SignatureVerifyProgram.ED25519_DATA_HEADER_LEN;
    assertEquals(signatureOffset, u16(data, base));
    assertEquals(SignatureVerifyProgram.ED25519_INSTRUCTION_INDEX_CURRENT, u16(data, base + 2));
    assertEquals(publicKeyOffset, u16(data, base + 4));
    assertEquals(SignatureVerifyProgram.ED25519_INSTRUCTION_INDEX_CURRENT, u16(data, base + 6));
    // message points at another instruction
    assertEquals(messageOffset, u16(data, base + 8));
    assertEquals(messageSize, u16(data, base + 10));
    assertEquals(messageInstructionIndex, u16(data, base + 12));

    final byte[] pubKeyBytes = new byte[PUBLIC_KEY_LENGTH];
    System.arraycopy(data, publicKeyOffset, pubKeyBytes, 0, PUBLIC_KEY_LENGTH);
    assertArrayEquals(publicKey.toByteArray(), pubKeyBytes);

    final byte[] sigBytes = new byte[Transaction.SIGNATURE_LENGTH];
    System.arraycopy(data, signatureOffset, sigBytes, 0, Transaction.SIGNATURE_LENGTH);
    assertArrayEquals(signature, sigBytes);

    // out-of-range offsets are rejected
    assertThrows(IllegalArgumentException.class, () -> SignatureVerifyProgram.verifyEd25519(
        ACCOUNTS.invokedEd25519Program(), publicKey, signature, 0x10000, messageOffset, messageSize)
    );
    assertThrows(IllegalArgumentException.class, () -> SignatureVerifyProgram.verifyEd25519(
        ACCOUNTS.invokedEd25519Program(), publicKey, signature, messageInstructionIndex, -1, messageSize)
    );
  }

  @Test
  void secp256k1Layout() {
    final byte[] ethAddress = new byte[SignatureVerifyProgram.SECP256K1_ETH_ADDRESS_LEN];
    final byte[] signature = new byte[SignatureVerifyProgram.SECP256K1_SIGNATURE_LEN];
    for (int i = 0; i < signature.length; i++) {
      signature[i] = (byte) (i + 1);
    }
    final byte recoveryId = 1;
    final byte[] message = "secp256k1 message".getBytes();

    final var ix = SignatureVerifyProgram.verifySecp256k1(ACCOUNTS.invokedSecp256k1Program(), ethAddress, signature, recoveryId, message);
    final byte[] data = ix.data();

    final int ethAddressOffset = SignatureVerifyProgram.SECP256K1_DATA_HEADER_LEN + SignatureVerifyProgram.SECP256K1_OFFSETS_LEN;
    final int signatureOffset = ethAddressOffset + SignatureVerifyProgram.SECP256K1_ETH_ADDRESS_LEN;
    final int recoveryIdOffset = signatureOffset + SignatureVerifyProgram.SECP256K1_SIGNATURE_LEN;
    final int messageOffset = recoveryIdOffset + SignatureVerifyProgram.SECP256K1_RECOVERY_ID_LEN;
    assertEquals(messageOffset + message.length, data.length);

    assertEquals(1, data[0]); // number of signatures

    final int base = SignatureVerifyProgram.SECP256K1_DATA_HEADER_LEN;
    assertEquals(signatureOffset, u16(data, base));
    assertEquals(0, data[base + 2]); // default instruction index
    assertEquals(ethAddressOffset, u16(data, base + 3));
    assertEquals(0, data[base + 5]);
    assertEquals(messageOffset, u16(data, base + 6));
    assertEquals(message.length, u16(data, base + 8));
    assertEquals(0, data[base + 10]);

    assertEquals(recoveryId, data[recoveryIdOffset]);
  }

  @Test
  void secp256k1ExplicitInstructionIndex() {
    final byte[] ethAddress = new byte[SignatureVerifyProgram.SECP256K1_ETH_ADDRESS_LEN];
    final byte[] signature = new byte[SignatureVerifyProgram.SECP256K1_SIGNATURE_LEN];
    final byte[] message = "indexed".getBytes();
    final int instructionIndex = 3;

    final var ix = SignatureVerifyProgram.verifySecp256k1(
        ACCOUNTS.invokedSecp256k1Program(), ethAddress, signature, (byte) 0, message, instructionIndex);
    final byte[] data = ix.data();

    final int base = SignatureVerifyProgram.SECP256K1_DATA_HEADER_LEN;
    assertEquals(instructionIndex, data[base + 2]);
    assertEquals(instructionIndex, data[base + 5]);
    assertEquals(instructionIndex, data[base + 10]);
  }

  @Test
  void secp256r1Layout() {
    final byte[] publicKey = new byte[SignatureVerifyProgram.SECP256R1_PUBKEY_LEN];
    final byte[] signature = new byte[SignatureVerifyProgram.SECP256R1_SIGNATURE_LEN];
    final byte[] message = "secp256r1".getBytes();

    final var ix = SignatureVerifyProgram.verifySecp256r1(
        AccountMeta.createInvoked(SignatureVerifyProgram.SECP256R1_PROGRAM), publicKey, signature, message);
    final byte[] data = ix.data();

    final int publicKeyOffset = SignatureVerifyProgram.SECP256R1_DATA_HEADER_LEN + SignatureVerifyProgram.SECP256R1_OFFSETS_LEN;
    final int signatureOffset = publicKeyOffset + SignatureVerifyProgram.SECP256R1_PUBKEY_LEN;
    final int messageOffset = signatureOffset + SignatureVerifyProgram.SECP256R1_SIGNATURE_LEN;
    assertEquals(messageOffset + message.length, data.length);

    assertEquals(1, data[0]);
    assertEquals(0, data[1]);

    final int base = SignatureVerifyProgram.SECP256R1_DATA_HEADER_LEN;
    assertEquals(signatureOffset, u16(data, base));
    assertEquals(SignatureVerifyProgram.SECP256R1_INSTRUCTION_INDEX_CURRENT, u16(data, base + 2));
    assertEquals(publicKeyOffset, u16(data, base + 4));
    assertEquals(SignatureVerifyProgram.SECP256R1_INSTRUCTION_INDEX_CURRENT, u16(data, base + 6));
    assertEquals(messageOffset, u16(data, base + 8));
    assertEquals(message.length, u16(data, base + 10));
    assertEquals(SignatureVerifyProgram.SECP256R1_INSTRUCTION_INDEX_CURRENT, u16(data, base + 12));
  }

  @Test
  void validation() {
    final byte[] ethAddress = new byte[SignatureVerifyProgram.SECP256K1_ETH_ADDRESS_LEN];
    final byte[] secpSig = new byte[SignatureVerifyProgram.SECP256K1_SIGNATURE_LEN];
    final byte[] message = "m".getBytes();

    // wrong signature length (ed25519)
    assertThrows(IllegalArgumentException.class, () -> SignatureVerifyProgram.verifyEd25519(
            ACCOUNTS.invokedEd25519Program(),
            PublicKey.fromBase58Encoded("3ntfH5pyhTGePb2cv2gqhyBmZHVW3EggCnbq1ND7YmgX"),
            new byte[10], message
        )
    );

    // message too long
    assertThrows(IllegalArgumentException.class, () -> SignatureVerifyProgram.verifySecp256k1(
        ACCOUNTS.invokedSecp256k1Program(), ethAddress, secpSig, (byte) 0, new byte[0x10000])
    );

    // recovery id out of range
    assertThrows(IllegalArgumentException.class, () -> SignatureVerifyProgram.verifySecp256k1(
        ACCOUNTS.invokedSecp256k1Program(), ethAddress, secpSig, (byte) 4, message)
    );

    // instruction index out of range
    assertThrows(IllegalArgumentException.class, () -> SignatureVerifyProgram.verifySecp256k1(
        ACCOUNTS.invokedSecp256k1Program(), ethAddress, secpSig, (byte) 0, message, 256)
    );
  }

  /// Distinct, non-zero bytes: an all-zero payload cannot distinguish "copied correctly" from
  /// "never copied at all", which is exactly what the write-side assertions have to pin down.
  private static byte[] bytes(final int len, final int seed) {
    final byte[] out = new byte[len];
    for (int i = 0; i < len; i++) {
      out[i] = (byte) (seed + i);
    }
    return out;
  }

  // ---------------------------------------------------------------------------
  // Builder output feeds the parser: the offsets a builder writes must address the
  // payload it wrote, byte for byte.
  // ---------------------------------------------------------------------------

  @Test
  void ed25519RoundTripsThroughParser() {
    final var publicKey = PublicKey.fromBase58Encoded("3ntfH5pyhTGePb2cv2gqhyBmZHVW3EggCnbq1ND7YmgX");
    final byte[] signature = bytes(Transaction.SIGNATURE_LENGTH, 0x40);
    final byte[] message = "round trip".getBytes();

    final byte[] data = SignatureVerifyProgram
        .verifyEd25519(ACCOUNTS.invokedEd25519Program(), publicKey, signature, message)
        .data();

    final var parsed = Ed25519Offsets.parseOffsets(data, 0);
    assertEquals(1, parsed.length);
    final var offsets = parsed[0];

    assertArrayEquals(publicKey.toByteArray(), offsets.resolvePublicKey(data, null));
    assertArrayEquals(signature, offsets.resolveSignature(data, null));
    assertArrayEquals(message, offsets.resolveMessage(data, null));
  }

  @Test
  void secp256r1RoundTripsThroughParser() {
    final byte[] publicKey = bytes(SignatureVerifyProgram.SECP256R1_PUBKEY_LEN, 0x20);
    final byte[] signature = bytes(SignatureVerifyProgram.SECP256R1_SIGNATURE_LEN, 0x50);
    final byte[] message = "r1 round trip".getBytes();

    final byte[] data = SignatureVerifyProgram
        .verifySecp256r1(AccountMeta.createInvoked(SignatureVerifyProgram.SECP256R1_PROGRAM), publicKey, signature, message)
        .data();

    final var parsed = Secp256r1Offsets.parseOffsets(data, 0);
    assertEquals(1, parsed.length);
    final var offsets = parsed[0];

    assertArrayEquals(publicKey, offsets.resolvePublicKey(data, null));
    assertArrayEquals(signature, offsets.resolveSignature(data, null));
    assertArrayEquals(message, offsets.resolveMessage(data, null));
  }

  /// Secp256k1 encodes literal instruction indexes, so the round trip only closes when the
  /// instruction sits at the position the builder was told about.
  @Test
  void secp256k1RoundTripsThroughParserAtItsInstructionIndex() {
    final byte[] ethAddress = bytes(SignatureVerifyProgram.SECP256K1_ETH_ADDRESS_LEN, 0x30);
    final byte[] signature = bytes(SignatureVerifyProgram.SECP256K1_SIGNATURE_LEN, 0x60);
    final byte recoveryId = 2;
    final byte[] message = "k1 round trip".getBytes();
    final int instructionIndex = 1;

    final byte[] data = SignatureVerifyProgram
        .verifySecp256k1(ACCOUNTS.invokedSecp256k1Program(), ethAddress, signature, recoveryId, message, instructionIndex)
        .data();
    final var txData = List.of(new byte[0], data);

    final var parsed = Secp256k1Offsets.parseOffsets(data, 0);
    assertEquals(1, parsed.length);
    final var offsets = parsed[0];

    assertArrayEquals(ethAddress, offsets.resolveEthAddress(txData));
    assertArrayEquals(message, offsets.resolveMessage(txData));

    final byte[] expectedSig = new byte[SignatureVerifyProgram.SECP256K1_SIGNATURE_LEN + 1];
    System.arraycopy(signature, 0, expectedSig, 0, signature.length);
    expectedSig[signature.length] = recoveryId;
    assertArrayEquals(expectedSig, offsets.resolveSignatureAndRecoveryId(txData));
  }

  // ---------------------------------------------------------------------------
  // Fully external Ed25519 offsets (no colocated payload)
  // ---------------------------------------------------------------------------

  @Test
  void ed25519AllRegionsExternal() {
    final int publicKeyInstructionIndex = 1;
    final int publicKeyOffset = 0x1101;
    final int signatureInstructionIndex = 2;
    final int signatureOffset = 0x2202;
    final int messageInstructionIndex = 3;
    final int messageOffset = 0x3303;
    final int messageSize = 0x4404;

    final var ix = SignatureVerifyProgram.verifyEd25519(
        ACCOUNTS.invokedEd25519Program(),
        publicKeyInstructionIndex, publicKeyOffset,
        signatureInstructionIndex, signatureOffset,
        messageInstructionIndex, messageOffset, messageSize);

    assertEquals(ACCOUNTS.invokedEd25519Program(), ix.programId());
    assertEquals(0, ix.accounts().size());

    final byte[] data = ix.data();
    // nothing is colocated: the data is exactly the header plus one offsets struct
    assertEquals(SignatureVerifyProgram.ED25519_DATA_HEADER_LEN + SignatureVerifyProgram.ED25519_OFFSETS_LEN, data.length);
    assertEquals(1, data[0]);
    assertEquals(0, data[1]);

    final int base = SignatureVerifyProgram.ED25519_DATA_HEADER_LEN;
    assertEquals(signatureOffset, u16(data, base));
    assertEquals(signatureInstructionIndex, u16(data, base + 2));
    assertEquals(publicKeyOffset, u16(data, base + 4));
    assertEquals(publicKeyInstructionIndex, u16(data, base + 6));
    assertEquals(messageOffset, u16(data, base + 8));
    assertEquals(messageSize, u16(data, base + 10));
    assertEquals(messageInstructionIndex, u16(data, base + 12));

    // and the parser agrees with the builder on every field
    final var offsets = Ed25519Offsets.parseOffsets(data, 0)[0];
    assertEquals(signatureOffset, offsets.signatureOffset());
    assertEquals(signatureInstructionIndex, offsets.signatureInstructionIndex());
    assertEquals(publicKeyOffset, offsets.publicKeyOffset());
    assertEquals(publicKeyInstructionIndex, offsets.publicKeyInstructionIndex());
    assertEquals(messageOffset, offsets.messageDataOffset());
    assertEquals(messageSize, offsets.messageDataSize());
    assertEquals(messageInstructionIndex, offsets.messageInstructionIndex());
  }

  /// Each of the seven fields is range checked independently — a missing check on any one of
  /// them would silently truncate that field to 16 bits when it is written.
  @Test
  void ed25519AllRegionsExternalValidatesEveryField() {
    final int ok = 1;
    final int tooBig = 0x10000;
    final var program = ACCOUNTS.invokedEd25519Program();

    assertThrows(IllegalArgumentException.class, () -> SignatureVerifyProgram.verifyEd25519(
        program, tooBig, ok, ok, ok, ok, ok, ok));
    assertThrows(IllegalArgumentException.class, () -> SignatureVerifyProgram.verifyEd25519(
        program, ok, tooBig, ok, ok, ok, ok, ok));
    assertThrows(IllegalArgumentException.class, () -> SignatureVerifyProgram.verifyEd25519(
        program, ok, ok, tooBig, ok, ok, ok, ok));
    assertThrows(IllegalArgumentException.class, () -> SignatureVerifyProgram.verifyEd25519(
        program, ok, ok, ok, tooBig, ok, ok, ok));
    assertThrows(IllegalArgumentException.class, () -> SignatureVerifyProgram.verifyEd25519(
        program, ok, ok, ok, ok, tooBig, ok, ok));
    assertThrows(IllegalArgumentException.class, () -> SignatureVerifyProgram.verifyEd25519(
        program, ok, ok, ok, ok, ok, tooBig, ok));
    assertThrows(IllegalArgumentException.class, () -> SignatureVerifyProgram.verifyEd25519(
        program, ok, ok, ok, ok, ok, ok, tooBig));

    // negatives are rejected on the same path
    assertThrows(IllegalArgumentException.class, () -> SignatureVerifyProgram.verifyEd25519(
        program, -1, ok, ok, ok, ok, ok, ok));
    assertThrows(IllegalArgumentException.class, () -> SignatureVerifyProgram.verifyEd25519(
        program, ok, ok, ok, ok, ok, ok, -1));
  }

  // ---------------------------------------------------------------------------
  // Secp256r1 with an external message
  // ---------------------------------------------------------------------------

  @Test
  void secp256r1ExternalMessage() {
    final byte[] publicKey = bytes(SignatureVerifyProgram.SECP256R1_PUBKEY_LEN, 0x20);
    final byte[] signature = bytes(SignatureVerifyProgram.SECP256R1_SIGNATURE_LEN, 0x50);
    final int messageInstructionIndex = 2;
    final int messageOffset = 41;
    final int messageSize = 96;
    final var program = AccountMeta.createInvoked(SignatureVerifyProgram.SECP256R1_PROGRAM);

    final var ix = SignatureVerifyProgram.verifySecp256r1(
        program, publicKey, signature, messageInstructionIndex, messageOffset, messageSize);
    final byte[] data = ix.data();

    final int publicKeyOffset = SignatureVerifyProgram.SECP256R1_DATA_HEADER_LEN + SignatureVerifyProgram.SECP256R1_OFFSETS_LEN;
    final int signatureOffset = publicKeyOffset + SignatureVerifyProgram.SECP256R1_PUBKEY_LEN;
    // the message lives elsewhere, so the data ends right after the signature
    assertEquals(signatureOffset + SignatureVerifyProgram.SECP256R1_SIGNATURE_LEN, data.length);

    assertEquals(1, data[0]);
    assertEquals(0, data[1]);

    final int base = SignatureVerifyProgram.SECP256R1_DATA_HEADER_LEN;
    assertEquals(signatureOffset, u16(data, base));
    assertEquals(SignatureVerifyProgram.SECP256R1_INSTRUCTION_INDEX_CURRENT, u16(data, base + 2));
    assertEquals(publicKeyOffset, u16(data, base + 4));
    assertEquals(SignatureVerifyProgram.SECP256R1_INSTRUCTION_INDEX_CURRENT, u16(data, base + 6));
    assertEquals(messageOffset, u16(data, base + 8));
    assertEquals(messageSize, u16(data, base + 10));
    assertEquals(messageInstructionIndex, u16(data, base + 12));

    // the colocated key and signature are still resolvable against this instruction's data
    final var offsets = Secp256r1Offsets.parseOffsets(data, 0)[0];
    assertArrayEquals(publicKey, offsets.resolvePublicKey(data, null));
    assertArrayEquals(signature, offsets.resolveSignature(data, null));

    assertThrows(IllegalArgumentException.class, () -> SignatureVerifyProgram.verifySecp256r1(
        program, new byte[SignatureVerifyProgram.SECP256R1_PUBKEY_LEN - 1], signature, messageInstructionIndex, messageOffset, messageSize));
    assertThrows(IllegalArgumentException.class, () -> SignatureVerifyProgram.verifySecp256r1(
        program, publicKey, new byte[SignatureVerifyProgram.SECP256R1_SIGNATURE_LEN + 1], messageInstructionIndex, messageOffset, messageSize));
    assertThrows(IllegalArgumentException.class, () -> SignatureVerifyProgram.verifySecp256r1(
        program, publicKey, signature, 0x10000, messageOffset, messageSize));
    assertThrows(IllegalArgumentException.class, () -> SignatureVerifyProgram.verifySecp256r1(
        program, publicKey, signature, messageInstructionIndex, -1, messageSize));
    assertThrows(IllegalArgumentException.class, () -> SignatureVerifyProgram.verifySecp256r1(
        program, publicKey, signature, messageInstructionIndex, messageOffset, 0x10000));
  }

  // ---------------------------------------------------------------------------
  // Range-check boundaries: the largest legal value must be accepted, not rejected
  // ---------------------------------------------------------------------------

  @Test
  void maximumMessageLengthAccepted() {
    final var publicKey = PublicKey.fromBase58Encoded("3ntfH5pyhTGePb2cv2gqhyBmZHVW3EggCnbq1ND7YmgX");
    final byte[] signature = bytes(Transaction.SIGNATURE_LENGTH, 0x40);
    final byte[] message = new byte[SignatureVerifyProgram.MAX_OFFSET];

    final byte[] data = SignatureVerifyProgram
        .verifyEd25519(ACCOUNTS.invokedEd25519Program(), publicKey, signature, message)
        .data();

    final int base = SignatureVerifyProgram.ED25519_DATA_HEADER_LEN;
    assertEquals(SignatureVerifyProgram.MAX_OFFSET, u16(data, base + 10));

    // one byte past the maximum is rejected
    assertThrows(IllegalArgumentException.class, () -> SignatureVerifyProgram.verifyEd25519(
        ACCOUNTS.invokedEd25519Program(), publicKey, signature, new byte[SignatureVerifyProgram.MAX_OFFSET + 1]));
  }

  @Test
  void maximumOffsetAccepted() {
    final var publicKey = PublicKey.fromBase58Encoded("3ntfH5pyhTGePb2cv2gqhyBmZHVW3EggCnbq1ND7YmgX");
    final byte[] signature = bytes(Transaction.SIGNATURE_LENGTH, 0x40);

    // MAX_OFFSET at one end, zero at the other: both are inside the legal range
    final byte[] data = SignatureVerifyProgram.verifyEd25519(
        ACCOUNTS.invokedEd25519Program(), publicKey, signature,
        SignatureVerifyProgram.MAX_OFFSET, 0, SignatureVerifyProgram.MAX_OFFSET).data();

    final int base = SignatureVerifyProgram.ED25519_DATA_HEADER_LEN;
    assertEquals(0, u16(data, base + 8));
    assertEquals(SignatureVerifyProgram.MAX_OFFSET, u16(data, base + 10));
    assertEquals(SignatureVerifyProgram.MAX_OFFSET, u16(data, base + 12));
  }

  /// The external-message overload validates its own signature length and message size; the
  /// colocated overload's checks do not cover it.
  @Test
  void ed25519ExternalMessageValidation() {
    final var publicKey = PublicKey.fromBase58Encoded("3ntfH5pyhTGePb2cv2gqhyBmZHVW3EggCnbq1ND7YmgX");
    final byte[] signature = bytes(Transaction.SIGNATURE_LENGTH, 0x40);
    final var program = ACCOUNTS.invokedEd25519Program();

    assertThrows(IllegalArgumentException.class, () -> SignatureVerifyProgram.verifyEd25519(
        program, publicKey, new byte[Transaction.SIGNATURE_LENGTH - 1], 0, 0, 1));
    assertThrows(IllegalArgumentException.class, () -> SignatureVerifyProgram.verifyEd25519(
        program, publicKey, signature, 0, 0, 0x10000));
  }

  @Test
  void secp256k1BoundaryValidation() {
    final byte[] ethAddress = bytes(SignatureVerifyProgram.SECP256K1_ETH_ADDRESS_LEN, 0x30);
    final byte[] signature = bytes(SignatureVerifyProgram.SECP256K1_SIGNATURE_LEN, 0x60);
    final byte[] message = "boundary".getBytes();
    final var program = ACCOUNTS.invokedSecp256k1Program();

    // both ends of the recovery id range are legal and land in the data verbatim
    final int recoveryIdOffset = SignatureVerifyProgram.SECP256K1_DATA_HEADER_LEN
        + SignatureVerifyProgram.SECP256K1_OFFSETS_LEN
        + SignatureVerifyProgram.SECP256K1_ETH_ADDRESS_LEN
        + SignatureVerifyProgram.SECP256K1_SIGNATURE_LEN;
    assertEquals(0, SignatureVerifyProgram.verifySecp256k1(program, ethAddress, signature, (byte) 0, message).data()[recoveryIdOffset]);
    assertEquals(3, SignatureVerifyProgram.verifySecp256k1(program, ethAddress, signature, (byte) 3, message).data()[recoveryIdOffset]);
    assertThrows(IllegalArgumentException.class, () -> SignatureVerifyProgram.verifySecp256k1(
        program, ethAddress, signature, (byte) -1, message));

    // the instruction index is a u8: 255 is legal, 256 and negatives are not
    final int base = SignatureVerifyProgram.SECP256K1_DATA_HEADER_LEN;
    assertEquals((byte) 0xFF, SignatureVerifyProgram.verifySecp256k1(
        program, ethAddress, signature, (byte) 0, message, 0xFF).data()[base + 2]);
    assertThrows(IllegalArgumentException.class, () -> SignatureVerifyProgram.verifySecp256k1(
        program, ethAddress, signature, (byte) 0, message, -1));

    // wrong-length inputs are rejected rather than truncated or padded
    assertThrows(IllegalArgumentException.class, () -> SignatureVerifyProgram.verifySecp256k1(
        program, new byte[SignatureVerifyProgram.SECP256K1_ETH_ADDRESS_LEN - 1], signature, (byte) 0, message));
    assertThrows(IllegalArgumentException.class, () -> SignatureVerifyProgram.verifySecp256k1(
        program, ethAddress, new byte[SignatureVerifyProgram.SECP256K1_SIGNATURE_LEN + 1], (byte) 0, message));

    // a message of exactly MAX_OFFSET bytes fits
    assertEquals(SignatureVerifyProgram.MAX_OFFSET, u16(SignatureVerifyProgram.verifySecp256k1(
        program, ethAddress, signature, (byte) 0, new byte[SignatureVerifyProgram.MAX_OFFSET]).data(), base + 8));
  }

  @Test
  void secp256r1Validation() {
    final byte[] publicKey = bytes(SignatureVerifyProgram.SECP256R1_PUBKEY_LEN, 0x20);
    final byte[] signature = bytes(SignatureVerifyProgram.SECP256R1_SIGNATURE_LEN, 0x50);
    final byte[] message = "r1".getBytes();
    final var program = AccountMeta.createInvoked(SignatureVerifyProgram.SECP256R1_PROGRAM);

    // a 32-byte (uncompressed-style) key is not a valid 33-byte compressed key
    assertThrows(IllegalArgumentException.class, () -> SignatureVerifyProgram.verifySecp256r1(
        program, new byte[PUBLIC_KEY_LENGTH], signature, message));
    assertThrows(IllegalArgumentException.class, () -> SignatureVerifyProgram.verifySecp256r1(
        program, publicKey, new byte[SignatureVerifyProgram.SECP256R1_SIGNATURE_LEN - 1], message));
    assertThrows(IllegalArgumentException.class, () -> SignatureVerifyProgram.verifySecp256r1(
        program, publicKey, signature, new byte[SignatureVerifyProgram.MAX_OFFSET + 1]));

    // a message of exactly MAX_OFFSET bytes fits
    final byte[] data = SignatureVerifyProgram.verifySecp256r1(
        program, publicKey, signature, new byte[SignatureVerifyProgram.MAX_OFFSET]).data();
    assertEquals(SignatureVerifyProgram.MAX_OFFSET, u16(data, SignatureVerifyProgram.SECP256R1_DATA_HEADER_LEN + 10));
  }
}

package software.sava.idl.clients.spl.precompiles;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.tx.Transaction;
import software.sava.core.tx.TransactionSkeleton;

import java.util.Arrays;
import java.util.Base64;

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
}

package software.sava.idl.clients.spl.token_2022;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.accounts.meta.AccountMeta;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;
import static software.sava.idl.clients.spl.token_2022.Token2022Instructions.*;

final class Token2022InstructionsTests {

  private static final AccountMeta INVOKED_PROGRAM = SolanaAccounts.MAIN_NET.invokedToken2022Program();
  private static final PublicKey METADATA_KEY = PublicKey.fromBase58Encoded("88WLQK58mbqNjaUBxYjEvhvdsWGQde4s1EqyagvEng2f");
  private static final PublicKey UPDATE_AUTHORITY = PublicKey.fromBase58Encoded("CvUqgjP892h66aYPC9E8gKTXnTebY8qaU5ehGrgEQSwV");

  @Test
  void roundTripUpdateName() {
    final var value = "MySuperCoolToken";
    final var ix = updateTokenMetadataName(INVOKED_PROGRAM, METADATA_KEY, UPDATE_AUTHORITY, value);

    final var parsed = Token2022Instructions.UpdateTokenMetadataFieldIxData.read(ix.data(), 0);

    assertArrayEquals(UPDATE_TOKEN_METADATA_FIELD_DISCRIMINATOR.data(), parsed.discriminator());
    assertEquals(Token2022Instructions.TokenMetadataField.Name, parsed.field());
    assertNull(parsed.key());
    assertNull(parsed._key());
    assertEquals(value, parsed.value());

    final var accounts = ix.accounts();
    assertEquals(2, accounts.size());
    assertEquals(AccountMeta.createWrite(METADATA_KEY), accounts.getFirst());
    assertEquals(AccountMeta.createReadOnlySigner(UPDATE_AUTHORITY), accounts.getLast());
  }

  @Test
  void roundTripUpdateSymbol() {
    final var value = "MINE";
    final var ix = updateTokenMetadataSymbol(INVOKED_PROGRAM, METADATA_KEY, UPDATE_AUTHORITY, value);

    final var parsed = UpdateTokenMetadataFieldIxData.read(ix.data(), 0);

    assertEquals(TokenMetadataField.Symbol, parsed.field());
    assertNull(parsed.key());
    assertEquals(value, parsed.value());
  }

  @Test
  void roundTripUpdateUri() {
    final var value = "https://example.com/metadata.json";
    final var ix = updateTokenMetadataUri(INVOKED_PROGRAM, METADATA_KEY, UPDATE_AUTHORITY, value);

    final var parsed = UpdateTokenMetadataFieldIxData.read(ix.data(), 0);

    assertEquals(TokenMetadataField.Uri, parsed.field());
    assertNull(parsed.key());
    assertEquals(value, parsed.value());
  }

  @Test
  void roundTripUpdateCustomKey() {
    final var key = "my new field";
    final var value = "Some data for the new field!";
    final var ix = updateTokenMetadataCustomField(INVOKED_PROGRAM, METADATA_KEY, UPDATE_AUTHORITY, key, value);

    final var parsed = UpdateTokenMetadataFieldIxData.read(ix.data(), 0);

    assertEquals(TokenMetadataField.Key, parsed.field());
    assertEquals(key, parsed.key());
    assertNotNull(parsed._key());
    assertEquals(value, parsed.value());
  }

  @Test
  void roundTripWriteAndRead() {
    final var key = "custom_field";
    final var value = "custom_value";
    final var ix = updateTokenMetadataCustomField(INVOKED_PROGRAM, METADATA_KEY, UPDATE_AUTHORITY, key, value);

    final var parsed = UpdateTokenMetadataFieldIxData.read(ix.data(), 0);

    // Re-serialize via the record's write method and verify byte-for-byte equality
    final byte[] reserialized = new byte[parsed.l()];
    final int written = parsed.write(reserialized, 0);
    assertEquals(reserialized.length, written);
    assertArrayEquals(ix.data(), reserialized);
  }

  @Test
  void roundTripWriteAndReadName() {
    final var value = "This is my larger name";
    final var ix = updateTokenMetadataName(INVOKED_PROGRAM, METADATA_KEY, UPDATE_AUTHORITY, value);

    final var parsed = UpdateTokenMetadataFieldIxData.read(ix.data(), 0);

    final byte[] reserialized = new byte[parsed.l()];
    final int written = parsed.write(reserialized, 0);
    assertEquals(reserialized.length, written);
    assertArrayEquals(ix.data(), reserialized);
  }

  @Test
  void onChainUpdateSymbol() {
    // devnet 5DYworNVzp7EZ8rrWqfZVtygrUMLKAh8d5E4vQGeCNRz1kbaM4GAgK5JoXKYjZic9i6wD5mokzqtwH6Yn3FyJyrY

    final var metadataKey = PublicKey.fromBase58Encoded("4iuiwEyRAmTeZkR9ttkaJtFMdSP1CMbYyUdSYmentesq");
    final var updateAuthorityKey = PublicKey.fromBase58Encoded("AF1cFeC2i6fzkvXRFGcin8SdqaM5bkjsWvezqpLGUs2D");
    final var expectedData = Base64.getDecoder().decode("3ekxLbXK3MgBAwAAAFQzMw==");

    final var ix = updateTokenMetadataSymbol(INVOKED_PROGRAM, metadataKey, updateAuthorityKey, "T33");

    assertArrayEquals(expectedData, ix.data());

    final var parsed = UpdateTokenMetadataFieldIxData.read(expectedData, 0);
    assertEquals(TokenMetadataField.Symbol, parsed.field());
    assertNull(parsed.key());
    assertEquals("T33", parsed.value());
  }

  @Test
  void onChainUpdateCustomKey() {
    // devnet 3GehERwU3DihQWk3FTvH5hMG7t6czg8vkBpV2BkFWdPZE3yjg3VVr26B1kLSKZd32DiCvckuugatp3dqA3G2TeUz

    final var metadataKey = PublicKey.fromBase58Encoded("4iuiwEyRAmTeZkR9ttkaJtFMdSP1CMbYyUdSYmentesq");
    final var updateAuthorityKey = PublicKey.fromBase58Encoded("AF1cFeC2i6fzkvXRFGcin8SdqaM5bkjsWvezqpLGUs2D");
    final var expectedData = Base64.getDecoder().decode("3ekxLbXK3MgDBgAAAHN0YWdlMgwAAAB2aXN1YWwtY2hlY2s=");

    final var ix = updateTokenMetadataCustomField(INVOKED_PROGRAM, metadataKey, updateAuthorityKey, "stage2", "visual-check");

    assertArrayEquals(expectedData, ix.data());

    final var parsed = UpdateTokenMetadataFieldIxData.read(expectedData, 0);
    assertEquals(TokenMetadataField.Key, parsed.field());
    assertEquals("stage2", parsed.key());
    assertEquals("visual-check", parsed.value());
  }

  @Test
  void readNullOrEmptyData() {
    assertNull(UpdateTokenMetadataFieldIxData.read(null, 0));
    assertNull(UpdateTokenMetadataFieldIxData.read(new byte[0], 0));
  }

  /// The [Instruction]-taking overload must honour the instruction's own data offset rather
  /// than assuming the payload starts at zero.
  @Test
  void readFromInstruction() {
    final var value = "from-instruction";
    final var ix = updateTokenMetadataName(INVOKED_PROGRAM, METADATA_KEY, UPDATE_AUTHORITY, value);

    final var parsed = UpdateTokenMetadataFieldIxData.read(ix);

    assertNotNull(parsed);
    assertEquals(TokenMetadataField.Name, parsed.field());
    assertNull(parsed.key());
    assertEquals(value, parsed.value());
    assertArrayEquals(UPDATE_TOKEN_METADATA_FIELD_DISCRIMINATOR.data(), parsed.discriminator());
  }

  /// The field ordinal is bounds checked against the enum: the last valid ordinal must still
  /// resolve, and the first invalid one must yield a null field rather than walking off the
  /// end of `VALUES`.
  @Test
  void fieldOrdinalBounds() {
    // Key is the highest ordinal and must still resolve
    final var highest = UpdateTokenMetadataFieldIxData.read(
        blobWithFieldOrdinal(TokenMetadataField.Key.ordinal(), true), 0);
    assertEquals(TokenMetadataField.Key, highest.field());

    // one past the end: unknown field, parsed as null, and the key block is not consumed
    final int pastEnd = TokenMetadataField.values().length;
    final var unknown = UpdateTokenMetadataFieldIxData.read(blobWithFieldOrdinal(pastEnd, false), 0);
    assertNull(unknown.field());
    assertNull(unknown.key());
    assertEquals("v", unknown.value());

    // an ordinal far out of range is handled the same way, not by indexing
    final var wayPastEnd = UpdateTokenMetadataFieldIxData.read(blobWithFieldOrdinal(0xFF, false), 0);
    assertNull(wayPastEnd.field());
  }

  /// `write` reports the number of bytes written, which must be independent of where in the
  /// buffer it started.
  @Test
  void writeReturnsLengthNotEndPosition() {
    final var ix = updateTokenMetadataCustomField(INVOKED_PROGRAM, METADATA_KEY, UPDATE_AUTHORITY, "k", "v");
    final var parsed = UpdateTokenMetadataFieldIxData.read(ix.data(), 0);
    final int len = parsed.l();

    final int offset = 9;
    final byte[] buffer = new byte[offset + len];
    assertEquals(len, parsed.write(buffer, offset));

    // the bytes land at the offset, and re-reading from there recovers the same record
    final var reparsed = UpdateTokenMetadataFieldIxData.read(buffer, offset);
    assertEquals("k", reparsed.key());
    assertEquals("v", reparsed.value());
    assertEquals(TokenMetadataField.Key, reparsed.field());
  }

  /// 8-byte discriminator, 1-byte field ordinal, optional u32-prefixed key, u32-prefixed value.
  private static byte[] blobWithFieldOrdinal(final int fieldOrdinal, final boolean withKey) {
    final byte[] key = withKey ? "k".getBytes(java.nio.charset.StandardCharsets.UTF_8) : new byte[0];
    final byte[] value = "v".getBytes(java.nio.charset.StandardCharsets.UTF_8);

    final int keyBlock = withKey ? Integer.BYTES + key.length : 0;
    final byte[] data = new byte[8 + 1 + keyBlock + Integer.BYTES + value.length];

    System.arraycopy(UPDATE_TOKEN_METADATA_FIELD_DISCRIMINATOR.data(), 0, data, 0, 8);
    int i = 8;
    data[i++] = (byte) fieldOrdinal;
    if (withKey) {
      software.sava.core.encoding.ByteUtil.putInt32LE(data, i, key.length);
      i += Integer.BYTES;
      System.arraycopy(key, 0, data, i, key.length);
      i += key.length;
    }
    software.sava.core.encoding.ByteUtil.putInt32LE(data, i, value.length);
    i += Integer.BYTES;
    System.arraycopy(value, 0, data, i, value.length);
    return data;
  }
}

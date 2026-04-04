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
}

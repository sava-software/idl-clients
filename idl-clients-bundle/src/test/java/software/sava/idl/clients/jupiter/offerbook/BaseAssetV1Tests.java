package software.sava.idl.clients.jupiter.offerbook;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.jupiter.offerbook.gen.types.BaseAssetV1;
import software.sava.idl.clients.jupiter.offerbook.gen.types.Key;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

/// Decodes a real Metaplex Core `AssetV1` account from mainnet.
///
/// This layout is the case where an Anchor IDL is genuinely ambiguous. The IDL declares
/// `"discriminator": [1]` **and** a first field `key: Key` — the same byte described twice, since
/// `Key::AssetV1` *is* 1. Nothing in the JSON distinguishes that from a one-byte prefix followed by
/// a separate field, so only the bytes settle it.
///
/// The account below settles it: 100 bytes that decompose exactly as
/// `key`(1) + `owner`(32) + `updateAuthority`(1 + 32) + `name`(4 + 25) + `uri`(4 + 0) + `seq`(1).
/// `owner` therefore starts at offset **1**. Two earlier generator behaviours both got this wrong —
/// consuming eight bytes and then reading `key` (owner at 9), and consuming one and then reading
/// `key` (owner at 2).
final class BaseAssetV1Tests {

  private static final String ADDRESS = "1Lz3bkYMDk5b17LZ3T5oJLEzFfSBjBYMp7hqfWmN1cQ";

  private static byte[] accountData() {
    try (var in = BaseAssetV1Tests.class.getResourceAsStream("/jupiter/baseAssetV1-" + ADDRESS + ".base64")) {
      assertNotNull(in, "fixture for " + ADDRESS + " is missing");
      return Base64.getDecoder().decode(new String(in.readAllBytes(), StandardCharsets.UTF_8).trim());
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Test
  void theLeadingByteIsTheKeyFieldNotAPrefix() {
    final byte[] data = accountData();
    assertEquals(100, data.length);
    assertEquals(1, data[0], "Key::AssetV1");

    assertEquals(0, BaseAssetV1.KEY_OFFSET);
    assertEquals(1, BaseAssetV1.OWNER_OFFSET);
    assertEquals(33, BaseAssetV1.UPDATE_AUTHORITY_OFFSET);
    assertEquals(1, BaseAssetV1.DISCRIMINATOR.length(), "the declared discriminator is one byte");
  }

  @Test
  void theAccountDecodesToItsRealFieldValues() {
    final byte[] data = accountData();
    final var asset = BaseAssetV1.read(PublicKey.fromBase58Encoded(ADDRESS), data, 0);
    assertNotNull(asset);

    assertEquals(Key.AssetV1, asset.key());
    // bytes 1..33 of the account, read as a pubkey
    assertEquals(
        PublicKey.readPubKey(data, 1), asset.owner(),
        "owner must be read from offset 1, immediately after the key byte"
    );
    assertEquals("00AZskBJ06kUjwO2U4gymHKI7", asset.name());
    assertEquals("", asset.uri());
    assertTrue(asset.seq().isEmpty());

    // the decoder must account for every byte the account holds, and no more
    assertEquals(data.length, asset.l());
  }

  @Test
  void theAccountReserializesToTheCapturedBytes() {
    final byte[] data = accountData();
    final var asset = BaseAssetV1.read(PublicKey.fromBase58Encoded(ADDRESS), data, 0);

    final byte[] out = new byte[data.length];
    assertEquals(data.length, asset.write(out, 0));
    assertArrayEquals(data, out);
  }

  /// The discriminator filter selects these accounts by their leading byte. Zero-padded to eight it
  /// matched essentially nothing, since bytes 1-7 are the first seven bytes of `owner` — an
  /// independent second defect the width fix repairs.
  @Test
  void theDiscriminatorFilterMatchesTheAccountsLeadingByte() {
    final byte[] data = accountData();
    final byte[] discriminator = BaseAssetV1.DISCRIMINATOR.data();
    assertEquals(1, discriminator.length);
    assertEquals(data[0], discriminator[0]);
  }
}

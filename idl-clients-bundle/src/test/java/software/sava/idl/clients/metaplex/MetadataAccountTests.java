package software.sava.idl.clients.metaplex;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.metaplex.token.metadata.gen.types.Key;
import software.sava.idl.clients.metaplex.token.metadata.gen.types.Metadata;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

/// Decodes a real Token Metadata account, and exists as the counterpart to
/// `jupiter/offerbook/BaseAssetV1Tests`.
///
/// The two are the same IDL shape — an account whose leading field is an enum — with opposite
/// correct handling, which is exactly why the generator cannot infer it. `BaseAssetV1` declares a
/// one-byte discriminator that *is* its `key` field and is configured as such. `Metadata` declares
/// no discriminator at all: `key` is simply its first field, so nothing is consumed ahead of it and
/// the layout is right without any configuration. If the inline rule ever widened into an
/// inference again, this account is what it would corrupt — every field after `key` would shift.
///
/// The fixture is upstream's own test data, `faulty_13gxS4r6…buf` from mpl-token-metadata's js
/// tests (sha256 582f1d4862efcc7e4beb1864220991bf523ec0bacd88915cbee891579b38a7eb). Upstream names
/// it "faulty" because its trailing optionals are absent, not because the account is malformed; its
/// own deserializer test asserts the same nulls this one does.
final class MetadataAccountTests {

  /// The embedded keys are the update authority and the mint. The `13gxS4r6…` in the filename is
  /// upstream's label for the account, and nothing in the bytes confirms it, so it is not asserted.
  private static final String FIXTURE =
      "/metaplex/metadata-13gxS4r6SiJn8fwizKZT2W8x8DL6vjN1nAhPWsfNXegb.base64";

  private static byte[] accountData() {
    return read(FIXTURE);
  }

  private static byte[] read(final String resource) {
    try (var in = MetadataAccountTests.class.getResourceAsStream(resource)) {
      assertNotNull(in, "fixture " + resource + " is missing");
      return Base64.getDecoder().decode(new String(in.readAllBytes(), StandardCharsets.UTF_8).trim());
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Test
  void theLeadingKeyFieldIsNotADiscriminator() {
    final byte[] data = accountData();
    assertEquals(679, data.length);
    assertEquals(4, data[0], "Key::MetadataV1");

    // no discriminator is consumed, so the fields start at zero
    assertEquals(0, Metadata.KEY_OFFSET);
    assertEquals(1, Metadata.UPDATE_AUTHORITY_OFFSET);
    assertEquals(33, Metadata.MINT_OFFSET);
  }

  @Test
  void theAccountDecodesToItsRealFieldValues() {
    final byte[] data = accountData();
    // Read at fixed offsets rather than through the record: upstream's fixture is named "faulty"
    // for a reason — past its edition nonce the bytes are noise, with a `tokenStandard` presence
    // byte of 250 where Borsh permits only 0 or 1, and 315 bytes of junk after it. Upstream's JS
    // deserializer is lenient there and yields nulls; a strict reader refuses it, correctly. What
    // the fixture can still prove is the part this test exists for: where the leading field sits.
    assertEquals(Key.MetadataV1, Key.read(data, Metadata.KEY_OFFSET));
    assertEquals(
        "DRGNjvBvnXNiQz9dTppGk1tAsVxtJsvhEmojEfBU3ezf",
        PublicKey.readPubKey(data, Metadata.UPDATE_AUTHORITY_OFFSET).toBase58()
    );
    assertEquals(
        "GoBTZuBqDZf7PYpcQBSSQ4FDBydYaqsRB5khrUjURdp1",
        PublicKey.readPubKey(data, Metadata.MINT_OFFSET).toBase58()
    );
  }

  /// The whole record, decoded from a well-formed mainnet account — BONK's metadata. Upstream's
  /// fixture cannot carry this because its tail is malformed, so the end-to-end layout is proved
  /// against an account captured from chain instead.
  @Test
  void aWellFormedAccountDecodesEndToEnd() {
    final var address = "FDZZbyY9XGpL3CNKUZxLk3wFTTQYL3TkDiDzqxrizcPN";
    final byte[] data = read("/metaplex/metadata-" + address + ".base64");

    final var metadata = Metadata.read(PublicKey.fromBase58Encoded(address), data, 0);
    assertNotNull(metadata);

    assertEquals(Key.MetadataV1, metadata.key());
    assertEquals("Bonk", metadata.data().name().trim());
    assertEquals("Bonk", metadata.data().symbol().trim());
    assertEquals(
        "https://arweave.net/QPC6FYdUn-3V8ytFNuoCS85S2tHAuiDblh6u3CIZLsw",
        metadata.data().uri().trim()
    );
    // the update authority is read from offset 1, immediately after the key byte
    assertEquals(PublicKey.readPubKey(data, 1), metadata.updateAuthority());
    assertEquals(PublicKey.readPubKey(data, 33), metadata.mint());
    assertNotNull(metadata.tokenStandard(), "this account's trailing optionals are well formed");
  }

}

package software.sava.idl.clients.spl.attestation_service;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.spl.attestation_service.gen.types.Attestation;
import software.sava.idl.clients.spl.attestation_service.gen.types.Credential;
import software.sava.idl.clients.spl.attestation_service.gen.types.Schema;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

/// The account-side twin of [AttestationInstructionTests], and the same defect one frame over.
///
/// SAS is Shank, and its Pinocchio program writes a **one**-byte account tag — Credential 0,
/// Schema 1, Attestation 2 — with fields starting at offset 1. The generator's Anchor fallback
/// synthesized `sha256("account:<Name>")[0..8]` instead, matching none of the 12,340 accounts the
/// program owns. It now takes the literal bytes from `accountDiscriminators` in
/// main_net_programs.json.
///
/// Every assertion below is written against **the bytes the program actually writes**, so none of
/// it is derived from the client under test. Do not relax one to match a generated constant.
final class AttestationAccountTests {

  private static final String CREDENTIAL = "12nA4fZvLYUC6dtYHzi9wbtUhQs5SfouuLbYLKqJpbnx";
  private static final String SCHEMA = "21FENHTPtM7zoZVKPV42QUMb8UqKZetLj8PMQghQkYVG";
  private static final String ATTESTATION = "11UR6sZ57WRDtmd27JK9vMaoCwXeb8rVgdJ93zCajhH";

  /// Captured with `getAccountInfo` from mainnet; each file is one account's raw data.
  private static byte[] account(final String name) {
    try (var in = AttestationAccountTests.class.getResourceAsStream(
        "/attestation_service/" + name + ".base64")) {
      assertNotNull(in, "fixture /attestation_service/" + name + ".base64 is missing");
      return Base64.getDecoder().decode(new String(in.readAllBytes(), StandardCharsets.UTF_8).trim());
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  /// The declared widths, checked against the enum the program serializes from. This is the
  /// assertion that fails first and the only one that matters — everything below follows from it.
  @Test
  void everyAccountDiscriminatorIsTheSingleByteTheProgramWrites() {
    assertEquals(1, Credential.DISCRIMINATOR.length(), "AttestationAccountDiscriminators is #[repr(u8)]");
    assertEquals(0, Credential.DISCRIMINATOR.data()[0] & 0xFF, "CredentialDiscriminator = 0");

    assertEquals(1, Schema.DISCRIMINATOR.length());
    assertEquals(1, Schema.DISCRIMINATOR.data()[0] & 0xFF, "SchemaDiscriminator = 1");

    assertEquals(1, Attestation.DISCRIMINATOR.length());
    assertEquals(2, Attestation.DISCRIMINATOR.data()[0] & 0xFF, "AttestationDiscriminator = 2");
  }

  /// Fields begin at offset one, not eight. The offsets are public constants used to build memcmp
  /// filters, so a wrong one silently returns nothing from a scan rather than failing.
  @Test
  void fieldsBeginAfterTheSingleDiscriminatorByte() {
    assertEquals(1, Credential.AUTHORITY_OFFSET);
    assertEquals(1, Schema.CREDENTIAL_OFFSET);
    assertEquals(1, Attestation.NONCE_OFFSET);
  }

  /// A real credential, decoded exactly. 83 bytes decompose as
  /// `discriminator`(1) + `authority`(32) + `name`(4 + 10) + `authorizedSigners`(4 + 32), and the
  /// account's sole authorized signer is its own authority.
  @Test
  void aRealCredentialDecodesToItsFieldValues() {
    final byte[] data = account("credential-" + CREDENTIAL);
    assertEquals(83, data.length);
    assertEquals(0, data[0] & 0xFF, "the discriminator byte the program wrote");

    final var credential = Credential.readChecked(PublicKey.fromBase58Encoded(CREDENTIAL), data, 0);
    assertNotNull(credential);
    assertEquals(
        "49pihiqwqBb87qmYYdABxnBZQH6YovTZW1wxmFRQTPD9", credential.authority().toBase58(),
        "an authority read from offset 8 would be garbage"
    );
    assertEquals("hackshield", new String(credential.name(), StandardCharsets.UTF_8));
    assertEquals(1, credential.authorizedSigners().length);
    assertEquals(credential.authority(), credential.authorizedSigners()[0]);

    // Exactly, not at-most: `<=` would pass on a decoder that dropped a trailing field.
    assertEquals(data.length, credential.l(), "the decoder must account for every byte");
  }

  /// The other two accounts decode through the checked entry point rather than throwing, and they
  /// account for every byte.
  ///
  /// Exact equality, like the credential above and for the same reason: `<=` passes on a decoder
  /// that drops a *trailing* field, and Attestation is precisely where that would bite — it ends
  /// `signer`(32) + `expiry`(8) + `tokenAccount`(32), so losing the last one is invisible to an
  /// over-read check. Equality is safe to assert rather than a guess: every one of the 12,340 live
  /// accounts decodes with zero bytes left over under this layout.
  @Test
  void theOtherAccountsAccountForEveryByte() {
    final byte[] schemaData = account("schema-" + SCHEMA);
    assertEquals(1, schemaData[0] & 0xFF);
    final var schema = Schema.readChecked(PublicKey.fromBase58Encoded(SCHEMA), schemaData, 0);
    assertNotNull(schema);
    assertEquals(schemaData.length, schema.l(), "Schema must consume its whole account");

    final byte[] attestationData = account("attestation-" + ATTESTATION);
    assertEquals(2, attestationData[0] & 0xFF);
    final var attestation = Attestation.readChecked(PublicKey.fromBase58Encoded(ATTESTATION), attestationData, 0);
    assertNotNull(attestation);
    assertEquals(attestationData.length, attestation.l(), "Attestation must consume its whole account");
  }

  /// A narrowed discriminator has to narrow `write` too. A `read` that consumes one byte paired
  /// with a `write` that emits eight would corrupt every account this client serializes, and no
  /// assertion above would notice — they all decode.
  @Test
  void eachAccountReserializesToTheCapturedBytes() {
    final byte[] credentialData = account("credential-" + CREDENTIAL);
    final var credential = Credential.readChecked(PublicKey.fromBase58Encoded(CREDENTIAL), credentialData, 0);
    final byte[] out = new byte[credentialData.length];
    assertEquals(credentialData.length, credential.write(out, 0));
    assertArrayEquals(credentialData, out, "a rebuilt Credential must be byte-identical to the chain's");

    final byte[] schemaData = account("schema-" + SCHEMA);
    final var schema = Schema.readChecked(PublicKey.fromBase58Encoded(SCHEMA), schemaData, 0);
    final byte[] schemaOut = new byte[schemaData.length];
    assertEquals(schemaData.length, schema.write(schemaOut, 0));
    assertArrayEquals(schemaData, schemaOut);

    final byte[] attestationData = account("attestation-" + ATTESTATION);
    final var attestation = Attestation.readChecked(PublicKey.fromBase58Encoded(ATTESTATION), attestationData, 0);
    final byte[] attestationOut = new byte[attestationData.length];
    assertEquals(attestationData.length, attestation.write(attestationOut, 0));
    assertArrayEquals(attestationData, attestationOut);
  }

  /// The three are mutually exclusive on one byte, which is the whole of the program's own check.
  /// A generator that emitted a one-byte discriminator but got the *value* wrong would pass every
  /// assertion above for a single type and fail here.
  @Test
  void theThreeAccountTypesRefuseEachOther() {
    final byte[] credentialData = account("credential-" + CREDENTIAL);
    final byte[] schemaData = account("schema-" + SCHEMA);
    final byte[] attestationData = account("attestation-" + ATTESTATION);

    final var credentialKey = PublicKey.fromBase58Encoded(CREDENTIAL);
    assertThrows(IllegalArgumentException.class, () -> Schema.readChecked(credentialKey, credentialData, 0));
    assertThrows(IllegalArgumentException.class, () -> Attestation.readChecked(credentialKey, credentialData, 0));
    assertThrows(IllegalArgumentException.class, () -> Credential.readChecked(credentialKey, schemaData, 0));
    assertThrows(IllegalArgumentException.class, () -> Credential.readChecked(credentialKey, attestationData, 0));
  }
}

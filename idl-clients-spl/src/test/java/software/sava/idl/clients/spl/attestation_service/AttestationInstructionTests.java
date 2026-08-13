package software.sava.idl.clients.spl.attestation_service;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.idl.clients.spl.attestation_service.gen.SolanaAttestationServiceProgram;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/// Instruction data captured from mainnet, against a client that until 2026-08-13 could not have
/// produced any of it.
///
/// Solana Attestation Service declares every instruction's dispatch key as
/// `"discriminant": {"type": "u8", "value": N}` and declares no `"discriminator"` anywhere. The
/// generator's instruction parser skipped unknown fields silently, so `discriminant` was dropped,
/// the instruction looked like an Anchor instruction with a missing discriminator, and the
/// fallback synthesized an eight-byte `sha256("global:<name>")` prefix. All twelve builders sent
/// that hash to a program that reads one byte.
///
/// `CloseAttestation` is the clearest case in the corpus: the whole on-chain instruction is
/// **one byte**, `[7]`. An eight-byte discriminator cannot express a one-byte instruction at all,
/// so there is no argument about offsets or interpretation — the old output was longer than the
/// entire real instruction.
final class AttestationInstructionTests {

  private static final PublicKey PROGRAM =
      PublicKey.fromBase58Encoded("22zoJMtdu4tQc2PzL74ZUT7FrwgB1Udec8DdW4yw4BdG");

  /// Captured with `getTransaction` from mainnet; each file records one instruction's `data`.
  private static byte[] instruction(final String name) {
    try (var in = AttestationInstructionTests.class.getResourceAsStream(
        "/attestation_service/ix-" + name + ".base64")) {
      assertNotNull(in, "fixture /attestation_service/ix-" + name + ".base64 is missing");
      return Base64.getDecoder().decode(new String(in.readAllBytes(), StandardCharsets.UTF_8).trim());
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  /// Signature `2y3MDZbP2D9RDJpZhVUxziGqBn5gdr1CErdGoPdy8vNFdk2DVtusxcCioyWbmAZvabE9htojUruyzvYLUxGwzBWe`,
  /// which succeeded on chain.
  private static final byte[] CLOSE_ATTESTATION = instruction("closeAttestation-7");
  private static final byte[] CREATE_ATTESTATION = instruction("createAttestation-6");

  /// One byte on the wire, one byte generated. This is the assertion the old client could not
  /// have satisfied under any reading: it emitted eight bytes for an instruction that is one.
  @Test
  void closeAttestationIsTheSingleByteTheChainCarried() {
    assertEquals(1, CLOSE_ATTESTATION.length, "the entire instruction is its dispatch byte");
    assertEquals(7, CLOSE_ATTESTATION[0] & 0xFF);

    final var generated = SolanaAttestationServiceProgram.CLOSE_ATTESTATION_DISCRIMINATOR;
    assertEquals(1, generated.length());
    assertEquals(7, generated.data()[0] & 0xFF);
    assertArrayEquals(CLOSE_ATTESTATION, generated.data(),
        "the generated constant IS the whole instruction here");
  }

  /// The builder a caller actually uses, against the bytes the chain actually carried.
  @Test
  void theBuilderProducesTheBytesTheChainCarried() {
    final var invoked = AccountMeta.createInvoked(PROGRAM);
    final var closeAttestation = SolanaAttestationServiceProgram.closeAttestation(invoked, List.of());

    assertArrayEquals(CLOSE_ATTESTATION, closeAttestation.data(),
        "a built CloseAttestation must equal the one that succeeded on chain");
  }

  /// An instruction with arguments decodes from the real payload, which pins that the arguments
  /// begin at offset one rather than eight. A wrong discriminator width does not fail loudly here
  /// — it reads seven bytes of the payload as part of the key and shifts every field.
  @Test
  void aRealInstructionWithArgumentsDecodesFromOffsetOne() {
    assertEquals(6, CREATE_ATTESTATION[0] & 0xFF);
    assertEquals(148, CREATE_ATTESTATION.length);

    final var createAttestation =
        SolanaAttestationServiceProgram.CreateAttestationIxData.read(CREATE_ATTESTATION, 0);
    assertNotNull(createAttestation);
    assertEquals(1, createAttestation.discriminator().length());
    assertEquals(6, createAttestation.discriminator().data()[0] & 0xFF);
    assertNotNull(createAttestation.nonce(), "a pubkey read from the wrong offset would be garbage");
    assertNotNull(createAttestation.data());

    // Everything the instruction carried is accounted for, and nothing beyond it was read.
    assertTrue(createAttestation.l() <= CREATE_ATTESTATION.length,
        () -> "read past the instruction: " + createAttestation.l() + " > " + CREATE_ATTESTATION.length);
  }

  /// Re-serializing the decoded instruction reproduces the captured bytes.
  @Test
  void aRealInstructionReserializesToTheCapturedBytes() {
    final var createAttestation =
        SolanaAttestationServiceProgram.CreateAttestationIxData.read(CREATE_ATTESTATION, 0);
    final byte[] out = new byte[createAttestation.l()];
    createAttestation.write(out, 0);

    assertArrayEquals(
        java.util.Arrays.copyOf(CREATE_ATTESTATION, out.length), out,
        "a rebuilt instruction must be byte-identical to the chain's"
    );
  }

  /// `EmitEvent` is the documented exception, and it is the IDL that is imprecise rather than the
  /// generator.
  ///
  /// The IDL declares it `{"type": "u8", "value": 228}`, but the captured instruction begins
  /// `e4 45 a5 2e 51 cb 9a 1d` — Anchor's eight-byte event-CPI marker, whose first byte is 228.
  /// So the declaration describes one byte of an eight-byte key. It costs nothing here: this is
  /// the instruction the program issues to *itself* to log an event, a client never builds it, and
  /// under Anchor's `starts_with` dispatch a one-byte `[228]` still selects it. Asserted so the
  /// discrepancy is recorded where someone meets it rather than rediscovered.
  @Test
  void emitEventIsAnchorsEventCpiMarkerWhichTheIdlUnderDescribes() {
    final byte[] emitEvent = instruction("emitEvent-228");
    final int[] anchorEventCpi = {228, 69, 165, 46, 81, 203, 154, 29};

    for (int i = 0; i < anchorEventCpi.length; i++) {
      assertEquals(anchorEventCpi[i], emitEvent[i] & 0xFF, "byte " + i + " of the event-CPI marker");
    }
    assertEquals(228, SolanaAttestationServiceProgram.EMIT_EVENT_DISCRIMINATOR.data()[0] & 0xFF,
        "the generator emits exactly what the IDL declares");
  }

  /// Every declared ordinal is one byte, so the fix is not limited to the three instructions a
  /// transaction happened to contain.
  @Test
  void everyInstructionDispatchesOnASingleByte() {
    final var wrongWidth = new java.util.ArrayList<String>();
    for (final var field : SolanaAttestationServiceProgram.class.getDeclaredFields()) {
      if (!field.getName().endsWith("_DISCRIMINATOR")
          || field.getType() != software.sava.core.programs.Discriminator.class) {
        continue;
      }
      try {
        final var discriminator = (software.sava.core.programs.Discriminator) field.get(null);
        if (discriminator.length() != 1) {
          wrongWidth.add(field.getName() + " is " + discriminator.length() + " bytes");
        }
      } catch (final IllegalAccessException e) {
        throw new AssertionError(e);
      }
    }
    assertEquals(List.of(), wrongWidth, "this program dispatches on one byte, everywhere");
  }
}

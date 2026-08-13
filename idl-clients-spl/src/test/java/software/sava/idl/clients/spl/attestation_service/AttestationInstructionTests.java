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
import java.util.Set;

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

    // Exactly, not at-most: `<=` would pass on a decoder that silently dropped a trailing field.
    assertEquals(CREATE_ATTESTATION.length, createAttestation.l(),
        "the decoder must account for every byte the instruction carried");
  }

  /// Re-serializing the decoded instruction reproduces the captured bytes.
  @Test
  void aRealInstructionReserializesToTheCapturedBytes() {
    final var createAttestation =
        SolanaAttestationServiceProgram.CreateAttestationIxData.read(CREATE_ATTESTATION, 0);
    final byte[] out = new byte[createAttestation.l()];
    final int written = createAttestation.write(out, 0);

    assertEquals(CREATE_ATTESTATION.length, written, "a short write would truncate the instruction");
    // The whole fixture, not a prefix of it: comparing against copyOf(fixture, out.length) would
    // accept any decoder that dropped a suffix.
    assertArrayEquals(CREATE_ATTESTATION, out, "a rebuilt instruction must be byte-identical to the chain's");
  }

  /// `EmitEvent` dispatches on byte 228 exactly, like every other instruction here.
  ///
  /// An earlier revision of this test claimed the IDL "under-described an eight-byte Anchor
  /// event-CPI key" and that `starts_with` dispatch rescued it. Every part of that was wrong. The
  /// program is **pinocchio**, not Anchor; its entrypoint is
  /// `let (discriminator, instruction_data) = instruction_data.split_first()` followed by an exact
  /// `match`, with `228 => process_emit_event(program_id, accounts)` — carrying a comment reading
  /// `matches EVENT_IX_TAG[0]`, and not passing the remaining data to the handler at all.
  ///
  /// So there is no `starts_with`, no eight-byte key, and nothing under-described: the trailing
  /// `e4 45 a5 2e 51 cb 9a 1d` is the event payload's own framing tag, which the handler ignores.
  /// The generated one-byte `[228]` is simply correct.
  @Test
  void emitEventDispatchesOnByte228Exactly() {
    final byte[] emitEvent = instruction("emitEvent-228");

    assertEquals(228, emitEvent[0] & 0xFF, "the dispatch byte the entrypoint matches on");
    assertEquals(1, SolanaAttestationServiceProgram.EMIT_EVENT_DISCRIMINATOR.length());
    assertEquals(228, SolanaAttestationServiceProgram.EMIT_EVENT_DISCRIMINATOR.data()[0] & 0xFF);

    // The bytes after the dispatch byte are the event's framing tag, which the handler never
    // reads. Asserted so the next reader can see why they look like a discriminator and are not.
    final int[] eventTagTail = {69, 165, 46, 81, 203, 154, 29};
    for (int i = 0; i < eventTagTail.length; i++) {
      assertEquals(eventTagTail[i], emitEvent[i + 1] & 0xFF, "framing byte " + (i + 1));
    }
  }

  /// The whole dispatch table, by exact value, against the deployed entrypoint's `match` arms.
  ///
  /// Width alone is too weak a check: it passes if a constant is dropped, and it passes if two
  /// ordinals are swapped. These are the twelve arms in
  /// `attestation-service/program/src/entrypoint.rs` — note the gap at 8, which is why a
  /// count-and-range check would not do either.
  @Test
  void theDispatchTableMatchesTheDeployedEntrypoint() {
    final var expected = new java.util.LinkedHashMap<String, Integer>();
    expected.put("CREATE_CREDENTIAL_DISCRIMINATOR", 0);
    expected.put("CREATE_SCHEMA_DISCRIMINATOR", 1);
    expected.put("CHANGE_SCHEMA_STATUS_DISCRIMINATOR", 2);
    expected.put("CHANGE_AUTHORIZED_SIGNERS_DISCRIMINATOR", 3);
    expected.put("CHANGE_SCHEMA_DESCRIPTION_DISCRIMINATOR", 4);
    expected.put("CHANGE_SCHEMA_VERSION_DISCRIMINATOR", 5);
    expected.put("CREATE_ATTESTATION_DISCRIMINATOR", 6);
    expected.put("CLOSE_ATTESTATION_DISCRIMINATOR", 7);
    expected.put("TOKENIZE_SCHEMA_DISCRIMINATOR", 9);
    expected.put("CREATE_TOKENIZED_ATTESTATION_DISCRIMINATOR", 10);
    expected.put("CLOSE_TOKENIZED_ATTESTATION_DISCRIMINATOR", 11);
    expected.put("EMIT_EVENT_DISCRIMINATOR", 228);

    final var actual = new java.util.LinkedHashMap<String, Integer>();
    for (final var field : SolanaAttestationServiceProgram.class.getDeclaredFields()) {
      if (!field.getName().endsWith("_DISCRIMINATOR")
          || field.getType() != software.sava.core.programs.Discriminator.class) {
        continue;
      }
      try {
        final var discriminator = (software.sava.core.programs.Discriminator) field.get(null);
        assertEquals(1, discriminator.length(), field.getName() + " must be one byte");
        actual.put(field.getName(), discriminator.data()[0] & 0xFF);
      } catch (final IllegalAccessException e) {
        throw new AssertionError(e);
      }
    }

    assertEquals(expected, actual, "the client's dispatch table must be the program's");
    assertEquals(expected.size(), Set.copyOf(actual.values()).size(), "ordinals must be distinct");
  }
}

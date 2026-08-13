package software.sava.idl.clients.metaplex.token.metadata;

import org.junit.jupiter.api.Test;
import software.sava.idl.clients.metaplex.token.metadata.gen.TokenMetadataProgram;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

/// Instruction data captured from mainnet, against a client that until 2026-08-13 could not have
/// produced any of it.
///
/// Token Metadata dispatches on the first byte and nothing else — its processor opens with
/// `let (variant, _args) = input.split_first()` — and its IDL declares that byte per instruction
/// as `"discriminant": {"type": "u8", "value": N}`, with no `"discriminator"` anywhere.
///
/// The generator's instruction parser skipped fields it did not model, in silence. `discriminant`
/// was dropped, every instruction then looked like an Anchor instruction with a missing
/// discriminator, and that falls back to synthesizing `sha256("global:<name>")` and taking eight
/// bytes. All 58 builders sent a name hash to a program that reads one byte. The run stayed green
/// because the skip was silent; the sibling *type* parser has always thrown on an unknown field.
///
/// These are three real instructions from two successful transactions, spanning a short
/// instruction, a long one, and one whose argument is a defined type.
final class RealInstructionTests {

  private static byte[] instruction(final String name) {
    try (var in = RealInstructionTests.class.getResourceAsStream("/metaplex/ix-" + name + ".base64")) {
      assertNotNull(in, "fixture /metaplex/ix-" + name + ".base64 is missing");
      return Base64.getDecoder().decode(new String(in.readAllBytes(), StandardCharsets.UTF_8).trim());
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  /// Signature `2nPpxCE6JUzaJjMKtji4iLfRbBYcyMoxNvKspdMSyjkhk1UZL3PQJnynSxg8GLhTthm8RqTvPRP3pqoiomm1TaX6`.
  private static final byte[] TRANSFER = instruction("transfer-49");
  /// Both from `4KdExHwVGVwrkAzvAd6nDY7bL3xhdvHuCaBkJrx8HfTLgQQDEgUPHUJhFD6wSC3AsmVgafDpdPr57bak2G22P9oZ`.
  private static final byte[] CREATE_METADATA_ACCOUNT_V3 = instruction("createMetadataAccountV3-33");
  private static final byte[] UPDATE_METADATA_ACCOUNT_V2 = instruction("updateMetadataAccountV2-15");

  /// The dispatch byte, for each captured instruction, against the constant the client generates.
  @Test
  void theGeneratedOrdinalsMatchTheChain() {
    assertEquals(49, TRANSFER[0] & 0xFF);
    assertEquals(33, CREATE_METADATA_ACCOUNT_V3[0] & 0xFF);
    assertEquals(15, UPDATE_METADATA_ACCOUNT_V2[0] & 0xFF);

    assertEquals(49, TokenMetadataProgram.TRANSFER_DISCRIMINATOR.data()[0] & 0xFF);
    assertEquals(33, TokenMetadataProgram.CREATE_METADATA_ACCOUNT_V_3_DISCRIMINATOR.data()[0] & 0xFF);
    assertEquals(15, TokenMetadataProgram.UPDATE_METADATA_ACCOUNT_V_2_DISCRIMINATOR.data()[0] & 0xFF);

    for (final var discriminator : new software.sava.core.programs.Discriminator[]{
        TokenMetadataProgram.TRANSFER_DISCRIMINATOR,
        TokenMetadataProgram.CREATE_METADATA_ACCOUNT_V_3_DISCRIMINATOR,
        TokenMetadataProgram.UPDATE_METADATA_ACCOUNT_V_2_DISCRIMINATOR}) {
      assertEquals(1, discriminator.length(), "the program reads one byte");
    }
  }

  /// The eleven-byte instruction, decoded. Arguments begin at offset **one**: an eight-byte
  /// discriminator would not merely mis-read this one, it is longer than three quarters of it.
  @Test
  void aRealTransferDecodesFromOffsetOne() {
    assertEquals(11, TRANSFER.length);

    final var transfer = TokenMetadataProgram.TransferIxData.read(TRANSFER, 0);
    assertNotNull(transfer);
    assertEquals(1, transfer.discriminator().length());
    assertEquals(49, transfer.discriminator().data()[0] & 0xFF);
    assertNotNull(transfer.transferArgs(), "the argument would be garbage read from offset eight");
    // Exactly, not at-most: `<=` passes on a decoder that silently drops a trailing field.
    assertEquals(TRANSFER.length, transfer.l(),
        "the decoder must account for every byte the instruction carried");
  }

  /// A longer instruction, so the fix is not an artifact of one payload happening to fit.
  @Test
  void aRealCreateMetadataAccountDecodes() {
    assertEquals(102, CREATE_METADATA_ACCOUNT_V3.length);

    final var create = TokenMetadataProgram.CreateMetadataAccountV3IxData.read(CREATE_METADATA_ACCOUNT_V3, 0);
    assertNotNull(create);
    assertEquals(33, create.discriminator().data()[0] & 0xFF);
    assertEquals(CREATE_METADATA_ACCOUNT_V3.length, create.l(),
        "the decoder must account for every byte the instruction carried");
  }

  /// Re-serializing reproduces the captured bytes, which is the round trip a builder has to make.
  @Test
  void aRealInstructionReserializesToTheCapturedBytes() {
    final var transfer = TokenMetadataProgram.TransferIxData.read(TRANSFER, 0);
    final byte[] out = new byte[transfer.l()];
    final int written = transfer.write(out, 0);

    assertEquals(TRANSFER.length, written, "a short write would truncate the instruction");
    // The whole fixture, not a prefix of it: copyOf(fixture, out.length) would accept any decoder
    // that dropped a suffix, which is the failure a round trip exists to catch.
    assertArrayEquals(TRANSFER, out, "a rebuilt instruction must be byte-identical to the chain's");
  }

  /// The third fixture decodes too, and to its exact length. It was previously read only for its
  /// first byte, which left the longest of the three untested as a payload.
  @Test
  void aRealUpdateMetadataAccountDecodes() {
    assertEquals(37, UPDATE_METADATA_ACCOUNT_V2.length);

    final var update = TokenMetadataProgram.UpdateMetadataAccountV2IxData.read(UPDATE_METADATA_ACCOUNT_V2, 0);
    assertNotNull(update);
    assertEquals(15, update.discriminator().data()[0] & 0xFF);
    assertEquals(UPDATE_METADATA_ACCOUNT_V2.length, update.l());

    final byte[] out = new byte[update.l()];
    assertEquals(UPDATE_METADATA_ACCOUNT_V2.length, update.write(out, 0));
    assertArrayEquals(UPDATE_METADATA_ACCOUNT_V2, out);
  }

  /// The regression, stated against real data: the discriminator the client emits must be a
  /// prefix of what the chain carried. Under the synthesized eight-byte hash this was false for
  /// every instruction in the program — the first byte alone was wrong.
  @Test
  void everyCapturedInstructionStartsWithItsGeneratedDiscriminator() {
    record Captured(String name, byte[] data, software.sava.core.programs.Discriminator discriminator) {
    }
    for (final var captured : new Captured[]{
        new Captured("Transfer", TRANSFER, TokenMetadataProgram.TRANSFER_DISCRIMINATOR),
        new Captured("CreateMetadataAccountV3", CREATE_METADATA_ACCOUNT_V3,
            TokenMetadataProgram.CREATE_METADATA_ACCOUNT_V_3_DISCRIMINATOR),
        new Captured("UpdateMetadataAccountV2", UPDATE_METADATA_ACCOUNT_V2,
            TokenMetadataProgram.UPDATE_METADATA_ACCOUNT_V_2_DISCRIMINATOR)}) {
      final byte[] expected = captured.discriminator().data();
      assertTrue(captured.data().length >= expected.length,
          captured.name() + ": the discriminator is longer than the whole instruction");
      for (int i = 0; i < expected.length; i++) {
        assertEquals(expected[i], captured.data()[i],
            captured.name() + ": byte " + i + " does not match what the chain carried");
      }
    }
  }
}

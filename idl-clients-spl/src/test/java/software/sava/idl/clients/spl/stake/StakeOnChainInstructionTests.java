package software.sava.idl.clients.spl.stake;

import org.junit.jupiter.api.Test;
import software.sava.idl.clients.spl.stake.gen.StakeProgram;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

/// Real mainnet instruction data, pinning the discriminant **width**.
///
/// `StakeInstruction` is serialized with bincode — every builder in `solana-program/stake`'s
/// `interface/src/instruction.rs` calls `Instruction::new_with_bincode`, and the enum derives
/// `serde_derive::Serialize`. Bincode tags enum variants with a **4-byte little-endian** integer,
/// which is what the bytes below carry.
///
/// **The `idl.json` at that repository's root declares `u8`, and that is not a bug in it.** It is
/// an *intermediate* artifact: `codama.mjs` there applies a `before` pipeline — rewriting the
/// discriminator to `u32`, injecting the `epoch` and `unixTimestamp` aliases, renaming four
/// `…Args` types to `…Params` — and every client upstream publishes is generated after those
/// transforms run. Consuming the raw file skips the half of the process that makes it correct.
/// Generating from it on a branch (2026-08-14) produced `toDiscriminator(0)` and `BYTES` of 113
/// instead of 116, shifting every argument offset down by three.
///
/// So the IDL this repository generates from is that pipeline's own output, written by
/// `tools/stake-idl.mjs`. It was a pinned copy paired with 393 lines of hand-maintained
/// `definedTypes` in `main_net_programs.json` until 2026-08-14; both are gone. What did not change
/// is why this test exists. The pipeline, the builders generated from it and the readers generated
/// beside them all descend from one IDL, so none of them can testify that the IDL matches the
/// program that is deployed. These bytes can, and nothing else here does. The Stake program is
/// immutable — `Authority: none`, last deployed at slot 427248000 — so they cannot go out of date.
///
/// [StakeReferenceEncodingTests] holds all seventeen instructions, this one included, against
/// upstream's generated JavaScript client. That is a second *encoder*, not a second source: it
/// agrees with the chain only by way of the fixture below.
final class StakeOnChainInstructionTests {

  /// Captured with `getTransaction` from mainnet: transaction
  /// `5Jbvv52bNfVxE7HVguEqQtYV9qsnouiYjFpg1v7staZ4d2DE7b23SRT7wfFhZyvo5GuGjpHcbTDbTvGkf4Bbfj5k`,
  /// slot 439292616. The file records that instruction's `data` and nothing else.
  private static byte[] instruction(final String name) {
    try (var in = StakeOnChainInstructionTests.class.getResourceAsStream("/stake/ix-" + name + ".base64")) {
      assertNotNull(in, "fixture /stake/ix-" + name + ".base64 is missing");
      return Base64.getDecoder().decode(new String(in.readAllBytes(), StandardCharsets.UTF_8).trim());
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  /// The whole claim in one assertion: a real `Initialize` is 116 bytes, and the generated layout
  /// says so. Under the upstream `u8` IDL this constant is 113, and no real instruction is that
  /// length.
  @Test
  void aRealInitializeMatchesTheGeneratedLayout() {
    final byte[] data = instruction("initialize-0");

    assertEquals(116, data.length,
        "4-byte discriminant + 64-byte Authorized + 48-byte Lockup");
    assertEquals(StakeProgram.InitializeIxData.BYTES, data.length,
        "the generated layout and the chain must agree on the size of this instruction");
  }

  /// The discriminant occupies four bytes, not one.
  ///
  /// `Initialize` is variant 0, so all four read as zero — which is why the length assertion above
  /// carries the weight and this one pins the offset. A one-byte reading would take `data[0]` as
  /// the tag and then start `Authorized` at index 1, three bytes early, mid-pubkey.
  @Test
  void theDiscriminantIsFourLittleEndianBytes() {
    final byte[] data = instruction("initialize-0");

    assertEquals(0, StakeProgram.InitializeIxData.DISCRIMINATOR_OFFSET);
    assertEquals(4, StakeProgram.InitializeIxData.ARG_0_OFFSET,
        "the first argument begins after a 4-byte tag; upstream's u8 IDL puts it at 1");
    assertEquals(68, StakeProgram.InitializeIxData.ARG_1_OFFSET);

    for (int i = 0; i < 4; ++i) {
      assertEquals(0, data[i], "byte " + i + " of the Initialize discriminant");
    }
  }

  /// Decoding the captured bytes has to produce variant 0 and consume the whole instruction.
  @Test
  void theCapturedInstructionDecodesAsInitialize() {
    final byte[] data = instruction("initialize-0");

    final var decoded = StakeProgram.InitializeIxData.read(data, 0);

    assertNotNull(decoded);
    assertEquals(0L, decoded.discriminator());
    assertNotNull(decoded.arg0(), "Authorized");
    assertNotNull(decoded.arg1(), "Lockup");
    assertEquals(data.length, decoded.l(),
        "the decoded instruction accounts for every captured byte");
  }
}

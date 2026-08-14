package software.sava.idl.clients.spl.stake;

import org.junit.jupiter.api.Test;
import software.sava.idl.clients.spl.stake.gen.SolanaStakeInterfaceProgram;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

/// Real mainnet instruction data, pinning the discriminant **width** against an upstream IDL that
/// gets it wrong.
///
/// `StakeInstruction` is serialized with bincode — every builder in `solana-program/stake`'s
/// `interface/src/instruction.rs` calls `Instruction::new_with_bincode`, and the enum derives
/// `serde_derive::Serialize`. Bincode tags enum variants with a **4-byte little-endian** integer.
///
/// The `idl.json` that repository publishes declares the discriminator as `u8`, one byte, almost
/// certainly because the `CodamaInstructions` derive emits Borsh's convention. Generating from that
/// IDL produces a client that writes a one-byte tag and shifts every argument offset down by three,
/// so the deployed program rejects everything it builds. Tried on a branch 2026-08-14: the
/// generated `INITIALIZE_DISCRIMINATOR` became `toDiscriminator(0)` and `BYTES` fell from 116 to
/// 113.
///
/// This repository therefore pins its own copy of the IDL rather than tracking upstream, and this
/// test is why that pin exists. The Stake program is immutable — `Authority: none`, last deployed
/// at slot 427248000 — so the deployed encoding cannot change and upstream cannot become right
/// about it later.
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
    assertEquals(SolanaStakeInterfaceProgram.InitializeIxData.BYTES, data.length,
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

    assertEquals(0, SolanaStakeInterfaceProgram.InitializeIxData.DISCRIMINATOR_OFFSET);
    assertEquals(4, SolanaStakeInterfaceProgram.InitializeIxData.ARG_0_OFFSET,
        "the first argument begins after a 4-byte tag; upstream's u8 IDL puts it at 1");
    assertEquals(68, SolanaStakeInterfaceProgram.InitializeIxData.ARG_1_OFFSET);

    for (int i = 0; i < 4; ++i) {
      assertEquals(0, data[i], "byte " + i + " of the Initialize discriminant");
    }
  }

  /// Decoding the captured bytes has to produce variant 0 and consume the whole instruction.
  @Test
  void theCapturedInstructionDecodesAsInitialize() {
    final byte[] data = instruction("initialize-0");

    final var decoded = SolanaStakeInterfaceProgram.InitializeIxData.read(data, 0);

    assertNotNull(decoded);
    assertEquals(0L, decoded.discriminator());
    assertNotNull(decoded.arg0(), "Authorized");
    assertNotNull(decoded.arg1(), "Lockup");
    assertEquals(data.length, decoded.l(),
        "the decoded instruction accounts for every captured byte");
  }
}

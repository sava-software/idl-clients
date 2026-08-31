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
/// **Upstream's `idl.json` used to declare `u8`, and that was not a bug in it.** Until 2026-08-25
/// that file was an *intermediate* artifact: `codama.mjs` applied a `before` pipeline — rewriting
/// the discriminator to `u32`, injecting the `epoch` and `unixTimestamp` aliases, renaming four
/// `…Args` types to `…Params` — and every client upstream published was generated after those
/// transforms ran. Consuming the raw file skipped the half of the process that made it correct.
/// Generating from it on a branch (2026-08-14) produced `toDiscriminator(0)` and `BYTES` of 113
/// instead of 116, shifting every argument offset down by three.
///
/// Upstream's #501 ended that split: the raw tree moved to `interface-idl.json` and `idl.json` now
/// carries the visited output, so this repository generates straight from it. Before that the same
/// output was derived locally by `tools/stake-idl.mjs` and committed, and before 2026-08-14 it was
/// a pinned copy paired with 393 lines of hand-maintained `definedTypes` in
/// `main_net_programs.json`. What did not change across any of it is why this test exists. The IDL,
/// the builders generated from it and the readers generated beside them all descend from one
/// source, so none of them can testify that it matches the program that is deployed. These bytes
/// can, for the instruction data. Only for that: the fixture below records one instruction's
/// `data` and nothing else, so it is silent on the account lists declared beside it in the same
/// IDL — which is how `solana-program/stake#520` moved ten of them on 2026-08-31 with all three
/// assertions here green and this file unedited.
///
/// They do not expire, and the reason is narrower than the one this javadoc used to give. The
/// program is **not** immutable: `Option::None` as upgrade authority forecloses only
/// transaction-driven upgrades, and the runtime replaces the ELF at feature activation. That is
/// how mainnet reached `program@v5.0.0` — the Agave gate `upgrade_bpf_stake_program_to_v5`
/// (`STk5Xj8hdAx3sTzmtJ3QysKkq6X2A3yj73JtxttiRyk`) activated at slot 427248000, byte-identical to
/// `ProgramData.last_deploy_slot`, with no transaction to read it from — and
/// `upgrade_bpf_stake_program_to_v5_1` is staged behind it, unactivated on mainnet as of
/// 2026-08-31. What keeps these bytes good is that no gate so far has moved a discriminant width
/// or an argument layout; v5's own change was to make sysvar *accounts* optional. A gate
/// activating on mainnet is the trigger to re-capture.
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

package software.sava.idl.clients.meteora;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/// Pins the hand-written Meteora DLMM PDA-derivation helpers against a real
/// mainnet LbPair. A wrong seed encoding (mint ordering, u16 little-endian bin
/// step / base factor) would derive an address that does not exist on-chain.
///
/// Anchor account: LbPair `112JU91seUnMLBmXxwqiyXbaZhbFRxGJprvh7pYWT98`
/// (owner `LBUZKhRxPF3XUpBCjp4YzTKgLccjZhTSDM9YuVaPwxo`, 904 bytes). The LbPair's
/// OWN address equals `lbPairPDA(tokenXMint, tokenYMint, binStep, baseFactor)`,
/// and its stored `reserveX` / `reserveY` are `reservePDA(lbPair, mint)` outputs.
/// All expected values are read directly from that on-chain account.
final class MeteoraPDATests {

  private static final PublicKey DLMM_PROGRAM =
      PublicKey.fromBase58Encoded("LBUZKhRxPF3XUpBCjp4YzTKgLccjZhTSDM9YuVaPwxo");

  private static final PublicKey LB_PAIR =
      PublicKey.fromBase58Encoded("112JU91seUnMLBmXxwqiyXbaZhbFRxGJprvh7pYWT98");

  // Fields read out of the LbPair account (the program's own PDA inputs / outputs).
  private static final PublicKey TOKEN_X_MINT =
      PublicKey.fromBase58Encoded("2CZNBcjjxE6wYfHKdnRAemynnoU9v24vW83W3PUZnPrj");
  private static final PublicKey TOKEN_Y_MINT =
      PublicKey.fromBase58Encoded("74SBV4zDXxTRgv1pEMoECskKBkZHc2yGPnc7GYVepump");
  private static final int BIN_STEP = 80;
  private static final int BASE_FACTOR = 62500;

  private static final PublicKey RESERVE_X =
      PublicKey.fromBase58Encoded("4NJqVStmuPc7ZhEn6ELPNmUry95Nikegu9ejniMGccyJ");
  private static final PublicKey RESERVE_Y =
      PublicKey.fromBase58Encoded("7KFGP56m9DuHnceK2ajaCrFYdrM4z533yYCoAuG4TYg1");

  @Test
  void lbPairPDA() {
    assertEquals(
        LB_PAIR.toBase58(),
        MeteoraPDAs.lbPairPDA(TOKEN_X_MINT, TOKEN_Y_MINT, BIN_STEP, BASE_FACTOR, DLMM_PROGRAM).publicKey().toBase58()
    );
    // Seed ordering is min/max by key, so passing the mints in the opposite
    // order must derive the same address.
    assertEquals(
        LB_PAIR.toBase58(),
        MeteoraPDAs.lbPairPDA(TOKEN_Y_MINT, TOKEN_X_MINT, BIN_STEP, BASE_FACTOR, DLMM_PROGRAM).publicKey().toBase58()
    );
  }

  @Test
  void reservePDA() {
    assertEquals(
        RESERVE_X.toBase58(),
        MeteoraPDAs.reservePDA(LB_PAIR, TOKEN_X_MINT, DLMM_PROGRAM).publicKey().toBase58()
    );
    assertEquals(
        RESERVE_Y.toBase58(),
        MeteoraPDAs.reservePDA(LB_PAIR, TOKEN_Y_MINT, DLMM_PROGRAM).publicKey().toBase58()
    );
  }

  // The pair-flavor helpers below have no real on-chain anchor fetched yet, so
  // they are tested by property rather than pinned value (see AGENTS.md and
  // the KaminoAccounts precedent): mint order must not matter (the seeds are
  // min/max sorted, matching the verified `lbPairPDA` above), every input must
  // participate, and the flavors must separate from each other. The u16 seed
  // encodes little-endian, so an index above 0xFF pins the second byte.

  @Test
  void customizablePermissionlessLbPairIsMintOrderInvariant() {
    final var pair = MeteoraPDAs.customizablePermissionlessLbPairPDA(TOKEN_X_MINT, TOKEN_Y_MINT, DLMM_PROGRAM);
    assertEquals(
        pair.publicKey(),
        MeteoraPDAs.customizablePermissionlessLbPairPDA(TOKEN_Y_MINT, TOKEN_X_MINT, DLMM_PROGRAM).publicKey()
    );
    // both mints participate
    assertNotEquals(
        pair.publicKey(),
        MeteoraPDAs.customizablePermissionlessLbPairPDA(TOKEN_X_MINT, LB_PAIR, DLMM_PROGRAM).publicKey()
    );
    // the ILM base seed separates it from a preset-parameter pair of the same mints
    assertNotEquals(
        pair.publicKey(),
        MeteoraPDAs.lbPairWithPresetParamPDA(LB_PAIR, TOKEN_X_MINT, TOKEN_Y_MINT, DLMM_PROGRAM).publicKey()
    );
  }

  @Test
  void permissionLbPairIsMintOrderInvariantAndBindsEverySeed() {
    final var base = LB_PAIR; // any key distinct from the mints
    final var pair = MeteoraPDAs.permissionLbPairPDA(base, TOKEN_X_MINT, TOKEN_Y_MINT, BIN_STEP, DLMM_PROGRAM);
    assertEquals(
        pair.publicKey(),
        MeteoraPDAs.permissionLbPairPDA(base, TOKEN_Y_MINT, TOKEN_X_MINT, BIN_STEP, DLMM_PROGRAM).publicKey()
    );
    // the base key, each mint, and the bin step all participate
    assertNotEquals(
        pair.publicKey(),
        MeteoraPDAs.permissionLbPairPDA(TOKEN_X_MINT, TOKEN_X_MINT, TOKEN_Y_MINT, BIN_STEP, DLMM_PROGRAM).publicKey()
    );
    assertNotEquals(
        pair.publicKey(),
        MeteoraPDAs.permissionLbPairPDA(base, TOKEN_X_MINT, RESERVE_X, BIN_STEP, DLMM_PROGRAM).publicKey()
    );
    assertNotEquals(
        pair.publicKey(),
        MeteoraPDAs.permissionLbPairPDA(base, TOKEN_X_MINT, TOKEN_Y_MINT, BIN_STEP + 1, DLMM_PROGRAM).publicKey(),
        "a dropped bin-step encoding collapses every bin step onto zero"
    );
    // the little-endian u16's high byte participates too
    assertNotEquals(
        MeteoraPDAs.permissionLbPairPDA(base, TOKEN_X_MINT, TOKEN_Y_MINT, 0x0100, DLMM_PROGRAM).publicKey(),
        MeteoraPDAs.permissionLbPairPDA(base, TOKEN_X_MINT, TOKEN_Y_MINT, 0x0001, DLMM_PROGRAM).publicKey()
    );
  }

  @Test
  void presetParameterPDABindsTheIndex() {
    final var preset = MeteoraPDAs.presetParameterPDA(3, DLMM_PROGRAM);
    assertEquals(preset.publicKey(), MeteoraPDAs.presetParameterPDA(3, DLMM_PROGRAM).publicKey());
    assertNotEquals(preset.publicKey(), MeteoraPDAs.presetParameterPDA(4, DLMM_PROGRAM).publicKey());
    assertNotEquals(preset.publicKey(), MeteoraPDAs.presetParameterPDA(0, DLMM_PROGRAM).publicKey(),
        "a dropped index encoding collapses every index onto zero");
    assertNotEquals(
        MeteoraPDAs.presetParameterPDA(0x0100, DLMM_PROGRAM).publicKey(),
        MeteoraPDAs.presetParameterPDA(0x0001, DLMM_PROGRAM).publicKey(),
        "the u16 encodes little-endian, so the high byte must land in the second slot"
    );
  }

  @Test
  void lbPairWithPresetParamIsMintOrderInvariantAndBindsThePreset() {
    final var preset = MeteoraPDAs.presetParameterPDA(3, DLMM_PROGRAM).publicKey();
    final var pair = MeteoraPDAs.lbPairWithPresetParamPDA(preset, TOKEN_X_MINT, TOKEN_Y_MINT, DLMM_PROGRAM);
    assertEquals(
        pair.publicKey(),
        MeteoraPDAs.lbPairWithPresetParamPDA(preset, TOKEN_Y_MINT, TOKEN_X_MINT, DLMM_PROGRAM).publicKey()
    );
    assertNotEquals(
        pair.publicKey(),
        MeteoraPDAs.lbPairWithPresetParamPDA(LB_PAIR, TOKEN_X_MINT, TOKEN_Y_MINT, DLMM_PROGRAM).publicKey()
    );
    assertNotEquals(
        pair.publicKey(),
        MeteoraPDAs.lbPairWithPresetParamPDA(preset, TOKEN_X_MINT, RESERVE_X, DLMM_PROGRAM).publicKey()
    );
  }
}

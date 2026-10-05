package software.sava.idl.clients.meteora;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/// Pins the hand-written Meteora DLMM PDA-derivation helpers against real
/// mainnet accounts. A wrong seed encoding (mint ordering, u16 little-endian bin
/// step / base factor) would derive an address that does not exist on-chain.
///
/// Anchor account: LbPair `112JU91seUnMLBmXxwqiyXbaZhbFRxGJprvh7pYWT98`
/// (owner `LBUZKhRxPF3XUpBCjp4YzTKgLccjZhTSDM9YuVaPwxo`, 904 bytes). The LbPair's
/// OWN address equals `lbPairPDA(tokenXMint, tokenYMint, binStep, baseFactor)`,
/// and its stored `reserveX` / `reserveY` are `reservePDA(lbPair, mint)` outputs.
/// Its expected values are read directly from that on-chain account.
///
/// Its two mints first differ at byte 0, 0x11 against 0x5a, both below 0x80, so it
/// cannot tell an unsigned mint ordering from a signed one. Four wSOL/USDC pools,
/// one per pair flavour, pin that: 0x06 against 0xc6. The program orders mints as
/// Rust orders `Pubkey`, by unsigned bytes (`min`/`max` in dlmm-sdk's
/// `commons/src/pda.rs`, `Buffer.compare` in its TypeScript client), and each of
/// these pools' own address and stored bump is the program's output for that order.
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

  // The wSOL/USDC pools, read from mainnet on 2026-10-05: token X is wSOL and token Y
  // is USDC in each, and each account is owned by the DLMM program.
  private static final PublicKey WSOL =
      PublicKey.fromBase58Encoded("So11111111111111111111111111111111111111112");
  private static final PublicKey USDC =
      PublicKey.fromBase58Encoded("EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v");
  // Permissionless with a base-factor seed: bin step 10, base factor 10000, bump 255.
  private static final PublicKey WSOL_USDC_PAIR =
      PublicKey.fromBase58Encoded("BGm1tav58oGcsQJehL9WXBFXF7D27vZsKefj4xJKD5Y");
  // Preset-parameter pair: its stored base key is the preset parameter account at
  // index 81, and its bump is 254.
  private static final PublicKey PRESET_PARAMETER_81 =
      PublicKey.fromBase58Encoded("BGvGPmPWstVPjCv4mvn91AMkAucFPyjucEpuRqrLGnVW");
  private static final PublicKey WSOL_USDC_PRESET_PAIR =
      PublicKey.fromBase58Encoded("3D9MyL5iD9uqbe2FYvivHsywHWr3nJR1krEcEWXUCGzr");
  // Customizable permissionless pair: bump 255.
  private static final PublicKey WSOL_USDC_CUSTOMIZABLE_PAIR =
      PublicKey.fromBase58Encoded("GNY3YbGqdhZv8R3kD2NRJQ4NLD6tb3PPthJ2mpUR89Lc");
  // Permissioned pair: its stored base key, bin step 8, bump 255.
  private static final PublicKey PERMISSION_BASE_KEY =
      PublicKey.fromBase58Encoded("831hv2NxS7K9k5D4DgFfwvLU4u6TMZ4vaVU5rtHojBC6");
  private static final PublicKey WSOL_USDC_PERMISSION_PAIR =
      PublicKey.fromBase58Encoded("3msVd34R5KxonDzyNSV5nT19UtUeJ2RF1NaQhvVPNLxL");

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

  @Test
  void lbPairPDAOrdersMintsByUnsignedBytes() {
    final var pair = MeteoraPDAs.lbPairPDA(WSOL, USDC, 10, 10000, DLMM_PROGRAM);
    assertEquals(WSOL_USDC_PAIR, pair.publicKey());
    assertEquals(255, pair.nonce());
    assertEquals(WSOL_USDC_PAIR, MeteoraPDAs.lbPairPDA(USDC, WSOL, 10, 10000, DLMM_PROGRAM).publicKey());
  }

  @Test
  void presetParameterPDAMatchesTheAccountAPresetPairStores() {
    assertEquals(PRESET_PARAMETER_81, MeteoraPDAs.presetParameterPDA(81, DLMM_PROGRAM).publicKey());
  }

  @Test
  void lbPairWithPresetParamPDAOrdersMintsByUnsignedBytes() {
    final var pair = MeteoraPDAs.lbPairWithPresetParamPDA(PRESET_PARAMETER_81, WSOL, USDC, DLMM_PROGRAM);
    assertEquals(WSOL_USDC_PRESET_PAIR, pair.publicKey());
    assertEquals(254, pair.nonce());
    assertEquals(
        WSOL_USDC_PRESET_PAIR,
        MeteoraPDAs.lbPairWithPresetParamPDA(PRESET_PARAMETER_81, USDC, WSOL, DLMM_PROGRAM).publicKey()
    );
  }

  @Test
  void customizablePermissionlessLbPairPDAOrdersMintsByUnsignedBytes() {
    final var pair = MeteoraPDAs.customizablePermissionlessLbPairPDA(WSOL, USDC, DLMM_PROGRAM);
    assertEquals(WSOL_USDC_CUSTOMIZABLE_PAIR, pair.publicKey());
    assertEquals(255, pair.nonce());
    assertEquals(
        WSOL_USDC_CUSTOMIZABLE_PAIR,
        MeteoraPDAs.customizablePermissionlessLbPairPDA(USDC, WSOL, DLMM_PROGRAM).publicKey()
    );
  }

  @Test
  void permissionLbPairPDAOrdersMintsByUnsignedBytes() {
    final var pair = MeteoraPDAs.permissionLbPairPDA(PERMISSION_BASE_KEY, WSOL, USDC, 8, DLMM_PROGRAM);
    assertEquals(WSOL_USDC_PERMISSION_PAIR, pair.publicKey());
    assertEquals(255, pair.nonce());
    assertEquals(
        WSOL_USDC_PERMISSION_PAIR,
        MeteoraPDAs.permissionLbPairPDA(PERMISSION_BASE_KEY, USDC, WSOL, 8, DLMM_PROGRAM).publicKey()
    );
  }

  // The pinned pools anchor each pair flavour to chain. The properties below cover
  // what one address cannot: mint order must not matter (the seeds are min/max
  // sorted), every input must participate, and the flavors must separate from each
  // other. The u16 seed encodes little-endian, so an index above 0xFF pins the
  // second byte.

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

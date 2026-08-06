package software.sava.idl.clients.kamino.lend;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.kamino.KaminoAccounts;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/// Pins the hand-written Kamino KLend PDA-derivation helpers against a real
/// mainnet reserve. A wrong seed encoding would derive addresses that do not
/// exist on-chain, so every expected value below is authoritative ground truth
/// read directly from the on-chain accounts the program itself derived.
///
/// Two real reserves, because klend has used two different vault derivations and both
/// are live. `D6q6wuQSrifJKZYpR1M8R4YawnLDtDsMmWM1NbBmgJ59` predates the change and its
/// vaults are seeded on `[market, mint]`; `7gHxajRCcU5sb9rQrpYDvkugSqgGsi9EG947pW7xJueT`
/// postdates it and is seeded on `[reserve]`. A reserve keeps whatever it was created
/// with, so no single derivation reproduces both — which is why the vaults of an
/// existing reserve are read off its account and only `initReserve` derives them.
///
/// The market authority is anchored via the on-chain SPL token authority of the
/// liquidity supply vault.
final class KaminoPDATests {

  private static final PublicKey KLEND_PROGRAM = KaminoAccounts.MAIN_NET.kLendProgram();

  // Fields read out of the reserve account (the program's own PDA inputs / outputs).
  private static final PublicKey MARKET =
      PublicKey.fromBase58Encoded("7u3HeHxYDLhnCoErrtycNokbQYbWGzLs6JSDqGAv5PfF");
  private static final PublicKey LIQUIDITY_MINT =
      PublicKey.fromBase58Encoded("EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v"); // USDC

  // Expected on-chain PDA outputs.
  private static final PublicKey MARKET_AUTHORITY =
      PublicKey.fromBase58Encoded("9DrvZvyWh1HuAoZxvYWMvkf2XCzryCpGgHqrMjyDWpmo");
  private static final PublicKey LIQ_SUPPLY_VAULT =
      PublicKey.fromBase58Encoded("Bgq7trRgVMeq33yt235zM2onQ4bRDBsY5EWiTetF4qw6");
  private static final PublicKey FEE_VAULT =
      PublicKey.fromBase58Encoded("BbDUrk1bVtSixgQsPLBJFZEF7mwGstnD5joA1WzYvYFX");
  private static final PublicKey COLLATERAL_MINT =
      PublicKey.fromBase58Encoded("B8V6WVjPxW1UGwVDfxH2d2r8SyT4cqn7dQRK6XneVa7D");
  private static final PublicKey COLLATERAL_SUPPLY_VAULT =
      PublicKey.fromBase58Encoded("3DzjXRfxRm6iejfyyMynR4tScddaanrePJ1NJU2XnPPL");

  // A reserve created under the current scheme, whose vaults seed on the reserve itself.
  private static final PublicKey NEW_RESERVE =
      PublicKey.fromBase58Encoded("7gHxajRCcU5sb9rQrpYDvkugSqgGsi9EG947pW7xJueT");
  private static final PublicKey NEW_LIQ_SUPPLY_VAULT =
      PublicKey.fromBase58Encoded("13P6HSRFN74Qu8bbqvgYKTrKhKs2tDnshG8QRvwuV7X1");
  private static final PublicKey NEW_FEE_VAULT =
      PublicKey.fromBase58Encoded("C3WD5UU8VMbVpn2GHtR7Zzi9eqmt3AEFApUna69TyPhE");
  private static final PublicKey NEW_COLLATERAL_MINT =
      PublicKey.fromBase58Encoded("ARkzvRzvLF6Ag9TgbUNMBEVV3L779DrH4ghnxW7a4ZXa");
  private static final PublicKey NEW_COLLATERAL_SUPPLY_VAULT =
      PublicKey.fromBase58Encoded("5euA13FuSa8aapcSzhdPQYtWPcaRuAkhZ4Pa9L8Aakz2");

  // The older reserve the market/mint constants above come from.
  private static final PublicKey OLD_RESERVE =
      PublicKey.fromBase58Encoded("D6q6wuQSrifJKZYpR1M8R4YawnLDtDsMmWM1NbBmgJ59");

  // SPL token program; only stored on the record, does not affect derivation.
  private static final PublicKey TOKEN_PROGRAM =
      PublicKey.fromBase58Encoded("TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA");

  @Test
  void lendingMarketAuthPda() {
    assertEquals(
        MARKET_AUTHORITY.toBase58(),
        KaminoAccounts.lendingMarketAuthPda(MARKET, KLEND_PROGRAM).publicKey().toBase58()
    );
  }

  /// The derivation the deployed program uses today, checked against a reserve created
  /// under it: every vault the program stored is reproduced from the reserve address
  /// alone.
  @Test
  void reserveVaultPdasSeedOnTheReserve() {
    assertEquals(
        NEW_LIQ_SUPPLY_VAULT.toBase58(),
        KaminoAccounts.reserveLiqSupplyPda(NEW_RESERVE, KLEND_PROGRAM).publicKey().toBase58()
    );
    assertEquals(
        NEW_FEE_VAULT.toBase58(),
        KaminoAccounts.reserveFeeVaultPda(NEW_RESERVE, KLEND_PROGRAM).publicKey().toBase58()
    );
    assertEquals(
        NEW_COLLATERAL_MINT.toBase58(),
        KaminoAccounts.reserveCollateralMintPda(NEW_RESERVE, KLEND_PROGRAM).publicKey().toBase58()
    );
    assertEquals(
        NEW_COLLATERAL_SUPPLY_VAULT.toBase58(),
        KaminoAccounts.reserveCollateralSupplyPda(NEW_RESERVE, KLEND_PROGRAM).publicKey().toBase58()
    );
  }

  /// And it does not reproduce a reserve created under the older scheme — the reason
  /// these are read from the account rather than derived.
  @Test
  void theSameDerivationDoesNotReproduceAnOlderReserve() {
    assertNotEquals(
        LIQ_SUPPLY_VAULT.toBase58(),
        KaminoAccounts.reserveLiqSupplyPda(OLD_RESERVE, KLEND_PROGRAM).publicKey().toBase58()
    );
  }

  @Test
  void composedMarketPdas() {
    final var marketPDAs = KaminoMarketPDAs.createPDAs(KLEND_PROGRAM, MARKET);
    assertEquals(MARKET.toBase58(), marketPDAs.market().toBase58());
    assertEquals(MARKET_AUTHORITY.toBase58(), marketPDAs.authority().toBase58());
  }

  @Test
  void composedReservePdas() {
    final var marketPDAs = KaminoMarketPDAs.createPDAs(KLEND_PROGRAM, MARKET);
    final var reservePDAs = KaminoReservePDAs.createPDAs(
        marketPDAs, LIQUIDITY_MINT, TOKEN_PROGRAM,
        LIQ_SUPPLY_VAULT, COLLATERAL_MINT, COLLATERAL_SUPPLY_VAULT, FEE_VAULT);

    assertEquals(MARKET.toBase58(), reservePDAs.market().toBase58());
    assertEquals(MARKET_AUTHORITY.toBase58(), reservePDAs.marketAuthority().toBase58());
    assertEquals(LIQUIDITY_MINT.toBase58(), reservePDAs.mint().toBase58());
    assertEquals(LIQ_SUPPLY_VAULT.toBase58(), reservePDAs.liquiditySupplyVault().toBase58());
    assertEquals(FEE_VAULT.toBase58(), reservePDAs.feeVault().toBase58());
    assertEquals(COLLATERAL_MINT.toBase58(), reservePDAs.collateralMint().toBase58());
    assertEquals(COLLATERAL_SUPPLY_VAULT.toBase58(), reservePDAs.collateralSupplyVault().toBase58());
  }

  /// The primary path, end to end against the real account: decode a live mainnet
  /// reserve and confirm every address the record exposes is the one the program stored,
  /// and that the derivation agrees with it for a reserve of this vintage.
  ///
  /// This is what makes the reserve-reading form trustworthy for the reserves the
  /// derivation cannot reach — the mechanism is the same either way, and here both are
  /// checkable at once.
  @Test
  void reservePdasReadFromARealAccount() throws Exception {
    final byte[] data;
    try (var in = KaminoPDATests.class.getResourceAsStream(
        "/kamino/reserve-7gHxajRCcU5sb9rQrpYDvkugSqgGsi9EG947pW7xJueT.base64")) {
      data = java.util.Base64.getDecoder().decode(new String(in.readAllBytes()).trim());
    }
    final var reserve = software.sava.idl.clients.kamino.lend.gen.types.Reserve.read(NEW_RESERVE, data);
    final var pdas = KaminoReservePDAs.createPDAs(KLEND_PROGRAM, reserve);

    // read off the account
    assertEquals(NEW_LIQ_SUPPLY_VAULT, pdas.liquiditySupplyVault());
    assertEquals(NEW_FEE_VAULT, pdas.feeVault());
    assertEquals(NEW_COLLATERAL_MINT, pdas.collateralMint());
    assertEquals(NEW_COLLATERAL_SUPPLY_VAULT, pdas.collateralSupplyVault());
    assertEquals("EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v", pdas.mint().toBase58());
    assertEquals("4iRHKGsTq3e4uut6e4PfyV9AEbNuXaMu9JaSP378p9qy", pdas.market().toBase58());

    // and, for this vintage, the derivation reproduces the same four
    final var derived = KaminoReservePDAs.createPDAsForNewReserve(
        KLEND_PROGRAM,
        KaminoMarketPDAs.createPDAs(KLEND_PROGRAM, pdas.market()),
        NEW_RESERVE,
        pdas.mint(),
        pdas.tokenProgram());
    assertEquals(pdas.liquiditySupplyVault(), derived.liquiditySupplyVault());
    assertEquals(pdas.feeVault(), derived.feeVault());
    assertEquals(pdas.collateralMint(), derived.collateralMint());
    assertEquals(pdas.collateralSupplyVault(), derived.collateralSupplyVault());

    // the accounts helper resolves the same record, by either route
    final var viaAccounts = KaminoAccounts.MAIN_NET.createReservePDAs(reserve);
    assertEquals(pdas.liquiditySupplyVault(), viaAccounts.liquiditySupplyVault());
    assertEquals(pdas.collateralMint(), viaAccounts.collateralMint());

    final var derivedViaAccounts = KaminoAccounts.MAIN_NET.createPDAsForNewReserve(
        KaminoMarketPDAs.createPDAs(KLEND_PROGRAM, pdas.market()),
        NEW_RESERVE,
        pdas.mint(),
        pdas.tokenProgram());
    assertEquals(derived.liquiditySupplyVault(), derivedViaAccounts.liquiditySupplyVault());
    assertEquals(derived.feeVault(), derivedViaAccounts.feeVault());
  }

}

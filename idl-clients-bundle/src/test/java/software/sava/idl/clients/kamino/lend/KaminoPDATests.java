package software.sava.idl.clients.kamino.lend;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.kamino.KaminoAccounts;

import static org.junit.jupiter.api.Assertions.assertEquals;

/// Pins the hand-written Kamino KLend PDA-derivation helpers against a real
/// mainnet reserve. A wrong seed encoding would derive addresses that do not
/// exist on-chain, so every expected value below is authoritative ground truth
/// read directly from the on-chain accounts the program itself derived.
///
/// Anchor account: reserve `D6q6wuQSrifJKZYpR1M8R4YawnLDtDsMmWM1NbBmgJ59`
/// (owner `KLend2g3cP87fffoy8q1mQqGKjrxjC8boSyAYavgmjD`, 8624 bytes). Its stored
/// `lendingMarket`, `liquidity.{supplyVault,feeVault}` and
/// `collateral.{mintPubkey,supplyVault}` are the exact PDAs the program derived
/// from (market, liquidity-mint). The market authority is anchored via the
/// on-chain SPL token authority of the liquidity supply vault.
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

  @Test
  void reserveVaultPdas() {
    assertEquals(
        LIQ_SUPPLY_VAULT.toBase58(),
        KaminoAccounts.reserveLiqSupplyPda(MARKET, LIQUIDITY_MINT, KLEND_PROGRAM).publicKey().toBase58()
    );
    assertEquals(
        FEE_VAULT.toBase58(),
        KaminoAccounts.reserveFeeVaultPda(MARKET, LIQUIDITY_MINT, KLEND_PROGRAM).publicKey().toBase58()
    );
    assertEquals(
        COLLATERAL_MINT.toBase58(),
        KaminoAccounts.reserveCollateralMintPda(MARKET, LIQUIDITY_MINT, KLEND_PROGRAM).publicKey().toBase58()
    );
    assertEquals(
        COLLATERAL_SUPPLY_VAULT.toBase58(),
        KaminoAccounts.reserveCollateralSupplyPda(MARKET, LIQUIDITY_MINT, KLEND_PROGRAM).publicKey().toBase58()
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
    final var reservePDAs = KaminoReservePDAs.createPDAs(KLEND_PROGRAM, marketPDAs, LIQUIDITY_MINT, TOKEN_PROGRAM);

    assertEquals(MARKET.toBase58(), reservePDAs.market().toBase58());
    assertEquals(MARKET_AUTHORITY.toBase58(), reservePDAs.marketAuthority().toBase58());
    assertEquals(LIQUIDITY_MINT.toBase58(), reservePDAs.mint().toBase58());
    assertEquals(LIQ_SUPPLY_VAULT.toBase58(), reservePDAs.liquiditySupplyVault().toBase58());
    assertEquals(FEE_VAULT.toBase58(), reservePDAs.feeVault().toBase58());
    assertEquals(COLLATERAL_MINT.toBase58(), reservePDAs.collateralMint().toBase58());
    assertEquals(COLLATERAL_SUPPLY_VAULT.toBase58(), reservePDAs.collateralSupplyVault().toBase58());
  }
}

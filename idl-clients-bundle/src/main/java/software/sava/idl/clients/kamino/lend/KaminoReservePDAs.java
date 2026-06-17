package software.sava.idl.clients.kamino.lend;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.kamino.KaminoAccounts;

// https://github.com/Kamino-Finance/klend-sdk/blob/master/src/utils/seeds.ts
public interface KaminoReservePDAs {

  static KaminoReservePDAs createPDAs(final PublicKey programId,
                                      final KaminoMarketPDAs marketPDAs,
                                      final PublicKey mint,
                                      final PublicKey tokenProgram) {
    final var market = marketPDAs.market();
    return new KaminoReservePDAsRecord(
        marketPDAs,
        mint,
        tokenProgram,
        KaminoAccounts.reserveLiqSupplyPda(market, mint, programId).publicKey(),
        KaminoAccounts.reserveCollateralMintPda(market, mint, programId).publicKey(),
        KaminoAccounts.reserveCollateralSupplyPda(market, mint, programId).publicKey(),
        KaminoAccounts.reserveFeeVaultPda(market, mint, programId).publicKey()
    );
  }

  KaminoMarketPDAs marketPDAs();

  default PublicKey market() {
    return marketPDAs().market();
  }

  default PublicKey marketAuthority() {
    return marketPDAs().authority();
  }

  PublicKey mint();

  PublicKey tokenProgram();

  PublicKey liquiditySupplyVault();

  PublicKey collateralMint();

  PublicKey collateralSupplyVault();

  PublicKey feeVault();
}

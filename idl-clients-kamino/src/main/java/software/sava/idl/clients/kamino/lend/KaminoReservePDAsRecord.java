package software.sava.idl.clients.kamino.lend;

import software.sava.core.accounts.PublicKey;

record KaminoReservePDAsRecord(KaminoMarketPDAs marketPDAs,
                               PublicKey mint,
                               PublicKey tokenProgram,
                               PublicKey liquiditySupplyVault,
                               PublicKey collateralMint,
                               PublicKey collateralSupplyVault,
                               PublicKey feeVault) implements KaminoReservePDAs {
}

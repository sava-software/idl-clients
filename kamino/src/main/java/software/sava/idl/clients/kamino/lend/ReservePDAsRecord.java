package software.sava.idl.clients.kamino.lend;

import software.sava.core.accounts.PublicKey;

record ReservePDAsRecord(MarketPDAs marketPDAs,
                         PublicKey mint,
                         PublicKey tokenProgram,
                         PublicKey liquiditySupplyVault,
                         PublicKey collateralMint,
                         PublicKey collateralSupplyVault,
                         PublicKey feeVault) implements ReservePDAs {
}

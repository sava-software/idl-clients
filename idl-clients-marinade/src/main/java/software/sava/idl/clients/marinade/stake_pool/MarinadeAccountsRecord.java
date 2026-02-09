package software.sava.idl.clients.marinade.stake_pool;

import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.meta.AccountMeta;

record MarinadeAccountsRecord(PublicKey mSolTokenMint,
                              PublicKey mSolTokenMintAuthorityPDA,
                              AccountMeta writeMSolTokenMint,
                              PublicKey marinadeProgram,
                              AccountMeta invokedMarinadeProgram,
                              PublicKey stateAccount,
                              AccountMeta writeStateAccount,
                              // Treasury
                              PublicKey treasuryReserveSolPDA,
                              AccountMeta writeReserveSolPDA,
                              PublicKey treasuryMSolAccount,
                              // Liquidity Pool
                              PublicKey liquidityPoolMSolSolMint,
                              PublicKey liquidityPoolAuthPDA,
                              PublicKey liquidityPoolMSolLegAccount,
                              PublicKey liquidityPoolMSolLegAuthority,
                              PublicKey liquidityPoolSolLegAccount,
                              PublicKey validatorListAccount) implements MarinadeAccounts {
}

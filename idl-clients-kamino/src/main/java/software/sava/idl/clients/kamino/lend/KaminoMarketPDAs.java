package software.sava.idl.clients.kamino.lend;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.kamino.KaminoAccounts;

// https://github.com/Kamino-Finance/klend-sdk/blob/master/src/utils/seeds.ts
public interface KaminoMarketPDAs {

  static KaminoMarketPDAs createPDAs(final PublicKey programId, final PublicKey market) {
    return new KaminoMarketPDAsRecord(
        market,
        KaminoAccounts.lendingMarketAuthPda(market, programId).publicKey()
    );
  }

  PublicKey market();

  PublicKey authority();
}

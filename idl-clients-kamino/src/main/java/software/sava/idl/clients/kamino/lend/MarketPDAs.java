package software.sava.idl.clients.kamino.lend;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.kamino.KaminoAccounts;

public interface MarketPDAs {

  static MarketPDAs createPDAs(final PublicKey programId, final PublicKey market) {
    return new MarketPDAsRecord(
        market,
        KaminoAccounts.lendingMarketAuthPda(market, programId).publicKey()
    );
  }

  PublicKey market();

  PublicKey authority();
}

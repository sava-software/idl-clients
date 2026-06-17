package software.sava.idl.clients.kamino.lend;

import software.sava.core.accounts.PublicKey;

public record KaminoMarketPDAsRecord(PublicKey market, PublicKey authority) implements KaminoMarketPDAs {
}

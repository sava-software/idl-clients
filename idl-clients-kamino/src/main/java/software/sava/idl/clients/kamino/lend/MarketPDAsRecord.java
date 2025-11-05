package software.sava.idl.clients.kamino.lend;

import software.sava.core.accounts.PublicKey;

public record MarketPDAsRecord(PublicKey market, PublicKey authority) implements MarketPDAs {
}

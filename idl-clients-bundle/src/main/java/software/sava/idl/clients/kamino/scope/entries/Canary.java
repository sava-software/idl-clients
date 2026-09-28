package software.sava.idl.clients.kamino.scope.entries;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.kamino.scope.gen.types.EmaType;
import software.sava.idl.clients.kamino.scope.gen.types.OracleType;

import java.util.Set;

/// Prices a Canary median feed: [#oracle()] is the Canary `PriceFeed` account, owned by
/// the canary program (`CanarFxHDSnbrPmrE79Qq6hL2p7ZMyyV4ZLTKQ6g7tpK` on mainnet), and the
/// refresh CPIs that program's `get_price` and reads the price from its return data
/// (`oracles/canary.rs`). The CPI consumes the canary program as an extra account, which
/// is why
/// [software.sava.idl.clients.kamino.scope.ScopeProgramClient#refreshPriceListExtraAccounts]
/// rejects the type; the mapping stores nothing for it beyond the feed.
public record Canary(int index, PublicKey oracle, Set<EmaType> emaTypes) implements OracleEntry {

  @Override
  public OracleType oracleType() {
    return OracleType.Canary;
  }
}

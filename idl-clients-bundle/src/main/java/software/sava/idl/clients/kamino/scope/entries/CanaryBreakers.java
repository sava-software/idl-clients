package software.sava.idl.clients.kamino.scope.entries;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.kamino.scope.gen.types.EmaType;
import software.sava.idl.clients.kamino.scope.gen.types.OracleType;

import java.util.Set;

/// A Canary breaker feed, which reads 0 (paused) or 1 (enabled): [#oracle()] is the Canary
/// `PriceFeed` account, and the mapping and the refresh are those of [Canary], the same
/// account validation and the same `get_price` CPI into the canary program
/// (`oracles/canary_breakers.rs`). The refresh then refuses, with `PriceNotValid`, any
/// price but exactly 0 or 1 at exponent 0, so zero is a price this type publishes, which
/// the refresh refuses from a [Canary] entry. Scope documents it as a factor of a
/// [MultiplicationChain], or as the reference price of an entry priced near 1
/// (`states/oracle_type.rs`). Its CPI consumes the canary program as an extra account, as
/// Canary's does, which is why
/// [software.sava.idl.clients.kamino.scope.ScopeProgramClient#refreshPriceListExtraAccounts]
/// rejects the type. The slot's generic data is never read for it.
public record CanaryBreakers(int index, PublicKey oracle, Set<EmaType> emaTypes) implements OracleEntry {

  @Override
  public OracleType oracleType() {
    return OracleType.CanaryBreakers;
  }
}

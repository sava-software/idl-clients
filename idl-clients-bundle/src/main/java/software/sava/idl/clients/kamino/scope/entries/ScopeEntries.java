package software.sava.idl.clients.kamino.scope.entries;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.kamino.lend.gen.types.Reserve;
import software.sava.idl.clients.kamino.lend.gen.types.ScopeConfiguration;
import software.sava.idl.clients.kamino.scope.gen.types.Configuration;
import software.sava.idl.clients.kamino.scope.gen.types.OracleType;

import java.util.List;
import java.util.OptionalInt;

public interface ScopeEntries {

  /// The oracle mappings account these entries were parsed from.
  PublicKey pubKey();

  /// The oracle prices account of the same feed, or `null` when the parse was not
  /// given a [Configuration] to bind it.
  ///
  /// This is the key a Kamino reserve's `ScopeConfiguration.priceFeed` names — the
  /// program compares that field against the *prices* account, not the mappings
  /// account — so it is what lets [#readPriceChains(Reserve)] tell that a reserve
  /// belongs to this feed.
  PublicKey oraclePrices();

  long slot();

  /// @throws IllegalArgumentException when the reserve names a different price feed
  ///                                  and this parse knows which feed it came from
  PriceChains readPriceChains(final Reserve reserve);

  /// @throws IllegalArgumentException when the configuration names a different price
  ///                                  feed and this parse knows which feed it came from
  PriceChains readPriceChains(final PublicKey mintKey, final ScopeConfiguration scopeConfiguration);

  ScopeEntry scopeEntry(final int index);

  int numEntries();

  List<ScopeEntry> oracleEntries(final PublicKey oracle, final OracleType oracleType);

  /// Whether the program has frozen this slot. A frozen entry keeps its last price
  /// and is skipped by every refresh handler, so it is stale by design rather than
  /// by staleness — which is invisible in the entry itself, since a frozen slot
  /// decodes byte for byte like a live one.
  boolean frozen(final int index);

  /// The reference price configured for this slot, or `null`.
  ///
  /// Every slot can carry one: the program sets it without consulting the oracle
  /// type, and enforces the divergence bound on refresh whatever the type is. Only
  /// [ReferencesEntry] models it on the entry itself, so this is the uniform way to
  /// ask, and the only way for the types that have no field for it.
  ScopeEntry referencePrice(final int index);

  /// Max divergence from [#referencePrice(int)] tolerated on refresh, in bps.
  /// Empty when the slot has no reference price or no tolerance configured.
  OptionalInt referenceToleranceBps(final int index);
}

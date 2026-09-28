package software.sava.idl.clients.kamino.scope.entries;

import java.util.OptionalInt;

public sealed interface ReferencesEntry
    extends OracleEntry
    permits PythLazer, Chainlink, PythPull, Securitize {

  /// The slot whose last stored price bounds this one on refresh.
  ///
  /// Resolved while the entry graph is still being built, so a pair of slots that
  /// reference each other — which the program accepts, each bounding its refresh
  /// against the other's last stored price — reads as `null` here for whichever of
  /// them is resolved second, and so does a slot that references itself, which bounds
  /// how far each refresh may move its own price and which the program has refused on
  /// write since Scope 0.42.0 (`RefPriceSelfReference`) but still honours on entries
  /// configured earlier. `ScopeEntries.referencePrice(int)` resolves the same field
  /// after the whole account is walked and has no such gap; prefer it when the answer
  /// has to be complete.
  ScopeEntry refPrice();

  /// Max divergence from [#refPrice()] tolerated on refresh, in bps.
  ///
  /// Empty here means the field is unset, which is not the same as unbounded: the
  /// program applies its own default whenever a reference price is configured.
  /// `ScopeEntries.referenceToleranceBps(int)` reports the bound actually enforced.
  OptionalInt refPriceToleranceBps();
}

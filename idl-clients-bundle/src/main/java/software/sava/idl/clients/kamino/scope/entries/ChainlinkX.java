package software.sava.idl.clients.kamino.scope.entries;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.kamino.scope.gen.types.EmaType;
import software.sava.idl.clients.kamino.scope.gen.types.MarketStatusBehavior;
import software.sava.idl.clients.kamino.scope.gen.types.OracleType;

import java.util.Set;

/// Prices a Chainlink v10 report: the report's price times the multiplier it carries. It
/// is refreshed only by `refresh_chainlink_price`, which since Scope 0.43.0 rejects a
/// report observed more than 120 seconds before the refresh. Since 0.43.0 the mapping's
/// generic data is [software.sava.idl.clients.kamino.scope.gen.types.ChainlinkXMappingData]
/// — the market status behaviour at offset 0, as before, then `dailyAutoApprovalBps`, a
/// `u16` at offset 1. `ChainlinkRWA` keeps the behaviour alone
/// ([software.sava.idl.clients.kamino.scope.gen.types.V8]).
///
/// `dailyAutoApprovalBps` is how far, in bps, a reported multiplier may move from the
/// entry's reference and still publish without a resume; an announced multiplier switch
/// within it triggers no blackout either. The reference moves to the multiplier of the
/// first report published once 24 hours have passed since its period started, and a
/// period starts when the reference is set or moves, when a suspension lifts, and when a
/// threshold is configured on an entry that had none, which keeps its old reference.
/// Zero means every change suspends the price, except one within one part in 10^12 of
/// the reference.
///
/// The state the refresh keeps lives in the *prices* account's `DatedPrice.genericData`
/// as [software.sava.idl.clients.kamino.scope.gen.types.ChainlinkXPriceData], whose
/// fields follow the suspended flag: while suspended, the activation the report
/// announced and the multiplier a resume approved, absent until one does; otherwise the
/// period start, zero at a zero threshold, and the reference.
///
/// Unlike [Token2022Multiplier]'s threshold, this one is checked at every refresh as well
/// as on write: the program caps it at 100 (`MAX_DAILY_AUTO_APPROVAL_BPS`), and because
/// mappings written before the field existed were never validated past the behaviour
/// byte, a stored value above the cap makes every refresh of the entry fail with
/// `AutoApprovalBpsOutOfRange`. This reads what is stored, unsigned; a mapping written
/// before 0.43.0 holds whatever its tooling put in those two bytes — zero when it wrote
/// zeros, which reads as "every change suspends".
public record ChainlinkX(int index,
                         PublicKey oracle,
                         MarketStatusBehavior marketStatusBehavior,
                         Set<EmaType> emaTypes,
                         int dailyAutoApprovalBps) implements ChainlinkStatusEntry {

  @Override
  public OracleType oracleType() {
    return OracleType.ChainlinkX;
  }
}

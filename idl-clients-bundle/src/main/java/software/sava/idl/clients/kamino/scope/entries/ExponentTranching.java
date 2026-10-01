package software.sava.idl.clients.kamino.scope.entries;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.kamino.scope.gen.types.EmaType;
import software.sava.idl.clients.kamino.scope.gen.types.ExponentTrancheSide;
import software.sava.idl.clients.kamino.scope.gen.types.OracleType;

import java.util.Set;

/// Prices one tranche of an Exponent tranching market: [#oracle()] is the market, owned by
/// the Exponent tranching program (`XPTrnchoawiUc9iYJrpfchS8vgr8Y5X2QGBdHPXukty`), and
/// [#trancheSide()] is the senior or junior side, the one value the mapping stores for the
/// type ([software.sava.idl.clients.kamino.scope.gen.types.ExponentTranchingData]).
///
/// The refresh CPIs the market's `update_market`, which syncs the market with its SY rate
/// source and returns each side's net asset value per LP token, and publishes that value
/// (`oracles/exponent_tranching.rs`). The CPI consumes, after the market, its return model
/// storage, address lookup table and SY program, the program's event authority and the
/// program itself, then the market's `get_sy_state` accounts, which is why
/// [software.sava.idl.clients.kamino.scope.ScopeProgramClient#refreshPriceListExtraAccounts]
/// rejects the type. The outer instruction must pass the market, its return model storage
/// and each `get_sy_state` account the market flags writable as writable. A tranche with
/// no LP supply does not price. A wiped one, with supply but no net asset value, still
/// publishes the value a deposit would mint at, which can round to zero, and the program
/// accepts a zero price from this type. A failed
/// CPI aborts the whole transaction, even in a multi-token refresh, which otherwise skips
/// an entry whose price cannot be computed — whether `update_market` reverts or the
/// runtime refuses the call because an account it needs writable was passed read-only.
///
/// `trancheSide` is `null` for a side this client does not know. The program refuses to
/// write one, so only a program newer than this IDL could store it.
public record ExponentTranching(int index,
                                PublicKey oracle,
                                ExponentTrancheSide trancheSide,
                                Set<EmaType> emaTypes) implements OracleEntry {

  @Override
  public OracleType oracleType() {
    return OracleType.ExponentTranching;
  }
}

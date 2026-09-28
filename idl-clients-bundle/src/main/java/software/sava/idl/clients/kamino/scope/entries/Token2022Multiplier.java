package software.sava.idl.clients.kamino.scope.entries;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.kamino.scope.gen.types.EmaType;
import software.sava.idl.clients.kamino.scope.gen.types.OracleType;

import java.util.Set;

/// Prices the effective multiplier of a Token-2022 mint's `ScaledUiAmount` extension:
/// [#oracle()] is that mint, and the refresh reads the multiplier straight from it. The
/// approval state the refresh keeps — suspension, the approved or reference multiplier
/// bits, and the switch or period timestamp — lives in the *prices* account's
/// `DatedPrice.genericData` as
/// [software.sava.idl.clients.kamino.scope.gen.types.Token2022MultiplierStoredData].
///
/// `dailyAutoApprovalBps` is the one value the mapping stores for the type
/// ([software.sava.idl.clients.kamino.scope.gen.types.Token2022MultiplierMappingData]): a
/// multiplier within this many bps of the approved one is published without a resume, and
/// a scheduled switch within it triggers no blackout. Zero means every change suspends the
/// price. The program caps it at 100 on write (`MAX_DAILY_AUTO_APPROVAL_BPS`) and reads it
/// at every refresh without re-checking; this reads what is stored. An entry configured
/// before Scope 0.42.0 holds whatever its tooling wrote in those two bytes, which 0.41.0
/// neither read nor validated — zero when it wrote zeros.
public record Token2022Multiplier(int index,
                                  PublicKey oracle,
                                  Set<EmaType> emaTypes,
                                  int dailyAutoApprovalBps) implements OracleEntry {

  @Override
  public OracleType oracleType() {
    return OracleType.Token2022Multiplier;
  }
}

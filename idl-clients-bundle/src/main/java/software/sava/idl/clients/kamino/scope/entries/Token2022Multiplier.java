package software.sava.idl.clients.kamino.scope.entries;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.kamino.scope.gen.types.EmaType;
import software.sava.idl.clients.kamino.scope.gen.types.OracleType;

import java.util.Set;

/// Prices the effective multiplier of a Token-2022 mint's `ScaledUiAmount` extension:
/// [#oracle()] is that mint, and the refresh reads the multiplier straight from it. The
/// approval state the refresh keeps — suspension, approved multiplier bits — lives in
/// the *prices* account's `DatedPrice.genericData` as
/// [software.sava.idl.clients.kamino.scope.gen.types.Token2022MultiplierStoredData];
/// the mapping this entry is parsed from stores nothing for the type.
public record Token2022Multiplier(int index, PublicKey oracle, Set<EmaType> emaTypes) implements OracleEntry {

  @Override
  public OracleType oracleType() {
    return OracleType.Token2022Multiplier;
  }
}

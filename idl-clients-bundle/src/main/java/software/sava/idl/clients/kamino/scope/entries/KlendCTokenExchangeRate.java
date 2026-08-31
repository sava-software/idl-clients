package software.sava.idl.clients.kamino.scope.entries;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.kamino.scope.gen.types.EmaType;
import software.sava.idl.clients.kamino.scope.gen.types.OracleType;

import java.util.Set;

/// Prices a klend cToken's exchange rate — underlying tokens per cToken: [#oracle()]
/// is the klend Reserve, and the refresh CPIs into klend to refresh that reserve and
/// read the rate from return data. Those CPIs consume the klend program and lending
/// market as extra accounts, which is why
/// [software.sava.idl.clients.kamino.scope.ScopeProgramClient#refreshPriceListExtraAccounts]
/// rejects the type; the mapping stores nothing for it beyond the reserve.
public record KlendCTokenExchangeRate(int index, PublicKey oracle, Set<EmaType> emaTypes) implements OracleEntry {

  @Override
  public OracleType oracleType() {
    return OracleType.KlendCTokenExchangeRate;
  }
}

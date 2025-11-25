package software.sava.idl.clients.kamino.scope.entries;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.kamino.scope.gen.types.OracleType;

public record NotYetSupported(PublicKey priceAccount,
                              OracleType oracleType,
                              ScopeEntry twapSource,
                              boolean twapEnabled,
                              ScopeEntry refPrice,
                              byte[] generic) implements ScopeEntry {
}

package software.sava.idl.clients.kamino.scope.entries;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.kamino.lend.gen.types.Reserve;
import software.sava.idl.clients.kamino.scope.gen.types.DatedPrice;
import software.sava.idl.clients.kamino.scope.gen.types.OracleMappings;
import software.sava.idl.clients.kamino.scope.gen.types.OracleType;

import java.math.BigDecimal;
import java.util.Map;

public interface ScopeReader {

  static ScopeReader createReader(final OracleMappings oracleMappings) {
    return new ScopeReaderRecord(
        oracleMappings.priceInfoAccounts(),
        oracleMappings.priceTypes(),
        oracleMappings.twapSource(),
        oracleMappings.twapEnabled(),
        oracleMappings.refPrice(),
        oracleMappings.generic(),
        OracleType.values()
    );
  }

  static BigDecimal scaleScopePrice(final DatedPrice datedPrice) {
    final var scaledPrice = datedPrice.price();
    final long val = scaledPrice.value();
    final var price = val < 0
        ? new BigDecimal(Long.toUnsignedString(val))
        : new BigDecimal(val);
    return price.movePointRight(Math.toIntExact(scaledPrice.exp()));
  }

  ScopeEntry[] readEntries();

  PriceChains readPriceChains(final Map<PublicKey, ScopeEntry[]> oracleMappings, final Reserve reserve);
}

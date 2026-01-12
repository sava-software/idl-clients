package software.sava.idl.clients.kamino.scope.entries;

import software.sava.idl.clients.kamino.scope.gen.types.DatedPrice;
import software.sava.idl.clients.kamino.scope.gen.types.OracleMappings;
import software.sava.idl.clients.kamino.scope.gen.types.OracleType;
import software.sava.rpc.json.http.response.AccountInfo;

import java.math.BigDecimal;

public interface ScopeReader {

  static ScopeEntries parseEntries(final AccountInfo<byte[]> accountInfo) {
    final long slot = accountInfo.context().slot();
    final var mappings = OracleMappings.read(accountInfo);
    return parseEntries(slot, mappings);
  }

  static ScopeEntries parseEntries(final long slot, final OracleMappings oracleMappings) {
    final var priceAccounts = oracleMappings.priceInfoAccounts();
    final var entries = new ScopeEntry[priceAccounts.length];
    final var reader = new ScopeReaderRecord(
        entries,
        priceAccounts,
        oracleMappings.priceTypes(),
        oracleMappings.twapSource(),
        oracleMappings.twapEnabledBitmask(),
        oracleMappings.refPrice(),
        oracleMappings.generic(),
        OracleType.values()
    );
    return reader.readEntries(oracleMappings._address(), slot);
  }

  static BigDecimal scaleScopePrice(final DatedPrice datedPrice) {
    final var scaledPrice = datedPrice.price();
    final long val = scaledPrice.value();
    final var price = val < 0
        ? new BigDecimal(Long.toUnsignedString(val))
        : new BigDecimal(val);
    return price.movePointRight(Math.toIntExact(scaledPrice.exp()));
  }
}

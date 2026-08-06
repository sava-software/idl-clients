package software.sava.idl.clients.kamino.scope.entries;

import software.sava.core.encoding.ByteUtil;
import software.sava.idl.clients.core.math.SafeMath;
import software.sava.idl.clients.kamino.scope.gen.types.DatedPrice;
import software.sava.idl.clients.kamino.scope.gen.types.OracleMappings;
import software.sava.idl.clients.kamino.scope.gen.types.OraclePrices;
import software.sava.idl.clients.kamino.scope.gen.types.OracleType;
import software.sava.rpc.json.http.response.AccountInfo;

import java.math.BigDecimal;

public interface ScopeReader {

  /// Resolve a raw `OracleMappings.price_types` byte to its oracle type, or `null`
  /// when the deployed program carries a type newer than the IDL this client was
  /// generated from.
  ///
  /// Bit 7 of the byte is the program's frozen flag (`FROZEN_FLAG = 0x80`), not part
  /// of the oracle type — freezing an entry sets it in place. This masks it off, which
  /// the program calls `strip_frozen_flag`. Reading the byte without that mask is not
  /// a mis-typing but an out-of-bounds index, because a Java `byte` carrying bit 7 is
  /// negative.
  ///
  /// Takes the values array rather than calling `OracleType.values()` itself, because
  /// that clones on every call and this runs once per slot across 512 slots.
  static OracleType oracleType(final OracleType[] oracleTypes, final byte priceType) {
    final int ordinal = priceType & ScopeReaderRecord.ORACLE_TYPE_MASK;
    return ordinal < oracleTypes.length ? oracleTypes[ordinal] : null;
  }

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
        oracleMappings.twapSourceOrRefPriceToleranceBps(),
        oracleMappings.twapEnabledBitmask(),
        oracleMappings.refPrice(),
        oracleMappings.generic(),
        OracleType.values(),
        new boolean[priceAccounts.length]
    );
    return reader.readEntries(oracleMappings._address(), slot);
  }

  static BigDecimal scaleScopePrice(final DatedPrice datedPrice) {
    final var scaledPrice = datedPrice.price();
    final long val = scaledPrice.value();
    final var price = SafeMath.toUnsignedBigDecimal(val);
    // scaleByPowerOfTen, not movePointLeft: movePointLeft normalizes a negative
    // resulting scale through setScale(0), materializing val*10^|exp| — a hostile
    // exp inflates that into a billion-digit BigInteger (see FixedPrice.createEntry)
    return price.scaleByPowerOfTen(Math.toIntExact(-scaledPrice.exp()));
  }

  static BigDecimal scaleScopePrice(final byte[] oraclePricesData, final int index) {
    int offset = OraclePrices.PRICES_OFFSET + (DatedPrice.BYTES * index);
    final long val = ByteUtil.getInt64LE(oraclePricesData, offset);
    if (val == 0) {
      return BigDecimal.ZERO;
    } else {
      final var price = SafeMath.toUnsignedBigDecimal(val);
      final long exp = ByteUtil.getInt64LE(oraclePricesData, offset + Long.BYTES);
      return price.scaleByPowerOfTen(Math.toIntExact(-exp));
    }
  }
}

package software.sava.idl.clients.kamino.scope.entries;

import software.sava.core.accounts.PublicKey;
import software.sava.core.encoding.ByteUtil;
import software.sava.idl.clients.core.math.SafeMath;
import software.sava.idl.clients.kamino.scope.gen.types.Configuration;
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

  /// Binds the feed's oracle prices account, which is what a Kamino reserve's
  /// `ScopeConfiguration.priceFeed` names — so the entries can tell whether a reserve
  /// handed to [ScopeEntries#readPriceChains(Reserve)] belongs to this feed. Prefer
  /// this overload wherever the configuration is at hand; the ones without it parse
  /// identically but cannot make that check.
  static ScopeEntries parseEntries(final AccountInfo<byte[]> accountInfo, final Configuration configuration) {
    return parseEntries(accountInfo.context().slot(), OracleMappings.read(accountInfo), configuration);
  }

  /// @throws IllegalArgumentException when the configuration describes a different
  ///                                  mappings account than the one being parsed —
  ///                                  the two are paired in one on-chain account, so
  ///                                  taking the prices key from an unrelated
  ///                                  configuration would attach a confident but wrong
  ///                                  feed identity to these entries
  static ScopeEntries parseEntries(final long slot,
                                   final OracleMappings oracleMappings,
                                   final Configuration configuration) {
    final var address = oracleMappings._address();
    if (address != null && !address.equals(configuration.oracleMappings())) {
      throw new IllegalArgumentException("Configuration describes oracle mappings "
          + configuration.oracleMappings() + ", not " + address);
    }
    return parseEntries(slot, oracleMappings, configuration.oraclePrices());
  }

  static ScopeEntries parseEntries(final long slot, final OracleMappings oracleMappings) {
    return parseEntries(slot, oracleMappings, (PublicKey) null);
  }

  static ScopeEntries parseEntries(final long slot,
                                   final OracleMappings oracleMappings,
                                   final PublicKey oraclePrices) {
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
    return reader.readEntries(oracleMappings._address(), oraclePrices, slot);
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

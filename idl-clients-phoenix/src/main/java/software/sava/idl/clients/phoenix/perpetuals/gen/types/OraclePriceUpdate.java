package software.sava.idl.clients.phoenix.perpetuals.gen.types;

import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

/// Single oracle price update targeting a specific perpetual asset.
///
public record OraclePriceUpdate(Symbol perpAssetId,
                                Price newExchangePerpPrice,
                                Price newExchangeSpotPrice) implements SerDe {

  public static final int PERP_ASSET_ID_OFFSET = 0;
  public static final int NEW_EXCHANGE_PERP_PRICE_OFFSET = 17;

  public static OraclePriceUpdate read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var perpAssetId = Symbol.read(_data, i);
    i += perpAssetId.l();
    final Price newExchangePerpPrice;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      newExchangePerpPrice = null;
      ++i;
    } else {
      ++i;
      newExchangePerpPrice = Price.read(_data, i);
      i += newExchangePerpPrice.l();
    }
    final var newExchangeSpotPrice = Price.read(_data, i);
    return new OraclePriceUpdate(perpAssetId, newExchangePerpPrice, newExchangeSpotPrice);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += perpAssetId.write(_data, i);
    i += SerDeUtil.writeOptional(1, newExchangePerpPrice, _data, i);
    i += newExchangeSpotPrice.write(_data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return perpAssetId.l() + (newExchangePerpPrice == null ? 1 : (1 + newExchangePerpPrice.l())) + newExchangeSpotPrice.l();
  }
}

package software.sava.idl.clients.drift.gen.types;

import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.encoding.ByteUtil.getInt16LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt16LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

/// @param lastCumulativeFundingRate The perp market's last cumulative funding rate. Used to calculate the funding payment owed to user
///                                  precision: FUNDING_RATE_PRECISION
/// @param baseAssetAmount the size of the users perp position
///                        precision: BASE_PRECISION
/// @param quoteAssetAmount Used to calculate the users pnl. Upon entry, is equal to base_asset_amount * avg entry price - fees
///                         Updated when the user open/closes position or settles pnl. Includes fees/funding
///                         precision: QUOTE_PRECISION
/// @param quoteBreakEvenAmount The amount of quote the user would need to exit their position at to break even
///                             Updated when the user open/closes position or settles pnl. Includes fees/funding
///                             precision: QUOTE_PRECISION
/// @param quoteEntryAmount The amount quote the user entered the position with. Equal to base asset amount * avg entry price
///                         Updated when the user open/closes position. Excludes fees/funding
///                         precision: QUOTE_PRECISION
/// @param openBids The amount of non reduce only trigger orders the user has open
///                 precision: BASE_PRECISION
/// @param openAsks The amount of non reduce only trigger orders the user has open
///                 precision: BASE_PRECISION
/// @param settledPnl The amount of pnl settled in this market since opening the position
///                   precision: QUOTE_PRECISION
/// @param lpShares The number of lp (liquidity provider) shares the user has in this perp market
///                 LP shares allow users to provide liquidity via the AMM
///                 precision: BASE_PRECISION
/// @param lastBaseAssetAmountPerLp The last base asset amount per lp the amm had
///                                 Used to settle the users lp position
///                                 precision: BASE_PRECISION
/// @param lastQuoteAssetAmountPerLp The last quote asset amount per lp the amm had
///                                  Used to settle the users lp position
///                                  precision: QUOTE_PRECISION
/// @param marketIndex The market index for the perp market
/// @param openOrders The number of open orders
public record PerpPosition(long lastCumulativeFundingRate,
                           long baseAssetAmount,
                           long quoteAssetAmount,
                           long quoteBreakEvenAmount,
                           long quoteEntryAmount,
                           long openBids,
                           long openAsks,
                           long settledPnl,
                           long lpShares,
                           long lastBaseAssetAmountPerLp,
                           long lastQuoteAssetAmountPerLp,
                           byte[] padding,
                           int maxMarginRatio,
                           int marketIndex,
                           int openOrders,
                           int perLpBase) implements SerDe {

  public static final int BYTES = 96;
  public static final int PADDING_LEN = 2;

  public static PerpPosition read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var lastCumulativeFundingRate = getInt64LE(_data, i);
    i += 8;
    final var baseAssetAmount = getInt64LE(_data, i);
    i += 8;
    final var quoteAssetAmount = getInt64LE(_data, i);
    i += 8;
    final var quoteBreakEvenAmount = getInt64LE(_data, i);
    i += 8;
    final var quoteEntryAmount = getInt64LE(_data, i);
    i += 8;
    final var openBids = getInt64LE(_data, i);
    i += 8;
    final var openAsks = getInt64LE(_data, i);
    i += 8;
    final var settledPnl = getInt64LE(_data, i);
    i += 8;
    final var lpShares = getInt64LE(_data, i);
    i += 8;
    final var lastBaseAssetAmountPerLp = getInt64LE(_data, i);
    i += 8;
    final var lastQuoteAssetAmountPerLp = getInt64LE(_data, i);
    i += 8;
    final var padding = new byte[2];
    i += SerDeUtil.readArray(padding, _data, i);
    final var maxMarginRatio = getInt16LE(_data, i);
    i += 2;
    final var marketIndex = getInt16LE(_data, i);
    i += 2;
    final var openOrders = _data[i] & 0xFF;
    ++i;
    final var perLpBase = _data[i];
    return new PerpPosition(lastCumulativeFundingRate,
                            baseAssetAmount,
                            quoteAssetAmount,
                            quoteBreakEvenAmount,
                            quoteEntryAmount,
                            openBids,
                            openAsks,
                            settledPnl,
                            lpShares,
                            lastBaseAssetAmountPerLp,
                            lastQuoteAssetAmountPerLp,
                            padding,
                            maxMarginRatio,
                            marketIndex,
                            openOrders,
                            perLpBase);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt64LE(_data, i, lastCumulativeFundingRate);
    i += 8;
    putInt64LE(_data, i, baseAssetAmount);
    i += 8;
    putInt64LE(_data, i, quoteAssetAmount);
    i += 8;
    putInt64LE(_data, i, quoteBreakEvenAmount);
    i += 8;
    putInt64LE(_data, i, quoteEntryAmount);
    i += 8;
    putInt64LE(_data, i, openBids);
    i += 8;
    putInt64LE(_data, i, openAsks);
    i += 8;
    putInt64LE(_data, i, settledPnl);
    i += 8;
    putInt64LE(_data, i, lpShares);
    i += 8;
    putInt64LE(_data, i, lastBaseAssetAmountPerLp);
    i += 8;
    putInt64LE(_data, i, lastQuoteAssetAmountPerLp);
    i += 8;
    i += SerDeUtil.writeArrayChecked(padding, 2, _data, i);
    putInt16LE(_data, i, maxMarginRatio);
    i += 2;
    putInt16LE(_data, i, marketIndex);
    i += 2;
    _data[i] = (byte) openOrders;
    ++i;
    _data[i] = (byte) perLpBase;
    ++i;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

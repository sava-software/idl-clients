package software.sava.idl.clients.drift.gen.types;

import software.sava.core.borsh.Borsh;

import static software.sava.core.encoding.ByteUtil.getInt16LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt16LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

/// @param scaledBalance The scaled balance of the position. To get the token amount, multiply by the cumulative deposit/borrow
///                      interest of corresponding market.
///                      precision: SPOT_BALANCE_PRECISION
/// @param openBids How many spot non reduce only trigger orders the user has open
///                 precision: token mint precision
/// @param openAsks How many spot non reduce only trigger orders the user has open
///                 precision: token mint precision
/// @param cumulativeDeposits The cumulative deposits/borrows a user has made into a market
///                           precision: token mint precision
/// @param marketIndex The market index of the corresponding spot market
/// @param balanceType Whether the position is deposit or borrow
/// @param openOrders Number of open orders
public record SpotPosition(long scaledBalance,
                           long openBids,
                           long openAsks,
                           long cumulativeDeposits,
                           int marketIndex,
                           SpotBalanceType balanceType,
                           int openOrders,
                           byte[] padding) implements Borsh {

  public static final int BYTES = 40;
  public static final int PADDING_LEN = 4;

  public static SpotPosition read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var scaledBalance = getInt64LE(_data, i);
    i += 8;
    final var openBids = getInt64LE(_data, i);
    i += 8;
    final var openAsks = getInt64LE(_data, i);
    i += 8;
    final var cumulativeDeposits = getInt64LE(_data, i);
    i += 8;
    final var marketIndex = getInt16LE(_data, i);
    i += 2;
    final var balanceType = SpotBalanceType.read(_data, i);
    i += balanceType.l();
    final var openOrders = _data[i] & 0xFF;
    ++i;
    final var padding = new byte[4];
    Borsh.readArray(padding, _data, i);
    return new SpotPosition(scaledBalance,
                            openBids,
                            openAsks,
                            cumulativeDeposits,
                            marketIndex,
                            balanceType,
                            openOrders,
                            padding);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt64LE(_data, i, scaledBalance);
    i += 8;
    putInt64LE(_data, i, openBids);
    i += 8;
    putInt64LE(_data, i, openAsks);
    i += 8;
    putInt64LE(_data, i, cumulativeDeposits);
    i += 8;
    putInt16LE(_data, i, marketIndex);
    i += 2;
    i += balanceType.write(_data, i);
    _data[i] = (byte) openOrders;
    ++i;
    i += Borsh.writeArrayChecked(padding, 4, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

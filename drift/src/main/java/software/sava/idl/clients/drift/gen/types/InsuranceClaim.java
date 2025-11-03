package software.sava.idl.clients.drift.gen.types;

import software.sava.core.borsh.Borsh;

import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

/// @param revenueWithdrawSinceLastSettle The amount of revenue last settled
///                                       Positive if funds left the perp market,
///                                       negative if funds were pulled into the perp market
///                                       precision: QUOTE_PRECISION
/// @param maxRevenueWithdrawPerPeriod The max amount of revenue that can be withdrawn per period
///                                    precision: QUOTE_PRECISION
/// @param quoteMaxInsurance The max amount of insurance that perp market can use to resolve bankruptcy and pnl deficits
///                          precision: QUOTE_PRECISION
/// @param quoteSettledInsurance The amount of insurance that has been used to resolve bankruptcy and pnl deficits
///                              precision: QUOTE_PRECISION
/// @param lastRevenueWithdrawTs The last time revenue was settled in/out of market
public record InsuranceClaim(long revenueWithdrawSinceLastSettle,
                             long maxRevenueWithdrawPerPeriod,
                             long quoteMaxInsurance,
                             long quoteSettledInsurance,
                             long lastRevenueWithdrawTs) implements Borsh {

  public static final int BYTES = 40;

  public static InsuranceClaim read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var revenueWithdrawSinceLastSettle = getInt64LE(_data, i);
    i += 8;
    final var maxRevenueWithdrawPerPeriod = getInt64LE(_data, i);
    i += 8;
    final var quoteMaxInsurance = getInt64LE(_data, i);
    i += 8;
    final var quoteSettledInsurance = getInt64LE(_data, i);
    i += 8;
    final var lastRevenueWithdrawTs = getInt64LE(_data, i);
    return new InsuranceClaim(revenueWithdrawSinceLastSettle,
                              maxRevenueWithdrawPerPeriod,
                              quoteMaxInsurance,
                              quoteSettledInsurance,
                              lastRevenueWithdrawTs);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt64LE(_data, i, revenueWithdrawSinceLastSettle);
    i += 8;
    putInt64LE(_data, i, maxRevenueWithdrawPerPeriod);
    i += 8;
    putInt64LE(_data, i, quoteMaxInsurance);
    i += 8;
    putInt64LE(_data, i, quoteSettledInsurance);
    i += 8;
    putInt64LE(_data, i, lastRevenueWithdrawTs);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

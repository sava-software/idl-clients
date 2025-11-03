package software.sava.idl.clients.drift.gen.types;

import software.sava.core.borsh.Borsh;

import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

/// @param totalFeePaid Total taker fee paid
///                     precision: QUOTE_PRECISION
/// @param totalFeeRebate Total maker fee rebate
///                       precision: QUOTE_PRECISION
/// @param totalTokenDiscount Total discount from holding token
///                           precision: QUOTE_PRECISION
/// @param totalRefereeDiscount Total discount from being referred
///                             precision: QUOTE_PRECISION
/// @param totalReferrerReward Total reward to referrer
///                            precision: QUOTE_PRECISION
/// @param currentEpochReferrerReward Total reward to referrer this epoch
///                                   precision: QUOTE_PRECISION
public record UserFees(long totalFeePaid,
                       long totalFeeRebate,
                       long totalTokenDiscount,
                       long totalRefereeDiscount,
                       long totalReferrerReward,
                       long currentEpochReferrerReward) implements Borsh {

  public static final int BYTES = 48;

  public static UserFees read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var totalFeePaid = getInt64LE(_data, i);
    i += 8;
    final var totalFeeRebate = getInt64LE(_data, i);
    i += 8;
    final var totalTokenDiscount = getInt64LE(_data, i);
    i += 8;
    final var totalRefereeDiscount = getInt64LE(_data, i);
    i += 8;
    final var totalReferrerReward = getInt64LE(_data, i);
    i += 8;
    final var currentEpochReferrerReward = getInt64LE(_data, i);
    return new UserFees(totalFeePaid,
                        totalFeeRebate,
                        totalTokenDiscount,
                        totalRefereeDiscount,
                        totalReferrerReward,
                        currentEpochReferrerReward);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt64LE(_data, i, totalFeePaid);
    i += 8;
    putInt64LE(_data, i, totalFeeRebate);
    i += 8;
    putInt64LE(_data, i, totalTokenDiscount);
    i += 8;
    putInt64LE(_data, i, totalRefereeDiscount);
    i += 8;
    putInt64LE(_data, i, totalReferrerReward);
    i += 8;
    putInt64LE(_data, i, currentEpochReferrerReward);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

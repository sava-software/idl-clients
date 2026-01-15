package software.sava.idl.clients.drift.gen.types;

import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;

public record FeeTier(int feeNumerator,
                      int feeDenominator,
                      int makerRebateNumerator,
                      int makerRebateDenominator,
                      int referrerRewardNumerator,
                      int referrerRewardDenominator,
                      int refereeFeeNumerator,
                      int refereeFeeDenominator) implements SerDe {

  public static final int BYTES = 32;

  public static final int FEE_NUMERATOR_OFFSET = 0;
  public static final int FEE_DENOMINATOR_OFFSET = 4;
  public static final int MAKER_REBATE_NUMERATOR_OFFSET = 8;
  public static final int MAKER_REBATE_DENOMINATOR_OFFSET = 12;
  public static final int REFERRER_REWARD_NUMERATOR_OFFSET = 16;
  public static final int REFERRER_REWARD_DENOMINATOR_OFFSET = 20;
  public static final int REFEREE_FEE_NUMERATOR_OFFSET = 24;
  public static final int REFEREE_FEE_DENOMINATOR_OFFSET = 28;

  public static FeeTier read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var feeNumerator = getInt32LE(_data, i);
    i += 4;
    final var feeDenominator = getInt32LE(_data, i);
    i += 4;
    final var makerRebateNumerator = getInt32LE(_data, i);
    i += 4;
    final var makerRebateDenominator = getInt32LE(_data, i);
    i += 4;
    final var referrerRewardNumerator = getInt32LE(_data, i);
    i += 4;
    final var referrerRewardDenominator = getInt32LE(_data, i);
    i += 4;
    final var refereeFeeNumerator = getInt32LE(_data, i);
    i += 4;
    final var refereeFeeDenominator = getInt32LE(_data, i);
    return new FeeTier(feeNumerator,
                       feeDenominator,
                       makerRebateNumerator,
                       makerRebateDenominator,
                       referrerRewardNumerator,
                       referrerRewardDenominator,
                       refereeFeeNumerator,
                       refereeFeeDenominator);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt32LE(_data, i, feeNumerator);
    i += 4;
    putInt32LE(_data, i, feeDenominator);
    i += 4;
    putInt32LE(_data, i, makerRebateNumerator);
    i += 4;
    putInt32LE(_data, i, makerRebateDenominator);
    i += 4;
    putInt32LE(_data, i, referrerRewardNumerator);
    i += 4;
    putInt32LE(_data, i, referrerRewardDenominator);
    i += 4;
    putInt32LE(_data, i, refereeFeeNumerator);
    i += 4;
    putInt32LE(_data, i, refereeFeeDenominator);
    i += 4;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

package software.sava.idl.clients.kamino.lend.gen.types;

import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

/// Additional fee information on a reserve
/// 
/// These exist separately from interest accrual fees, and are specifically for the program owner
/// and referral fee. The fees are paid out as a percentage of liquidity token amounts during
/// repayments and liquidations.
///
/// @param originationFeeSf Fee assessed on `BorrowObligationLiquidity`, as scaled fraction (60 bits fractional part)
///                         Must be between `0` and `2^60`, such that `2^60 = 1`.  A few examples for
///                         clarity:
///                         1% = (1 << 60) / 100 = 11529215046068470
///                         0.01% (1 basis point) = 115292150460685
///                         0.00001% (Aave origination fee) = 115292150461
/// @param flashLoanFeeSf Fee for flash loan, expressed as scaled fraction.
///                       0.3% (Aave flash loan fee) = 0.003 * 2^60 = 3458764513820541
/// @param padding Used for allignment
public record ReserveFees(long originationFeeSf,
                          long flashLoanFeeSf,
                          byte[] padding) implements SerDe {

  public static final int BYTES = 24;
  public static final int PADDING_LEN = 8;

  public static ReserveFees read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var originationFeeSf = getInt64LE(_data, i);
    i += 8;
    final var flashLoanFeeSf = getInt64LE(_data, i);
    i += 8;
    final var padding = new byte[8];
    SerDeUtil.readArray(padding, _data, i);
    return new ReserveFees(originationFeeSf, flashLoanFeeSf, padding);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt64LE(_data, i, originationFeeSf);
    i += 8;
    putInt64LE(_data, i, flashLoanFeeSf);
    i += 8;
    i += SerDeUtil.writeArrayChecked(padding, 8, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

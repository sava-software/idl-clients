package software.sava.idl.clients.kamino.lend.gen.types;

import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

/// A subset of BorrowOrderConfig excluding the accounts passed via SetBorrowOrder.
///
public record BorrowOrderConfigArgs(long remainingDebtAmount,
                                    int maxBorrowRateBps,
                                    long minDebtTermSeconds,
                                    long fillableUntilTimestamp) implements SerDe {

  public static final int BYTES = 28;

  public static final int REMAINING_DEBT_AMOUNT_OFFSET = 0;
  public static final int MAX_BORROW_RATE_BPS_OFFSET = 8;
  public static final int MIN_DEBT_TERM_SECONDS_OFFSET = 12;
  public static final int FILLABLE_UNTIL_TIMESTAMP_OFFSET = 20;

  public static BorrowOrderConfigArgs read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var remainingDebtAmount = getInt64LE(_data, i);
    i += 8;
    final var maxBorrowRateBps = getInt32LE(_data, i);
    i += 4;
    final var minDebtTermSeconds = getInt64LE(_data, i);
    i += 8;
    final var fillableUntilTimestamp = getInt64LE(_data, i);
    return new BorrowOrderConfigArgs(remainingDebtAmount,
                                     maxBorrowRateBps,
                                     minDebtTermSeconds,
                                     fillableUntilTimestamp);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt64LE(_data, i, remainingDebtAmount);
    i += 8;
    putInt32LE(_data, i, maxBorrowRateBps);
    i += 4;
    putInt64LE(_data, i, minDebtTermSeconds);
    i += 8;
    putInt64LE(_data, i, fillableUntilTimestamp);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

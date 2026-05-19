package software.sava.idl.clients.phoenix.perpetuals.gen.types;

import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

/// Fixed WithdrawThrottle payload embedded in WithdrawQueueHeader.
///
public record WithdrawThrottle(QuoteLots maxBudget,
                               QuoteLots remainingBudget,
                               QuoteLots replenishAmountPerSlot,
                               long lastUpdateSlot) implements SerDe {

  public static final int BYTES = 32;

  public static final int MAX_BUDGET_OFFSET = 0;
  public static final int REMAINING_BUDGET_OFFSET = 8;
  public static final int REPLENISH_AMOUNT_PER_SLOT_OFFSET = 16;
  public static final int LAST_UPDATE_SLOT_OFFSET = 24;

  public static WithdrawThrottle read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var maxBudget = QuoteLots.read(_data, i);
    i += maxBudget.l();
    final var remainingBudget = QuoteLots.read(_data, i);
    i += remainingBudget.l();
    final var replenishAmountPerSlot = QuoteLots.read(_data, i);
    i += replenishAmountPerSlot.l();
    final var lastUpdateSlot = getInt64LE(_data, i);
    return new WithdrawThrottle(maxBudget,
                                remainingBudget,
                                replenishAmountPerSlot,
                                lastUpdateSlot);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += maxBudget.write(_data, i);
    i += remainingBudget.write(_data, i);
    i += replenishAmountPerSlot.write(_data, i);
    putInt64LE(_data, i, lastUpdateSlot);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

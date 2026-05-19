package software.sava.idl.clients.phoenix.perpetuals.gen.types;

import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

public record WithdrawRateLimitConfig(long maxBudget, long replenishAmountPerSlot) implements SerDe {

  public static final int BYTES = 16;

  public static final int MAX_BUDGET_OFFSET = 0;
  public static final int REPLENISH_AMOUNT_PER_SLOT_OFFSET = 8;

  public static WithdrawRateLimitConfig read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var maxBudget = getInt64LE(_data, i);
    i += 8;
    final var replenishAmountPerSlot = getInt64LE(_data, i);
    return new WithdrawRateLimitConfig(maxBudget, replenishAmountPerSlot);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt64LE(_data, i, maxBudget);
    i += 8;
    putInt64LE(_data, i, replenishAmountPerSlot);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

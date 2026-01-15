package software.sava.idl.clients.drift.gen.types;

import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

public record ValidityGuardRails(long slotsBeforeStaleForAmm,
                                 long slotsBeforeStaleForMargin,
                                 long confidenceIntervalMaxSize,
                                 long tooVolatileRatio) implements SerDe {

  public static final int BYTES = 32;

  public static final int SLOTS_BEFORE_STALE_FOR_AMM_OFFSET = 0;
  public static final int SLOTS_BEFORE_STALE_FOR_MARGIN_OFFSET = 8;
  public static final int CONFIDENCE_INTERVAL_MAX_SIZE_OFFSET = 16;
  public static final int TOO_VOLATILE_RATIO_OFFSET = 24;

  public static ValidityGuardRails read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var slotsBeforeStaleForAmm = getInt64LE(_data, i);
    i += 8;
    final var slotsBeforeStaleForMargin = getInt64LE(_data, i);
    i += 8;
    final var confidenceIntervalMaxSize = getInt64LE(_data, i);
    i += 8;
    final var tooVolatileRatio = getInt64LE(_data, i);
    return new ValidityGuardRails(slotsBeforeStaleForAmm,
                                  slotsBeforeStaleForMargin,
                                  confidenceIntervalMaxSize,
                                  tooVolatileRatio);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt64LE(_data, i, slotsBeforeStaleForAmm);
    i += 8;
    putInt64LE(_data, i, slotsBeforeStaleForMargin);
    i += 8;
    putInt64LE(_data, i, confidenceIntervalMaxSize);
    i += 8;
    putInt64LE(_data, i, tooVolatileRatio);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

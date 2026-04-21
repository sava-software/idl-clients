package software.sava.idl.clients.loopscale.gen.types;

import java.util.OptionalLong;

import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

public record UpdateRewardsScheduleParams(long amountToTransfer,
                                          OptionalLong extendEndTime,
                                          int scheduleIndex) implements SerDe {

  public static final int AMOUNT_TO_TRANSFER_OFFSET = 0;
  public static final int EXTEND_END_TIME_OFFSET = 9;

  public static UpdateRewardsScheduleParams read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var amountToTransfer = getInt64LE(_data, i);
    i += 8;
    final OptionalLong extendEndTime;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      extendEndTime = OptionalLong.empty();
      ++i;
    } else {
      ++i;
      extendEndTime = OptionalLong.of(getInt64LE(_data, i));
      i += 8;
    }
    final var scheduleIndex = _data[i] & 0xFF;
    return new UpdateRewardsScheduleParams(amountToTransfer, extendEndTime, scheduleIndex);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt64LE(_data, i, amountToTransfer);
    i += 8;
    i += SerDeUtil.writeOptional(1, extendEndTime, _data, i);
    _data[i] = (byte) scheduleIndex;
    ++i;
    return i - _offset;
  }

  @Override
  public int l() {
    return 8 + (extendEndTime == null || extendEndTime.isEmpty() ? 1 : (1 + 8)) + 1;
  }
}

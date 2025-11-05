package software.sava.idl.clients.kamino.lend.gen.types;

import software.sava.core.borsh.Borsh;

import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

/// Reserve Withdrawal Caps State
///
public record WithdrawalCaps(long configCapacity,
                             long currentTotal,
                             long lastIntervalStartTimestamp,
                             long configIntervalLengthSeconds) implements Borsh {

  public static final int BYTES = 32;

  public static WithdrawalCaps read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var configCapacity = getInt64LE(_data, i);
    i += 8;
    final var currentTotal = getInt64LE(_data, i);
    i += 8;
    final var lastIntervalStartTimestamp = getInt64LE(_data, i);
    i += 8;
    final var configIntervalLengthSeconds = getInt64LE(_data, i);
    return new WithdrawalCaps(configCapacity,
                              currentTotal,
                              lastIntervalStartTimestamp,
                              configIntervalLengthSeconds);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt64LE(_data, i, configCapacity);
    i += 8;
    putInt64LE(_data, i, currentTotal);
    i += 8;
    putInt64LE(_data, i, lastIntervalStartTimestamp);
    i += 8;
    putInt64LE(_data, i, configIntervalLengthSeconds);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

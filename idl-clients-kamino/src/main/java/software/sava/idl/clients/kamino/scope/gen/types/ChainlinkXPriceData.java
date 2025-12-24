package software.sava.idl.clients.kamino.scope.gen.types;

import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

/// Price data for ChainlinkX type (v10)
///
public record ChainlinkXPriceData(long observationsTimestamp,
                                  boolean suspended,
                                  long activationDateTime) implements SerDe {

  public static final int BYTES = 17;

  public static ChainlinkXPriceData read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var observationsTimestamp = getInt64LE(_data, i);
    i += 8;
    final var suspended = _data[i] == 1;
    ++i;
    final var activationDateTime = getInt64LE(_data, i);
    return new ChainlinkXPriceData(observationsTimestamp, suspended, activationDateTime);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt64LE(_data, i, observationsTimestamp);
    i += 8;
    _data[i] = (byte) (suspended ? 1 : 0);
    ++i;
    putInt64LE(_data, i, activationDateTime);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

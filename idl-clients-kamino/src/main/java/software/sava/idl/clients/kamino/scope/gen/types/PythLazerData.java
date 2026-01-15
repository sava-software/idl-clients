package software.sava.idl.clients.kamino.scope.gen.types;

import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.core.encoding.ByteUtil.getInt16LE;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt16LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;

public record PythLazerData(int feedId,
                            int exponent,
                            int confidenceFactor) implements SerDe {

  public static final int BYTES = 7;

  public static final int FEED_ID_OFFSET = 0;
  public static final int EXPONENT_OFFSET = 2;
  public static final int CONFIDENCE_FACTOR_OFFSET = 3;

  public static PythLazerData read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var feedId = getInt16LE(_data, i);
    i += 2;
    final var exponent = _data[i] & 0xFF;
    ++i;
    final var confidenceFactor = getInt32LE(_data, i);
    return new PythLazerData(feedId, exponent, confidenceFactor);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt16LE(_data, i, feedId);
    i += 2;
    _data[i] = (byte) exponent;
    ++i;
    putInt32LE(_data, i, confidenceFactor);
    i += 4;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

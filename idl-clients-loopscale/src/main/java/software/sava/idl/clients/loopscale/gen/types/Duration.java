package software.sava.idl.clients.loopscale.gen.types;

import software.sava.idl.clients.core.gen.SerDe;

public record Duration(PodU32 duration, int durationType) implements SerDe {

  public static final int BYTES = 5;

  public static final int DURATION_OFFSET = 0;
  public static final int DURATION_TYPE_OFFSET = 4;

  public static Duration read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var duration = PodU32.read(_data, i);
    i += duration.l();
    final var durationType = _data[i] & 0xFF;
    return new Duration(duration, durationType);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += duration.write(_data, i);
    _data[i] = (byte) durationType;
    ++i;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

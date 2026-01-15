package software.sava.idl.clients.oracles.switchboard.on_demand.gen.types;

import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.core.encoding.ByteUtil.getFloat32LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putFloat32LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

/// @param stdDev The standard deviation of the submissions needed for quorom size
/// @param mean The mean of the submissions needed for quorom size
/// @param slot The slot at which this value was signed.
public record CompactResult(float stdDev,
                            float mean,
                            long slot) implements SerDe {

  public static final int BYTES = 16;

  public static final int STD_DEV_OFFSET = 0;
  public static final int MEAN_OFFSET = 4;
  public static final int SLOT_OFFSET = 8;

  public static CompactResult read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var stdDev = getFloat32LE(_data, i);
    i += 4;
    final var mean = getFloat32LE(_data, i);
    i += 4;
    final var slot = getInt64LE(_data, i);
    return new CompactResult(stdDev, mean, slot);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putFloat32LE(_data, i, stdDev);
    i += 4;
    putFloat32LE(_data, i, mean);
    i += 4;
    putInt64LE(_data, i, slot);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

package software.sava.idl.clients.switchboard.on_demand.gen.types;

import software.sava.core.borsh.Borsh;

import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

public record RandomnessInitParams(long recentSlot) implements Borsh {

  public static final int BYTES = 8;

  public static RandomnessInitParams read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var recentSlot = getInt64LE(_data, _offset);
    return new RandomnessInitParams(recentSlot);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt64LE(_data, i, recentSlot);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

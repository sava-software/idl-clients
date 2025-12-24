package software.sava.idl.clients.oracles.switchboard.on_demand.gen.types;

import java.math.BigInteger;

import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

public record PullFeedSubmitResponseConsensusParams(long slot, BigInteger[] values) implements SerDe {

  public static PullFeedSubmitResponseConsensusParams read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var slot = getInt64LE(_data, i);
    i += 8;
    final var values = SerDeUtil.read128Vector(4, _data, i);
    return new PullFeedSubmitResponseConsensusParams(slot, values);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt64LE(_data, i, slot);
    i += 8;
    i += SerDeUtil.write128Vector(4, values, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return 8 + SerDeUtil.len128Vector(4, values);
  }
}

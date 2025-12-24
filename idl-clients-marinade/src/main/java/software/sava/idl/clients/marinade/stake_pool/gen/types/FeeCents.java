package software.sava.idl.clients.marinade.stake_pool.gen.types;

import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;

/// FeeCents, same as Fee but / 1_000_000 instead of 10_000
/// 1 FeeCent = 0.0001%, 10_000 FeeCent = 1%, 1_000_000 FeeCent = 100%
///
public record FeeCents(int bpCents) implements SerDe {

  public static final int BYTES = 4;

  public static FeeCents read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var bpCents = getInt32LE(_data, _offset);
    return new FeeCents(bpCents);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt32LE(_data, i, bpCents);
    i += 4;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

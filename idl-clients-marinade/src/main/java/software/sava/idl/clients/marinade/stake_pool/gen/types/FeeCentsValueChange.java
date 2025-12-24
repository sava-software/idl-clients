package software.sava.idl.clients.marinade.stake_pool.gen.types;

import software.sava.idl.clients.core.gen.SerDe;

public record FeeCentsValueChange(FeeCents old, FeeCents _new) implements SerDe {

  public static final int BYTES = 8;

  public static FeeCentsValueChange read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var old = FeeCents.read(_data, i);
    i += old.l();
    final var _new = FeeCents.read(_data, i);
    return new FeeCentsValueChange(old, _new);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += old.write(_data, i);
    i += _new.write(_data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

package software.sava.idl.clients.kamino.scope.gen.types;

import software.sava.core.borsh.Borsh;

public record V8V10(MarketStatusBehavior marketStatusBehavior) implements Borsh {

  public static final int BYTES = 1;

  public static V8V10 read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var marketStatusBehavior = MarketStatusBehavior.read(_data, _offset);
    return new V8V10(marketStatusBehavior);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += Borsh.write(marketStatusBehavior, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

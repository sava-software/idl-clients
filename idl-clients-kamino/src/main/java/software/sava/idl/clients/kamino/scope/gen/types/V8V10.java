package software.sava.idl.clients.kamino.scope.gen.types;

import software.sava.idl.clients.core.gen.SerDe;

public record V8V10(MarketStatusBehavior marketStatusBehavior) implements SerDe {

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
    i += marketStatusBehavior.write(_data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

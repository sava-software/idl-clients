package software.sava.idl.clients.jupiter.swap.gen.types;

import software.sava.core.borsh.Borsh;

public record RoutePlanStep(Swap swap,
                            int percent,
                            int inputIndex,
                            int outputIndex) implements Borsh {

  public static RoutePlanStep read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var swap = Swap.read(_data, i);
    i += swap.l();
    final var percent = _data[i] & 0xFF;
    ++i;
    final var inputIndex = _data[i] & 0xFF;
    ++i;
    final var outputIndex = _data[i] & 0xFF;
    return new RoutePlanStep(swap,
                             percent,
                             inputIndex,
                             outputIndex);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += swap.write(_data, i);
    _data[i] = (byte) percent;
    ++i;
    _data[i] = (byte) inputIndex;
    ++i;
    _data[i] = (byte) outputIndex;
    ++i;
    return i - _offset;
  }

  @Override
  public int l() {
    return swap.l() + 1 + 1 + 1;
  }
}

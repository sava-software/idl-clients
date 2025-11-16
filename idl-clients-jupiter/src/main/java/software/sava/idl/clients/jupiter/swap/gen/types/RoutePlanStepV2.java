package software.sava.idl.clients.jupiter.swap.gen.types;

import software.sava.core.borsh.Borsh;

import static software.sava.core.encoding.ByteUtil.getInt16LE;
import static software.sava.core.encoding.ByteUtil.putInt16LE;

public record RoutePlanStepV2(Swap swap,
                              int bps,
                              int inputIndex,
                              int outputIndex) implements Borsh {

  public static RoutePlanStepV2 read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var swap = Swap.read(_data, i);
    i += swap.l();
    final var bps = getInt16LE(_data, i);
    i += 2;
    final var inputIndex = _data[i] & 0xFF;
    ++i;
    final var outputIndex = _data[i] & 0xFF;
    return new RoutePlanStepV2(swap,
                               bps,
                               inputIndex,
                               outputIndex);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += swap.write(_data, i);
    putInt16LE(_data, i, bps);
    i += 2;
    _data[i] = (byte) inputIndex;
    ++i;
    _data[i] = (byte) outputIndex;
    ++i;
    return i - _offset;
  }

  @Override
  public int l() {
    return swap.l() + 2 + 1 + 1;
  }
}

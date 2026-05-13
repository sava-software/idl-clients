package software.sava.idl.clients.phoenix.dev.perpetuals.gen.types;

import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

public record TickRegionParams(Ticks startOffset,
                               Ticks endOffset,
                               BaseLotsPerTick density,
                               long lifespan) implements SerDe {

  public static final int BYTES = 32;

  public static final int START_OFFSET_OFFSET = 0;
  public static final int END_OFFSET_OFFSET = 8;
  public static final int DENSITY_OFFSET = 16;
  public static final int LIFESPAN_OFFSET = 24;

  public static TickRegionParams read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var startOffset = Ticks.read(_data, i);
    i += startOffset.l();
    final var endOffset = Ticks.read(_data, i);
    i += endOffset.l();
    final var density = BaseLotsPerTick.read(_data, i);
    i += density.l();
    final var lifespan = getInt64LE(_data, i);
    return new TickRegionParams(startOffset,
                                endOffset,
                                density,
                                lifespan);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += startOffset.write(_data, i);
    i += endOffset.write(_data, i);
    i += density.write(_data, i);
    putInt64LE(_data, i, lifespan);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

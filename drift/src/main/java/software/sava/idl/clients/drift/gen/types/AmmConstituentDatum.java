package software.sava.idl.clients.drift.gen.types;

import software.sava.core.borsh.Borsh;

import static software.sava.core.encoding.ByteUtil.getInt16LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt16LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

/// @param weight PERCENTAGE_PRECISION. The weight this constituent has on the perp market
public record AmmConstituentDatum(int perpMarketIndex,
                                  int constituentIndex,
                                  byte[] padding,
                                  long lastSlot,
                                  long weight) implements Borsh {

  public static final int BYTES = 24;
  public static final int PADDING_LEN = 4;

  public static AmmConstituentDatum read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var perpMarketIndex = getInt16LE(_data, i);
    i += 2;
    final var constituentIndex = getInt16LE(_data, i);
    i += 2;
    final var padding = new byte[4];
    i += Borsh.readArray(padding, _data, i);
    final var lastSlot = getInt64LE(_data, i);
    i += 8;
    final var weight = getInt64LE(_data, i);
    return new AmmConstituentDatum(perpMarketIndex,
                                   constituentIndex,
                                   padding,
                                   lastSlot,
                                   weight);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt16LE(_data, i, perpMarketIndex);
    i += 2;
    putInt16LE(_data, i, constituentIndex);
    i += 2;
    i += Borsh.writeArrayChecked(padding, 4, _data, i);
    putInt64LE(_data, i, lastSlot);
    i += 8;
    putInt64LE(_data, i, weight);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

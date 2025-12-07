package software.sava.idl.clients.meteora.dlmm.gen.types;

import java.util.OptionalInt;

import software.sava.core.borsh.Borsh;

import static software.sava.core.encoding.ByteUtil.getInt16LE;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt16LE;

public record RemoveLiquidityParams(OptionalInt minBinId,
                                    OptionalInt maxBinId,
                                    int bps,
                                    byte[] padding) implements Borsh {

  public static final int PADDING_LEN = 16;
  public static RemoveLiquidityParams read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final OptionalInt minBinId;
    if (_data[i] == 0) {
      minBinId = OptionalInt.empty();
      ++i;
    } else {
      ++i;
      minBinId = OptionalInt.of(getInt32LE(_data, i));
      i += 4;
    }
    final OptionalInt maxBinId;
    if (_data[i] == 0) {
      maxBinId = OptionalInt.empty();
      ++i;
    } else {
      ++i;
      maxBinId = OptionalInt.of(getInt32LE(_data, i));
      i += 4;
    }
    final var bps = getInt16LE(_data, i);
    i += 2;
    final var padding = new byte[16];
    Borsh.readArray(padding, _data, i);
    return new RemoveLiquidityParams(minBinId,
                                     maxBinId,
                                     bps,
                                     padding);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += Borsh.writeOptional(minBinId, _data, i);
    i += Borsh.writeOptional(maxBinId, _data, i);
    putInt16LE(_data, i, bps);
    i += 2;
    i += Borsh.writeArrayChecked(padding, 16, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return (minBinId == null || minBinId.isEmpty() ? 1 : (1 + 4)) + (maxBinId == null || maxBinId.isEmpty() ? 1 : (1 + 4)) + 2 + Borsh.lenArray(padding);
  }
}

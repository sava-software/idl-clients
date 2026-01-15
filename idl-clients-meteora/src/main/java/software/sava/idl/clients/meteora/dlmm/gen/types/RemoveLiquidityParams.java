package software.sava.idl.clients.meteora.dlmm.gen.types;

import java.util.OptionalInt;

import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.encoding.ByteUtil.getInt16LE;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt16LE;

public record RemoveLiquidityParams(OptionalInt minBinId,
                                    OptionalInt maxBinId,
                                    int bps,
                                    byte[] padding) implements SerDe {

  public static final int PADDING_LEN = 16;
  public static final int MIN_BIN_ID_OFFSET = 1;

  public static RemoveLiquidityParams read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final OptionalInt minBinId;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      minBinId = OptionalInt.empty();
      ++i;
    } else {
      ++i;
      minBinId = OptionalInt.of(getInt32LE(_data, i));
      i += 4;
    }
    final OptionalInt maxBinId;
    if (SerDeUtil.isAbsent(1, _data, i)) {
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
    SerDeUtil.readArray(padding, _data, i);
    return new RemoveLiquidityParams(minBinId,
                                     maxBinId,
                                     bps,
                                     padding);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += SerDeUtil.writeOptional(1, minBinId, _data, i);
    i += SerDeUtil.writeOptional(1, maxBinId, _data, i);
    putInt16LE(_data, i, bps);
    i += 2;
    i += SerDeUtil.writeArrayChecked(padding, 16, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return (minBinId == null || minBinId.isEmpty() ? 1 : (1 + 4)) + (maxBinId == null || maxBinId.isEmpty() ? 1 : (1 + 4)) + 2 + SerDeUtil.lenArray(padding);
  }
}

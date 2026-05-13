package software.sava.idl.clients.meteora.dlmm.gen.types;

import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

public record PlaceLimitOrderParams(boolean isAskSide,
                                    byte[] padding,
                                    RelativeBin relativeBin,
                                    BinLimitOrderAmount[] bins) implements SerDe {

  public static final int PADDING_LEN = 16;
  public static final int IS_ASK_SIDE_OFFSET = 0;
  public static final int PADDING_OFFSET = 1;
  public static final int RELATIVE_BIN_OFFSET = 18;

  public static PlaceLimitOrderParams read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var isAskSide = _data[i] == 1;
    ++i;
    final var padding = new byte[16];
    i += SerDeUtil.readArray(padding, _data, i);
    final RelativeBin relativeBin;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      relativeBin = null;
      ++i;
    } else {
      ++i;
      relativeBin = RelativeBin.read(_data, i);
      i += relativeBin.l();
    }
    final var bins = SerDeUtil.readVector(4, BinLimitOrderAmount.class, BinLimitOrderAmount::read, _data, i);
    return new PlaceLimitOrderParams(isAskSide,
                                     padding,
                                     relativeBin,
                                     bins);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    _data[i] = (byte) (isAskSide ? 1 : 0);
    ++i;
    i += SerDeUtil.writeArrayChecked(padding, 16, _data, i);
    i += SerDeUtil.writeOptional(1, relativeBin, _data, i);
    i += SerDeUtil.writeVector(4, bins, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return 1 + SerDeUtil.lenArray(padding) + (relativeBin == null ? 1 : (1 + relativeBin.l())) + SerDeUtil.lenVector(4, bins);
  }
}

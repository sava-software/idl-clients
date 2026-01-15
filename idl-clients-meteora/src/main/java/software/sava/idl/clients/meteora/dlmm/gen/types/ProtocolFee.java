package software.sava.idl.clients.meteora.dlmm.gen.types;

import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

public record ProtocolFee(long amountX, long amountY) implements SerDe {

  public static final int BYTES = 16;

  public static final int AMOUNT_X_OFFSET = 0;
  public static final int AMOUNT_Y_OFFSET = 8;

  public static ProtocolFee read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var amountX = getInt64LE(_data, i);
    i += 8;
    final var amountY = getInt64LE(_data, i);
    return new ProtocolFee(amountX, amountY);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt64LE(_data, i, amountX);
    i += 8;
    putInt64LE(_data, i, amountY);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

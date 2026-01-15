package software.sava.idl.clients.meteora.dlmm.gen.types;

import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;

public record CompressedBinDepositAmount(int binId, int amount) implements SerDe {

  public static final int BYTES = 8;

  public static final int BIN_ID_OFFSET = 0;
  public static final int AMOUNT_OFFSET = 4;

  public static CompressedBinDepositAmount read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var binId = getInt32LE(_data, i);
    i += 4;
    final var amount = getInt32LE(_data, i);
    return new CompressedBinDepositAmount(binId, amount);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt32LE(_data, i, binId);
    i += 4;
    putInt32LE(_data, i, amount);
    i += 4;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

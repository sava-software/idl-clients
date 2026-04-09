package software.sava.idl.clients.kamino.vaults.gen.types;

import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

public record ReserveFees(long originationFeeSf,
                          long flashLoanFeeSf,
                          byte[] padding) implements SerDe {

  public static final int BYTES = 24;
  public static final int PADDING_LEN = 8;

  public static final int ORIGINATION_FEE_SF_OFFSET = 0;
  public static final int FLASH_LOAN_FEE_SF_OFFSET = 8;
  public static final int PADDING_OFFSET = 16;

  public static ReserveFees read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var originationFeeSf = getInt64LE(_data, i);
    i += 8;
    final var flashLoanFeeSf = getInt64LE(_data, i);
    i += 8;
    final var padding = new byte[8];
    SerDeUtil.readArray(padding, _data, i);
    return new ReserveFees(originationFeeSf, flashLoanFeeSf, padding);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt64LE(_data, i, originationFeeSf);
    i += 8;
    putInt64LE(_data, i, flashLoanFeeSf);
    i += 8;
    i += SerDeUtil.writeArrayChecked(padding, 8, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

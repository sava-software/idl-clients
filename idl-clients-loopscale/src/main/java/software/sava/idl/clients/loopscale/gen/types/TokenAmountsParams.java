package software.sava.idl.clients.loopscale.gen.types;

import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

public record TokenAmountsParams(long tokenAAmount, long tokenBAmount) implements SerDe {

  public static final int BYTES = 16;

  public static final int TOKEN_A_AMOUNT_OFFSET = 0;
  public static final int TOKEN_B_AMOUNT_OFFSET = 8;

  public static TokenAmountsParams read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var tokenAAmount = getInt64LE(_data, i);
    i += 8;
    final var tokenBAmount = getInt64LE(_data, i);
    return new TokenAmountsParams(tokenAAmount, tokenBAmount);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt64LE(_data, i, tokenAAmount);
    i += 8;
    putInt64LE(_data, i, tokenBAmount);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

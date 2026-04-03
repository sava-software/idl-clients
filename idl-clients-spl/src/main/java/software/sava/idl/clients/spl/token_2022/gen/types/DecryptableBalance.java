package software.sava.idl.clients.spl.token_2022.gen.types;

import java.util.Arrays;

import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.idl.clients.core.gen.SerDeUtil.checkMaxLength;

/// Authenticated encryption containing an account balance.
///
/// @param val Authenticated encryption containing an account balance.
public record DecryptableBalance(byte[] val) implements SerDe {

  public static final int BYTES = 36;
  public static final int VAL_SIZE = 36;

  public static final int VAL_OFFSET = 0;

  public static DecryptableBalance read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final byte[] val = new byte[36];
    System.arraycopy(_data, _offset, val, 0, val.length);

    return new DecryptableBalance(checkMaxLength(val, 36));
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    if (val.length > 36) {
      throw new IllegalStateException(String.format(
          "Encoded [length=%d] of [val=%s] cannot be greater than 36.",
          val.length, Arrays.toString(val)
      ));
    }
    System.arraycopy(val, 0, _data, i, val.length);
    i += 36;

    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

package software.sava.idl.clients.spl.token_2022.gen.types;

import java.util.Arrays;

import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.idl.clients.core.gen.SerDeUtil.checkMaxLength;

/// ElGamal ciphertext containing an account balance.
///
/// @param val ElGamal ciphertext containing an account balance.
public record EncryptedBalance(byte[] val) implements SerDe {

  public static final int BYTES = 64;
  public static final int VAL_SIZE = 64;

  public static final int VAL_OFFSET = 0;

  public static EncryptedBalance read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final byte[] val = new byte[64];
    System.arraycopy(_data, _offset, val, 0, val.length);

    return new EncryptedBalance(checkMaxLength(val, 64));
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    if (val.length > 64) {
      throw new IllegalStateException(String.format(
          "Encoded [length=%d] of [val=%s] cannot be greater than 64.",
          val.length, Arrays.toString(val)
      ));
    }
    System.arraycopy(val, 0, _data, i, val.length);
    i += 64;

    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

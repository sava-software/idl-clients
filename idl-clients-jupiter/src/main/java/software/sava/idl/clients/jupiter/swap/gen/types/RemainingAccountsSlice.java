package software.sava.idl.clients.jupiter.swap.gen.types;

import software.sava.idl.clients.core.gen.SerDe;

public record RemainingAccountsSlice(int accountsType, int length) implements SerDe {

  public static final int BYTES = 2;

  public static final int ACCOUNTS_TYPE_OFFSET = 0;
  public static final int LENGTH_OFFSET = 1;

  public static RemainingAccountsSlice read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var accountsType = _data[i] & 0xFF;
    ++i;
    final var length = _data[i] & 0xFF;
    return new RemainingAccountsSlice(accountsType, length);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    _data[i] = (byte) accountsType;
    ++i;
    _data[i] = (byte) length;
    ++i;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

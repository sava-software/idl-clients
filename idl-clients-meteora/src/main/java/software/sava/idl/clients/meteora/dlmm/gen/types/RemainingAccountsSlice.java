package software.sava.idl.clients.meteora.dlmm.gen.types;

import software.sava.idl.clients.core.gen.SerDe;

public record RemainingAccountsSlice(AccountsType accountsType, int length) implements SerDe {

  public static RemainingAccountsSlice read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var accountsType = AccountsType.read(_data, i);
    i += accountsType.l();
    final var length = _data[i] & 0xFF;
    return new RemainingAccountsSlice(accountsType, length);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += accountsType.write(_data, i);
    _data[i] = (byte) length;
    ++i;
    return i - _offset;
  }

  @Override
  public int l() {
    return accountsType.l() + 1;
  }
}

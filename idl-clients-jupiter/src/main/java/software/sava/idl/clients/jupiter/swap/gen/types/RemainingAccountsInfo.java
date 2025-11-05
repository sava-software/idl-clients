package software.sava.idl.clients.jupiter.swap.gen.types;

import software.sava.core.borsh.Borsh;

public record RemainingAccountsInfo(RemainingAccountsSlice[] slices) implements Borsh {

  public static RemainingAccountsInfo read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var slices = Borsh.readVector(RemainingAccountsSlice.class, RemainingAccountsSlice::read, _data, _offset);
    return new RemainingAccountsInfo(slices);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += Borsh.writeVector(slices, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return Borsh.lenVector(slices);
  }
}

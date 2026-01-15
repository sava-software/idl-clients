package software.sava.idl.clients.jupiter.lend_borrow.gen.types;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.core.accounts.PublicKey.readPubKey;

public record AddressBool(PublicKey addr, boolean value) implements SerDe {

  public static final int BYTES = 33;

  public static final int ADDR_OFFSET = 0;
  public static final int VALUE_OFFSET = 32;

  public static AddressBool read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var addr = readPubKey(_data, i);
    i += 32;
    final var value = _data[i] == 1;
    return new AddressBool(addr, value);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    addr.write(_data, i);
    i += 32;
    _data[i] = (byte) (value ? 1 : 0);
    ++i;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

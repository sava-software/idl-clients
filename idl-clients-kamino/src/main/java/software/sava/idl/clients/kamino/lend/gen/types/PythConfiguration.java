package software.sava.idl.clients.kamino.lend.gen.types;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.core.accounts.PublicKey.readPubKey;

/// @param price Pubkey of the base price feed (disabled if `null` or `default`)
public record PythConfiguration(PublicKey price) implements SerDe {

  public static final int BYTES = 32;

  public static final int PRICE_OFFSET = 0;

  public static PythConfiguration read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var price = readPubKey(_data, _offset);
    return new PythConfiguration(price);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    price.write(_data, i);
    i += 32;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

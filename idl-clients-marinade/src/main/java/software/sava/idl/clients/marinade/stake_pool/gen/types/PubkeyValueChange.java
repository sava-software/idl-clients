package software.sava.idl.clients.marinade.stake_pool.gen.types;

import software.sava.core.accounts.PublicKey;
import software.sava.core.borsh.Borsh;

import static software.sava.core.accounts.PublicKey.readPubKey;

public record PubkeyValueChange(PublicKey old, PublicKey _new) implements Borsh {

  public static final int BYTES = 64;

  public static PubkeyValueChange read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var old = readPubKey(_data, i);
    i += 32;
    final var _new = readPubKey(_data, i);
    return new PubkeyValueChange(old, _new);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    old.write(_data, i);
    i += 32;
    _new.write(_data, i);
    i += 32;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

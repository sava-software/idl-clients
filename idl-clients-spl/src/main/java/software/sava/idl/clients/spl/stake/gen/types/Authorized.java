package software.sava.idl.clients.spl.stake.gen.types;

import software.sava.core.accounts.PublicKey;
import software.sava.core.borsh.Borsh;

import static software.sava.core.accounts.PublicKey.readPubKey;

public record Authorized(PublicKey staker, PublicKey withdrawer) implements Borsh {

  public static final int BYTES = 64;

  public static Authorized read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var staker = readPubKey(_data, i);
    i += 32;
    final var withdrawer = readPubKey(_data, i);
    return new Authorized(staker, withdrawer);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    staker.write(_data, i);
    i += 32;
    withdrawer.write(_data, i);
    i += 32;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

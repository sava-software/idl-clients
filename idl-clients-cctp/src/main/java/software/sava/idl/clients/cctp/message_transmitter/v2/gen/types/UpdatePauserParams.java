package software.sava.idl.clients.cctp.message_transmitter.v2.gen.types;

import software.sava.core.accounts.PublicKey;
import software.sava.core.borsh.Borsh;

import static software.sava.core.accounts.PublicKey.readPubKey;

public record UpdatePauserParams(PublicKey newPauser) implements Borsh {

  public static final int BYTES = 32;

  public static UpdatePauserParams read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var newPauser = readPubKey(_data, _offset);
    return new UpdatePauserParams(newPauser);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    newPauser.write(_data, i);
    i += 32;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

package software.sava.idl.clients.cctp.message_transmitter.v2.gen.types;

import software.sava.core.accounts.PublicKey;
import software.sava.core.borsh.Borsh;

import static software.sava.core.accounts.PublicKey.readPubKey;

public record EnableAttesterParams(PublicKey newAttester) implements Borsh {

  public static final int BYTES = 32;

  public static EnableAttesterParams read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var newAttester = readPubKey(_data, _offset);
    return new EnableAttesterParams(newAttester);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    newAttester.write(_data, i);
    i += 32;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

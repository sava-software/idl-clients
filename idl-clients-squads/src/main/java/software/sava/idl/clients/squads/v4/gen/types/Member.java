package software.sava.idl.clients.squads.v4.gen.types;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.core.accounts.PublicKey.readPubKey;

public record Member(PublicKey key, Permissions permissions) implements SerDe {

  public static final int BYTES = 33;

  public static final int KEY_OFFSET = 0;
  public static final int PERMISSIONS_OFFSET = 32;

  public static Member read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var key = readPubKey(_data, i);
    i += 32;
    final var permissions = Permissions.read(_data, i);
    return new Member(key, permissions);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    key.write(_data, i);
    i += 32;
    i += permissions.write(_data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

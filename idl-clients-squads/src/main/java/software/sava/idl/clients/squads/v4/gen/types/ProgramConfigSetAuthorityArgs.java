package software.sava.idl.clients.squads.v4.gen.types;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.core.accounts.PublicKey.readPubKey;

public record ProgramConfigSetAuthorityArgs(PublicKey newAuthority) implements SerDe {

  public static final int BYTES = 32;

  public static final int NEW_AUTHORITY_OFFSET = 0;

  public static ProgramConfigSetAuthorityArgs read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var newAuthority = readPubKey(_data, _offset);
    return new ProgramConfigSetAuthorityArgs(newAuthority);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    newAuthority.write(_data, i);
    i += 32;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

package software.sava.idl.clients.metaplex.token.metadata.gen.types;

import software.sava.core.borsh.Borsh;

import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

public record MintNewEditionFromMasterEditionViaTokenArgs(long edition) implements Borsh {

  public static final int BYTES = 8;

  public static MintNewEditionFromMasterEditionViaTokenArgs read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var edition = getInt64LE(_data, _offset);
    return new MintNewEditionFromMasterEditionViaTokenArgs(edition);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt64LE(_data, i, edition);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

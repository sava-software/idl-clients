package software.sava.idl.clients.metaplex.token.metadata.gen.types;

import software.sava.core.borsh.Borsh;
import software.sava.core.borsh.RustEnum;

import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

public sealed interface MintArgs extends RustEnum permits
  MintArgs.V1 {

  static MintArgs read(final byte[] _data, final int _offset) {
    final int ordinal = _data[_offset] & 0xFF;
    final int i = _offset + 1;
    return switch (ordinal) {
      case 0 -> V1.read(_data, i);
      default -> null;
    };
  }

  record V1(long amount, AuthorizationData authorizationData) implements MintArgs {

    public static V1 read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      int i = _offset;
      final var amount = getInt64LE(_data, i);
      i += 8;
      final AuthorizationData authorizationData;
      if (_data[i] == 0) {
        authorizationData = null;
      } else {
        ++i;
        authorizationData = AuthorizationData.read(_data, i);
      }
      return new V1(amount, authorizationData);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = writeOrdinal(_data, _offset);
      putInt64LE(_data, i, amount);
      i += 8;
      i += Borsh.writeOptional(authorizationData, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 1 + 8 + (authorizationData == null ? 1 : (1 + authorizationData.l()));
    }

    @Override
    public int ordinal() {
      return 0;
    }
  }
}

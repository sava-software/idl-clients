package software.sava.idl.clients.metaplex.token.metadata.gen.types;

import software.sava.core.accounts.PublicKey;
import software.sava.core.borsh.RustEnum;

import static software.sava.core.accounts.PublicKey.readPubKey;

public sealed interface ProgrammableConfig extends RustEnum permits
  ProgrammableConfig.V1 {

  static ProgrammableConfig read(final byte[] _data, final int _offset) {
    final int ordinal = _data[_offset] & 0xFF;
    final int i = _offset + 1;
    return switch (ordinal) {
      case 0 -> V1.read(_data, i);
      default -> null;
    };
  }

  record V1(PublicKey val) implements OptionalEnumPublicKey, ProgrammableConfig {

    public static V1 read(final byte[] _data, int i) {
      return new V1(_data[i++] == 0 ? null : readPubKey(_data, i));
    }

    @Override
    public int ordinal() {
      return 0;
    }
  }
}

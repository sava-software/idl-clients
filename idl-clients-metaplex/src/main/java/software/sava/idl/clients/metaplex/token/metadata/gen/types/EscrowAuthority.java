package software.sava.idl.clients.metaplex.token.metadata.gen.types;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.core.gen.RustEnum;

import static software.sava.core.accounts.PublicKey.readPubKey;

public sealed interface EscrowAuthority extends RustEnum permits
  EscrowAuthority.TokenOwner,
  EscrowAuthority.Creator {

  static EscrowAuthority read(final byte[] _data, final int _offset) {
    final int ordinal = _data[_offset] & 0xFF;
    final int i = _offset + 1;
    return switch (ordinal) {
      case 0 -> TokenOwner.INSTANCE;
      case 1 -> Creator.read(_data, i);
      default -> null;
    };
  }

  record TokenOwner() implements EnumNone, EscrowAuthority {

    public static final TokenOwner INSTANCE = new TokenOwner();

    @Override
    public int ordinal() {
      return 0;
    }
  }

  record Creator(PublicKey val) implements EnumPublicKey, EscrowAuthority {

    public static Creator read(final byte[] _data, int i) {
      return new Creator(readPubKey(_data, i));
    }

    @Override
    public int ordinal() {
      return 1;
    }
  }
}

package software.sava.idl.clients.metaplex.token.metadata.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;

public sealed interface LockArgs extends RustEnum permits
  LockArgs.V1 {

  static LockArgs read(final byte[] _data, final int _offset) {
    final int ordinal = _data[_offset] & 0xFF;
    final int i = _offset + 1;
    return switch (ordinal) {
      case 0 -> V1.read(_data, i);
      default -> null;
    };
  }

  record V1(AuthorizationData val) implements SerDeEnum, LockArgs {

    public static V1 read(final byte[] _data, final int _offset) {
      return new V1(AuthorizationData.read(_data, _offset));
    }

    @Override
    public int ordinal() {
      return 0;
    }
  }
}

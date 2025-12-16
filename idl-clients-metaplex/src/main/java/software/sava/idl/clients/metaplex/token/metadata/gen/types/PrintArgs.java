package software.sava.idl.clients.metaplex.token.metadata.gen.types;

import software.sava.core.borsh.RustEnum;

import static software.sava.core.encoding.ByteUtil.getInt64LE;

public sealed interface PrintArgs extends RustEnum permits
  PrintArgs.V1,
  PrintArgs.V2 {

  static PrintArgs read(final byte[] _data, final int _offset) {
    final int ordinal = _data[_offset] & 0xFF;
    final int i = _offset + 1;
    return switch (ordinal) {
      case 0 -> V1.read(_data, i);
      case 1 -> V2.read(_data, i);
      default -> null;
    };
  }

  record V1(long val) implements EnumInt64, PrintArgs {

    public static V1 read(final byte[] _data, int i) {
      return new V1(getInt64LE(_data, i));
    }

    @Override
    public int ordinal() {
      return 0;
    }
  }

  record V2(long val) implements EnumInt64, PrintArgs {

    public static V2 read(final byte[] _data, int i) {
      return new V2(getInt64LE(_data, i));
    }

    @Override
    public int ordinal() {
      return 1;
    }
  }
}

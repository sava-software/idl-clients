package software.sava.idl.clients.metaplex.token.metadata.gen.types;

import software.sava.core.borsh.RustEnum;

import static software.sava.core.encoding.ByteUtil.getInt64LE;

public sealed interface PrintSupply extends RustEnum permits
  PrintSupply.Zero,
  PrintSupply.Limited,
  PrintSupply.Unlimited {

  static PrintSupply read(final byte[] _data, final int _offset) {
    final int ordinal = _data[_offset] & 0xFF;
    final int i = _offset + 1;
    return switch (ordinal) {
      case 0 -> Zero.INSTANCE;
      case 1 -> Limited.read(_data, i);
      case 2 -> Unlimited.INSTANCE;
      default -> null;
    };
  }

  record Zero() implements EnumNone, PrintSupply {

    public static final Zero INSTANCE = new Zero();

    @Override
    public int ordinal() {
      return 0;
    }
  }

  record Limited(long val) implements EnumInt64, PrintSupply {

    public static Limited read(final byte[] _data, int i) {
      return new Limited(getInt64LE(_data, i));
    }

    @Override
    public int ordinal() {
      return 1;
    }
  }

  record Unlimited() implements EnumNone, PrintSupply {

    public static final Unlimited INSTANCE = new Unlimited();

    @Override
    public int ordinal() {
      return 2;
    }
  }
}

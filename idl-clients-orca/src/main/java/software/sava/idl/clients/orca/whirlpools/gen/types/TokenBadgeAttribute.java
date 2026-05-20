package software.sava.idl.clients.orca.whirlpools.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;

public sealed interface TokenBadgeAttribute extends RustEnum permits
  TokenBadgeAttribute.RequireNonTransferablePosition {

  static TokenBadgeAttribute read(final byte[] _data, final int _offset) {
    final int ordinal = _data[_offset] & 0xFF;
    final int i = _offset + 1;
    return switch (ordinal) {
      case 0 -> RequireNonTransferablePosition.read(_data, i);
      default -> null;
    };
  }

  record RequireNonTransferablePosition(boolean val) implements EnumBool, TokenBadgeAttribute {

    public static final RequireNonTransferablePosition TRUE = new RequireNonTransferablePosition(true);
    public static final RequireNonTransferablePosition FALSE = new RequireNonTransferablePosition(false);

    public static RequireNonTransferablePosition read(final byte[] _data, int i) {
      return _data[i] == 1 ? RequireNonTransferablePosition.TRUE : RequireNonTransferablePosition.FALSE;
    }

    @Override
    public int ordinal() {
      return 0;
    }
  }
}

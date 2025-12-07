package software.sava.idl.clients.metaplex.token.metadata.gen.types;

import software.sava.core.borsh.RustEnum;

public sealed interface UsesToggle extends RustEnum permits
  UsesToggle.None,
  UsesToggle.Clear,
  UsesToggle.Set {

  static UsesToggle read(final byte[] _data, final int _offset) {
    final int ordinal = _data[_offset] & 0xFF;
    final int i = _offset + 1;
    return switch (ordinal) {
      case 0 -> None.INSTANCE;
      case 1 -> Clear.INSTANCE;
      case 2 -> Set.read(_data, i);
      default -> throw new IllegalStateException(java.lang.String.format(
          "Unexpected ordinal [%d] for enum [UsesToggle]", ordinal
      ));
    };
  }

  record None() implements EnumNone, UsesToggle {

    public static final None INSTANCE = new None();

    @Override
    public int ordinal() {
      return 0;
    }
  }

  record Clear() implements EnumNone, UsesToggle {

    public static final Clear INSTANCE = new Clear();

    @Override
    public int ordinal() {
      return 1;
    }
  }

  record Set(Uses val) implements BorshEnum, UsesToggle {

    public static Set read(final byte[] _data, final int _offset) {
      return new Set(Uses.read(_data, _offset));
    }

    @Override
    public int ordinal() {
      return 2;
    }
  }
}

package software.sava.idl.clients.metaplex.token.metadata.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;

public sealed interface CollectionDetailsToggle extends RustEnum permits
  CollectionDetailsToggle.None,
  CollectionDetailsToggle.Clear,
  CollectionDetailsToggle.Set {

  static CollectionDetailsToggle read(final byte[] _data, final int _offset) {
    final int ordinal = _data[_offset] & 0xFF;
    final int i = _offset + 1;
    return switch (ordinal) {
      case 0 -> None.INSTANCE;
      case 1 -> Clear.INSTANCE;
      case 2 -> Set.read(_data, i);
      default -> null;
    };
  }

  record None() implements EnumNone, CollectionDetailsToggle {

    public static final None INSTANCE = new None();

    @Override
    public int ordinal() {
      return 0;
    }
  }

  record Clear() implements EnumNone, CollectionDetailsToggle {

    public static final Clear INSTANCE = new Clear();

    @Override
    public int ordinal() {
      return 1;
    }
  }

  record Set(CollectionDetails val) implements SerDeEnum, CollectionDetailsToggle {

    public static Set read(final byte[] _data, final int _offset) {
      return new Set(CollectionDetails.read(_data, _offset));
    }

    @Override
    public int ordinal() {
      return 2;
    }
  }
}

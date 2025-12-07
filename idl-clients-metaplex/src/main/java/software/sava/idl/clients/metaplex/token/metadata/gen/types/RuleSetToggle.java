package software.sava.idl.clients.metaplex.token.metadata.gen.types;

import software.sava.core.accounts.PublicKey;
import software.sava.core.borsh.RustEnum;

import static software.sava.core.accounts.PublicKey.readPubKey;

public sealed interface RuleSetToggle extends RustEnum permits
  RuleSetToggle.None,
  RuleSetToggle.Clear,
  RuleSetToggle.Set {

  static RuleSetToggle read(final byte[] _data, final int _offset) {
    final int ordinal = _data[_offset] & 0xFF;
    final int i = _offset + 1;
    return switch (ordinal) {
      case 0 -> None.INSTANCE;
      case 1 -> Clear.INSTANCE;
      case 2 -> Set.read(_data, i);
      default -> throw new IllegalStateException(java.lang.String.format(
          "Unexpected ordinal [%d] for enum [RuleSetToggle]", ordinal
      ));
    };
  }

  record None() implements EnumNone, RuleSetToggle {

    public static final None INSTANCE = new None();

    @Override
    public int ordinal() {
      return 0;
    }
  }

  record Clear() implements EnumNone, RuleSetToggle {

    public static final Clear INSTANCE = new Clear();

    @Override
    public int ordinal() {
      return 1;
    }
  }

  record Set(PublicKey val) implements EnumPublicKey, RuleSetToggle {

    public static Set read(final byte[] _data, int i) {
      return new Set(readPubKey(_data, i));
    }

    @Override
    public int ordinal() {
      return 2;
    }
  }
}

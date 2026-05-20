package software.sava.idl.clients.orca.whirlpools.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;

public sealed interface DynamicTick extends RustEnum permits
  DynamicTick.Uninitialized,
  DynamicTick.Initialized {

  static DynamicTick read(final byte[] _data, final int _offset) {
    final int ordinal = _data[_offset] & 0xFF;
    final int i = _offset + 1;
    return switch (ordinal) {
      case 0 -> Uninitialized.INSTANCE;
      case 1 -> Initialized.read(_data, i);
      default -> null;
    };
  }

  record Uninitialized() implements EnumNone, DynamicTick {

    public static final Uninitialized INSTANCE = new Uninitialized();

    @Override
    public int ordinal() {
      return 0;
    }
  }

  record Initialized(DynamicTickData val) implements SerDeEnum, DynamicTick {

    public static Initialized read(final byte[] _data, final int _offset) {
      return new Initialized(DynamicTickData.read(_data, _offset));
    }

    @Override
    public int ordinal() {
      return 1;
    }
  }
}

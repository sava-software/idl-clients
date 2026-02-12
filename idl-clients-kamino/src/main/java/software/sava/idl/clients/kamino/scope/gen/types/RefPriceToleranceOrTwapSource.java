package software.sava.idl.clients.kamino.scope.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;

import static software.sava.core.encoding.ByteUtil.getInt16LE;

public sealed interface RefPriceToleranceOrTwapSource extends RustEnum permits
  RefPriceToleranceOrTwapSource.None,
  RefPriceToleranceOrTwapSource.RefPriceToleranceBps,
  RefPriceToleranceOrTwapSource.TwapSource {

  static RefPriceToleranceOrTwapSource read(final byte[] _data, final int _offset) {
    final int ordinal = _data[_offset] & 0xFF;
    final int i = _offset + 1;
    return switch (ordinal) {
      case 0 -> None.INSTANCE;
      case 1 -> RefPriceToleranceBps.read(_data, i);
      case 2 -> TwapSource.read(_data, i);
      default -> null;
    };
  }

  record None() implements EnumNone, RefPriceToleranceOrTwapSource {

    public static final None INSTANCE = new None();

    @Override
    public int ordinal() {
      return 0;
    }
  }

  record RefPriceToleranceBps(int val) implements EnumInt16, RefPriceToleranceOrTwapSource {

    public static RefPriceToleranceBps read(final byte[] _data, int i) {
      return new RefPriceToleranceBps(getInt16LE(_data, i));
    }

    @Override
    public int ordinal() {
      return 1;
    }
  }

  record TwapSource(int val) implements EnumInt16, RefPriceToleranceOrTwapSource {

    public static TwapSource read(final byte[] _data, int i) {
      return new TwapSource(getInt16LE(_data, i));
    }

    @Override
    public int ordinal() {
      return 2;
    }
  }
}

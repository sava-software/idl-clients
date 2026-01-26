package software.sava.idl.clients.drift.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;

public sealed interface FillMode extends RustEnum permits
  FillMode.Fill,
  FillMode.PlaceAndMake,
  FillMode.PlaceAndTake,
  FillMode.Liquidation {

  static FillMode read(final byte[] _data, final int _offset) {
    final int ordinal = _data[_offset] & 0xFF;
    final int i = _offset + 1;
    return switch (ordinal) {
      case 0 -> Fill.INSTANCE;
      case 1 -> PlaceAndMake.INSTANCE;
      case 2 -> PlaceAndTake.read(_data, i);
      case 3 -> Liquidation.INSTANCE;
      default -> null;
    };
  }

  record Fill() implements EnumNone, FillMode {

    public static final Fill INSTANCE = new Fill();

    @Override
    public int ordinal() {
      return 0;
    }
  }

  record PlaceAndMake() implements EnumNone, FillMode {

    public static final PlaceAndMake INSTANCE = new PlaceAndMake();

    @Override
    public int ordinal() {
      return 1;
    }
  }

  record PlaceAndTake(boolean _bool, int _u8) implements FillMode {

    public static final int BYTES = 2;

    public static final int _BOOL_OFFSET = 0;
    public static final int _U_8_OFFSET = 1;

    public static PlaceAndTake read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      int i = _offset;
      final var _bool = _data[i] == 1;
      ++i;
      final var _u8 = _data[i] & 0xFF;
      return new PlaceAndTake(_bool, _u8);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + writeOrdinal(_data, _offset);
      _data[i] = (byte) (_bool ? 1 : 0);
      ++i;
      _data[i] = (byte) _u8;
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }

    @Override
    public int ordinal() {
      return 2;
    }
  }

  record Liquidation() implements EnumNone, FillMode {

    public static final Liquidation INSTANCE = new Liquidation();

    @Override
    public int ordinal() {
      return 3;
    }
  }
}

package software.sava.idl.clients.drift.gen.types;

import software.sava.core.borsh.RustEnum;

public sealed interface MarginCalculationMode extends RustEnum permits
  MarginCalculationMode.Standard,
  MarginCalculationMode.Liquidation {

  static MarginCalculationMode read(final byte[] _data, final int _offset) {
    final int ordinal = _data[_offset] & 0xFF;
    final int i = _offset + 1;
    return switch (ordinal) {
      case 0 -> Standard.read(_data, i);
      case 1 -> Liquidation.read(_data, i);
      default -> throw new IllegalStateException(java.lang.String.format(
          "Unexpected ordinal [%d] for enum [MarginCalculationMode]", ordinal
      ));
    };
  }

  record Standard(boolean val) implements EnumBool, MarginCalculationMode {

    public static final Standard TRUE = new Standard(true);
    public static final Standard FALSE = new Standard(false);

    public static Standard read(final byte[] _data, int i) {
      return _data[i] == 1 ? Standard.TRUE : Standard.FALSE;
    }

    @Override
    public int ordinal() {
      return 0;
    }
  }

  record Liquidation(MarketIdentifier val) implements BorshEnum, MarginCalculationMode {

    public static Liquidation read(final byte[] _data, final int _offset) {
      return new Liquidation(MarketIdentifier.read(_data, _offset));
    }

    @Override
    public int ordinal() {
      return 1;
    }
  }
}

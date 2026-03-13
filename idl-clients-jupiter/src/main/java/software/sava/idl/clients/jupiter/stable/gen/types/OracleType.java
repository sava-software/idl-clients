package software.sava.idl.clients.jupiter.stable.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;

public sealed interface OracleType extends RustEnum permits
  OracleType.Empty,
  OracleType.Pyth,
  OracleType.Doves,
  OracleType.SwitchboardOnDemand {

  static OracleType read(final byte[] _data, final int _offset) {
    final int ordinal = _data[_offset] & 0xFF;
    final int i = _offset + 1;
    return switch (ordinal) {
      case 0 -> Empty.read(_data, i);
      case 1 -> Pyth.read(_data, i);
      case 2 -> Doves.read(_data, i);
      case 3 -> SwitchboardOnDemand.read(_data, i);
      default -> null;
    };
  }

  record Empty(EmptyOracle val) implements SerDeEnum, OracleType {

    public static Empty read(final byte[] _data, final int _offset) {
      return new Empty(EmptyOracle.read(_data, _offset));
    }

    @Override
    public int ordinal() {
      return 0;
    }
  }

  record Pyth(PythV2Oracle val) implements SerDeEnum, OracleType {

    public static Pyth read(final byte[] _data, final int _offset) {
      return new Pyth(PythV2Oracle.read(_data, _offset));
    }

    @Override
    public int ordinal() {
      return 1;
    }
  }

  record Doves(DovesOracle val) implements SerDeEnum, OracleType {

    public static Doves read(final byte[] _data, final int _offset) {
      return new Doves(DovesOracle.read(_data, _offset));
    }

    @Override
    public int ordinal() {
      return 2;
    }
  }

  record SwitchboardOnDemand(SwitchboardOnDemandOracle val) implements SerDeEnum, OracleType {

    public static SwitchboardOnDemand read(final byte[] _data, final int _offset) {
      return new SwitchboardOnDemand(SwitchboardOnDemandOracle.read(_data, _offset));
    }

    @Override
    public int ordinal() {
      return 3;
    }
  }
}

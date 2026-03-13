package software.sava.idl.clients.jupiter.stable.gen.types;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.accounts.PublicKey.readPubKey;

public sealed interface OracleConfig extends RustEnum permits
  OracleConfig.None,
  OracleConfig.Pyth,
  OracleConfig.SwitchboardOnDemand,
  OracleConfig.Doves {

  static OracleConfig read(final byte[] _data, final int _offset) {
    final int ordinal = _data[_offset] & 0xFF;
    final int i = _offset + 1;
    return switch (ordinal) {
      case 0 -> None.INSTANCE;
      case 1 -> Pyth.read(_data, i);
      case 2 -> SwitchboardOnDemand.read(_data, i);
      case 3 -> Doves.read(_data, i);
      default -> null;
    };
  }

  record None() implements EnumNone, OracleConfig {

    public static final None INSTANCE = new None();

    @Override
    public int ordinal() {
      return 0;
    }
  }

  record Pyth(byte[] _array, PublicKey _publicKey) implements OracleConfig {

    public static final int BYTES = 64;
    public static final int _ARRAY_LEN = 32;

    public static final int _ARRAY_OFFSET = 0;
    public static final int _PUBLIC_KEY_OFFSET = 32;

    public static Pyth read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      int i = _offset;
      final var _array = new byte[32];
      i += SerDeUtil.readArray(_array, _data, i);
      final var _publicKey = readPubKey(_data, i);
      return new Pyth(_array, _publicKey);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + writeOrdinal(_data, _offset);
      i += SerDeUtil.writeArrayChecked(_array, 32, _data, i);
      _publicKey.write(_data, i);
      i += 32;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }

    @Override
    public int ordinal() {
      return 1;
    }
  }

  record SwitchboardOnDemand(PublicKey val) implements EnumPublicKey, OracleConfig {

    public static SwitchboardOnDemand read(final byte[] _data, int i) {
      return new SwitchboardOnDemand(readPubKey(_data, i));
    }

    @Override
    public int ordinal() {
      return 2;
    }
  }

  record Doves(PublicKey val) implements EnumPublicKey, OracleConfig {

    public static Doves read(final byte[] _data, int i) {
      return new Doves(readPubKey(_data, i));
    }

    @Override
    public int ordinal() {
      return 3;
    }
  }
}

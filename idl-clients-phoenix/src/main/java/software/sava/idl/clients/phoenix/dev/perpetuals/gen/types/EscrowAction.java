package software.sava.idl.clients.phoenix.dev.perpetuals.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

public sealed interface EscrowAction extends RustEnum permits
  EscrowAction.Noop,
  EscrowAction.Cash {

  static EscrowAction read(final byte[] _data, final int _offset) {
    final int ordinal = _data[_offset] & 0xFF;
    final int i = _offset + 1;
    return switch (ordinal) {
      case 0 -> Noop.read(_data, i);
      case 1 -> Cash.read(_data, i);
      default -> null;
    };
  }

  record Noop(byte[] padding0, byte[] padding1) implements EscrowAction {

    public static final int BYTES = 136;
    public static final int PADDING_0_LEN = 8;
    public static final int PADDING_1_LEN = 128;

    public static final int PADDING_0_OFFSET = 0;
    public static final int PADDING_1_OFFSET = 8;

    public static Noop read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      int i = _offset;
      final var padding0 = new byte[8];
      i += SerDeUtil.readArray(padding0, _data, i);
      final var padding1 = new byte[128];
      SerDeUtil.readArray(padding1, _data, i);
      return new Noop(padding0, padding1);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + writeOrdinal(_data, _offset);
      i += SerDeUtil.writeArrayChecked(padding0, 8, _data, i);
      i += SerDeUtil.writeArrayChecked(padding1, 128, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }

    @Override
    public int ordinal() {
      return 0;
    }
  }

  record Cash(long amount, byte[] padding0) implements EscrowAction {

    public static final int BYTES = 136;
    public static final int PADDING_0_LEN = 128;

    public static final int AMOUNT_OFFSET = 0;
    public static final int PADDING_0_OFFSET = 8;

    public static Cash read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      int i = _offset;
      final var amount = getInt64LE(_data, i);
      i += 8;
      final var padding0 = new byte[128];
      SerDeUtil.readArray(padding0, _data, i);
      return new Cash(amount, padding0);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + writeOrdinal(_data, _offset);
      putInt64LE(_data, i, amount);
      i += 8;
      i += SerDeUtil.writeArrayChecked(padding0, 128, _data, i);
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
}

package software.sava.idl.clients.drift.gen.types;

import software.sava.core.borsh.RustEnum;

import static software.sava.core.encoding.ByteUtil.getInt32LE;

public sealed interface ModifyOrderId extends RustEnum permits
  ModifyOrderId.UserOrderId,
  ModifyOrderId.OrderId {

  static ModifyOrderId read(final byte[] _data, final int _offset) {
    final int ordinal = _data[_offset] & 0xFF;
    final int i = _offset + 1;
    return switch (ordinal) {
      case 0 -> UserOrderId.read(_data, i);
      case 1 -> OrderId.read(_data, i);
      default -> null;
    };
  }

  record UserOrderId(int val) implements EnumInt8, ModifyOrderId {

    public static UserOrderId read(final byte[] _data, int i) {
      return new UserOrderId(_data[i] & 0xFF);
    }

    @Override
    public int ordinal() {
      return 0;
    }
  }

  record OrderId(int val) implements EnumInt32, ModifyOrderId {

    public static OrderId read(final byte[] _data, int i) {
      return new OrderId(getInt32LE(_data, i));
    }

    @Override
    public int ordinal() {
      return 1;
    }
  }
}

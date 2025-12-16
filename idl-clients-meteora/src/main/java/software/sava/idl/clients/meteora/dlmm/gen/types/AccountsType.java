package software.sava.idl.clients.meteora.dlmm.gen.types;

import software.sava.core.borsh.RustEnum;

public sealed interface AccountsType extends RustEnum permits
  AccountsType.TransferHookX,
  AccountsType.TransferHookY,
  AccountsType.TransferHookReward,
  AccountsType.TransferHookMultiReward {

  static AccountsType read(final byte[] _data, final int _offset) {
    final int ordinal = _data[_offset] & 0xFF;
    final int i = _offset + 1;
    return switch (ordinal) {
      case 0 -> TransferHookX.INSTANCE;
      case 1 -> TransferHookY.INSTANCE;
      case 2 -> TransferHookReward.INSTANCE;
      case 3 -> TransferHookMultiReward.read(_data, i);
      default -> null;
    };
  }

  record TransferHookX() implements EnumNone, AccountsType {

    public static final TransferHookX INSTANCE = new TransferHookX();

    @Override
    public int ordinal() {
      return 0;
    }
  }

  record TransferHookY() implements EnumNone, AccountsType {

    public static final TransferHookY INSTANCE = new TransferHookY();

    @Override
    public int ordinal() {
      return 1;
    }
  }

  record TransferHookReward() implements EnumNone, AccountsType {

    public static final TransferHookReward INSTANCE = new TransferHookReward();

    @Override
    public int ordinal() {
      return 2;
    }
  }

  record TransferHookMultiReward(int val) implements EnumInt8, AccountsType {

    public static TransferHookMultiReward read(final byte[] _data, int i) {
      return new TransferHookMultiReward(_data[i] & 0xFF);
    }

    @Override
    public int ordinal() {
      return 3;
    }
  }
}

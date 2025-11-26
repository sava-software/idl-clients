package software.sava.idl.clients.kamino.vaults.gen.types;

import software.sava.core.accounts.PublicKey;
import software.sava.core.borsh.RustEnum;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt64LE;

public sealed interface UpdateGlobalConfigMode extends RustEnum permits
  UpdateGlobalConfigMode.PendingAdmin,
  UpdateGlobalConfigMode.MinWithdrawalPenaltyLamports,
  UpdateGlobalConfigMode.MinWithdrawalPenaltyBPS {

  static UpdateGlobalConfigMode read(final byte[] _data, final int _offset) {
    final int ordinal = _data[_offset] & 0xFF;
    final int i = _offset + 1;
    return switch (ordinal) {
      case 0 -> PendingAdmin.read(_data, i);
      case 1 -> MinWithdrawalPenaltyLamports.read(_data, i);
      case 2 -> MinWithdrawalPenaltyBPS.read(_data, i);
      default -> throw new IllegalStateException(java.lang.String.format(
          "Unexpected ordinal [%d] for enum [UpdateGlobalConfigMode]", ordinal
      ));
    };
  }

  record PendingAdmin(PublicKey val) implements EnumPublicKey, UpdateGlobalConfigMode {

    public static PendingAdmin read(final byte[] _data, int i) {
      return new PendingAdmin(readPubKey(_data, i));
    }

    @Override
    public int ordinal() {
      return 0;
    }
  }

  record MinWithdrawalPenaltyLamports(long val) implements EnumInt64, UpdateGlobalConfigMode {

    public static MinWithdrawalPenaltyLamports read(final byte[] _data, int i) {
      return new MinWithdrawalPenaltyLamports(getInt64LE(_data, i));
    }

    @Override
    public int ordinal() {
      return 1;
    }
  }

  record MinWithdrawalPenaltyBPS(long val) implements EnumInt64, UpdateGlobalConfigMode {

    public static MinWithdrawalPenaltyBPS read(final byte[] _data, int i) {
      return new MinWithdrawalPenaltyBPS(getInt64LE(_data, i));
    }

    @Override
    public int ordinal() {
      return 2;
    }
  }
}

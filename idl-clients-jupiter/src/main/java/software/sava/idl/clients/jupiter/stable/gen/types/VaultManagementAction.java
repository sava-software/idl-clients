package software.sava.idl.clients.jupiter.stable.gen.types;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.core.gen.RustEnum;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

public sealed interface VaultManagementAction extends RustEnum permits
  VaultManagementAction.Disable,
  VaultManagementAction.SetStatus,
  VaultManagementAction.UpdateOracle,
  VaultManagementAction.UpdatePeriodLimit,
  VaultManagementAction.ResetPeriodLimit,
  VaultManagementAction.SetCustodian,
  VaultManagementAction.SetStalesnessThreshold,
  VaultManagementAction.SetMinOraclePrice,
  VaultManagementAction.SetMaxOraclePrice {

  static VaultManagementAction read(final byte[] _data, final int _offset) {
    final int ordinal = _data[_offset] & 0xFF;
    final int i = _offset + 1;
    return switch (ordinal) {
      case 0 -> Disable.INSTANCE;
      case 1 -> SetStatus.read(_data, i);
      case 2 -> UpdateOracle.read(_data, i);
      case 3 -> UpdatePeriodLimit.read(_data, i);
      case 4 -> ResetPeriodLimit.read(_data, i);
      case 5 -> SetCustodian.read(_data, i);
      case 6 -> SetStalesnessThreshold.read(_data, i);
      case 7 -> SetMinOraclePrice.read(_data, i);
      case 8 -> SetMaxOraclePrice.read(_data, i);
      default -> null;
    };
  }

  record Disable() implements EnumNone, VaultManagementAction {

    public static final Disable INSTANCE = new Disable();

    @Override
    public int ordinal() {
      return 0;
    }
  }

  record SetStatus(VaultStatus val) implements SerDeEnum, VaultManagementAction {

    public static SetStatus read(final byte[] _data, final int _offset) {
      return new SetStatus(VaultStatus.read(_data, _offset));
    }

    @Override
    public int ordinal() {
      return 1;
    }
  }

  record UpdateOracle(int index, OracleConfig oracle) implements VaultManagementAction {

    public static final int INDEX_OFFSET = 0;
    public static final int ORACLE_OFFSET = 1;

    public static UpdateOracle read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      int i = _offset;
      final var index = _data[i] & 0xFF;
      ++i;
      final var oracle = OracleConfig.read(_data, i);
      return new UpdateOracle(index, oracle);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + writeOrdinal(_data, _offset);
      _data[i] = (byte) index;
      ++i;
      i += oracle.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 1 + 1 + oracle.l();
    }

    @Override
    public int ordinal() {
      return 2;
    }
  }

  record UpdatePeriodLimit(int index,
                           long durationSeconds,
                           long maxMintAmount,
                           long maxRedeemAmount) implements VaultManagementAction {

    public static final int BYTES = 25;

    public static final int INDEX_OFFSET = 0;
    public static final int DURATION_SECONDS_OFFSET = 1;
    public static final int MAX_MINT_AMOUNT_OFFSET = 9;
    public static final int MAX_REDEEM_AMOUNT_OFFSET = 17;

    public static UpdatePeriodLimit read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      int i = _offset;
      final var index = _data[i] & 0xFF;
      ++i;
      final var durationSeconds = getInt64LE(_data, i);
      i += 8;
      final var maxMintAmount = getInt64LE(_data, i);
      i += 8;
      final var maxRedeemAmount = getInt64LE(_data, i);
      return new UpdatePeriodLimit(index,
                                   durationSeconds,
                                   maxMintAmount,
                                   maxRedeemAmount);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + writeOrdinal(_data, _offset);
      _data[i] = (byte) index;
      ++i;
      putInt64LE(_data, i, durationSeconds);
      i += 8;
      putInt64LE(_data, i, maxMintAmount);
      i += 8;
      putInt64LE(_data, i, maxRedeemAmount);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }

    @Override
    public int ordinal() {
      return 3;
    }
  }

  record ResetPeriodLimit(int val) implements EnumInt8, VaultManagementAction {

    public static ResetPeriodLimit read(final byte[] _data, int i) {
      return new ResetPeriodLimit(_data[i] & 0xFF);
    }

    @Override
    public int ordinal() {
      return 4;
    }
  }

  record SetCustodian(PublicKey val) implements EnumPublicKey, VaultManagementAction {

    public static SetCustodian read(final byte[] _data, int i) {
      return new SetCustodian(readPubKey(_data, i));
    }

    @Override
    public int ordinal() {
      return 5;
    }
  }

  record SetStalesnessThreshold(long val) implements EnumInt64, VaultManagementAction {

    public static SetStalesnessThreshold read(final byte[] _data, int i) {
      return new SetStalesnessThreshold(getInt64LE(_data, i));
    }

    @Override
    public int ordinal() {
      return 6;
    }
  }

  record SetMinOraclePrice(long val) implements EnumInt64, VaultManagementAction {

    public static SetMinOraclePrice read(final byte[] _data, int i) {
      return new SetMinOraclePrice(getInt64LE(_data, i));
    }

    @Override
    public int ordinal() {
      return 7;
    }
  }

  record SetMaxOraclePrice(long val) implements EnumInt64, VaultManagementAction {

    public static SetMaxOraclePrice read(final byte[] _data, int i) {
      return new SetMaxOraclePrice(getInt64LE(_data, i));
    }

    @Override
    public int ordinal() {
      return 8;
    }
  }
}

package software.sava.idl.clients.jupiter.stable.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;

import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

public sealed interface ConfigManagementAction extends RustEnum permits
  ConfigManagementAction.Pause,
  ConfigManagementAction.UpdatePauseFlag,
  ConfigManagementAction.UpdatePeriodLimit,
  ConfigManagementAction.ResetPeriodLimit,
  ConfigManagementAction.SetPegPriceUSD {

  static ConfigManagementAction read(final byte[] _data, final int _offset) {
    final int ordinal = _data[_offset] & 0xFF;
    final int i = _offset + 1;
    return switch (ordinal) {
      case 0 -> Pause.INSTANCE;
      case 1 -> UpdatePauseFlag.read(_data, i);
      case 2 -> UpdatePeriodLimit.read(_data, i);
      case 3 -> ResetPeriodLimit.read(_data, i);
      case 4 -> SetPegPriceUSD.read(_data, i);
      default -> null;
    };
  }

  record Pause() implements EnumNone, ConfigManagementAction {

    public static final Pause INSTANCE = new Pause();

    @Override
    public int ordinal() {
      return 0;
    }
  }

  record UpdatePauseFlag(boolean val) implements EnumBool, ConfigManagementAction {

    public static final UpdatePauseFlag TRUE = new UpdatePauseFlag(true);
    public static final UpdatePauseFlag FALSE = new UpdatePauseFlag(false);

    public static UpdatePauseFlag read(final byte[] _data, int i) {
      return _data[i] == 1 ? UpdatePauseFlag.TRUE : UpdatePauseFlag.FALSE;
    }

    @Override
    public int ordinal() {
      return 1;
    }
  }

  record UpdatePeriodLimit(int index,
                           long durationSeconds,
                           long maxMintAmount,
                           long maxRedeemAmount) implements ConfigManagementAction {

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
      return 2;
    }
  }

  record ResetPeriodLimit(int val) implements EnumInt8, ConfigManagementAction {

    public static ResetPeriodLimit read(final byte[] _data, int i) {
      return new ResetPeriodLimit(_data[i] & 0xFF);
    }

    @Override
    public int ordinal() {
      return 3;
    }
  }

  record SetPegPriceUSD(long val) implements EnumInt64, ConfigManagementAction {

    public static SetPegPriceUSD read(final byte[] _data, int i) {
      return new SetPegPriceUSD(getInt64LE(_data, i));
    }

    @Override
    public int ordinal() {
      return 4;
    }
  }
}

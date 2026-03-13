package software.sava.idl.clients.jupiter.stable.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;

import static software.sava.core.encoding.ByteUtil.getInt16LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt16LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

public sealed interface BenefactorManagementAction extends RustEnum permits
  BenefactorManagementAction.Disable,
  BenefactorManagementAction.SetStatus,
  BenefactorManagementAction.UpdateFeeRates,
  BenefactorManagementAction.UpdatePeriodLimit,
  BenefactorManagementAction.ResetPeriodLimit {

  static BenefactorManagementAction read(final byte[] _data, final int _offset) {
    final int ordinal = _data[_offset] & 0xFF;
    final int i = _offset + 1;
    return switch (ordinal) {
      case 0 -> Disable.INSTANCE;
      case 1 -> SetStatus.read(_data, i);
      case 2 -> UpdateFeeRates.read(_data, i);
      case 3 -> UpdatePeriodLimit.read(_data, i);
      case 4 -> ResetPeriodLimit.read(_data, i);
      default -> null;
    };
  }

  record Disable() implements EnumNone, BenefactorManagementAction {

    public static final Disable INSTANCE = new Disable();

    @Override
    public int ordinal() {
      return 0;
    }
  }

  record SetStatus(BenefactorStatus val) implements SerDeEnum, BenefactorManagementAction {

    public static SetStatus read(final byte[] _data, final int _offset) {
      return new SetStatus(BenefactorStatus.read(_data, _offset));
    }

    @Override
    public int ordinal() {
      return 1;
    }
  }

  record UpdateFeeRates(int mintFeeRate, int redeemFeeRate) implements BenefactorManagementAction {

    public static final int BYTES = 4;

    public static final int MINT_FEE_RATE_OFFSET = 0;
    public static final int REDEEM_FEE_RATE_OFFSET = 2;

    public static UpdateFeeRates read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      int i = _offset;
      final var mintFeeRate = getInt16LE(_data, i);
      i += 2;
      final var redeemFeeRate = getInt16LE(_data, i);
      return new UpdateFeeRates(mintFeeRate, redeemFeeRate);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + writeOrdinal(_data, _offset);
      putInt16LE(_data, i, mintFeeRate);
      i += 2;
      putInt16LE(_data, i, redeemFeeRate);
      i += 2;
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

  record UpdatePeriodLimit(int index,
                           long durationSeconds,
                           long maxMintAmount,
                           long maxRedeemAmount) implements BenefactorManagementAction {

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

  record ResetPeriodLimit(int val) implements EnumInt8, BenefactorManagementAction {

    public static ResetPeriodLimit read(final byte[] _data, int i) {
      return new ResetPeriodLimit(_data[i] & 0xFF);
    }

    @Override
    public int ordinal() {
      return 4;
    }
  }
}

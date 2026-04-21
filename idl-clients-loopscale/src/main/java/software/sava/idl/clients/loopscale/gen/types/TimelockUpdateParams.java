package software.sava.idl.clients.loopscale.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;

import static software.sava.core.encoding.ByteUtil.getInt64LE;

public sealed interface TimelockUpdateParams extends RustEnum permits
  TimelockUpdateParams.AddCollateral,
  TimelockUpdateParams.UpdateLtv,
  TimelockUpdateParams.RemoveCollateral,
  TimelockUpdateParams.UpdateApy,
  TimelockUpdateParams.UpdateStrategy,
  TimelockUpdateParams.UpdateVault,
  TimelockUpdateParams.UpdateCaps,
  TimelockUpdateParams.UpdateCollateralAllocationPct {

  static TimelockUpdateParams read(final byte[] _data, final int _offset) {
    final int ordinal = _data[_offset] & 0xFF;
    final int i = _offset + 1;
    return switch (ordinal) {
      case 0 -> AddCollateral.read(_data, i);
      case 1 -> UpdateLtv.read(_data, i);
      case 2 -> RemoveCollateral.read(_data, i);
      case 3 -> UpdateApy.read(_data, i);
      case 4 -> UpdateStrategy.read(_data, i);
      case 5 -> UpdateVault.read(_data, i);
      case 6 -> UpdateCaps.read(_data, i);
      case 7 -> UpdateCollateralAllocationPct.read(_data, i);
      default -> null;
    };
  }

  record AddCollateral(AddCollateralTimeLockArgs val) implements SerDeEnum, TimelockUpdateParams {

    public static AddCollateral read(final byte[] _data, final int _offset) {
      return new AddCollateral(AddCollateralTimeLockArgs.read(_data, _offset));
    }

    @Override
    public int ordinal() {
      return 0;
    }
  }

  record UpdateLtv(UpdateAssetDataParams val) implements SerDeEnum, TimelockUpdateParams {

    public static UpdateLtv read(final byte[] _data, final int _offset) {
      return new UpdateLtv(UpdateAssetDataParams.read(_data, _offset));
    }

    @Override
    public int ordinal() {
      return 1;
    }
  }

  record RemoveCollateral(CollateralTerms val) implements SerDeEnum, TimelockUpdateParams {

    public static RemoveCollateral read(final byte[] _data, final int _offset) {
      return new RemoveCollateral(CollateralTerms.read(_data, _offset));
    }

    @Override
    public int ordinal() {
      return 2;
    }
  }

  record UpdateApy(CollateralTerms val) implements SerDeEnum, TimelockUpdateParams {

    public static UpdateApy read(final byte[] _data, final int _offset) {
      return new UpdateApy(CollateralTerms.read(_data, _offset));
    }

    @Override
    public int ordinal() {
      return 3;
    }
  }

  record UpdateStrategy(UpdateStrategyParams val) implements SerDeEnum, TimelockUpdateParams {

    public static UpdateStrategy read(final byte[] _data, final int _offset) {
      return new UpdateStrategy(UpdateStrategyParams.read(_data, _offset));
    }

    @Override
    public int ordinal() {
      return 4;
    }
  }

  record UpdateVault(long val) implements EnumInt64, TimelockUpdateParams {

    public static UpdateVault read(final byte[] _data, int i) {
      return new UpdateVault(getInt64LE(_data, i));
    }

    @Override
    public int ordinal() {
      return 5;
    }
  }

  record UpdateCaps(UpdateCapsParams val) implements SerDeEnum, TimelockUpdateParams {

    public static UpdateCaps read(final byte[] _data, final int _offset) {
      return new UpdateCaps(UpdateCapsParams.read(_data, _offset));
    }

    @Override
    public int ordinal() {
      return 6;
    }
  }

  record UpdateCollateralAllocationPct(UpdateAssetDataParams val) implements SerDeEnum, TimelockUpdateParams {

    public static UpdateCollateralAllocationPct read(final byte[] _data, final int _offset) {
      return new UpdateCollateralAllocationPct(UpdateAssetDataParams.read(_data, _offset));
    }

    @Override
    public int ordinal() {
      return 7;
    }
  }
}

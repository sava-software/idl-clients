package software.sava.idl.clients.loopscale.gen.types;

import software.sava.idl.clients.core.gen.SerDe;

public record AddCollateralTimeLockArgs(CollateralTerms collateralTerms, UpdateAssetDataParams updateAssetDataParams) implements SerDe {

  public static final int COLLATERAL_TERMS_OFFSET = 0;
  public static final int UPDATE_ASSET_DATA_PARAMS_OFFSET = 72;

  public static AddCollateralTimeLockArgs read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var collateralTerms = CollateralTerms.read(_data, i);
    i += collateralTerms.l();
    final var updateAssetDataParams = UpdateAssetDataParams.read(_data, i);
    return new AddCollateralTimeLockArgs(collateralTerms, updateAssetDataParams);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += collateralTerms.write(_data, i);
    i += updateAssetDataParams.write(_data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return collateralTerms.l() + updateAssetDataParams.l();
  }
}

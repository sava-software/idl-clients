package software.sava.idl.clients.loopscale.gen.types;

import java.math.BigInteger;

import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.encoding.ByteUtil.getInt128LE;
import static software.sava.core.encoding.ByteUtil.putInt128LE;

public record ManageRaydiumLiquidityParams(int collateralIndex,
                                           BigInteger liquidityAmount,
                                           TokenAmountsParams manageParams,
                                           byte[] assetIndexGuidance) implements SerDe {

  public static final int COLLATERAL_INDEX_OFFSET = 0;
  public static final int LIQUIDITY_AMOUNT_OFFSET = 1;
  public static final int MANAGE_PARAMS_OFFSET = 17;
  public static final int ASSET_INDEX_GUIDANCE_OFFSET = 33;

  public static ManageRaydiumLiquidityParams read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var collateralIndex = _data[i] & 0xFF;
    ++i;
    final var liquidityAmount = getInt128LE(_data, i);
    i += 16;
    final var manageParams = TokenAmountsParams.read(_data, i);
    i += manageParams.l();
    final var assetIndexGuidance = SerDeUtil.readbyteVector(4, _data, i);
    return new ManageRaydiumLiquidityParams(collateralIndex,
                                            liquidityAmount,
                                            manageParams,
                                            assetIndexGuidance);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    _data[i] = (byte) collateralIndex;
    ++i;
    putInt128LE(_data, i, liquidityAmount);
    i += 16;
    i += manageParams.write(_data, i);
    i += SerDeUtil.writeVector(4, assetIndexGuidance, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return 1 + 16 + manageParams.l() + SerDeUtil.lenVector(4, assetIndexGuidance);
  }
}

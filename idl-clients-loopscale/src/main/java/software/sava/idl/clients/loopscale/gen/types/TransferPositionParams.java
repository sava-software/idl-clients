package software.sava.idl.clients.loopscale.gen.types;

import java.math.BigInteger;

import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.encoding.ByteUtil.getInt128LE;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt128LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;

public record TransferPositionParams(BigInteger liquidityAmount,
                                     int collateralIndex,
                                     TransferTypeParams transferParams,
                                     int tickLowerIndex,
                                     int tickUpperIndex,
                                     byte[] assetIndexGuidance) implements SerDe {

  public static final int LIQUIDITY_AMOUNT_OFFSET = 0;
  public static final int COLLATERAL_INDEX_OFFSET = 16;
  public static final int TRANSFER_PARAMS_OFFSET = 17;

  public static TransferPositionParams read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var liquidityAmount = getInt128LE(_data, i);
    i += 16;
    final var collateralIndex = _data[i] & 0xFF;
    ++i;
    final var transferParams = TransferTypeParams.read(_data, i);
    i += transferParams.l();
    final var tickLowerIndex = getInt32LE(_data, i);
    i += 4;
    final var tickUpperIndex = getInt32LE(_data, i);
    i += 4;
    final var assetIndexGuidance = SerDeUtil.readbyteVector(4, _data, i);
    return new TransferPositionParams(liquidityAmount,
                                      collateralIndex,
                                      transferParams,
                                      tickLowerIndex,
                                      tickUpperIndex,
                                      assetIndexGuidance);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt128LE(_data, i, liquidityAmount);
    i += 16;
    _data[i] = (byte) collateralIndex;
    ++i;
    i += transferParams.write(_data, i);
    putInt32LE(_data, i, tickLowerIndex);
    i += 4;
    putInt32LE(_data, i, tickUpperIndex);
    i += 4;
    i += SerDeUtil.writeVector(4, assetIndexGuidance, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return 16
         + 1
         + transferParams.l()
         + 4
         + 4
         + SerDeUtil.lenVector(4, assetIndexGuidance);
  }
}

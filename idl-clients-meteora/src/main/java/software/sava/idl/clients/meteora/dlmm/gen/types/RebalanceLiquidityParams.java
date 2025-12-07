package software.sava.idl.clients.meteora.dlmm.gen.types;

import software.sava.core.borsh.Borsh;

import static software.sava.core.encoding.ByteUtil.getInt16LE;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt16LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

/// @param activeId active id
/// @param maxActiveBinSlippage max active bin slippage allowed
/// @param shouldClaimFee a flag to indicate that whether fee should be harvested
/// @param shouldClaimReward a flag to indicate that whether rewards should be harvested
/// @param minWithdrawXAmount threshold for withdraw token x
/// @param maxDepositXAmount threshold for deposit token x
/// @param minWithdrawYAmount threshold for withdraw token y
/// @param maxDepositYAmount threshold for deposit token y
/// @param shrinkMode shrink mode
/// @param padding padding 32 bytes for future usage
/// @param removes removes
/// @param adds adds
public record RebalanceLiquidityParams(int activeId,
                                       int maxActiveBinSlippage,
                                       boolean shouldClaimFee,
                                       boolean shouldClaimReward,
                                       long minWithdrawXAmount,
                                       long maxDepositXAmount,
                                       long minWithdrawYAmount,
                                       long maxDepositYAmount,
                                       int shrinkMode,
                                       byte[] padding,
                                       RemoveLiquidityParams[] removes,
                                       AddLiquidityParams[] adds) implements Borsh {

  public static final int PADDING_LEN = 31;
  public static RebalanceLiquidityParams read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var activeId = getInt32LE(_data, i);
    i += 4;
    final var maxActiveBinSlippage = getInt16LE(_data, i);
    i += 2;
    final var shouldClaimFee = _data[i] == 1;
    ++i;
    final var shouldClaimReward = _data[i] == 1;
    ++i;
    final var minWithdrawXAmount = getInt64LE(_data, i);
    i += 8;
    final var maxDepositXAmount = getInt64LE(_data, i);
    i += 8;
    final var minWithdrawYAmount = getInt64LE(_data, i);
    i += 8;
    final var maxDepositYAmount = getInt64LE(_data, i);
    i += 8;
    final var shrinkMode = _data[i] & 0xFF;
    ++i;
    final var padding = new byte[31];
    i += Borsh.readArray(padding, _data, i);
    final var removes = Borsh.readVector(RemoveLiquidityParams.class, RemoveLiquidityParams::read, _data, i);
    i += Borsh.lenVector(removes);
    final var adds = Borsh.readVector(AddLiquidityParams.class, AddLiquidityParams::read, _data, i);
    return new RebalanceLiquidityParams(activeId,
                                        maxActiveBinSlippage,
                                        shouldClaimFee,
                                        shouldClaimReward,
                                        minWithdrawXAmount,
                                        maxDepositXAmount,
                                        minWithdrawYAmount,
                                        maxDepositYAmount,
                                        shrinkMode,
                                        padding,
                                        removes,
                                        adds);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt32LE(_data, i, activeId);
    i += 4;
    putInt16LE(_data, i, maxActiveBinSlippage);
    i += 2;
    _data[i] = (byte) (shouldClaimFee ? 1 : 0);
    ++i;
    _data[i] = (byte) (shouldClaimReward ? 1 : 0);
    ++i;
    putInt64LE(_data, i, minWithdrawXAmount);
    i += 8;
    putInt64LE(_data, i, maxDepositXAmount);
    i += 8;
    putInt64LE(_data, i, minWithdrawYAmount);
    i += 8;
    putInt64LE(_data, i, maxDepositYAmount);
    i += 8;
    _data[i] = (byte) shrinkMode;
    ++i;
    i += Borsh.writeArrayChecked(padding, 31, _data, i);
    i += Borsh.writeVector(removes, _data, i);
    i += Borsh.writeVector(adds, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return 4
         + 2
         + 1
         + 1
         + 8
         + 8
         + 8
         + 8
         + 1
         + Borsh.lenArray(padding)
         + Borsh.lenVector(removes)
         + Borsh.lenVector(adds);
  }
}

package software.sava.idl.clients.meteora.dlmm.gen.types;

import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

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
                                       AddLiquidityParams[] adds) implements SerDe {

  public static final int PADDING_LEN = 31;
  public static final int ACTIVE_ID_OFFSET = 0;
  public static final int MAX_ACTIVE_BIN_SLIPPAGE_OFFSET = 4;
  public static final int SHOULD_CLAIM_FEE_OFFSET = 6;
  public static final int SHOULD_CLAIM_REWARD_OFFSET = 7;
  public static final int MIN_WITHDRAW_X_AMOUNT_OFFSET = 8;
  public static final int MAX_DEPOSIT_X_AMOUNT_OFFSET = 16;
  public static final int MIN_WITHDRAW_Y_AMOUNT_OFFSET = 24;
  public static final int MAX_DEPOSIT_Y_AMOUNT_OFFSET = 32;
  public static final int SHRINK_MODE_OFFSET = 40;
  public static final int PADDING_OFFSET = 41;
  public static final int REMOVES_OFFSET = 72;

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
    i += SerDeUtil.readArray(padding, _data, i);
    final var removes = SerDeUtil.readVector(4, RemoveLiquidityParams.class, RemoveLiquidityParams::read, _data, i);
    i += SerDeUtil.lenVector(4, removes);
    final var adds = SerDeUtil.readVector(4, AddLiquidityParams.class, AddLiquidityParams::read, _data, i);
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
    i += SerDeUtil.writeArrayChecked(padding, 31, _data, i);
    i += SerDeUtil.writeVector(4, removes, _data, i);
    i += SerDeUtil.writeVector(4, adds, _data, i);
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
         + SerDeUtil.lenArray(padding)
         + SerDeUtil.lenVector(4, removes)
         + SerDeUtil.lenVector(4, adds);
  }
}

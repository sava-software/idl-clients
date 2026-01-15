package software.sava.idl.clients.drift.gen.types;

import java.util.OptionalInt;
import java.util.OptionalLong;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt16LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

public record SignedMsgOrderParamsDelegateMessage(OrderParams signedMsgOrderParams,
                                                  PublicKey takerPubkey,
                                                  long slot,
                                                  byte[] uuid,
                                                  SignedMsgTriggerOrderParams takeProfitOrderParams,
                                                  SignedMsgTriggerOrderParams stopLossOrderParams,
                                                  OptionalInt maxMarginRatio,
                                                  OptionalInt builderIdx,
                                                  OptionalInt builderFeeTenthBps,
                                                  OptionalLong isolatedPositionDeposit) implements SerDe {

  public static final int UUID_LEN = 8;
  public static final int SIGNED_MSG_ORDER_PARAMS_OFFSET = 0;

  public static SignedMsgOrderParamsDelegateMessage read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var signedMsgOrderParams = OrderParams.read(_data, i);
    i += signedMsgOrderParams.l();
    final var takerPubkey = readPubKey(_data, i);
    i += 32;
    final var slot = getInt64LE(_data, i);
    i += 8;
    final var uuid = new byte[8];
    i += SerDeUtil.readArray(uuid, _data, i);
    final SignedMsgTriggerOrderParams takeProfitOrderParams;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      takeProfitOrderParams = null;
      ++i;
    } else {
      ++i;
      takeProfitOrderParams = SignedMsgTriggerOrderParams.read(_data, i);
      i += takeProfitOrderParams.l();
    }
    final SignedMsgTriggerOrderParams stopLossOrderParams;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      stopLossOrderParams = null;
      ++i;
    } else {
      ++i;
      stopLossOrderParams = SignedMsgTriggerOrderParams.read(_data, i);
      i += stopLossOrderParams.l();
    }
    final OptionalInt maxMarginRatio;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      maxMarginRatio = OptionalInt.empty();
      ++i;
    } else {
      ++i;
      maxMarginRatio = OptionalInt.of(getInt16LE(_data, i));
      i += 2;
    }
    final OptionalInt builderIdx;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      builderIdx = OptionalInt.empty();
      ++i;
    } else {
      ++i;
      builderIdx = OptionalInt.of(_data[i] & 0xFF);
      ++i;
    }
    final OptionalInt builderFeeTenthBps;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      builderFeeTenthBps = OptionalInt.empty();
      ++i;
    } else {
      ++i;
      builderFeeTenthBps = OptionalInt.of(getInt16LE(_data, i));
      i += 2;
    }
    final OptionalLong isolatedPositionDeposit;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      isolatedPositionDeposit = OptionalLong.empty();
    } else {
      ++i;
      isolatedPositionDeposit = OptionalLong.of(getInt64LE(_data, i));
    }
    return new SignedMsgOrderParamsDelegateMessage(signedMsgOrderParams,
                                                   takerPubkey,
                                                   slot,
                                                   uuid,
                                                   takeProfitOrderParams,
                                                   stopLossOrderParams,
                                                   maxMarginRatio,
                                                   builderIdx,
                                                   builderFeeTenthBps,
                                                   isolatedPositionDeposit);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += signedMsgOrderParams.write(_data, i);
    takerPubkey.write(_data, i);
    i += 32;
    putInt64LE(_data, i, slot);
    i += 8;
    i += SerDeUtil.writeArrayChecked(uuid, 8, _data, i);
    i += SerDeUtil.writeOptional(1, takeProfitOrderParams, _data, i);
    i += SerDeUtil.writeOptional(1, stopLossOrderParams, _data, i);
    i += SerDeUtil.writeOptionalshort(1, maxMarginRatio, _data, i);
    i += SerDeUtil.writeOptionalbyte(1, builderIdx, _data, i);
    i += SerDeUtil.writeOptionalshort(1, builderFeeTenthBps, _data, i);
    i += SerDeUtil.writeOptional(1, isolatedPositionDeposit, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return signedMsgOrderParams.l()
         + 32
         + 8
         + SerDeUtil.lenArray(uuid)
         + (takeProfitOrderParams == null ? 1 : (1 + takeProfitOrderParams.l()))
         + (stopLossOrderParams == null ? 1 : (1 + stopLossOrderParams.l()))
         + (maxMarginRatio == null || maxMarginRatio.isEmpty() ? 1 : (1 + 2))
         + (builderIdx == null || builderIdx.isEmpty() ? 1 : (1 + 1))
         + (builderFeeTenthBps == null || builderFeeTenthBps.isEmpty() ? 1 : (1 + 2))
         + (isolatedPositionDeposit == null || isolatedPositionDeposit.isEmpty() ? 1 : (1 + 8));
  }
}

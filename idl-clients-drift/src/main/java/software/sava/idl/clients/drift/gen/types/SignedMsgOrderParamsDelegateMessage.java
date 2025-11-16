package software.sava.idl.clients.drift.gen.types;

import java.util.OptionalInt;
import java.util.OptionalLong;

import software.sava.core.accounts.PublicKey;
import software.sava.core.borsh.Borsh;

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
                                                  OptionalLong isolatedPositionDeposit) implements Borsh {

  public static final int UUID_LEN = 8;
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
    i += Borsh.readArray(uuid, _data, i);
    final SignedMsgTriggerOrderParams takeProfitOrderParams;
    if (_data[i] == 0) {
      takeProfitOrderParams = null;
      ++i;
    } else {
      ++i;
      takeProfitOrderParams = SignedMsgTriggerOrderParams.read(_data, i);
      i += takeProfitOrderParams.l();
    }
    final SignedMsgTriggerOrderParams stopLossOrderParams;
    if (_data[i] == 0) {
      stopLossOrderParams = null;
      ++i;
    } else {
      ++i;
      stopLossOrderParams = SignedMsgTriggerOrderParams.read(_data, i);
      i += stopLossOrderParams.l();
    }
    final OptionalInt maxMarginRatio;
    if (_data[i] == 0) {
      maxMarginRatio = OptionalInt.empty();
      ++i;
    } else {
      ++i;
      maxMarginRatio = OptionalInt.of(getInt16LE(_data, i));
      i += 2;
    }
    final OptionalInt builderIdx;
    if (_data[i] == 0) {
      builderIdx = OptionalInt.empty();
      ++i;
    } else {
      ++i;
      builderIdx = OptionalInt.of(_data[i] & 0xFF);
      ++i;
    }
    final OptionalInt builderFeeTenthBps;
    if (_data[i] == 0) {
      builderFeeTenthBps = OptionalInt.empty();
      ++i;
    } else {
      ++i;
      builderFeeTenthBps = OptionalInt.of(getInt16LE(_data, i));
      i += 2;
    }
    final OptionalLong isolatedPositionDeposit;
    if (_data[i] == 0) {
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
    i += Borsh.writeArrayChecked(uuid, 8, _data, i);
    i += Borsh.writeOptional(takeProfitOrderParams, _data, i);
    i += Borsh.writeOptional(stopLossOrderParams, _data, i);
    i += Borsh.writeOptionalshort(maxMarginRatio, _data, i);
    i += Borsh.writeOptionalbyte(builderIdx, _data, i);
    i += Borsh.writeOptionalshort(builderFeeTenthBps, _data, i);
    i += Borsh.writeOptional(isolatedPositionDeposit, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return signedMsgOrderParams.l()
         + 32
         + 8
         + Borsh.lenArray(uuid)
         + (takeProfitOrderParams == null ? 1 : (1 + takeProfitOrderParams.l()))
         + (stopLossOrderParams == null ? 1 : (1 + stopLossOrderParams.l()))
         + (maxMarginRatio == null || maxMarginRatio.isEmpty() ? 1 : (1 + 2))
         + (builderIdx == null || builderIdx.isEmpty() ? 1 : (1 + 1))
         + (builderFeeTenthBps == null || builderFeeTenthBps.isEmpty() ? 1 : (1 + 2))
         + (isolatedPositionDeposit == null || isolatedPositionDeposit.isEmpty() ? 1 : (1 + 8));
  }
}

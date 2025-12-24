package software.sava.idl.clients.drift.gen.types;

import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

public record SignedMsgTriggerOrderParams(long triggerPrice, long baseAssetAmount) implements SerDe {

  public static final int BYTES = 16;

  public static SignedMsgTriggerOrderParams read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var triggerPrice = getInt64LE(_data, i);
    i += 8;
    final var baseAssetAmount = getInt64LE(_data, i);
    return new SignedMsgTriggerOrderParams(triggerPrice, baseAssetAmount);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt64LE(_data, i, triggerPrice);
    i += 8;
    putInt64LE(_data, i, baseAssetAmount);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

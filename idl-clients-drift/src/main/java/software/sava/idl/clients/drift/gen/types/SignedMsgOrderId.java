package software.sava.idl.clients.drift.gen.types;

import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

public record SignedMsgOrderId(byte[] uuid,
                               long maxSlot,
                               int orderId,
                               int padding) implements SerDe {

  public static final int BYTES = 24;
  public static final int UUID_LEN = 8;

  public static SignedMsgOrderId read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var uuid = new byte[8];
    i += SerDeUtil.readArray(uuid, _data, i);
    final var maxSlot = getInt64LE(_data, i);
    i += 8;
    final var orderId = getInt32LE(_data, i);
    i += 4;
    final var padding = getInt32LE(_data, i);
    return new SignedMsgOrderId(uuid,
                                maxSlot,
                                orderId,
                                padding);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += SerDeUtil.writeArrayChecked(uuid, 8, _data, i);
    putInt64LE(_data, i, maxSlot);
    i += 8;
    putInt32LE(_data, i, orderId);
    i += 4;
    putInt32LE(_data, i, padding);
    i += 4;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

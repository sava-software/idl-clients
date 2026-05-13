package software.sava.idl.clients.phoenix.perpetuals.gen.types;

import java.util.OptionalLong;

import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

/// Parameters for updating a spline's mid-price with anti-reordering controls.
///
public record UpdateSplinePriceParamsWithOrdering(long newMidPrice,
                                                  OptionalLong userUpdateSlot,
                                                  boolean refreshRegions,
                                                  long userSequenceNumber,
                                                  byte[] clientOrderId,
                                                  boolean overrideSequenceNumber) implements SerDe {

  public static final int CLIENT_ORDER_ID_LEN = 16;
  public static final int NEW_MID_PRICE_OFFSET = 0;
  public static final int USER_UPDATE_SLOT_OFFSET = 9;

  public static UpdateSplinePriceParamsWithOrdering read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var newMidPrice = getInt64LE(_data, i);
    i += 8;
    final OptionalLong userUpdateSlot;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      userUpdateSlot = OptionalLong.empty();
      ++i;
    } else {
      ++i;
      userUpdateSlot = OptionalLong.of(getInt64LE(_data, i));
      i += 8;
    }
    final var refreshRegions = _data[i] == 1;
    ++i;
    final var userSequenceNumber = getInt64LE(_data, i);
    i += 8;
    final var clientOrderId = new byte[16];
    i += SerDeUtil.readArray(clientOrderId, _data, i);
    final var overrideSequenceNumber = _data[i] == 1;
    return new UpdateSplinePriceParamsWithOrdering(newMidPrice,
                                                   userUpdateSlot,
                                                   refreshRegions,
                                                   userSequenceNumber,
                                                   clientOrderId,
                                                   overrideSequenceNumber);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt64LE(_data, i, newMidPrice);
    i += 8;
    i += SerDeUtil.writeOptional(1, userUpdateSlot, _data, i);
    _data[i] = (byte) (refreshRegions ? 1 : 0);
    ++i;
    putInt64LE(_data, i, userSequenceNumber);
    i += 8;
    i += SerDeUtil.writeArrayChecked(clientOrderId, 16, _data, i);
    _data[i] = (byte) (overrideSequenceNumber ? 1 : 0);
    ++i;
    return i - _offset;
  }

  @Override
  public int l() {
    return 8
         + (userUpdateSlot == null || userUpdateSlot.isEmpty() ? 1 : (1 + 8))
         + 1
         + 8
         + SerDeUtil.lenArray(clientOrderId)
         + 1;
  }
}

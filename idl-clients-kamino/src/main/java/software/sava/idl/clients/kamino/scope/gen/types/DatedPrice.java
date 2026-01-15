package software.sava.idl.clients.kamino.scope.gen.types;

import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

public record DatedPrice(Price price,
                         long lastUpdatedSlot,
                         long unixTimestamp,
                         byte[] genericData) implements SerDe {

  public static final int BYTES = 56;
  public static final int GENERIC_DATA_LEN = 24;

  public static final int PRICE_OFFSET = 0;
  public static final int LAST_UPDATED_SLOT_OFFSET = 16;
  public static final int UNIX_TIMESTAMP_OFFSET = 24;
  public static final int GENERIC_DATA_OFFSET = 32;

  public static DatedPrice read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var price = Price.read(_data, i);
    i += price.l();
    final var lastUpdatedSlot = getInt64LE(_data, i);
    i += 8;
    final var unixTimestamp = getInt64LE(_data, i);
    i += 8;
    final var genericData = new byte[24];
    SerDeUtil.readArray(genericData, _data, i);
    return new DatedPrice(price,
                          lastUpdatedSlot,
                          unixTimestamp,
                          genericData);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += price.write(_data, i);
    putInt64LE(_data, i, lastUpdatedSlot);
    i += 8;
    putInt64LE(_data, i, unixTimestamp);
    i += 8;
    i += SerDeUtil.writeArrayChecked(genericData, 24, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

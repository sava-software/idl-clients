package software.sava.idl.clients.kamino.farms.gen.types;

import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;
import software.sava.idl.clients.kamino.scope.gen.types.Price;

import static software.sava.core.encoding.ByteUtil.getInt16LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt16LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

public record DatedPrice(Price price,
                         long lastUpdatedSlot,
                         long unixTimestamp,
                         long[] reserved,
                         short[] reserved2,
                         int index) implements SerDe {

  public static final int BYTES = 56;
  public static final int RESERVED_LEN = 2;
  public static final int RESERVED_2_LEN = 3;

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
    final var reserved = new long[2];
    i += SerDeUtil.readArray(reserved, _data, i);
    final var reserved2 = new short[3];
    i += SerDeUtil.readArray(reserved2, _data, i);
    final var index = getInt16LE(_data, i);
    return new DatedPrice(price,
                          lastUpdatedSlot,
                          unixTimestamp,
                          reserved,
                          reserved2,
                          index);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += price.write(_data, i);
    putInt64LE(_data, i, lastUpdatedSlot);
    i += 8;
    putInt64LE(_data, i, unixTimestamp);
    i += 8;
    i += SerDeUtil.writeArrayChecked(reserved, 2, _data, i);
    i += SerDeUtil.writeArrayChecked(reserved2, 3, _data, i);
    putInt16LE(_data, i, index);
    i += 2;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

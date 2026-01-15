package software.sava.idl.clients.kamino.scope.gen.types;

import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

public record TokenMetadata(byte[] name,
                            long maxAgePriceSlots,
                            long groupIdsBitset,
                            long[] reserved) implements SerDe {

  public static final int BYTES = 168;
  public static final int NAME_LEN = 32;
  public static final int RESERVED_LEN = 15;

  public static final int NAME_OFFSET = 0;
  public static final int MAX_AGE_PRICE_SLOTS_OFFSET = 32;
  public static final int GROUP_IDS_BITSET_OFFSET = 40;
  public static final int RESERVED_OFFSET = 48;

  public static TokenMetadata read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var name = new byte[32];
    i += SerDeUtil.readArray(name, _data, i);
    final var maxAgePriceSlots = getInt64LE(_data, i);
    i += 8;
    final var groupIdsBitset = getInt64LE(_data, i);
    i += 8;
    final var reserved = new long[15];
    SerDeUtil.readArray(reserved, _data, i);
    return new TokenMetadata(name,
                             maxAgePriceSlots,
                             groupIdsBitset,
                             reserved);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += SerDeUtil.writeArrayChecked(name, 32, _data, i);
    putInt64LE(_data, i, maxAgePriceSlots);
    i += 8;
    putInt64LE(_data, i, groupIdsBitset);
    i += 8;
    i += SerDeUtil.writeArrayChecked(reserved, 15, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

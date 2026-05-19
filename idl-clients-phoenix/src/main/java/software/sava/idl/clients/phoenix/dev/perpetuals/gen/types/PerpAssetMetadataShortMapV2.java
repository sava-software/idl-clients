package software.sava.idl.clients.phoenix.dev.perpetuals.gen.types;

import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

/// Zero-copy ShortMapV2<Symbol, PerpAssetMetadata, 1024> layout.
///
public record PerpAssetMetadataShortMapV2(int slotsUsed,
                                          int tombstones,
                                          long capacity,
                                          PerpAssetMapEntry[] data) implements SerDe {

  public static final int BYTES = 1622032;
  public static final int DATA_LEN = 1024;

  public static final int SLOTS_USED_OFFSET = 0;
  public static final int TOMBSTONES_OFFSET = 4;
  public static final int CAPACITY_OFFSET = 8;
  public static final int DATA_OFFSET = 16;

  public static PerpAssetMetadataShortMapV2 read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var slotsUsed = getInt32LE(_data, i);
    i += 4;
    final var tombstones = getInt32LE(_data, i);
    i += 4;
    final var capacity = getInt64LE(_data, i);
    i += 8;
    final var data = new PerpAssetMapEntry[1024];
    SerDeUtil.readArray(data, PerpAssetMapEntry::read, _data, i);
    return new PerpAssetMetadataShortMapV2(slotsUsed,
                                           tombstones,
                                           capacity,
                                           data);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt32LE(_data, i, slotsUsed);
    i += 4;
    putInt32LE(_data, i, tombstones);
    i += 4;
    putInt64LE(_data, i, capacity);
    i += 8;
    i += SerDeUtil.writeArrayChecked(data, 1024, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

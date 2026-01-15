package software.sava.idl.clients.kamino.scope.gen.types;

import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.encoding.ByteUtil.getInt16LE;
import static software.sava.core.encoding.ByteUtil.putInt16LE;

public record UpdateOracleMappingAndMetadataEntriesWithId(int entryId, UpdateOracleMappingAndMetadataEntry[] updates) implements SerDe {

  public static final int ENTRY_ID_OFFSET = 0;
  public static final int UPDATES_OFFSET = 2;

  public static UpdateOracleMappingAndMetadataEntriesWithId read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var entryId = getInt16LE(_data, i);
    i += 2;
    final var updates = SerDeUtil.readVector(4, UpdateOracleMappingAndMetadataEntry.class, UpdateOracleMappingAndMetadataEntry::read, _data, i);
    return new UpdateOracleMappingAndMetadataEntriesWithId(entryId, updates);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt16LE(_data, i, entryId);
    i += 2;
    i += SerDeUtil.writeVector(4, updates, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return 2 + SerDeUtil.lenVector(4, updates);
  }
}

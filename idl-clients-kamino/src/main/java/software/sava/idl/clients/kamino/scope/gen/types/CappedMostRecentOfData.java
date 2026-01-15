package software.sava.idl.clients.kamino.scope.gen.types;

import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.encoding.ByteUtil.getInt16LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt16LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

public record CappedMostRecentOfData(short[] sourceEntries,
                                     int maxDivergenceBps,
                                     long sourcesMaxAgeS,
                                     int capEntry) implements SerDe {

  public static final int BYTES = 20;
  public static final int SOURCE_ENTRIES_LEN = 4;

  public static final int SOURCE_ENTRIES_OFFSET = 0;
  public static final int MAX_DIVERGENCE_BPS_OFFSET = 8;
  public static final int SOURCES_MAX_AGE_S_OFFSET = 10;
  public static final int CAP_ENTRY_OFFSET = 18;

  public static CappedMostRecentOfData read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var sourceEntries = new short[4];
    i += SerDeUtil.readArray(sourceEntries, _data, i);
    final var maxDivergenceBps = getInt16LE(_data, i);
    i += 2;
    final var sourcesMaxAgeS = getInt64LE(_data, i);
    i += 8;
    final var capEntry = getInt16LE(_data, i);
    return new CappedMostRecentOfData(sourceEntries,
                                      maxDivergenceBps,
                                      sourcesMaxAgeS,
                                      capEntry);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += SerDeUtil.writeArrayChecked(sourceEntries, 4, _data, i);
    putInt16LE(_data, i, maxDivergenceBps);
    i += 2;
    putInt64LE(_data, i, sourcesMaxAgeS);
    i += 8;
    putInt16LE(_data, i, capEntry);
    i += 2;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

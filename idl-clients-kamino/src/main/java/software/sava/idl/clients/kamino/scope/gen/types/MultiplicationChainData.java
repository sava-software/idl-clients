package software.sava.idl.clients.kamino.scope.gen.types;

import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

public record MultiplicationChainData(short[] sourceEntries, long sourcesMaxAgeS) implements SerDe {

  public static final int BYTES = 16;
  public static final int SOURCE_ENTRIES_LEN = 4;

  public static final int SOURCE_ENTRIES_OFFSET = 0;
  public static final int SOURCES_MAX_AGE_S_OFFSET = 8;

  public static MultiplicationChainData read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var sourceEntries = new short[4];
    i += SerDeUtil.readArray(sourceEntries, _data, i);
    final var sourcesMaxAgeS = getInt64LE(_data, i);
    return new MultiplicationChainData(sourceEntries, sourcesMaxAgeS);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += SerDeUtil.writeArrayChecked(sourceEntries, 4, _data, i);
    putInt64LE(_data, i, sourcesMaxAgeS);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

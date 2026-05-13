package software.sava.idl.clients.phoenix.perpetuals.gen.types;

import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;

/// @param logBatchIndex Index of this log batch for tracking event sequences.
public record LogHeader(int logBatchIndex) implements SerDe {

  public static final int BYTES = 4;

  public static final int LOG_BATCH_INDEX_OFFSET = 0;

  public static LogHeader read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var logBatchIndex = getInt32LE(_data, _offset);
    return new LogHeader(logBatchIndex);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt32LE(_data, i, logBatchIndex);
    i += 4;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

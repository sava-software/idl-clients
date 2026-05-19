package software.sava.idl.clients.phoenix.perpetuals.gen.types;

import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

/// Pair of sequence number and last update slot.
///
public record SequenceNumber(long sequenceNumber, long lastUpdateSlot) implements SerDe {

  public static final int BYTES = 16;

  public static final int SEQUENCE_NUMBER_OFFSET = 0;
  public static final int LAST_UPDATE_SLOT_OFFSET = 8;

  public static SequenceNumber read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var sequenceNumber = getInt64LE(_data, i);
    i += 8;
    final var lastUpdateSlot = getInt64LE(_data, i);
    return new SequenceNumber(sequenceNumber, lastUpdateSlot);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt64LE(_data, i, sequenceNumber);
    i += 8;
    putInt64LE(_data, i, lastUpdateSlot);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

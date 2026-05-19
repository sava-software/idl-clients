package software.sava.idl.clients.phoenix.dev.perpetuals.gen.types;

import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

/// Borsh payload for cancelling an escrow request by sequence number.
///
public record CancelEscrowRequestParams(long sequenceNumber) implements SerDe {

  public static final int BYTES = 8;

  public static final int SEQUENCE_NUMBER_OFFSET = 0;

  public static CancelEscrowRequestParams read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var sequenceNumber = getInt64LE(_data, _offset);
    return new CancelEscrowRequestParams(sequenceNumber);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt64LE(_data, i, sequenceNumber);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

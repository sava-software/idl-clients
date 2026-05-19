package software.sava.idl.clients.phoenix.perpetuals.gen.types;

import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

/// uPnL risk factor encoded as basis points.
///
public record UPnlRiskFactor(long inner) implements SerDe {

  public static final int BYTES = 8;

  public static final int INNER_OFFSET = 0;

  public static UPnlRiskFactor read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var inner = getInt64LE(_data, _offset);
    return new UPnlRiskFactor(inner);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt64LE(_data, i, inner);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

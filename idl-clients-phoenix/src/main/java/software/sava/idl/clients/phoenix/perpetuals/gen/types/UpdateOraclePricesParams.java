package software.sava.idl.clients.phoenix.perpetuals.gen.types;

import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

/// Oracle instruction payload containing one or more price updates.
///
public record UpdateOraclePricesParams(OraclePriceUpdate[] updates) implements SerDe {

  public static final int UPDATES_OFFSET = 0;

  public static UpdateOraclePricesParams read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var updates = SerDeUtil.readVector(4, OraclePriceUpdate.class, OraclePriceUpdate::read, _data, _offset);
    return new UpdateOraclePricesParams(updates);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += SerDeUtil.writeVector(4, updates, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return SerDeUtil.lenVector(4, updates);
  }
}

package software.sava.idl.clients.phoenix.dev.perpetuals.gen.types;

import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

/// Oracle instruction payload containing ordered price updates and timestamp reset policy.
///
public record UpdateOraclePricesWithOrderingInstruction(long updateTimestamp,
                                                        OraclePriceUpdate[] updates,
                                                        boolean shouldResetTimestamp) implements SerDe {

  public static final int UPDATE_TIMESTAMP_OFFSET = 0;
  public static final int UPDATES_OFFSET = 8;

  public static UpdateOraclePricesWithOrderingInstruction read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var updateTimestamp = getInt64LE(_data, i);
    i += 8;
    final var updates = SerDeUtil.readVector(4, OraclePriceUpdate.class, OraclePriceUpdate::read, _data, i);
    i += SerDeUtil.lenVector(4, updates);
    final var shouldResetTimestamp = _data[i] == 1;
    return new UpdateOraclePricesWithOrderingInstruction(updateTimestamp, updates, shouldResetTimestamp);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt64LE(_data, i, updateTimestamp);
    i += 8;
    i += SerDeUtil.writeVector(4, updates, _data, i);
    _data[i] = (byte) (shouldResetTimestamp ? 1 : 0);
    ++i;
    return i - _offset;
  }

  @Override
  public int l() {
    return 8 + SerDeUtil.lenVector(4, updates) + 1;
  }
}

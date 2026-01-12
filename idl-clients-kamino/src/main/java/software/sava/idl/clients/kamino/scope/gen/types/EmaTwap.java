package software.sava.idl.clients.kamino.scope.gen.types;

import java.math.BigInteger;

import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.encoding.ByteUtil.getInt128LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt128LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

/// @param updatesTracker1h The sample tracker is a 64 bit number where each bit represents a point in time.
public record EmaTwap(long lastUpdateSlot,
                      long lastUpdateUnixTimestamp,
                      BigInteger currentEma1h,
                      long updatesTracker1h,
                      long padding0,
                      BigInteger currentEma8h,
                      BigInteger currentEma24h,
                      long updatesTracker8h,
                      long updatesTracker24h,
                      BigInteger[] padding1) implements SerDe {

  public static final int BYTES = 672;
  public static final int PADDING_1_LEN = 36;

  public static EmaTwap read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var lastUpdateSlot = getInt64LE(_data, i);
    i += 8;
    final var lastUpdateUnixTimestamp = getInt64LE(_data, i);
    i += 8;
    final var currentEma1h = getInt128LE(_data, i);
    i += 16;
    final var updatesTracker1h = getInt64LE(_data, i);
    i += 8;
    final var padding0 = getInt64LE(_data, i);
    i += 8;
    final var currentEma8h = getInt128LE(_data, i);
    i += 16;
    final var currentEma24h = getInt128LE(_data, i);
    i += 16;
    final var updatesTracker8h = getInt64LE(_data, i);
    i += 8;
    final var updatesTracker24h = getInt64LE(_data, i);
    i += 8;
    final var padding1 = new BigInteger[36];
    SerDeUtil.read128Array(padding1, _data, i);
    return new EmaTwap(lastUpdateSlot,
                       lastUpdateUnixTimestamp,
                       currentEma1h,
                       updatesTracker1h,
                       padding0,
                       currentEma8h,
                       currentEma24h,
                       updatesTracker8h,
                       updatesTracker24h,
                       padding1);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt64LE(_data, i, lastUpdateSlot);
    i += 8;
    putInt64LE(_data, i, lastUpdateUnixTimestamp);
    i += 8;
    putInt128LE(_data, i, currentEma1h);
    i += 16;
    putInt64LE(_data, i, updatesTracker1h);
    i += 8;
    putInt64LE(_data, i, padding0);
    i += 8;
    putInt128LE(_data, i, currentEma8h);
    i += 16;
    putInt128LE(_data, i, currentEma24h);
    i += 16;
    putInt64LE(_data, i, updatesTracker8h);
    i += 8;
    putInt64LE(_data, i, updatesTracker24h);
    i += 8;
    i += SerDeUtil.write128ArrayChecked(padding1, 36, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

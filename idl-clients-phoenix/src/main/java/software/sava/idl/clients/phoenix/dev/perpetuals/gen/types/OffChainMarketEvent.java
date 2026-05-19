package software.sava.idl.clients.phoenix.dev.perpetuals.gen.types;

import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;

/// Borsh payload of the Log instruction after the 8-byte instruction discriminator.
///
public record OffChainMarketEvent(int batchIndex, MarketEvent[] events) implements SerDe {

  public static final int BATCH_INDEX_OFFSET = 0;
  public static final int EVENTS_OFFSET = 4;

  public static OffChainMarketEvent read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var batchIndex = getInt32LE(_data, i);
    i += 4;
    final var events = SerDeUtil.readVector(4, MarketEvent.class, MarketEvent::read, _data, i);
    return new OffChainMarketEvent(batchIndex, events);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt32LE(_data, i, batchIndex);
    i += 4;
    i += SerDeUtil.writeVector(4, events, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return 4 + SerDeUtil.lenVector(4, events);
  }
}

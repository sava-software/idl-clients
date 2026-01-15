package software.sava.idl.clients.drift.gen.types;

import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.core.encoding.ByteUtil.getInt16LE;
import static software.sava.core.encoding.ByteUtil.putInt16LE;

public record MarketIdentifier(MarketType marketType, int marketIndex) implements SerDe {

  public static final int BYTES = 3;

  public static final int MARKET_TYPE_OFFSET = 0;
  public static final int MARKET_INDEX_OFFSET = 1;

  public static MarketIdentifier read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var marketType = MarketType.read(_data, i);
    i += marketType.l();
    final var marketIndex = getInt16LE(_data, i);
    return new MarketIdentifier(marketType, marketIndex);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += marketType.write(_data, i);
    putInt16LE(_data, i, marketIndex);
    i += 2;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

package software.sava.idl.clients.oracles.pyth.receiver.gen.types;

import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

/// The time weighted average price & conf for a feed over the window start_time, end_time.
/// This type is used to persist the calculated TWAP in TwapUpdate accounts on Solana.
///
/// @param downSlotsRatio Ratio out of 1_000_000, where a value of 1_000_000 represents
///                       all slots were missed and 0 represents no slots were missed.
public record TwapPrice(byte[] feedId,
                        long startTime,
                        long endTime,
                        long price,
                        long conf,
                        int exponent,
                        int downSlotsRatio) implements SerDe {

  public static final int BYTES = 72;
  public static final int FEED_ID_LEN = 32;

  public static final int FEED_ID_OFFSET = 0;
  public static final int START_TIME_OFFSET = 32;
  public static final int END_TIME_OFFSET = 40;
  public static final int PRICE_OFFSET = 48;
  public static final int CONF_OFFSET = 56;
  public static final int EXPONENT_OFFSET = 64;
  public static final int DOWN_SLOTS_RATIO_OFFSET = 68;

  public static TwapPrice read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var feedId = new byte[32];
    i += SerDeUtil.readArray(feedId, _data, i);
    final var startTime = getInt64LE(_data, i);
    i += 8;
    final var endTime = getInt64LE(_data, i);
    i += 8;
    final var price = getInt64LE(_data, i);
    i += 8;
    final var conf = getInt64LE(_data, i);
    i += 8;
    final var exponent = getInt32LE(_data, i);
    i += 4;
    final var downSlotsRatio = getInt32LE(_data, i);
    return new TwapPrice(feedId,
                         startTime,
                         endTime,
                         price,
                         conf,
                         exponent,
                         downSlotsRatio);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += SerDeUtil.writeArrayChecked(feedId, 32, _data, i);
    putInt64LE(_data, i, startTime);
    i += 8;
    putInt64LE(_data, i, endTime);
    i += 8;
    putInt64LE(_data, i, price);
    i += 8;
    putInt64LE(_data, i, conf);
    i += 8;
    putInt32LE(_data, i, exponent);
    i += 4;
    putInt32LE(_data, i, downSlotsRatio);
    i += 4;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

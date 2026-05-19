package software.sava.idl.clients.phoenix.perpetuals.gen.types;

import software.sava.core.programs.Discriminator;
import software.sava.idl.clients.core.gen.SerDeUtil;
import software.sava.idl.clients.phoenix.perpetuals.gen.types.BaseLots;
import software.sava.idl.clients.phoenix.perpetuals.gen.types.Side;
import software.sava.idl.clients.phoenix.perpetuals.gen.types.Ticks;

import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

/// MarketEvent::OrderRejected Borsh variant 4.
/// Payload type: OrderRejectedEvent.
///
public record OrderRejectedEvent(Discriminator discriminator,
                                 int orderIndex,
                                 byte[] clientOrderId,
                                 Ticks price,
                                 Side side,
                                 BaseLots numBaseLots,
                                 byte[] reason) implements EternalEvent {

  public static final int BYTES = 77;
  public static final int CLIENT_ORDER_ID_LEN = 16;
  public static final int REASON_LEN = 32;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(4, 0, 0, 0, 0, 0, 0, 0);

  public static final int ORDER_INDEX_OFFSET = 8;
  public static final int CLIENT_ORDER_ID_OFFSET = 12;
  public static final int PRICE_OFFSET = 28;
  public static final int SIDE_OFFSET = 36;
  public static final int NUM_BASE_LOTS_OFFSET = 37;
  public static final int REASON_OFFSET = 45;

  public static OrderRejectedEvent read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var orderIndex = getInt32LE(_data, i);
    i += 4;
    final var clientOrderId = new byte[16];
    i += SerDeUtil.readArray(clientOrderId, _data, i);
    final var price = Ticks.read(_data, i);
    i += price.l();
    final var side = Side.read(_data, i);
    i += side.l();
    final var numBaseLots = BaseLots.read(_data, i);
    i += numBaseLots.l();
    final var reason = new byte[32];
    SerDeUtil.readArray(reason, _data, i);
    return new OrderRejectedEvent(discriminator,
                                  orderIndex,
                                  clientOrderId,
                                  price,
                                  side,
                                  numBaseLots,
                                  reason);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    putInt32LE(_data, i, orderIndex);
    i += 4;
    i += SerDeUtil.writeArrayChecked(clientOrderId, 16, _data, i);
    i += price.write(_data, i);
    i += side.write(_data, i);
    i += numBaseLots.write(_data, i);
    i += SerDeUtil.writeArrayChecked(reason, 32, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

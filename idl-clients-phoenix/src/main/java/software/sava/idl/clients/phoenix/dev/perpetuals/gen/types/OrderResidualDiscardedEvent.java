package software.sava.idl.clients.phoenix.dev.perpetuals.gen.types;

import software.sava.core.programs.Discriminator;
import software.sava.idl.clients.core.gen.SerDeUtil;
import software.sava.idl.clients.phoenix.dev.perpetuals.gen.types.BaseLots;
import software.sava.idl.clients.phoenix.dev.perpetuals.gen.types.OrderResidualDiscardReason;
import software.sava.idl.clients.phoenix.dev.perpetuals.gen.types.Side;
import software.sava.idl.clients.phoenix.dev.perpetuals.gen.types.Ticks;

import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

/// MarketEvent::OrderResidualDiscarded Borsh variant 66.
/// Payload type: OrderResidualDiscardedEvent.
///
public record OrderResidualDiscardedEvent(Discriminator discriminator,
                                          byte[] clientOrderId,
                                          Ticks price,
                                          Side side,
                                          BaseLots baseLotsDiscarded,
                                          OrderResidualDiscardReason reason) implements EternalEvent {

  public static final int BYTES = 42;
  public static final int CLIENT_ORDER_ID_LEN = 16;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(66, 0, 0, 0, 0, 0, 0, 0);

  public static final int CLIENT_ORDER_ID_OFFSET = 8;
  public static final int PRICE_OFFSET = 24;
  public static final int SIDE_OFFSET = 32;
  public static final int BASE_LOTS_DISCARDED_OFFSET = 33;
  public static final int REASON_OFFSET = 41;

  public static OrderResidualDiscardedEvent read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var clientOrderId = new byte[16];
    i += SerDeUtil.readArray(clientOrderId, _data, i);
    final var price = Ticks.read(_data, i);
    i += price.l();
    final var side = Side.read(_data, i);
    i += side.l();
    final var baseLotsDiscarded = BaseLots.read(_data, i);
    i += baseLotsDiscarded.l();
    final var reason = OrderResidualDiscardReason.read(_data, i);
    return new OrderResidualDiscardedEvent(discriminator,
                                           clientOrderId,
                                           price,
                                           side,
                                           baseLotsDiscarded,
                                           reason);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    i += SerDeUtil.writeArrayChecked(clientOrderId, 16, _data, i);
    i += price.write(_data, i);
    i += side.write(_data, i);
    i += baseLotsDiscarded.write(_data, i);
    i += reason.write(_data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

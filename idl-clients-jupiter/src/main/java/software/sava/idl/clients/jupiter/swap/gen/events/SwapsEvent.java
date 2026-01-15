package software.sava.idl.clients.jupiter.swap.gen.events;

import software.sava.core.programs.Discriminator;
import software.sava.idl.clients.core.gen.SerDeUtil;
import software.sava.idl.clients.jupiter.swap.gen.types.SwapEventV2;

import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record SwapsEvent(Discriminator discriminator, SwapEventV2[] swapEvents) implements JupiterEvent {

  public static final Discriminator DISCRIMINATOR = toDiscriminator(152, 47, 78, 235, 192, 96, 110, 106);

  public static final int SWAP_EVENTS_OFFSET = 8;

  public static SwapsEvent read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var swapEvents = SerDeUtil.readVector(4, SwapEventV2.class, SwapEventV2::read, _data, i);
    return new SwapsEvent(discriminator, swapEvents);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    i += SerDeUtil.writeVector(4, swapEvents, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return 8 + SerDeUtil.lenVector(4, swapEvents);
  }
}

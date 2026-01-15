package software.sava.idl.clients.jupiter.lend_borrow.gen.events;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record LogUpdateRebalancer(Discriminator discriminator, PublicKey newRebalancer) implements LendingEvent {

  public static final int BYTES = 40;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(66, 79, 144, 204, 26, 217, 153, 225);

  public static final int NEW_REBALANCER_OFFSET = 8;

  public static LogUpdateRebalancer read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var newRebalancer = readPubKey(_data, i);
    return new LogUpdateRebalancer(discriminator, newRebalancer);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    newRebalancer.write(_data, i);
    i += 32;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

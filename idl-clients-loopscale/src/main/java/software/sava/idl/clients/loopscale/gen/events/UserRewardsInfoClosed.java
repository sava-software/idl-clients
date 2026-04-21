package software.sava.idl.clients.loopscale.gen.events;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record UserRewardsInfoClosed(Discriminator discriminator, PublicKey address) implements LoopscaleEvent {

  public static final int BYTES = 40;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(58, 168, 111, 2, 82, 232, 119, 37);

  public static final int ADDRESS_OFFSET = 8;

  public static UserRewardsInfoClosed read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var address = readPubKey(_data, i);
    return new UserRewardsInfoClosed(discriminator, address);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    address.write(_data, i);
    i += 32;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

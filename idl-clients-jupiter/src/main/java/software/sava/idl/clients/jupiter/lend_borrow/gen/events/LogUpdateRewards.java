package software.sava.idl.clients.jupiter.lend_borrow.gen.events;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record LogUpdateRewards(Discriminator discriminator, PublicKey rewardsRateModel) implements LendingEvent {

  public static final int BYTES = 40;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(37, 13, 111, 186, 47, 245, 162, 121);

  public static LogUpdateRewards read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var rewardsRateModel = readPubKey(_data, i);
    return new LogUpdateRewards(discriminator, rewardsRateModel);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    rewardsRateModel.write(_data, i);
    i += 32;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

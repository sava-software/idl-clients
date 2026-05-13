package software.sava.idl.clients.nt.bundle.gen.events;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record NewKeeperSet(Discriminator discriminator, PublicKey keeper) implements NtbundleEvent {

  public static final int BYTES = 40;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(33, 68, 171, 11, 43, 129, 192, 45);

  public static final int KEEPER_OFFSET = 8;

  public static NewKeeperSet read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var keeper = readPubKey(_data, i);
    return new NewKeeperSet(discriminator, keeper);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    keeper.write(_data, i);
    i += 32;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

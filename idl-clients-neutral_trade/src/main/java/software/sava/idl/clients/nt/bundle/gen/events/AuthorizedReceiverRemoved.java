package software.sava.idl.clients.nt.bundle.gen.events;

import software.sava.core.programs.Discriminator;

import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record AuthorizedReceiverRemoved(Discriminator discriminator, boolean allowed) implements NtbundleEvent {

  public static final int BYTES = 9;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(69, 73, 150, 51, 198, 98, 43, 0);

  public static final int ALLOWED_OFFSET = 8;

  public static AuthorizedReceiverRemoved read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var allowed = _data[i] == 1;
    return new AuthorizedReceiverRemoved(discriminator, allowed);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    _data[i] = (byte) (allowed ? 1 : 0);
    ++i;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

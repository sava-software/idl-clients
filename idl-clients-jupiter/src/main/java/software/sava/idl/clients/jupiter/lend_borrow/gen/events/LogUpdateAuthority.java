package software.sava.idl.clients.jupiter.lend_borrow.gen.events;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record LogUpdateAuthority(Discriminator discriminator, PublicKey newAuthority) implements LendingEvent {

  public static final int BYTES = 40;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(150, 152, 157, 143, 6, 135, 193, 101);

  public static final int NEW_AUTHORITY_OFFSET = 8;

  public static LogUpdateAuthority read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var newAuthority = readPubKey(_data, i);
    return new LogUpdateAuthority(discriminator, newAuthority);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    newAuthority.write(_data, i);
    i += 32;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

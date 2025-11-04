package software.sava.idl.clients.jupiter.voter.gen.events;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.programs.Discriminator.createDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record SetVoteDelegateEvent(Discriminator discriminator,
                                   PublicKey escrowOwner,
                                   PublicKey oldDelegate,
                                   PublicKey newDelegate) implements LockedVoterEvent {

  public static final int BYTES = 104;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(179, 231, 197, 195, 129, 224, 201, 14);

  public static SetVoteDelegateEvent read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createDiscriminator(_data, _offset, 8);
    int i = _offset + discriminator.length();
    final var escrowOwner = readPubKey(_data, i);
    i += 32;
    final var oldDelegate = readPubKey(_data, i);
    i += 32;
    final var newDelegate = readPubKey(_data, i);
    return new SetVoteDelegateEvent(discriminator, escrowOwner, oldDelegate, newDelegate);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    escrowOwner.write(_data, i);
    i += 32;
    oldDelegate.write(_data, i);
    i += 32;
    newDelegate.write(_data, i);
    i += 32;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

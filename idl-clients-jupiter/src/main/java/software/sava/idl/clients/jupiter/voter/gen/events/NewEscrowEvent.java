package software.sava.idl.clients.jupiter.voter.gen.events;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record NewEscrowEvent(Discriminator discriminator,
                             PublicKey escrow,
                             PublicKey escrowOwner,
                             PublicKey locker,
                             long timestamp) implements LockedVoterEvent {

  public static final int BYTES = 112;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(179, 231, 197, 195, 129, 224, 201, 14);

  public static final int ESCROW_OFFSET = 8;
  public static final int ESCROW_OWNER_OFFSET = 40;
  public static final int LOCKER_OFFSET = 72;
  public static final int TIMESTAMP_OFFSET = 104;

  public static NewEscrowEvent read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createDiscriminator(_data, _offset, 8);
    int i = _offset + discriminator.length();
    final var escrow = readPubKey(_data, i);
    i += 32;
    final var escrowOwner = readPubKey(_data, i);
    i += 32;
    final var locker = readPubKey(_data, i);
    i += 32;
    final var timestamp = getInt64LE(_data, i);
    return new NewEscrowEvent(discriminator,
                              escrow,
                              escrowOwner,
                              locker,
                              timestamp);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    escrow.write(_data, i);
    i += 32;
    escrowOwner.write(_data, i);
    i += 32;
    locker.write(_data, i);
    i += 32;
    putInt64LE(_data, i, timestamp);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

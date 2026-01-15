package software.sava.idl.clients.jupiter.voter.gen.events;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record ExitEscrowEvent(Discriminator discriminator,
                              PublicKey escrowOwner,
                              PublicKey locker,
                              long timestamp,
                              long lockerSupply,
                              long releasedAmount) implements LockedVoterEvent {

  public static final int BYTES = 96;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(179, 231, 197, 195, 129, 224, 201, 14);

  public static final int ESCROW_OWNER_OFFSET = 8;
  public static final int LOCKER_OFFSET = 40;
  public static final int TIMESTAMP_OFFSET = 72;
  public static final int LOCKER_SUPPLY_OFFSET = 80;
  public static final int RELEASED_AMOUNT_OFFSET = 88;

  public static ExitEscrowEvent read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createDiscriminator(_data, _offset, 8);
    int i = _offset + discriminator.length();
    final var escrowOwner = readPubKey(_data, i);
    i += 32;
    final var locker = readPubKey(_data, i);
    i += 32;
    final var timestamp = getInt64LE(_data, i);
    i += 8;
    final var lockerSupply = getInt64LE(_data, i);
    i += 8;
    final var releasedAmount = getInt64LE(_data, i);
    return new ExitEscrowEvent(discriminator,
                               escrowOwner,
                               locker,
                               timestamp,
                               lockerSupply,
                               releasedAmount);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    escrowOwner.write(_data, i);
    i += 32;
    locker.write(_data, i);
    i += 32;
    putInt64LE(_data, i, timestamp);
    i += 8;
    putInt64LE(_data, i, lockerSupply);
    i += 8;
    putInt64LE(_data, i, releasedAmount);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

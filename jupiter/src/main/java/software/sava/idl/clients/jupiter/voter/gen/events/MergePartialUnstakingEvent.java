package software.sava.idl.clients.jupiter.voter.gen.events;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record MergePartialUnstakingEvent(Discriminator discriminator,
                                         PublicKey partialUnstake,
                                         PublicKey escrow,
                                         long amount) implements LockedVoterEvent {

  public static final int BYTES = 80;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(179, 231, 197, 195, 129, 224, 201, 14);

  public static MergePartialUnstakingEvent read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createDiscriminator(_data, _offset, 8);
    int i = _offset + discriminator.length();
    final var partialUnstake = readPubKey(_data, i);
    i += 32;
    final var escrow = readPubKey(_data, i);
    i += 32;
    final var amount = getInt64LE(_data, i);
    return new MergePartialUnstakingEvent(discriminator, partialUnstake, escrow, amount);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    partialUnstake.write(_data, i);
    i += 32;
    escrow.write(_data, i);
    i += 32;
    putInt64LE(_data, i, amount);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

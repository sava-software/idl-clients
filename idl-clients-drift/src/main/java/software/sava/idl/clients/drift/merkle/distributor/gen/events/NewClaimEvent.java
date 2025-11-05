package software.sava.idl.clients.drift.merkle.distributor.gen.events;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record NewClaimEvent(Discriminator discriminator,
                            PublicKey claimant,
                            long timestamp,
                            long amountClaimed,
                            long amountForgone) implements MerkleDistributorEvent {

  public static final int BYTES = 64;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(156, 211, 255, 5, 24, 206, 76, 171);

  public static NewClaimEvent read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createDiscriminator(_data, _offset, 8);
    int i = _offset + discriminator.length();
    final var claimant = readPubKey(_data, i);
    i += 32;
    final var timestamp = getInt64LE(_data, i);
    i += 8;
    final var amountClaimed = getInt64LE(_data, i);
    i += 8;
    final var amountForgone = getInt64LE(_data, i);
    return new NewClaimEvent(discriminator,
                             claimant,
                             timestamp,
                             amountClaimed,
                             amountForgone);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    claimant.write(_data, i);
    i += 32;
    putInt64LE(_data, i, timestamp);
    i += 8;
    putInt64LE(_data, i, amountClaimed);
    i += 8;
    putInt64LE(_data, i, amountForgone);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

package software.sava.idl.clients.jupiter.merkle_distributor.gen.events;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record NewClaimEvent(Discriminator discriminator, PublicKey claimant, long timestamp) implements MerkleDistributorEvent {

  public static final int BYTES = 48;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(156, 211, 255, 5, 24, 206, 76, 171);

  public static final int CLAIMANT_OFFSET = 8;
  public static final int TIMESTAMP_OFFSET = 40;

  public static NewClaimEvent read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createDiscriminator(_data, _offset, 8);
    int i = _offset + discriminator.length();
    final var claimant = readPubKey(_data, i);
    i += 32;
    final var timestamp = getInt64LE(_data, i);
    return new NewClaimEvent(discriminator, claimant, timestamp);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    claimant.write(_data, i);
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

package software.sava.idl.clients.drift.merkle.distributor.gen.events;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record ClaimedEvent(Discriminator discriminator, PublicKey claimant, long amount) implements MerkleDistributorEvent {

  public static final int BYTES = 48;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(156, 211, 255, 5, 24, 206, 76, 171);

  public static ClaimedEvent read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createDiscriminator(_data, _offset, 8);
    int i = _offset + discriminator.length();
    final var claimant = readPubKey(_data, i);
    i += 32;
    final var amount = getInt64LE(_data, i);
    return new ClaimedEvent(discriminator, claimant, amount);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    claimant.write(_data, i);
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

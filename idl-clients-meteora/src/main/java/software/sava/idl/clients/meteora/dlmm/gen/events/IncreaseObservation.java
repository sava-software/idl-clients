package software.sava.idl.clients.meteora.dlmm.gen.events;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record IncreaseObservation(Discriminator discriminator, PublicKey oracle, long newObservationLength) implements LbClmmEvent {

  public static final int BYTES = 48;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(99, 249, 17, 121, 166, 156, 207, 215);

  public static IncreaseObservation read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var oracle = readPubKey(_data, i);
    i += 32;
    final var newObservationLength = getInt64LE(_data, i);
    return new IncreaseObservation(discriminator, oracle, newObservationLength);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    oracle.write(_data, i);
    i += 32;
    putInt64LE(_data, i, newObservationLength);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

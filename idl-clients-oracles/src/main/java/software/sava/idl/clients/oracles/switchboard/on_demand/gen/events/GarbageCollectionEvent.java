package software.sava.idl.clients.oracles.switchboard.on_demand.gen.events;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record GarbageCollectionEvent(Discriminator discriminator, PublicKey oracle, PublicKey queue) implements SbOnDemandEvent {

  public static final int BYTES = 72;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(232, 235, 2, 188, 8, 143, 145, 237);

  public static GarbageCollectionEvent read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var oracle = readPubKey(_data, i);
    i += 32;
    final var queue = readPubKey(_data, i);
    return new GarbageCollectionEvent(discriminator, oracle, queue);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    oracle.write(_data, i);
    i += 32;
    queue.write(_data, i);
    i += 32;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

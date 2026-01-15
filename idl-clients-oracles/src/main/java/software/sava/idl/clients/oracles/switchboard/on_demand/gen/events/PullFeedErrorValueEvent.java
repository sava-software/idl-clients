package software.sava.idl.clients.oracles.switchboard.on_demand.gen.events;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record PullFeedErrorValueEvent(Discriminator discriminator, PublicKey feed, PublicKey oracle) implements SbOnDemandEvent {

  public static final int BYTES = 72;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(225, 80, 192, 95, 14, 12, 83, 192);

  public static final int FEED_OFFSET = 8;
  public static final int ORACLE_OFFSET = 40;

  public static PullFeedErrorValueEvent read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var feed = readPubKey(_data, i);
    i += 32;
    final var oracle = readPubKey(_data, i);
    return new PullFeedErrorValueEvent(discriminator, feed, oracle);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    feed.write(_data, i);
    i += 32;
    oracle.write(_data, i);
    i += 32;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

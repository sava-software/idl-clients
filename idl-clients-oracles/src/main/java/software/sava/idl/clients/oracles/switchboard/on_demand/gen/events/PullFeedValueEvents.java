package software.sava.idl.clients.oracles.switchboard.on_demand.gen.events;

import java.math.BigInteger;

import software.sava.core.accounts.PublicKey;
import software.sava.core.borsh.Borsh;
import software.sava.core.programs.Discriminator;

import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record PullFeedValueEvents(Discriminator discriminator,
                                  PublicKey[] feeds,
                                  PublicKey[] oracles,
                                  BigInteger[][] values,
                                  int reward) implements SbOnDemandEvent {

  public static final Discriminator DISCRIMINATOR = toDiscriminator(86, 7, 231, 28, 122, 161, 117, 69);

  public static PullFeedValueEvents read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var feeds = Borsh.readPublicKeyVector(_data, i);
    i += Borsh.lenVector(feeds);
    final var oracles = Borsh.readPublicKeyVector(_data, i);
    i += Borsh.lenVector(oracles);
    final var values = Borsh.readMultiDimension128Vector(_data, i);
    i += Borsh.len128Vector(values);
    final var reward = getInt32LE(_data, i);
    return new PullFeedValueEvents(discriminator,
                                   feeds,
                                   oracles,
                                   values,
                                   reward);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    i += Borsh.writeVector(feeds, _data, i);
    i += Borsh.writeVector(oracles, _data, i);
    i += Borsh.write128Vector(values, _data, i);
    putInt32LE(_data, i, reward);
    i += 4;
    return i - _offset;
  }

  @Override
  public int l() {
    return 8 + Borsh.lenVector(feeds) + Borsh.lenVector(oracles) + Borsh.len128Vector(values) + 4;
  }
}

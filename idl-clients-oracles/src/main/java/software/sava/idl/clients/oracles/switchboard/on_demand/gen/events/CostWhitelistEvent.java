package software.sava.idl.clients.oracles.switchboard.on_demand.gen.events;

import java.math.BigInteger;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record CostWhitelistEvent(Discriminator discriminator,
                                 PublicKey[] feeds,
                                 PublicKey[] oracles,
                                 BigInteger[][] values,
                                 int reward) implements SbOnDemandEvent {

  public static final Discriminator DISCRIMINATOR = toDiscriminator(56, 107, 191, 127, 116, 6, 138, 149);

  public static final int FEEDS_OFFSET = 8;

  public static CostWhitelistEvent read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var feeds = SerDeUtil.readPublicKeyVector(4, _data, i);
    i += SerDeUtil.lenVector(4, feeds);
    final var oracles = SerDeUtil.readPublicKeyVector(4, _data, i);
    i += SerDeUtil.lenVector(4, oracles);
    final var values = SerDeUtil.readMultiDimension128Vector(4, _data, i);
    i += SerDeUtil.len128Vector(4, values);
    final var reward = getInt32LE(_data, i);
    return new CostWhitelistEvent(discriminator,
                                  feeds,
                                  oracles,
                                  values,
                                  reward);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    i += SerDeUtil.writeVector(4, feeds, _data, i);
    i += SerDeUtil.writeVector(4, oracles, _data, i);
    i += SerDeUtil.write128Vector(4, values, _data, i);
    putInt32LE(_data, i, reward);
    i += 4;
    return i - _offset;
  }

  @Override
  public int l() {
    return 8 + SerDeUtil.lenVector(4, feeds) + SerDeUtil.lenVector(4, oracles) + SerDeUtil.len128Vector(4, values) + 4;
  }
}

package software.sava.idl.clients.drift.gen.types;

import java.util.function.BiFunction;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;
import software.sava.core.rpc.Filter;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;
import software.sava.rpc.json.http.response.AccountInfo;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record ReferrerName(PublicKey _address,
                           Discriminator discriminator,
                           PublicKey authority,
                           PublicKey user,
                           PublicKey userStats,
                           byte[] name) implements SerDe {

  public static final int BYTES = 136;
  public static final int NAME_LEN = 32;
  public static final Filter SIZE_FILTER = Filter.createDataSizeFilter(BYTES);

  public static final Discriminator DISCRIMINATOR = toDiscriminator(105, 133, 170, 110, 52, 42, 28, 182);
  public static final Filter DISCRIMINATOR_FILTER = Filter.createMemCompFilter(0, DISCRIMINATOR.data());

  public static final int AUTHORITY_OFFSET = 8;
  public static final int USER_OFFSET = 40;
  public static final int USER_STATS_OFFSET = 72;
  public static final int NAME_OFFSET = 104;

  public static Filter createAuthorityFilter(final PublicKey authority) {
    return Filter.createMemCompFilter(AUTHORITY_OFFSET, authority);
  }

  public static Filter createUserFilter(final PublicKey user) {
    return Filter.createMemCompFilter(USER_OFFSET, user);
  }

  public static Filter createUserStatsFilter(final PublicKey userStats) {
    return Filter.createMemCompFilter(USER_STATS_OFFSET, userStats);
  }

  public static ReferrerName read(final byte[] _data, final int _offset) {
    return read(null, _data, _offset);
  }

  public static ReferrerName read(final AccountInfo<byte[]> accountInfo) {
    return read(accountInfo.pubKey(), accountInfo.data(), 0);
  }

  public static ReferrerName read(final PublicKey _address, final byte[] _data) {
    return read(_address, _data, 0);
  }

  public static final BiFunction<PublicKey, byte[], ReferrerName> FACTORY = ReferrerName::read;

  public static ReferrerName read(final PublicKey _address, final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var authority = readPubKey(_data, i);
    i += 32;
    final var user = readPubKey(_data, i);
    i += 32;
    final var userStats = readPubKey(_data, i);
    i += 32;
    final var name = new byte[32];
    SerDeUtil.readArray(name, _data, i);
    return new ReferrerName(_address,
                            discriminator,
                            authority,
                            user,
                            userStats,
                            name);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    authority.write(_data, i);
    i += 32;
    user.write(_data, i);
    i += 32;
    userStats.write(_data, i);
    i += 32;
    i += SerDeUtil.writeArrayChecked(name, 32, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

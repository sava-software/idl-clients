package software.sava.idl.clients.kamino.lend.gen.types;

import java.util.function.BiFunction;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;
import software.sava.core.rpc.Filter;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.rpc.json.http.response.AccountInfo;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record ReferrerState(PublicKey _address, Discriminator discriminator, PublicKey shortUrl, PublicKey owner) implements SerDe {

  public static final int BYTES = 72;
  public static final Filter SIZE_FILTER = Filter.createDataSizeFilter(BYTES);

  public static final Discriminator DISCRIMINATOR = toDiscriminator(194, 81, 217, 103, 12, 19, 12, 66);
  public static final Filter DISCRIMINATOR_FILTER = Filter.createMemCompFilter(0, DISCRIMINATOR.data());

  public static final int SHORT_URL_OFFSET = 8;
  public static final int OWNER_OFFSET = 40;

  public static Filter createShortUrlFilter(final PublicKey shortUrl) {
    return Filter.createMemCompFilter(SHORT_URL_OFFSET, shortUrl);
  }

  public static Filter createOwnerFilter(final PublicKey owner) {
    return Filter.createMemCompFilter(OWNER_OFFSET, owner);
  }

  public static ReferrerState read(final byte[] _data, final int _offset) {
    return read(null, _data, _offset);
  }

  public static ReferrerState read(final AccountInfo<byte[]> accountInfo) {
    return read(accountInfo.pubKey(), accountInfo.data(), 0);
  }

  public static ReferrerState read(final PublicKey _address, final byte[] _data) {
    return read(_address, _data, 0);
  }

  public static final BiFunction<PublicKey, byte[], ReferrerState> FACTORY = ReferrerState::read;

  public static ReferrerState read(final PublicKey _address, final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var shortUrl = readPubKey(_data, i);
    i += 32;
    final var owner = readPubKey(_data, i);
    return new ReferrerState(_address, discriminator, shortUrl, owner);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    shortUrl.write(_data, i);
    i += 32;
    owner.write(_data, i);
    i += 32;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

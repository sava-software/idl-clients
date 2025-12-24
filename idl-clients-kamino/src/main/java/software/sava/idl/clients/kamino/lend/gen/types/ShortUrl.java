package software.sava.idl.clients.kamino.lend.gen.types;

import java.lang.String;

import java.util.Arrays;
import java.util.function.BiFunction;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;
import software.sava.core.rpc.Filter;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;
import software.sava.rpc.json.http.response.AccountInfo;

import static java.nio.charset.StandardCharsets.UTF_8;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record ShortUrl(PublicKey _address, Discriminator discriminator, PublicKey referrer, String shortUrl, byte[] _shortUrl) implements SerDe {

  public static final Discriminator DISCRIMINATOR = toDiscriminator(28, 89, 174, 25, 226, 124, 126, 212);
  public static final Filter DISCRIMINATOR_FILTER = Filter.createMemCompFilter(0, DISCRIMINATOR.data());

  public static final int REFERRER_OFFSET = 8;
  public static final int SHORT_URL_OFFSET = 40;

  public static Filter createReferrerFilter(final PublicKey referrer) {
    return Filter.createMemCompFilter(REFERRER_OFFSET, referrer);
  }

  public static ShortUrl createRecord(final PublicKey _address, final Discriminator discriminator, final PublicKey referrer, final String shortUrl) {
    return new ShortUrl(_address, discriminator, referrer, shortUrl, shortUrl == null ? null : shortUrl.getBytes(UTF_8));
  }

  public static ShortUrl read(final byte[] _data, final int _offset) {
    return read(null, _data, _offset);
  }

  public static ShortUrl read(final AccountInfo<byte[]> accountInfo) {
    return read(accountInfo.pubKey(), accountInfo.data(), 0);
  }

  public static ShortUrl read(final PublicKey _address, final byte[] _data) {
    return read(_address, _data, 0);
  }

  public static final BiFunction<PublicKey, byte[], ShortUrl> FACTORY = ShortUrl::read;

  public static ShortUrl read(final PublicKey _address, final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var referrer = readPubKey(_data, i);
    i += 32;
    final int _shortUrlLength = getInt32LE(_data, i);
    i += 4;
    final byte[] _shortUrl = Arrays.copyOfRange(_data, i, i + _shortUrlLength);
    final var shortUrl = new String(_shortUrl, UTF_8);
    return new ShortUrl(_address, discriminator, referrer, shortUrl, shortUrl == null ? null : shortUrl.getBytes(UTF_8));
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    referrer.write(_data, i);
    i += 32;
    i += SerDeUtil.writeVector(4, _shortUrl, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return 8 + 32 + _shortUrl.length;
  }
}

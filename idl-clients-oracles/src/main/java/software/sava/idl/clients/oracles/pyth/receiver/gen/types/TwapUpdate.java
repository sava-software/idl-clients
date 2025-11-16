package software.sava.idl.clients.oracles.pyth.receiver.gen.types;

import java.util.function.BiFunction;

import software.sava.core.accounts.PublicKey;
import software.sava.core.borsh.Borsh;
import software.sava.core.programs.Discriminator;
import software.sava.core.rpc.Filter;
import software.sava.rpc.json.http.response.AccountInfo;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record TwapUpdate(PublicKey _address, Discriminator discriminator, PublicKey writeAuthority, TwapPrice twap) implements Borsh {

  public static final int BYTES = 112;
  public static final Filter SIZE_FILTER = Filter.createDataSizeFilter(BYTES);

  public static final Discriminator DISCRIMINATOR = toDiscriminator(104, 192, 188, 72, 246, 166, 12, 81);
  public static final Filter DISCRIMINATOR_FILTER = Filter.createMemCompFilter(0, DISCRIMINATOR.data());

  public static final int WRITE_AUTHORITY_OFFSET = 8;
  public static final int TWAP_OFFSET = 40;

  public static Filter createWriteAuthorityFilter(final PublicKey writeAuthority) {
    return Filter.createMemCompFilter(WRITE_AUTHORITY_OFFSET, writeAuthority);
  }

  public static Filter createTwapFilter(final TwapPrice twap) {
    return Filter.createMemCompFilter(TWAP_OFFSET, twap.write());
  }

  public static TwapUpdate read(final byte[] _data, final int _offset) {
    return read(null, _data, _offset);
  }

  public static TwapUpdate read(final AccountInfo<byte[]> accountInfo) {
    return read(accountInfo.pubKey(), accountInfo.data(), 0);
  }

  public static TwapUpdate read(final PublicKey _address, final byte[] _data) {
    return read(_address, _data, 0);
  }

  public static final BiFunction<PublicKey, byte[], TwapUpdate> FACTORY = TwapUpdate::read;

  public static TwapUpdate read(final PublicKey _address, final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var writeAuthority = readPubKey(_data, i);
    i += 32;
    final var twap = TwapPrice.read(_data, i);
    return new TwapUpdate(_address, discriminator, writeAuthority, twap);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    writeAuthority.write(_data, i);
    i += 32;
    i += twap.write(_data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

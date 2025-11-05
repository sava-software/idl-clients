package software.sava.idl.clients.cctp.message_transmitter.v2.gen.types;

import java.util.function.BiFunction;

import software.sava.core.accounts.PublicKey;
import software.sava.core.borsh.Borsh;
import software.sava.core.programs.Discriminator;
import software.sava.core.rpc.Filter;
import software.sava.rpc.json.http.response.AccountInfo;

import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record UsedNonce(PublicKey _address, Discriminator discriminator, boolean isUsed) implements Borsh {

  public static final int BYTES = 9;
  public static final Filter SIZE_FILTER = Filter.createDataSizeFilter(BYTES);

  public static final Discriminator DISCRIMINATOR = toDiscriminator(212, 222, 157, 252, 130, 71, 179, 238);
  public static final Filter DISCRIMINATOR_FILTER = Filter.createMemCompFilter(0, DISCRIMINATOR.data());

  public static final int IS_USED_OFFSET = 8;

  public static Filter createIsUsedFilter(final boolean isUsed) {
    return Filter.createMemCompFilter(IS_USED_OFFSET, new byte[]{(byte) (isUsed ? 1 : 0)});
  }

  public static UsedNonce read(final byte[] _data, final int _offset) {
    return read(null, _data, _offset);
  }

  public static UsedNonce read(final AccountInfo<byte[]> accountInfo) {
    return read(accountInfo.pubKey(), accountInfo.data(), 0);
  }

  public static UsedNonce read(final PublicKey _address, final byte[] _data) {
    return read(_address, _data, 0);
  }

  public static final BiFunction<PublicKey, byte[], UsedNonce> FACTORY = UsedNonce::read;

  public static UsedNonce read(final PublicKey _address, final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var isUsed = _data[i] == 1;
    return new UsedNonce(_address, discriminator, isUsed);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    _data[i] = (byte) (isUsed ? 1 : 0);
    ++i;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

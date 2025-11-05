package software.sava.idl.clients.kamino.scope.gen.types;

import java.util.function.BiFunction;

import software.sava.core.accounts.PublicKey;
import software.sava.core.borsh.Borsh;
import software.sava.core.programs.Discriminator;
import software.sava.core.rpc.Filter;
import software.sava.rpc.json.http.response.AccountInfo;

import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record TokenMetadatas(PublicKey _address, Discriminator discriminator, TokenMetadata[] metadatasArray) implements Borsh {

  public static final int BYTES = 86024;
  public static final int METADATAS_ARRAY_LEN = 512;
  public static final Filter SIZE_FILTER = Filter.createDataSizeFilter(BYTES);

  public static final Discriminator DISCRIMINATOR = toDiscriminator(221, 107, 64, 103, 67, 0, 165, 22);
  public static final Filter DISCRIMINATOR_FILTER = Filter.createMemCompFilter(0, DISCRIMINATOR.data());

  public static final int METADATAS_ARRAY_OFFSET = 8;

  public static TokenMetadatas read(final byte[] _data, final int _offset) {
    return read(null, _data, _offset);
  }

  public static TokenMetadatas read(final AccountInfo<byte[]> accountInfo) {
    return read(accountInfo.pubKey(), accountInfo.data(), 0);
  }

  public static TokenMetadatas read(final PublicKey _address, final byte[] _data) {
    return read(_address, _data, 0);
  }

  public static final BiFunction<PublicKey, byte[], TokenMetadatas> FACTORY = TokenMetadatas::read;

  public static TokenMetadatas read(final PublicKey _address, final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var metadatasArray = new TokenMetadata[512];
    Borsh.readArray(metadatasArray, TokenMetadata::read, _data, i);
    return new TokenMetadatas(_address, discriminator, metadatasArray);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    i += Borsh.writeArrayChecked(metadatasArray, 512, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

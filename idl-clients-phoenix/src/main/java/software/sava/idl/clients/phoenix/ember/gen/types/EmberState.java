package software.sava.idl.clients.phoenix.ember.gen.types;

import java.util.function.BiFunction;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;
import software.sava.core.rpc.Filter;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.rpc.json.http.response.AccountInfo;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record EmberState(PublicKey _address,
                         Discriminator discriminator,
                         long discriminant,
                         PublicKey authority,
                         PublicKey inputMint,
                         PublicKey outputMint) implements SerDe {

  public static final int BYTES = 112;
  public static final Filter SIZE_FILTER = Filter.createDataSizeFilter(BYTES);

  public static final Discriminator DISCRIMINATOR = toDiscriminator(0, 208, 11, 177, 63, 157, 55, 98);
  public static final Filter DISCRIMINATOR_FILTER = Filter.createMemCompFilter(0, DISCRIMINATOR.data());

  public static final int DISCRIMINANT_OFFSET = 8;
  public static final int AUTHORITY_OFFSET = 16;
  public static final int INPUT_MINT_OFFSET = 48;
  public static final int OUTPUT_MINT_OFFSET = 80;

  public static Filter createDiscriminantFilter(final long discriminant) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, discriminant);
    return Filter.createMemCompFilter(DISCRIMINANT_OFFSET, _data);
  }

  public static Filter createAuthorityFilter(final PublicKey authority) {
    return Filter.createMemCompFilter(AUTHORITY_OFFSET, authority);
  }

  public static Filter createInputMintFilter(final PublicKey inputMint) {
    return Filter.createMemCompFilter(INPUT_MINT_OFFSET, inputMint);
  }

  public static Filter createOutputMintFilter(final PublicKey outputMint) {
    return Filter.createMemCompFilter(OUTPUT_MINT_OFFSET, outputMint);
  }

  public static EmberState read(final byte[] _data, final int _offset) {
    return read(null, _data, _offset);
  }

  public static EmberState read(final AccountInfo<byte[]> accountInfo) {
    return read(accountInfo.pubKey(), accountInfo.data(), 0);
  }

  public static EmberState read(final PublicKey _address, final byte[] _data) {
    return read(_address, _data, 0);
  }

  public static final BiFunction<PublicKey, byte[], EmberState> FACTORY = EmberState::read;

  public static EmberState read(final PublicKey _address, final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var discriminant = getInt64LE(_data, i);
    i += 8;
    final var authority = readPubKey(_data, i);
    i += 32;
    final var inputMint = readPubKey(_data, i);
    i += 32;
    final var outputMint = readPubKey(_data, i);
    return new EmberState(_address,
                          discriminator,
                          discriminant,
                          authority,
                          inputMint,
                          outputMint);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    putInt64LE(_data, i, discriminant);
    i += 8;
    authority.write(_data, i);
    i += 32;
    inputMint.write(_data, i);
    i += 32;
    outputMint.write(_data, i);
    i += 32;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

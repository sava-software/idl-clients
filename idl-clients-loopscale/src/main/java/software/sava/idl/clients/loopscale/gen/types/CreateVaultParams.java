package software.sava.idl.clients.loopscale.gen.types;

import java.lang.String;

import java.util.Arrays;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static java.nio.charset.StandardCharsets.UTF_8;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt32LE;

public record CreateVaultParams(String tokenName, byte[] _tokenName,
                                String tokenSymbol, byte[] _tokenSymbol,
                                String tokenUri, byte[] _tokenUri,
                                PublicKey manager,
                                CreateStrategyParams createStrategyParams) implements SerDe {

  public static final int TOKEN_NAME_OFFSET = 0;

  public static CreateVaultParams createRecord(final String tokenName,
                                               final String tokenSymbol,
                                               final String tokenUri,
                                               final PublicKey manager,
                                               final CreateStrategyParams createStrategyParams) {
    return new CreateVaultParams(tokenName, tokenName == null ? null : tokenName.getBytes(UTF_8),
                                 tokenSymbol, tokenSymbol == null ? null : tokenSymbol.getBytes(UTF_8),
                                 tokenUri, tokenUri == null ? null : tokenUri.getBytes(UTF_8),
                                 manager,
                                 createStrategyParams);
  }

  public static CreateVaultParams read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final int _tokenNameLength = getInt32LE(_data, i);
    i += 4;
    final byte[] _tokenName = Arrays.copyOfRange(_data, i, i + _tokenNameLength);
    final var tokenName = new String(_tokenName, UTF_8);
    i += _tokenName.length;
    final int _tokenSymbolLength = getInt32LE(_data, i);
    i += 4;
    final byte[] _tokenSymbol = Arrays.copyOfRange(_data, i, i + _tokenSymbolLength);
    final var tokenSymbol = new String(_tokenSymbol, UTF_8);
    i += _tokenSymbol.length;
    final int _tokenUriLength = getInt32LE(_data, i);
    i += 4;
    final byte[] _tokenUri = Arrays.copyOfRange(_data, i, i + _tokenUriLength);
    final var tokenUri = new String(_tokenUri, UTF_8);
    i += _tokenUri.length;
    final var manager = readPubKey(_data, i);
    i += 32;
    final var createStrategyParams = CreateStrategyParams.read(_data, i);
    return new CreateVaultParams(tokenName, tokenName == null ? null : tokenName.getBytes(UTF_8),
                                 tokenSymbol, tokenSymbol == null ? null : tokenSymbol.getBytes(UTF_8),
                                 tokenUri, tokenUri == null ? null : tokenUri.getBytes(UTF_8),
                                 manager,
                                 createStrategyParams);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += SerDeUtil.writeVector(4, _tokenName, _data, i);
    i += SerDeUtil.writeVector(4, _tokenSymbol, _data, i);
    i += SerDeUtil.writeVector(4, _tokenUri, _data, i);
    manager.write(_data, i);
    i += 32;
    i += createStrategyParams.write(_data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return _tokenName.length
         + _tokenSymbol.length
         + _tokenUri.length
         + 32
         + createStrategyParams.l();
  }
}

package software.sava.idl.clients.meteora.dlmm.gen.types;

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

/// Parameter that set by the protocol
///
/// @param tokenMint token mint
/// @param padding Reserve
public record TokenBadge(PublicKey _address, Discriminator discriminator, PublicKey tokenMint, byte[] padding) implements SerDe {

  public static final int BYTES = 168;
  public static final int PADDING_LEN = 128;
  public static final Filter SIZE_FILTER = Filter.createDataSizeFilter(BYTES);

  public static final Discriminator DISCRIMINATOR = toDiscriminator(116, 219, 204, 229, 249, 116, 255, 150);
  public static final Filter DISCRIMINATOR_FILTER = Filter.createMemCompFilter(0, DISCRIMINATOR.data());

  public static final int TOKEN_MINT_OFFSET = 8;
  public static final int PADDING_OFFSET = 40;

  public static Filter createTokenMintFilter(final PublicKey tokenMint) {
    return Filter.createMemCompFilter(TOKEN_MINT_OFFSET, tokenMint);
  }

  public static TokenBadge read(final byte[] _data, final int _offset) {
    return read(null, _data, _offset);
  }

  public static TokenBadge read(final AccountInfo<byte[]> accountInfo) {
    return read(accountInfo.pubKey(), accountInfo.data(), 0);
  }

  public static TokenBadge read(final PublicKey _address, final byte[] _data) {
    return read(_address, _data, 0);
  }

  public static final BiFunction<PublicKey, byte[], TokenBadge> FACTORY = TokenBadge::read;

  public static TokenBadge read(final PublicKey _address, final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var tokenMint = readPubKey(_data, i);
    i += 32;
    final var padding = new byte[128];
    SerDeUtil.readArray(padding, _data, i);
    return new TokenBadge(_address, discriminator, tokenMint, padding);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    tokenMint.write(_data, i);
    i += 32;
    i += SerDeUtil.writeArrayChecked(padding, 128, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

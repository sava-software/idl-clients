package software.sava.idl.clients.jupiter.stable.gen.types;

import java.util.function.BiFunction;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;
import software.sava.core.rpc.Filter;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;
import software.sava.rpc.json.http.response.AccountInfo;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record Config(PublicKey _address,
                     Discriminator discriminator,
                     PublicKey mint,
                     PublicKey authority,
                     PublicKey tokenProgram,
                     PeriodLimit[] periodLimits,
                     long pegPriceUsd,
                     int decimals,
                     int isMintRedeemEnabled,
                     int authorityBump,
                     int configBump,
                     byte[] padding,
                     byte[] reserved) implements SerDe {

  public static final int BYTES = 504;
  public static final int PERIOD_LIMITS_LEN = 4;
  public static final int PADDING_LEN = 4;
  public static final int RESERVED_LEN = 192;
  public static final Filter SIZE_FILTER = Filter.createDataSizeFilter(BYTES);

  public static final Discriminator DISCRIMINATOR = toDiscriminator(155, 12, 170, 224, 30, 250, 204, 130);
  public static final Filter DISCRIMINATOR_FILTER = Filter.createMemCompFilter(0, DISCRIMINATOR.data());

  public static final int MINT_OFFSET = 8;
  public static final int AUTHORITY_OFFSET = 40;
  public static final int TOKEN_PROGRAM_OFFSET = 72;
  public static final int PERIOD_LIMITS_OFFSET = 104;
  public static final int PEG_PRICE_USD_OFFSET = 296;
  public static final int DECIMALS_OFFSET = 304;
  public static final int IS_MINT_REDEEM_ENABLED_OFFSET = 305;
  public static final int AUTHORITY_BUMP_OFFSET = 306;
  public static final int CONFIG_BUMP_OFFSET = 307;
  public static final int PADDING_OFFSET = 308;
  public static final int RESERVED_OFFSET = 312;

  public static Filter createMintFilter(final PublicKey mint) {
    return Filter.createMemCompFilter(MINT_OFFSET, mint);
  }

  public static Filter createAuthorityFilter(final PublicKey authority) {
    return Filter.createMemCompFilter(AUTHORITY_OFFSET, authority);
  }

  public static Filter createTokenProgramFilter(final PublicKey tokenProgram) {
    return Filter.createMemCompFilter(TOKEN_PROGRAM_OFFSET, tokenProgram);
  }

  public static Filter createPegPriceUsdFilter(final long pegPriceUsd) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, pegPriceUsd);
    return Filter.createMemCompFilter(PEG_PRICE_USD_OFFSET, _data);
  }

  public static Filter createDecimalsFilter(final int decimals) {
    return Filter.createMemCompFilter(DECIMALS_OFFSET, new byte[]{(byte) decimals});
  }

  public static Filter createIsMintRedeemEnabledFilter(final int isMintRedeemEnabled) {
    return Filter.createMemCompFilter(IS_MINT_REDEEM_ENABLED_OFFSET, new byte[]{(byte) isMintRedeemEnabled});
  }

  public static Filter createAuthorityBumpFilter(final int authorityBump) {
    return Filter.createMemCompFilter(AUTHORITY_BUMP_OFFSET, new byte[]{(byte) authorityBump});
  }

  public static Filter createConfigBumpFilter(final int configBump) {
    return Filter.createMemCompFilter(CONFIG_BUMP_OFFSET, new byte[]{(byte) configBump});
  }

  public static Config read(final byte[] _data, final int _offset) {
    return read(null, _data, _offset);
  }

  public static Config read(final AccountInfo<byte[]> accountInfo) {
    return read(accountInfo.pubKey(), accountInfo.data(), 0);
  }

  public static Config read(final PublicKey _address, final byte[] _data) {
    return read(_address, _data, 0);
  }

  public static final BiFunction<PublicKey, byte[], Config> FACTORY = Config::read;

  public static Config read(final PublicKey _address, final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var mint = readPubKey(_data, i);
    i += 32;
    final var authority = readPubKey(_data, i);
    i += 32;
    final var tokenProgram = readPubKey(_data, i);
    i += 32;
    final var periodLimits = new PeriodLimit[4];
    i += SerDeUtil.readArray(periodLimits, PeriodLimit::read, _data, i);
    final var pegPriceUsd = getInt64LE(_data, i);
    i += 8;
    final var decimals = _data[i] & 0xFF;
    ++i;
    final var isMintRedeemEnabled = _data[i] & 0xFF;
    ++i;
    final var authorityBump = _data[i] & 0xFF;
    ++i;
    final var configBump = _data[i] & 0xFF;
    ++i;
    final var padding = new byte[4];
    i += SerDeUtil.readArray(padding, _data, i);
    final var reserved = new byte[192];
    SerDeUtil.readArray(reserved, _data, i);
    return new Config(_address,
                      discriminator,
                      mint,
                      authority,
                      tokenProgram,
                      periodLimits,
                      pegPriceUsd,
                      decimals,
                      isMintRedeemEnabled,
                      authorityBump,
                      configBump,
                      padding,
                      reserved);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    mint.write(_data, i);
    i += 32;
    authority.write(_data, i);
    i += 32;
    tokenProgram.write(_data, i);
    i += 32;
    i += SerDeUtil.writeArrayChecked(periodLimits, 4, _data, i);
    putInt64LE(_data, i, pegPriceUsd);
    i += 8;
    _data[i] = (byte) decimals;
    ++i;
    _data[i] = (byte) isMintRedeemEnabled;
    ++i;
    _data[i] = (byte) authorityBump;
    ++i;
    _data[i] = (byte) configBump;
    ++i;
    i += SerDeUtil.writeArrayChecked(padding, 4, _data, i);
    i += SerDeUtil.writeArrayChecked(reserved, 192, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

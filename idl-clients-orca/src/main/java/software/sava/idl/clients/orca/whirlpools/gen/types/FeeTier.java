package software.sava.idl.clients.orca.whirlpools.gen.types;

import java.util.function.BiFunction;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;
import software.sava.core.rpc.Filter;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.rpc.json.http.response.AccountInfo;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt16LE;
import static software.sava.core.encoding.ByteUtil.putInt16LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record FeeTier(PublicKey _address,
                      Discriminator discriminator,
                      PublicKey whirlpoolsConfig,
                      int tickSpacing,
                      int defaultFeeRate) implements SerDe {

  public static final int BYTES = 44;
  public static final Filter SIZE_FILTER = Filter.createDataSizeFilter(BYTES);

  public static final Discriminator DISCRIMINATOR = toDiscriminator(56, 75, 159, 76, 142, 68, 190, 105);
  public static final Filter DISCRIMINATOR_FILTER = Filter.createMemCompFilter(0, DISCRIMINATOR.data());

  public static final int WHIRLPOOLS_CONFIG_OFFSET = 8;
  public static final int TICK_SPACING_OFFSET = 40;
  public static final int DEFAULT_FEE_RATE_OFFSET = 42;

  public static Filter createWhirlpoolsConfigFilter(final PublicKey whirlpoolsConfig) {
    return Filter.createMemCompFilter(WHIRLPOOLS_CONFIG_OFFSET, whirlpoolsConfig);
  }

  public static Filter createTickSpacingFilter(final int tickSpacing) {
    final byte[] _data = new byte[2];
    putInt16LE(_data, 0, tickSpacing);
    return Filter.createMemCompFilter(TICK_SPACING_OFFSET, _data);
  }

  public static Filter createDefaultFeeRateFilter(final int defaultFeeRate) {
    final byte[] _data = new byte[2];
    putInt16LE(_data, 0, defaultFeeRate);
    return Filter.createMemCompFilter(DEFAULT_FEE_RATE_OFFSET, _data);
  }

  public static FeeTier read(final byte[] _data, final int _offset) {
    return read(null, _data, _offset);
  }

  public static FeeTier read(final AccountInfo<byte[]> accountInfo) {
    return read(accountInfo.pubKey(), accountInfo.data(), 0);
  }

  public static FeeTier read(final PublicKey _address, final byte[] _data) {
    return read(_address, _data, 0);
  }

  public static final BiFunction<PublicKey, byte[], FeeTier> FACTORY = FeeTier::read;

  public static FeeTier read(final PublicKey _address, final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var whirlpoolsConfig = readPubKey(_data, i);
    i += 32;
    final var tickSpacing = getInt16LE(_data, i);
    i += 2;
    final var defaultFeeRate = getInt16LE(_data, i);
    return new FeeTier(_address, discriminator, whirlpoolsConfig, tickSpacing, defaultFeeRate);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    whirlpoolsConfig.write(_data, i);
    i += 32;
    putInt16LE(_data, i, tickSpacing);
    i += 2;
    putInt16LE(_data, i, defaultFeeRate);
    i += 2;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

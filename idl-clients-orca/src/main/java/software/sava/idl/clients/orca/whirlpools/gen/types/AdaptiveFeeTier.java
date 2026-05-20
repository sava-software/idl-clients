package software.sava.idl.clients.orca.whirlpools.gen.types;

import java.util.function.BiFunction;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;
import software.sava.core.rpc.Filter;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.rpc.json.http.response.AccountInfo;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt16LE;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt16LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record AdaptiveFeeTier(PublicKey _address,
                              Discriminator discriminator,
                              PublicKey whirlpoolsConfig,
                              int feeTierIndex,
                              int tickSpacing,
                              PublicKey initializePoolAuthority,
                              PublicKey delegatedFeeAuthority,
                              int defaultBaseFeeRate,
                              int filterPeriod,
                              int decayPeriod,
                              int reductionFactor,
                              int adaptiveFeeControlFactor,
                              int maxVolatilityAccumulator,
                              int tickGroupSize,
                              int majorSwapThresholdTicks) implements SerDe {

  public static final int BYTES = 128;
  public static final Filter SIZE_FILTER = Filter.createDataSizeFilter(BYTES);

  public static final Discriminator DISCRIMINATOR = toDiscriminator(147, 16, 144, 116, 47, 146, 149, 46);
  public static final Filter DISCRIMINATOR_FILTER = Filter.createMemCompFilter(0, DISCRIMINATOR.data());

  public static final int WHIRLPOOLS_CONFIG_OFFSET = 8;
  public static final int FEE_TIER_INDEX_OFFSET = 40;
  public static final int TICK_SPACING_OFFSET = 42;
  public static final int INITIALIZE_POOL_AUTHORITY_OFFSET = 44;
  public static final int DELEGATED_FEE_AUTHORITY_OFFSET = 76;
  public static final int DEFAULT_BASE_FEE_RATE_OFFSET = 108;
  public static final int FILTER_PERIOD_OFFSET = 110;
  public static final int DECAY_PERIOD_OFFSET = 112;
  public static final int REDUCTION_FACTOR_OFFSET = 114;
  public static final int ADAPTIVE_FEE_CONTROL_FACTOR_OFFSET = 116;
  public static final int MAX_VOLATILITY_ACCUMULATOR_OFFSET = 120;
  public static final int TICK_GROUP_SIZE_OFFSET = 124;
  public static final int MAJOR_SWAP_THRESHOLD_TICKS_OFFSET = 126;

  public static Filter createWhirlpoolsConfigFilter(final PublicKey whirlpoolsConfig) {
    return Filter.createMemCompFilter(WHIRLPOOLS_CONFIG_OFFSET, whirlpoolsConfig);
  }

  public static Filter createFeeTierIndexFilter(final int feeTierIndex) {
    final byte[] _data = new byte[2];
    putInt16LE(_data, 0, feeTierIndex);
    return Filter.createMemCompFilter(FEE_TIER_INDEX_OFFSET, _data);
  }

  public static Filter createTickSpacingFilter(final int tickSpacing) {
    final byte[] _data = new byte[2];
    putInt16LE(_data, 0, tickSpacing);
    return Filter.createMemCompFilter(TICK_SPACING_OFFSET, _data);
  }

  public static Filter createInitializePoolAuthorityFilter(final PublicKey initializePoolAuthority) {
    return Filter.createMemCompFilter(INITIALIZE_POOL_AUTHORITY_OFFSET, initializePoolAuthority);
  }

  public static Filter createDelegatedFeeAuthorityFilter(final PublicKey delegatedFeeAuthority) {
    return Filter.createMemCompFilter(DELEGATED_FEE_AUTHORITY_OFFSET, delegatedFeeAuthority);
  }

  public static Filter createDefaultBaseFeeRateFilter(final int defaultBaseFeeRate) {
    final byte[] _data = new byte[2];
    putInt16LE(_data, 0, defaultBaseFeeRate);
    return Filter.createMemCompFilter(DEFAULT_BASE_FEE_RATE_OFFSET, _data);
  }

  public static Filter createFilterPeriodFilter(final int filterPeriod) {
    final byte[] _data = new byte[2];
    putInt16LE(_data, 0, filterPeriod);
    return Filter.createMemCompFilter(FILTER_PERIOD_OFFSET, _data);
  }

  public static Filter createDecayPeriodFilter(final int decayPeriod) {
    final byte[] _data = new byte[2];
    putInt16LE(_data, 0, decayPeriod);
    return Filter.createMemCompFilter(DECAY_PERIOD_OFFSET, _data);
  }

  public static Filter createReductionFactorFilter(final int reductionFactor) {
    final byte[] _data = new byte[2];
    putInt16LE(_data, 0, reductionFactor);
    return Filter.createMemCompFilter(REDUCTION_FACTOR_OFFSET, _data);
  }

  public static Filter createAdaptiveFeeControlFactorFilter(final int adaptiveFeeControlFactor) {
    final byte[] _data = new byte[4];
    putInt32LE(_data, 0, adaptiveFeeControlFactor);
    return Filter.createMemCompFilter(ADAPTIVE_FEE_CONTROL_FACTOR_OFFSET, _data);
  }

  public static Filter createMaxVolatilityAccumulatorFilter(final int maxVolatilityAccumulator) {
    final byte[] _data = new byte[4];
    putInt32LE(_data, 0, maxVolatilityAccumulator);
    return Filter.createMemCompFilter(MAX_VOLATILITY_ACCUMULATOR_OFFSET, _data);
  }

  public static Filter createTickGroupSizeFilter(final int tickGroupSize) {
    final byte[] _data = new byte[2];
    putInt16LE(_data, 0, tickGroupSize);
    return Filter.createMemCompFilter(TICK_GROUP_SIZE_OFFSET, _data);
  }

  public static Filter createMajorSwapThresholdTicksFilter(final int majorSwapThresholdTicks) {
    final byte[] _data = new byte[2];
    putInt16LE(_data, 0, majorSwapThresholdTicks);
    return Filter.createMemCompFilter(MAJOR_SWAP_THRESHOLD_TICKS_OFFSET, _data);
  }

  public static AdaptiveFeeTier read(final byte[] _data, final int _offset) {
    return read(null, _data, _offset);
  }

  public static AdaptiveFeeTier read(final AccountInfo<byte[]> accountInfo) {
    return read(accountInfo.pubKey(), accountInfo.data(), 0);
  }

  public static AdaptiveFeeTier read(final PublicKey _address, final byte[] _data) {
    return read(_address, _data, 0);
  }

  public static final BiFunction<PublicKey, byte[], AdaptiveFeeTier> FACTORY = AdaptiveFeeTier::read;

  public static AdaptiveFeeTier read(final PublicKey _address, final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var whirlpoolsConfig = readPubKey(_data, i);
    i += 32;
    final var feeTierIndex = getInt16LE(_data, i);
    i += 2;
    final var tickSpacing = getInt16LE(_data, i);
    i += 2;
    final var initializePoolAuthority = readPubKey(_data, i);
    i += 32;
    final var delegatedFeeAuthority = readPubKey(_data, i);
    i += 32;
    final var defaultBaseFeeRate = getInt16LE(_data, i);
    i += 2;
    final var filterPeriod = getInt16LE(_data, i);
    i += 2;
    final var decayPeriod = getInt16LE(_data, i);
    i += 2;
    final var reductionFactor = getInt16LE(_data, i);
    i += 2;
    final var adaptiveFeeControlFactor = getInt32LE(_data, i);
    i += 4;
    final var maxVolatilityAccumulator = getInt32LE(_data, i);
    i += 4;
    final var tickGroupSize = getInt16LE(_data, i);
    i += 2;
    final var majorSwapThresholdTicks = getInt16LE(_data, i);
    return new AdaptiveFeeTier(_address,
                               discriminator,
                               whirlpoolsConfig,
                               feeTierIndex,
                               tickSpacing,
                               initializePoolAuthority,
                               delegatedFeeAuthority,
                               defaultBaseFeeRate,
                               filterPeriod,
                               decayPeriod,
                               reductionFactor,
                               adaptiveFeeControlFactor,
                               maxVolatilityAccumulator,
                               tickGroupSize,
                               majorSwapThresholdTicks);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    whirlpoolsConfig.write(_data, i);
    i += 32;
    putInt16LE(_data, i, feeTierIndex);
    i += 2;
    putInt16LE(_data, i, tickSpacing);
    i += 2;
    initializePoolAuthority.write(_data, i);
    i += 32;
    delegatedFeeAuthority.write(_data, i);
    i += 32;
    putInt16LE(_data, i, defaultBaseFeeRate);
    i += 2;
    putInt16LE(_data, i, filterPeriod);
    i += 2;
    putInt16LE(_data, i, decayPeriod);
    i += 2;
    putInt16LE(_data, i, reductionFactor);
    i += 2;
    putInt32LE(_data, i, adaptiveFeeControlFactor);
    i += 4;
    putInt32LE(_data, i, maxVolatilityAccumulator);
    i += 4;
    putInt16LE(_data, i, tickGroupSize);
    i += 2;
    putInt16LE(_data, i, majorSwapThresholdTicks);
    i += 2;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

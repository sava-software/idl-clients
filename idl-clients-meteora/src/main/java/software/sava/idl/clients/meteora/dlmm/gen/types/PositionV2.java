package software.sava.idl.clients.meteora.dlmm.gen.types;

import java.math.BigInteger;

import java.util.function.BiFunction;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;
import software.sava.core.rpc.Filter;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;
import software.sava.rpc.json.http.response.AccountInfo;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

/// @param lbPair The LB pair of this position
/// @param owner Owner of the position. Client rely on this to to fetch their positions.
/// @param liquidityShares Liquidity shares of this position in bins (lower_bin_id <-> upper_bin_id). This is the same as LP concept.
/// @param rewardInfos Farming reward information
/// @param feeInfos Swap fee to claim information
/// @param lowerBinId Lower bin ID
/// @param upperBinId Upper bin ID
/// @param lastUpdatedAt Last updated timestamp
/// @param totalClaimedFeeXAmount Total claimed token fee X
/// @param totalClaimedFeeYAmount Total claimed token fee Y
/// @param totalClaimedRewards Total claimed rewards
/// @param operator Operator of position
/// @param lockReleasePoint Time point which the locked liquidity can be withdraw
/// @param padding0 _padding_0, previous subjected_to_bootstrap_liquidity_locking, BE CAREFUL FOR TOMBSTONE WHEN REUSE !!
/// @param feeOwner Address is able to claim fee in this position, only valid for bootstrap_liquidity_position
/// @param version version to know whether we have reset tombstone fields
/// @param reserved Reserved space for future use
public record PositionV2(PublicKey _address,
                         Discriminator discriminator,
                         PublicKey lbPair,
                         PublicKey owner,
                         BigInteger[] liquidityShares,
                         UserRewardInfo[] rewardInfos,
                         FeeInfo[] feeInfos,
                         int lowerBinId,
                         int upperBinId,
                         long lastUpdatedAt,
                         long totalClaimedFeeXAmount,
                         long totalClaimedFeeYAmount,
                         long[] totalClaimedRewards,
                         PublicKey operator,
                         long lockReleasePoint,
                         int padding0,
                         PublicKey feeOwner,
                         int version,
                         byte[] reserved) implements SerDe {

  public static final int BYTES = 8120;
  public static final int LIQUIDITY_SHARES_LEN = 70;
  public static final int REWARD_INFOS_LEN = 70;
  public static final int FEE_INFOS_LEN = 70;
  public static final int TOTAL_CLAIMED_REWARDS_LEN = 2;
  public static final int RESERVED_LEN = 86;
  public static final Filter SIZE_FILTER = Filter.createDataSizeFilter(BYTES);

  public static final Discriminator DISCRIMINATOR = toDiscriminator(117, 176, 212, 199, 245, 180, 133, 182);
  public static final Filter DISCRIMINATOR_FILTER = Filter.createMemCompFilter(0, DISCRIMINATOR.data());

  public static final int LB_PAIR_OFFSET = 8;
  public static final int OWNER_OFFSET = 40;
  public static final int LIQUIDITY_SHARES_OFFSET = 72;
  public static final int REWARD_INFOS_OFFSET = 1192;
  public static final int FEE_INFOS_OFFSET = 4552;
  public static final int LOWER_BIN_ID_OFFSET = 7912;
  public static final int UPPER_BIN_ID_OFFSET = 7916;
  public static final int LAST_UPDATED_AT_OFFSET = 7920;
  public static final int TOTAL_CLAIMED_FEE_X_AMOUNT_OFFSET = 7928;
  public static final int TOTAL_CLAIMED_FEE_Y_AMOUNT_OFFSET = 7936;
  public static final int TOTAL_CLAIMED_REWARDS_OFFSET = 7944;
  public static final int OPERATOR_OFFSET = 7960;
  public static final int LOCK_RELEASE_POINT_OFFSET = 7992;
  public static final int PADDING_0_OFFSET = 8000;
  public static final int FEE_OWNER_OFFSET = 8001;
  public static final int VERSION_OFFSET = 8033;
  public static final int RESERVED_OFFSET = 8034;

  public static Filter createLbPairFilter(final PublicKey lbPair) {
    return Filter.createMemCompFilter(LB_PAIR_OFFSET, lbPair);
  }

  public static Filter createOwnerFilter(final PublicKey owner) {
    return Filter.createMemCompFilter(OWNER_OFFSET, owner);
  }

  public static Filter createLowerBinIdFilter(final int lowerBinId) {
    final byte[] _data = new byte[4];
    putInt32LE(_data, 0, lowerBinId);
    return Filter.createMemCompFilter(LOWER_BIN_ID_OFFSET, _data);
  }

  public static Filter createUpperBinIdFilter(final int upperBinId) {
    final byte[] _data = new byte[4];
    putInt32LE(_data, 0, upperBinId);
    return Filter.createMemCompFilter(UPPER_BIN_ID_OFFSET, _data);
  }

  public static Filter createLastUpdatedAtFilter(final long lastUpdatedAt) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, lastUpdatedAt);
    return Filter.createMemCompFilter(LAST_UPDATED_AT_OFFSET, _data);
  }

  public static Filter createTotalClaimedFeeXAmountFilter(final long totalClaimedFeeXAmount) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, totalClaimedFeeXAmount);
    return Filter.createMemCompFilter(TOTAL_CLAIMED_FEE_X_AMOUNT_OFFSET, _data);
  }

  public static Filter createTotalClaimedFeeYAmountFilter(final long totalClaimedFeeYAmount) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, totalClaimedFeeYAmount);
    return Filter.createMemCompFilter(TOTAL_CLAIMED_FEE_Y_AMOUNT_OFFSET, _data);
  }

  public static Filter createOperatorFilter(final PublicKey operator) {
    return Filter.createMemCompFilter(OPERATOR_OFFSET, operator);
  }

  public static Filter createLockReleasePointFilter(final long lockReleasePoint) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, lockReleasePoint);
    return Filter.createMemCompFilter(LOCK_RELEASE_POINT_OFFSET, _data);
  }

  public static Filter createPadding0Filter(final int padding0) {
    return Filter.createMemCompFilter(PADDING_0_OFFSET, new byte[]{(byte) padding0});
  }

  public static Filter createFeeOwnerFilter(final PublicKey feeOwner) {
    return Filter.createMemCompFilter(FEE_OWNER_OFFSET, feeOwner);
  }

  public static Filter createVersionFilter(final int version) {
    return Filter.createMemCompFilter(VERSION_OFFSET, new byte[]{(byte) version});
  }

  public static PositionV2 read(final byte[] _data, final int _offset) {
    return read(null, _data, _offset);
  }

  public static PositionV2 read(final AccountInfo<byte[]> accountInfo) {
    return read(accountInfo.pubKey(), accountInfo.data(), 0);
  }

  public static PositionV2 read(final PublicKey _address, final byte[] _data) {
    return read(_address, _data, 0);
  }

  public static final BiFunction<PublicKey, byte[], PositionV2> FACTORY = PositionV2::read;

  public static PositionV2 read(final PublicKey _address, final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var lbPair = readPubKey(_data, i);
    i += 32;
    final var owner = readPubKey(_data, i);
    i += 32;
    final var liquidityShares = new BigInteger[70];
    i += SerDeUtil.read128Array(liquidityShares, _data, i);
    final var rewardInfos = new UserRewardInfo[70];
    i += SerDeUtil.readArray(rewardInfos, UserRewardInfo::read, _data, i);
    final var feeInfos = new FeeInfo[70];
    i += SerDeUtil.readArray(feeInfos, FeeInfo::read, _data, i);
    final var lowerBinId = getInt32LE(_data, i);
    i += 4;
    final var upperBinId = getInt32LE(_data, i);
    i += 4;
    final var lastUpdatedAt = getInt64LE(_data, i);
    i += 8;
    final var totalClaimedFeeXAmount = getInt64LE(_data, i);
    i += 8;
    final var totalClaimedFeeYAmount = getInt64LE(_data, i);
    i += 8;
    final var totalClaimedRewards = new long[2];
    i += SerDeUtil.readArray(totalClaimedRewards, _data, i);
    final var operator = readPubKey(_data, i);
    i += 32;
    final var lockReleasePoint = getInt64LE(_data, i);
    i += 8;
    final var padding0 = _data[i] & 0xFF;
    ++i;
    final var feeOwner = readPubKey(_data, i);
    i += 32;
    final var version = _data[i] & 0xFF;
    ++i;
    final var reserved = new byte[86];
    SerDeUtil.readArray(reserved, _data, i);
    return new PositionV2(_address,
                          discriminator,
                          lbPair,
                          owner,
                          liquidityShares,
                          rewardInfos,
                          feeInfos,
                          lowerBinId,
                          upperBinId,
                          lastUpdatedAt,
                          totalClaimedFeeXAmount,
                          totalClaimedFeeYAmount,
                          totalClaimedRewards,
                          operator,
                          lockReleasePoint,
                          padding0,
                          feeOwner,
                          version,
                          reserved);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    lbPair.write(_data, i);
    i += 32;
    owner.write(_data, i);
    i += 32;
    i += SerDeUtil.write128ArrayChecked(liquidityShares, 70, _data, i);
    i += SerDeUtil.writeArrayChecked(rewardInfos, 70, _data, i);
    i += SerDeUtil.writeArrayChecked(feeInfos, 70, _data, i);
    putInt32LE(_data, i, lowerBinId);
    i += 4;
    putInt32LE(_data, i, upperBinId);
    i += 4;
    putInt64LE(_data, i, lastUpdatedAt);
    i += 8;
    putInt64LE(_data, i, totalClaimedFeeXAmount);
    i += 8;
    putInt64LE(_data, i, totalClaimedFeeYAmount);
    i += 8;
    i += SerDeUtil.writeArrayChecked(totalClaimedRewards, 2, _data, i);
    operator.write(_data, i);
    i += 32;
    putInt64LE(_data, i, lockReleasePoint);
    i += 8;
    _data[i] = (byte) padding0;
    ++i;
    feeOwner.write(_data, i);
    i += 32;
    _data[i] = (byte) version;
    ++i;
    i += SerDeUtil.writeArrayChecked(reserved, 86, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}
